package it.unive.cybertech.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Utils {
    public static class GenericMessageDialog extends DialogFragment {
        private final String title, message;
        private final Context context;

        public GenericMessageDialog(String title, String message, @NonNull Context context) {
            this.title = title;
            this.message = message;
            this.context = context;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                        dialog.dismiss();
                    });
            return builder.create();
        }
    }
}
