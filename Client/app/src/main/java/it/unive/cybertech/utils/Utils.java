package it.unive.cybertech.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.database.Profile.Sex;

public class Utils {
    public interface DialogResult {
        void onSuccess();

        void onCancel();
    }

    public static class Dialog {
        private DialogResult result;
        private boolean showOkButton, showCancelButton;
        private String okButtonText, cancelButtonText;
        private Context c;

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

        public Dialog hideCancelButton(){
            showCancelButton = false;
            return this;
        }

        public void showDialog(String title, String message) {
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
}
