package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
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
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;
/**
 * The Fragment that allows positive to COVID-19 users of Family Share (Plugin) to make help requests,
 * in order to get help from the volunteer users for their daily tasks.
 *
 * @author Mihail Racaru
 * @since 1.1
 */
public class HomePagePositive extends Fragment {
    private ListView listAlreadyMade;
    private final User user = CachedUser.user;
    private ArrayAdapter<QuarantineAssistance> adapter;
    private List<QuarantineAssistance> myRequestsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page_positive, container, false);
        try {
            initView(view);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View view) throws ExecutionException, InterruptedException {
        listAlreadyMade = view.findViewById(R.id.lst_myRequests);

        Utils.executeAsync(user::obtainMaterializedQuarantineAssistance, new Utils.TaskResult<List<QuarantineAssistance>>() {
            @Override
            public void onComplete(List<QuarantineAssistance> result) {
                myRequestsList = result;

                myRequestsList.sort((o1, o2) -> o2.getDeliveryDateToDate().compareTo(o1.getDeliveryDateToDate()));

                adapter = new CustomRequestsAdapter(getContext(), 0, myRequestsList);
                listAlreadyMade.setAdapter(adapter);

                listAlreadyMade.setOnItemClickListener(((parent, view1, position, id) -> {
                    Intent newIntent = new Intent(getContext(), RequestViz.class);

                    String strDate = Utils.formatDateToString(myRequestsList.get(position).getDeliveryDateToDate(), "kk:mm  dd-MM" );
                    geoPointer(newIntent, position);
                    putExtra(newIntent, position, strDate);

                    startActivityForResult(newIntent, 4);
                }));

            }

            @Override
            public void onError(Exception e) {
            }
        });


        view.findViewById(R.id.add_new_request).setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(), RequestDetails.class);

            startActivityForResult(newIntent, 2);
        });
    }

    /**
     * For the visualization of the selected request at the given position
     * Sets some key fields which will be used by {@link it.unive.cybertech.assistenza.RequestViz}
     *
     * @param position for the position in the list
     * @param strDate for the date with right pattern
     * @author Mihail Racaru
     * @since 1.1
     */
    private void putExtra(Intent newIntent, int position, String strDate) {
        newIntent.putExtra("title", adapter.getItem(position).getTitle());
        newIntent.putExtra("date", strDate);
        newIntent.putExtra("id", adapter.getItem(position).getId());
        newIntent.putExtra("class", "positive");
    }

    /**
     * Finds the nearest location to the given coordinates from request at the specified position in the list,
     * if find none, the putExtra method is set at a default value "Out of Bounds"
     *
     * @param position, the input position given
     * @author Mihail Racaru
     * @since 1.1
     */
    private void geoPointer(Intent newIntent, int position) {
        GeoPoint point = adapter.getItem(position).getLocation();

        @NonNull Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        @NonNull List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            if(addresses.size() != 0) {
                @NonNull String newCountry = addresses.get(0).getCountryName();
                newIntent.putExtra("country", newCountry);

                @NonNull String newCity = addresses.get(0).getLocality();
                newIntent.putExtra("city", newCity);
            }
            else {
                newIntent.putExtra("country", "Out of Bounds");
                newIntent.putExtra("city", "Out of Bounds");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ft.replace(R.id.main_fragment_content, new it.unive.cybertech.assistenza.HomePagePositive()).commit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        updateFr();
    }
}
