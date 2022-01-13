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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.Device;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

/**
 * This class extends FirebaseMessagingService in order to provide a service for the notification.
 * <p>
 * All methods are static and manage all the resource and functions to send notification and receve them
 *
 * @author Mattia Musone
 */
public class MessageService extends FirebaseMessagingService {

    /**
     * Enum used for track all the possible notification type
     */
    public enum NotificationType {
        base,
        coronavirus,
        assistance_chat,
        request_accepted,
        request_stop_helping,
    }

    private final static String TAG = "FirebaseMessage";

    /**
     * Function that returns the current user token for the notification
     *
     * @param listener the callback that will be called when the token is retrieved
     */
    public static void getCurrentToken(@NonNull OnCompleteListener<String> listener) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(listener);
    }

    /**
     * This function send a notification to all the user's devices provided.
     * It loops through the device user and send, one to one, a notification with the specified data
     *
     * @param user    The user to get the devices from
     * @param type    The type of notification to send
     * @param title   The title of notification shown
     * @param message The body of the notification message with further information
     * @param ctx     The context
     */
    public static void sendMessageToUserDevices(@NonNull User user, @NonNull NotificationType type, @NonNull String title, @NonNull String message, @NonNull Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        Utils.executeAsync(user::obtainMaterializedDevices, new Utils.TaskResult<List<Device>>() {
            @Override
            public void onComplete(List<Device> result) {
                for (Device device : result)
                    sendMessage(device, type, title, message, queue);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * This function send a message to a single device
     *
     * @param device  The user's device to send notification
     * @param type    The type of notification to send
     * @param title   The title of notification shown
     * @param message The body of the notification message with further information
     * @param ctx     The context
     */
    public static void sendMessage(@NonNull Device device, @NonNull NotificationType type, String title, String message, Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        sendMessage(device, type, title, message, queue);
    }

    /**
     * This function send a message to a single device
     * <p>
     * <strong>PLEASE NOTE that this method should be in a server and the key encrypted on it, but for university purpose is placed here</strong>
     * </p>
     *
     * @param device  The user's device to send notification
     * @param type    The type of notification to send
     * @param title   The title of notification shown
     * @param message The body of the notification message with further information
     * @param queue   The queue of http requests
     */
    private static void sendMessage(@NonNull Device device, @NonNull NotificationType type, String title, String message, RequestQueue queue) {
        //Upload the token in order to set it as "in use"
        Utils.executeAsync(device::updateLastUsed, null);
        //The url of Google fcm server
        String url = "https://fcm.googleapis.com/fcm/send";
        //Send the request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, response);
                }, error -> {
            Log.e(TAG, error.getMessage());
            //The request's body
        }) {
            /**
             * Build the notification body base on Google developer standard
             * */
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
                        case request_accepted:
                            notification.put("android_channel_id", "request_accepted");
                            notification.put("icon", "notification_icon");
                            break;
                        case request_stop_helping:
                            notification.put("android_channel_id", "request_stop_helping");
                            notification.put("icon", "notification_icon");
                            break;
                    }
                    notification.put("click_action", "OPEN_SPLASH_SCREEN");
                    Log.d(TAG, notification.toString());
                    parameters.put("notification", notification);
                    parameters.put("to", device.getToken());
                    //Additional parameters
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

            /**
             * <strong>NOTE that this key should be in a server and not here</strong>
             * */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=AAAAAVonNY8:APA91bGJJ6W629ZfdorEtiTDssz8C39mc9a4LFUxbDtourqN-OotX9cxkkSSpJ_bemZuyX7KBvC-1pMVa6UpPTiKOFrTwfl8fBVfnreDWwhJibp8Lp_HdpioEQgO4haWl0sqWydbQ8n2");
                return headers;
            }
        };
        queue.add(stringRequest);
    }

    /**
     * When a new token is generated, update and add it to the user's ones
     */
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (user != null) {
            String deviceID = Settings.Secure.ANDROID_ID;
            try {
                Device[] devices = (Device[]) Collections2.filter(user.obtainMaterializedDevices(), f -> f.getDeviceId().equals(deviceID)).toArray();
                if (devices.length > 0) {
                    Device device = devices[0];
                    if (device == null) {
                        user.addDevice(s, deviceID);
                    } else
                        device.updateLastUsed();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * When a notification is received when the app is open
     */
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

    /**
     * This function manage the notifications.
     * It creates a new one with a unique id and returns it
     *
     * @param ctx   The context
     * @param type  The type of notification to send
     * @param title The title of notification shown
     * @param text  The body of the notification message with further information
     */
    public static int createNotification(Context ctx, NotificationType type, String title, String text) {
        //Generate an unique id
        int id = (int) Timestamp.from(Instant.now()).getTime();
        int iconResource;
        String channelID = getChannelIDByType(type);
        int argb;
        //Choose the right channel in order ro set a correct image, color and priority
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
            case request_accepted:
                iconResource = R.drawable.notification_icon;
                argb = ctx.getColor(R.color.dark_green_fs);
                break;
            case request_stop_helping:
                iconResource = R.drawable.notification_icon;
                argb = ctx.getColor(R.color.orange_fs);
                break;

        }
        createNotificationChannelIfNotExists(type, ctx);
        //Call the system API to create a notification
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

    /**
     * This function manage the Android notification channel and creates it if not exists
     *
     * @param type The type of notification
     * @param ctx  The context
     */
    public static void createNotificationChannelIfNotExists(@NonNull NotificationType type, Context ctx) {
        String name, description, channelID = getChannelIDByType(type);
        int importance;
        switch (type) {
            default:
            case base:
                name = ctx.getString(R.string.alerts);
                description = ctx.getString(R.string.notification_channel_generic);
                importance = NotificationManager.IMPORTANCE_DEFAULT;
                break;
            case assistance_chat:
                name = ctx.getString(R.string.assistance_chat);
                description = ctx.getString(R.string.notification_channel_quarantine_assistance);
                importance = NotificationManager.IMPORTANCE_DEFAULT;
                break;
            case coronavirus:
                name = ctx.getString(R.string.covid_notification);
                description = ctx.getString(R.string.notification_channel_covid);
                importance = NotificationManager.IMPORTANCE_HIGH;
                break;
            case request_accepted:
                name = ctx.getString(R.string.alerts);
                description = ctx.getString(R.string.notification_channel_quarantine_request_accepted);
                importance = NotificationManager.IMPORTANCE_HIGH;
                break;
            case request_stop_helping:
                name = ctx.getString(R.string.alerts);
                description = ctx.getString(R.string.notification_channel_quarantine_assistance_dismissed);
                importance = NotificationManager.IMPORTANCE_HIGH;
                break;
        }

        //Call the system API to create a new notification channel
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Function to get the string value of the notification channel
     * */
    private static String getChannelIDByType(NotificationType type) {
        switch (type) {
            default:
            case base:
                return "base";
            case coronavirus:
                return "coronavirus";
            case assistance_chat:
                return "assistance_chat";
            case request_accepted:
                return "request_accepted";
            case request_stop_helping:
                return "request_stop_helping";
        }
    }

    /**
     * Function that create all the notification channels
     * */
    public static void initNotificationChannels(Context ctx) {
        createNotificationChannelIfNotExists(NotificationType.base, ctx);
        createNotificationChannelIfNotExists(NotificationType.coronavirus, ctx);
        createNotificationChannelIfNotExists(NotificationType.assistance_chat, ctx);
        createNotificationChannelIfNotExists(NotificationType.request_accepted, ctx);
        createNotificationChannelIfNotExists(NotificationType.request_stop_helping, ctx);
    }
}
