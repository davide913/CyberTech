package it.unive.cybertech.utils;
import android.content.Context;
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
    public static void showGenericDialog(String title, String message, Context c) {
        new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static Sex convertToSex(@NonNull String sex){
        switch(sex.toLowerCase()){
            case"maschio":
                return Sex.male;
            case "femmina":
                return  Sex.female;
            default:
                return Sex.nonBinary;
        }
    }
}
