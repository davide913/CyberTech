package it.unive.cybertech.messages;

import static it.unive.cybertech.utils.CachedUser.user;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.common.collect.Collections2;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.Device;
import it.unive.cybertech.database.Profile.User;

public class MessageService extends FirebaseMessagingService {

    public enum NotificationType {
        base,
        coronavirus,
        assistance_chat
    }

    private final static String TAG = "FirebaseMessage";

    public static void getCurrentToken(OnCompleteListener<String> listener) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(listener);
    }

    public static void sendMessageToUserDevices(@NonNull User user, @NonNull NotificationType type, String title, String message, Context ctx) {
        try {
            RequestQueue queue = Volley.newRequestQueue(ctx);
            for (Device device : user.getMaterializedDevices())
                sendMessage(device, type, title, message, queue);
        } catch (InterruptedException | ExecutionException e) {

        }
    }

    public static void sendMessage(@NonNull Device device, @NonNull NotificationType type, String title, String message, Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        sendMessage(device, type, title, message, queue);
    }

    private static void sendMessage(@NonNull Device device, @NonNull NotificationType type, String title, String message, RequestQueue queue) {
        //device.updateLastUsed();
        String url = "https://fcm.googleapis.com/fcm/send";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, response);
                }, error -> {
            Log.e(TAG, error.getMessage());
        }) {
            @Override
            public byte[] getBody() {
                JSONObject parameters = new JSONObject();
                try {
                    JSONObject notification = new JSONObject();
                    notification.put("title", title);
                    notification.put("body", message);
                    switch (type) {
                        default:
                        case base:
                            notification.put("android_channel_id", "base");
                            notification.put("icon", "notification_icon");
                            break;
                        case coronavirus:
                            notification.put("android_channel_id", "coronavirus");
                            notification.put("icon", "notification_coronavirus_icon");
                            break;
                        case assistance_chat:
                            notification.put("android_channel_id", "assistance_chat");
                            notification.put("icon", "notification_assistance_chat_icon");
                            break;
                    }
                    notification.put("click_action", "OPEN_SPLASH_SCREEN");
                    Log.d(TAG, notification.toString());
                    parameters.put("notification", notification);
                    parameters.put("to", device.getToken());
                    JSONObject data = new JSONObject();
                    data.put("type", type);
                    parameters.put("data", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, parameters.toString());
                return parameters.toString().getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AAAAAVonNY8:APA91bGJJ6W629ZfdorEtiTDssz8C39mc9a4LFUxbDtourqN-OotX9cxkkSSpJ_bemZuyX7KBvC-1pMVa6UpPTiKOFrTwfl8fBVfnreDWwhJibp8Lp_HdpioEQgO4haWl0sqWydbQ8n2");
                return headers;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String deviceID = Settings.Secure.ANDROID_ID;
        try {
            Device[] devices = (Device[]) Collections2.filter(user.getMaterializedDevices(), f -> f.getDeviceId().equals(deviceID)).toArray();
            if (devices.length > 0) {
                Device device = devices[0];
                if (device == null) {
                    device = Device.createDevice(s, deviceID);
                    user.addDevice(device);
                } /*else
                    device.updateLastUsed();*/
            }
        } catch (InterruptedException | ExecutionException e) {
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification not = remoteMessage.getNotification();
            Log.d(TAG, "Message Notification Data: " + remoteMessage.getData());
            Log.d(TAG, "Message Notification Body: " + not.getBody());
            Log.d(TAG, "Message Notification channel: " + not.getChannelId());
            Log.d(TAG, "Message Notification icon: " + not.getIcon());
            createNotification(getApplicationContext(), NotificationType.valueOf(remoteMessage.getData().get("type")), not.getTitle(), not.getBody());
        }
    }

    public static int createNotification(Context ctx, NotificationType type, String title, String text) {
        int id = (int) Timestamp.from(Instant.now()).getTime();
        int iconResource;
        String channelID = getChannelIDByType(type);
        int argb;
        switch (type) {
            default:
            case base:
                iconResource = R.drawable.notification_icon;
                argb = ctx.getColor(R.color.primary);
                break;
            case coronavirus:
                iconResource = R.drawable.notification_coronavirus_icon;
                argb = ctx.getColor(R.color.red_fs);
                break;
            case assistance_chat:
                iconResource = R.drawable.notification_assistance_chat_icon;
                argb = ctx.getColor(R.color.light_green_fs);
                break;
        }
        createNotificationChannelIfNotExists(type, ctx);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, channelID)
                .setSmallIcon(iconResource)
                .setContentTitle(title)
                .setContentText(text)
                .setColor(argb)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(id, builder.build());
        return id;
    }

    public static void createNotificationChannelIfNotExists(@NonNull NotificationType type, Context ctx) {
        String name, description, channelID = getChannelIDByType(type);
        int importance;
        switch (type) {
            default:
            case base:
                name = "Avvisi";
                description = "Canale per le notifiche generiche";
                importance = NotificationManager.IMPORTANCE_DEFAULT;
                break;
            case assistance_chat:
                name = "Chat di assistenza";
                description = "Canale utilizzato per le notifiche relative alla chat di assistenza in quarantena";
                importance = NotificationManager.IMPORTANCE_DEFAULT;
                break;
            case coronavirus:
                name = "Notifica di esposizione";
                description = "Canale utilizzato per avvisarti di una eventuale espoizione al nuovo coronavirus SARS-CoV-2 (COVID-19)";
                importance = NotificationManager.IMPORTANCE_HIGH;
                break;
        }

        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private static String getChannelIDByType(NotificationType type) {
        switch (type) {
            default:
            case base:
                return "base";
            case coronavirus:
                return "coronavirus";
            case assistance_chat:
                return "assistance_chat";
        }
    }

    public static void initNotificationChannels(Context ctx) {
        createNotificationChannelIfNotExists(NotificationType.base, ctx);
        createNotificationChannelIfNotExists(NotificationType.coronavirus, ctx);
        createNotificationChannelIfNotExists(NotificationType.assistance_chat, ctx);
    }
}
