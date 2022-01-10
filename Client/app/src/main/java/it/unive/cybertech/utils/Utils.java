package it.unive.cybertech.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.database.Profile.Sex;

public class Utils {
    public static final int HANDLER_DELAY = 500;

    public interface DialogResult {
        void onSuccess();

        void onCancel();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface TaskResult<T> {
        void onComplete(T result) throws ExecutionException, InterruptedException;

        OnFailureListener onError(Exception e) throws ExecutionException, InterruptedException;
    }

    /**
     * It can be invoked in this way
     * <p>
     * new Utils.Dialog(c).showDialog("title", "message");
     * <p>
     * OR
     * <p>
     * new Utils.Dialog(this)
     * .setCallback(new Utils.DialogResult() {
     *
     * @Override public void onSuccess() {
     * }
     * @Override public void onCancel() {
     * }
     * })
     * [.hideCancelButton()]
     * [.hideOkButton()]
     * .showDialog("", "");
     */
    public static class Dialog {
        private DialogResult result;
        private boolean showOkButton, showCancelButton;
        private final String okButtonText, cancelButtonText;
        private final Context c;

        public Dialog(Context c) {
            showOkButton = true;
            showCancelButton = true;
            okButtonText = c.getString(android.R.string.ok);
            cancelButtonText = c.getString(android.R.string.cancel);
            this.c = c;
        }

        public Dialog(Context c, boolean showOkButton, String okButtonText, boolean showCancelButton, String cancelButtonText) {
            this.showOkButton = showOkButton;
            this.showCancelButton = showCancelButton;
            this.okButtonText = okButtonText;
            this.cancelButtonText = cancelButtonText;
            this.c = c;
        }

        public Dialog setCallback(DialogResult fun) {
            result = fun;
            return this;
        }

        public Dialog hideCancelButton() {
            showCancelButton = false;
            return this;
        }

        public Dialog hideOkButton() {
            showOkButton = false;
            return this;
        }

        public void show(String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c)
                    .setTitle(title)
                    .setMessage(message);
            buildAndShow(builder);
        }

        public void show(String title, View content) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c)
                    .setTitle(title)
                    .setView(content);
            buildAndShow(builder);
        }

        private void buildAndShow(AlertDialog.Builder builder) {
            if (showOkButton)
                builder.setPositiveButton(okButtonText, (dialog, which) -> {
                    dialog.dismiss();
                    if (result != null)
                        result.onSuccess();
                });
            if (showCancelButton)
                builder.setNegativeButton(cancelButtonText, (dialog, which) -> {
                    dialog.dismiss();
                    if (result != null)
                        result.onCancel();
                });
            builder.show();
        }
    }

    public static Sex convertToSex(@NonNull String sex) {
        switch (sex.toLowerCase()) {
            case "maschio":
                return Sex.male;
            case "femmina":
                return Sex.female;
            default:
                return Sex.nonBinary;
        }
    }

    public static class FragmentAdapter extends FragmentPagerAdapter {

        class FragmentListAdapter {
            private String title, id;
            private Fragment fragment;

            public FragmentListAdapter(String id, String title, Fragment fragment) {
                this.title = title;
                this.id = id;
                this.fragment = fragment;
            }

            public String getTitle() {
                return title;
            }

            public String getId() {
                return id;
            }

            public Fragment getFragment() {
                return fragment;
            }
        }

        private final List<FragmentListAdapter> mFragmentList = new ArrayList<>();

        public FragmentAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position).fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, String id) {
            mFragmentList.add(new FragmentListAdapter(id, title, fragment));
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).getTitle();
        }

        public Fragment getFragmentById(String id) {
            for (FragmentListAdapter p : mFragmentList)
                if (p.getId().equals(id))
                    return p.fragment;
            return null;
        }
    }

    /**
     * Logout from application and disconnect user from database access
     *
     * @since 1.0
     */
    public static void logout(Context context) {
        FirebaseAuth.getInstance().signOut();
        @NonNull Intent intent = new Intent(context, SplashScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String formatDateToString(@NotNull Date date) {
        return formatDateToString(date, "dd/MM/yyyy");
    }

    public static String formatDateToString(@NotNull Date date, @NotNull String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static <R> void executeAsync(@NonNull Callable<R> callable, TaskResult<R> callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                R result = callable.call();
                if (callback != null)
                    handler.post(() -> {
                        try {
                            callback.onComplete(result);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
            } catch (Exception e) {
                if (callback != null)
                    handler.post(() -> {
                        try {
                            callback.onError(e);
                        } catch (ExecutionException | InterruptedException executionException) {
                            executionException.printStackTrace();
                        }
                    });
            }
        }).start();
    }

    /*
     * How to use executeAsync
     * new Utils.TaskResult<YourReturnType>

     * Vedi la funzione "initList" in ShowcaseFragment
     * */
    private void test() {
        Utils.executeAsync(() -> { /*Your db function here*/
            return null;
        }, new Utils.TaskResult<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
            }

            @Override
            public OnFailureListener onError(Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static class Location {
        public String city;
        public String country;
        public String address;
        public double latitude, longitude;
    }

    public static class PermissionDeniedException extends Exception {
        public PermissionDeniedException(String message) {
            super(message);
        }
    }

    public static void getLocation(@NonNull Activity activity, @NonNull TaskResult<Location> callback) throws PermissionDeniedException, ExecutionException, InterruptedException {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(activity);
            client.getLastLocation().addOnSuccessListener(activity, location -> {
                try {
                    Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                    List<Address> addresses;
                    Location result = new Location();
                    result.latitude = location.getLatitude();
                    result.longitude = location.getLongitude();
                    addresses = geocoder.getFromLocation(result.latitude, result.longitude, 1);
                    result.country = addresses.get(0).getCountryName();
                    result.city = addresses.get(0).getLocality();
                    result.address = addresses.get(0).getThoroughfare();
                    callback.onComplete(result);
                } catch (IOException | ExecutionException | InterruptedException e) {
                    try {
                        callback.onError(e);
                    } catch (ExecutionException | InterruptedException executionException) {
                        executionException.printStackTrace();
                    }
                }
            }).addOnFailureListener(callback.onError(new Exception("Error")));
        } else
            throw new PermissionDeniedException("GPS permission not granted");
    }
}