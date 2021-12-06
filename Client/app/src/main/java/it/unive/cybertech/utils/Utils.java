package it.unive.cybertech.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.sql.Timestamp;
import java.time.Instant;
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

    public static int createNotification(Context ctx, String channelID, String title, String text, String icon) {
        int id = (int) Timestamp.from(Instant.now()).getTime();
        createNotificationChannelIfNotExists(channelID, ctx);
        int iconResource = R.drawable.ic_baseline_notifications_24;
        if (icon != null)
            switch (icon) {
                case "coronavirus":
                    iconResource = R.drawable.ic_baseline_coronavirus_24;
                    break;
                case "assistance_chat":
                    iconResource = R.drawable.ic_baseline_chat_64;
                    break;
            }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, channelID)
                .setSmallIcon(iconResource)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(id, builder.build());
        return id;
    }

    public static void createNotificationChannelIfNotExists(@NonNull String channelID, Context ctx) {
        String name = "", description = "";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        switch (channelID) {
            case "default":
                name = "Avvisi";
                description = "Canale per le notifiche";
                importance = NotificationManager.IMPORTANCE_DEFAULT;
                break;
            case "assistance_chat":
                name = "Chat di assistenza";
                description = "Canale utilizzato per le notifiche relative alla chat di assistenza in quarantena";
                importance = NotificationManager.IMPORTANCE_DEFAULT;
                break;
            case "coronavirus":
                name = "Notifica di esposizione";
                description = "Canale utilizzato per avvisarti di una eventuale espoizione al nuovo coronavirus SARS-CoV-2 (COVID-19)";
                importance = NotificationManager.IMPORTANCE_HIGH;
                break;
            default:
                throw new RuntimeException("NOTIFICATION: the channel '" + channelID + "' was not found");
        }
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void initNotificationChannels(Context ctx) {
        createNotificationChannelIfNotExists("default", ctx);
        createNotificationChannelIfNotExists("assistance_chat", ctx);
        createNotificationChannelIfNotExists("coronavirus", ctx);
    }
}