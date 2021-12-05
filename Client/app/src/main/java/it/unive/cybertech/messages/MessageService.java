package it.unive.cybertech.messages;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessageService extends FirebaseMessagingService {

    private final String TAG = "FirebaseMessage";

    public static void getCurrentToken(OnCompleteListener<String> listener) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(listener);
    }

    public static void sendMessage(String token, String title, String message, Map<String, String> datas) {
        RemoteMessage.Builder msg = new RemoteMessage.Builder(token + "@fcm.googleapis.com")
                //.setMessageId(Integer.toString(messageId))
                .addData("title", title)
                .addData("message", message);
        if (datas != null)
            datas.forEach(msg::addData);
        FirebaseMessaging.getInstance().send(msg.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //todo update token to server
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
