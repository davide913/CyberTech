package it.unive.cybertech.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;


/**
 * Support class that contains some useful methods to show a message to the user
 * @author Daniele Dotto
 * @since 1.1
 */
public class Showables {

    private Showables() { }

    /**
     * Create and show a short-length toast (@see "{@link Toast}".
     * @since 1.1
     */
    public static void showShortToast(@NonNull final String message, @NonNull final Context context) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
