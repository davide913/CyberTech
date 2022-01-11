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
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.database.Profile.Sex;

/**
 * This class is only a namespace for utils and common function
 *
 * @author Mattia Musone
 */
public class Utils {
    public static final int HANDLER_DELAY = 500;

    /**
     * Interface used by the Utils.Dialog class when a Dialog button is pressed
     */
    public interface DialogResult {
        void onSuccess();

        void onCancel();
    }

    /**
     * Interface used by the list adapter when an item is pressed
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * Interface used by the Utils.executeAsync method to notify the end of a task
     */
    public interface TaskResult<T> {
        void onComplete(T result);

        void onError(Exception e);
    }

    /**
     * Create an alert dialog that display a message to the user
     */
    public static class Dialog {
        private DialogResult result;
        private boolean showOkButton, showCancelButton;
        private final String okButtonText, cancelButtonText;
        private final Context c;

        /**
         * The constructor
         *
         * @param c The context
         */
        public Dialog(Context c) {
            showOkButton = true;
            showCancelButton = true;
            okButtonText = c.getString(android.R.string.ok);
            cancelButtonText = c.getString(android.R.string.cancel);
            this.c = c;
        }

        /**
         * More detailed constructor that allow to customize the alert buttons
         *
         * @param c                The context
         * @param showOkButton     Indicate if the "ok" button should be showed
         * @param okButtonText     The "ok" button text displayed to the user
         * @param showCancelButton Indicate if the "cancel" button should be showed
         * @param cancelButtonText The "cancel" button text displayed to the user
         */
        public Dialog(Context c, boolean showOkButton, String okButtonText, boolean showCancelButton, String cancelButtonText) {
            this.showOkButton = showOkButton;
            this.showCancelButton = showCancelButton;
            this.okButtonText = okButtonText;
            this.cancelButtonText = cancelButtonText;
            this.c = c;
        }

        /**
         * Set a callback to be called when a button is pressed.
         * It is not mandatory
         */
        public Dialog setCallback(DialogResult fun) {
            result = fun;
            return this;
        }

        /**
         * Hide the left most button "cancel"
         */
        public Dialog hideCancelButton() {
            showCancelButton = false;
            return this;
        }

        /**
         * Hide the right most button "ok"
         */
        public Dialog hideOkButton() {
            showOkButton = false;
            return this;
        }

        /**
         * Shows the dialog
         *
         * @param title   The title displayed to the ussr
         * @param message The message displayed to the ussr
         */
        public void show(String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c)
                    .setTitle(title)
                    .setMessage(message);
            buildAndShow(builder);
        }

        /**
         * Shows the dialog
         *
         * @param title   The title displayed to the ussr
         * @param content The view to inflate instead of the default one
         */
        public void show(String title, View content) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c)
                    .setTitle(title)
                    .setView(content);
            buildAndShow(builder);
        }

        /**
         * Private function used to build the dialog and display it
         */
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

    /**
     * Function that convert a string to a Sex enum
     *
     * @param sex String sex
     * @return Sex
     */
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

    /**
     * This class provide a custom fragment adapter to manage the fragments
     */
    public static class FragmentAdapter extends FragmentPagerAdapter {

        /**
         * Custom data structure to hold the fragments
         */
        static class FragmentListAdapter {
            private final String title, id;
            private final Fragment fragment;

            /**
             * The constructor
             *
             * @param id       The id to associate to the passed fragment
             * @param title    The title of the fragment to display
             * @param fragment The fragment
             */
            public FragmentListAdapter(@NonNull String id, @NonNull String title, @NonNull Fragment fragment) {
                this.title = title;
                this.id = id;
                this.fragment = fragment;
            }

            /**
             * Get the fragment title
             */
            public String getTitle() {
                return title;
            }

            /**
             * Get the fragment id
             */
            public String getId() {
                return id;
            }

            /**
             * Get the fragment
             */
            public Fragment getFragment() {
                return fragment;
            }
        }

        //The list holding all the fragment in the custom data
        private final List<FragmentListAdapter> mFragmentList = new ArrayList<>();

        /**
         * The constructor
         */
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

        /**
         * Adds a fragment to the list of fragment
         *
         * @param fragment The fragment to add
         * @param id       The id to associate to the passed fragment
         * @param title    The title of the fragment to display
         */
        public void addFragment(@NonNull Fragment fragment, @NonNull String title, @NonNull String id) {
            mFragmentList.add(new FragmentListAdapter(id, title, fragment));
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).getTitle();
        }

        /**
         * Search a fragment by the provided id and returns it, if any
         *
         * @param id he id of the fragment to look for
         * @return The fragment with the provided id
         * */
        @Nullable
        public Fragment getFragmentById(@NonNull String id) {
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

    /**
     * Simple date to string formatter (dd/MM/yyyy)
     *
     * @param date The date to convert
     * */
    public static String formatDateToString(@NotNull Date date) {
        return formatDateToString(date, "dd/MM/yyyy");
    }

    /**
     * Simple date to string formatter with a custom pattern
     *
     * @param date The date to convert
     * @param pattern The pattern to use
     * */
    public static String formatDateToString(@NotNull Date date, @NotNull String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * This function allow to run some code asynchronous
     *
     * @param callable A callable code to execute in a new task
     * @param callback If provided, it will be called at the end of the task execution
     * */
    public static <R> void executeAsync(@NonNull Callable<R> callable, TaskResult<R> callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                R result = callable.call();
                if (callback != null)
                    handler.post(() -> {
                        callback.onComplete(result);
                    });
            } catch (Exception e) {
                if (callback != null)
                    handler.post(() -> {
                        callback.onError(e);
                    });
            }
        }).start();
    }

    /**
     * Custom class that represent a location returned by the GPS
     * */
    public static class Location {
        public String city;
        public String country;
        public String address;
        public double latitude, longitude;
    }

    /**
     * Custom exception thrown when the GPS permission are denied
     * */
    public static class PermissionDeniedException extends Exception {
        public PermissionDeniedException(String message) {
            super(message);
        }
    }

    /**
     * Return the current location of the user.
     * First checks if the permission are grantedm otherwise an exception will be thrown
     *
     * @param activity The activity from which the call start
     * @param callback The callback to invoke when the location is returned
     * @throws PermissionDeniedException when the permission of the location are denied
     * */
    public static void getLocation(@NonNull Activity activity, @NonNull TaskResult<Location> callback) throws PermissionDeniedException {
        //Check the GPS permission
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
                } catch (Exception e) {
                    callback.onError(e);
                }
            }).addOnFailureListener(callback::onError);
        } else
            throw new PermissionDeniedException("GPS permission not granted");
    }
}