package it.unive.cybertech.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.Sex;

public class Utils {
    public interface DialogResult {
        void onSuccess();

        void onCancel();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
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

        private final List<Pair<String, Fragment>> mFragmentList = new ArrayList<>();

        public FragmentAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position).second;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(new Pair<>(title, fragment));
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return  mFragmentList.get(position).first;
        }
    }
}