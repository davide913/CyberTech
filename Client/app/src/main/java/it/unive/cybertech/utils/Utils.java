package it.unive.cybertech.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
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
        void onComplete(T result);

        void onError(Exception e);
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
    public static class  Dialog {
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

        private void buildAndShow(AlertDialog.Builder builder){
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

    public static <R> void executeAsync(Callable<R> callable, TaskResult<R> callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                R result = callable.call();
                handler.post(() -> callback.onComplete(result));
            } catch (Exception e) {
                handler.post(() -> callback.onError(e));
            }
        }).start();
    }
    /*
    * How to use executeAsync
    * new Utils.TaskResult<YourReturnType>

    * Vedi la funzione "initList" in ShowcaseFragment
    * */
    private void test(){
        Utils.executeAsync(() -> { /*Your db function here*/return null; }, new Utils.TaskResult<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}