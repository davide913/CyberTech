package it.unive.cybertech.utils;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;

public class Utils {
    public static void showGenericDialog(String title, String message, Context c) {
        new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
