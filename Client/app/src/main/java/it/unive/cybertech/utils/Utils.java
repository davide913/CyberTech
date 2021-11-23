package it.unive.cybertech.utils;
import android.content.Context;
import android.location.Location;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.atomic.AtomicReference;

public class Utils {
    public static void showGenericDialog(String title, String message, Context c) {
        new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
