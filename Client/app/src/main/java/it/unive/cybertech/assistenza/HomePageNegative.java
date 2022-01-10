package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.CustomRequestsAdapter;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;

/**
 * The Fragment that allows volunteer users of Family Share (Plugin) to interact with help requests made
 * by users found positive to COVID-19.
 *
 * @author Mihail Racaru
 * @since 1.1
 */
public class HomePageNegative extends Fragment {
    private ListView listView;
    private final User user = CachedUser.user;
    private ArrayAdapter<QuarantineAssistance> adapter;
    private ArrayList<AssistanceType> tList = new ArrayList<>();
    private List<QuarantineAssistance> myQuar = new ArrayList<>();
    private ArrayList<QuarantineAssistance> myQuarantineList = new ArrayList<>();
    private QuarantineAssistance inCharge = null;
    private AssistanceType aux = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_assistenza, container, false);

        try {
            initViews(view);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) throws ExecutionException, InterruptedException {
        listView = view.findViewById(R.id.listRequests);
        GeoPoint myGeoPosition = user.getLocation();
        Spinner sp = view.findViewById(R.id.homeNegSpinner);
        ArrayList<String> names = new ArrayList<>();

        Utils.executeAsync(AssistanceType::obtainAssistanceTypes, new Utils.TaskResult<ArrayList<AssistanceType>>() {
            @Override
            public void onComplete(ArrayList<AssistanceType> result) {
                tList = result;

                Thread t = new Thread(() -> {
                    try {
                        myQuar = QuarantineAssistance.obtainJoinableQuarantineAssistance(null, myGeoPosition, 50);
                        inCharge = QuarantineAssistance.obtainQuarantineAssistanceByInCharge(user);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (AssistanceType a: tList) {
                        names.add(a.getType());
                    }
                    names.add(0, "Tutte le richieste");
                    myQuarantineList.addAll(myQuar);
                });
                t.start();
                try {
                    t.join();
                }
                catch(InterruptedException ignored) {}

                adapter = new CustomRequestsAdapter(getContext(), 0, myQuarantineList);
                listView.setAdapter(adapter);

                ArrayAdapter<String> arr = new ArrayAdapter<>(getContext(), R.layout.spinner_item, names);
                sp.setAdapter(arr);
                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        String selectedItemText = (String) parent.getItemAtPosition(position);

                        if(position >= 0){
                            showShortToast("Selected : " + selectedItemText);
                        }

                        for (AssistanceType a: tList) {
                            if(a.getType().equals(selectedItemText))
                                aux = a;
                        }

                        Thread m = new Thread(() -> {
                            myQuarantineList = new ArrayList<>();

                            try {
                                myQuar = QuarantineAssistance.obtainJoinableQuarantineAssistance(aux, myGeoPosition, 50);
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }

                            myQuarantineList.addAll(myQuar);
                        });
                        m.start();
                        try {
                            m.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        adapter.clear();
                        adapter.addAll(myQuarantineList);
                        adapter.notifyDataSetChanged();
                    }

                    public void onNothingSelected(AdapterView<?> parent){}
                });

                listView.setOnItemClickListener(((parent, view1, position, id) -> {
                    Intent newIntent = new Intent(getContext(), RequestViz.class);

                    geoDecoder(adapter.getItem(position), newIntent);
                    String taken = null;
                    try {
                        taken = inCharge.getId();
                    }
                    catch(NullPointerException ignored) {}

                    newIntent.putExtra("alreadyTaken", taken);
                    newIntent.putExtra("id", adapter.getItem(position).getId());
                    newIntent.putExtra("class", "Homenegative");

                    startActivityForResult(newIntent, 1);
                }));

                view.findViewById(R.id.alreadyTaken).setOnClickListener(v -> {
                    Intent newIntent = new Intent(getContext(), RequestViz.class);

                    String id;
                    if(inCharge != null) {
                        id = inCharge.getId();
                        geoDecoder(inCharge, newIntent);

                        newIntent.putExtra("user", id);
                    }
                    startActivityForResult(newIntent, 0);
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * Allows to update Fragment
     *
     * @author Mihail Racaru
     * @since 1.1
     */
    private void updateFr(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_fragment_content, new it.unive.cybertech.assistenza.HomePageNegative()).commit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        updateFr();
    }

    /**
     * Finds the nearest location to the given coordinates from request, if find none, the putExtra
     * method is set at a default value "Out of Bounds"
     *
     * @param request, the input request given
     * @author Mihail Racaru
     * @since 1.1
     */
    private void geoDecoder(QuarantineAssistance request, Intent newIntent){
        GeoPoint point = request.getLocation();

        @NonNull Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        @NonNull List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            if(addresses.size() != 0) {
                newIntent.putExtra("country",addresses.get(0).getCountryName());
                newIntent.putExtra("city", addresses.get(0).getLocality());
            }
            else {
                newIntent.putExtra("country", "Out of Bounds");
                newIntent.putExtra("city", "Out of Bounds");
            }
            newIntent.putExtra("title",  request.getTitle());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to show a Toast with the given message String
     *
     * @param message, the input String
     * @author Mihail Racaru
     * @since 1.1
     */
    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

}