package it.unive.cybertech.messages;

import static it.unive.cybertech.utils.CachedUser.user;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import it.unive.cybertech.database.Profile.Device;
import it.unive.cybertech.utils.Utils;

public class MessageService extends FirebaseMessagingService {

    private final static String TAG = "FirebaseMessage";

    public static void getCurrentToken(OnCompleteListener<String> listener) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(listener);
    }

    public static void sendMessage(@NonNull String token, String title, String message, Context ctx) {//, Map<String, String> datas
        /*RemoteMessage.Builder msg = new RemoteMessage.Builder("5807486351@fcm.googleapis.com")
                .setMessageId("jghkgj")
                .addData("title", title)
                .addData("message", message);
        //if (datas != null)
        //    datas.forEach(msg::addData);
        //TODO update last used token
        FirebaseMessaging.getInstance().send(msg.build());*/
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="https://fcm.googleapis.com/fcm/send";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,response);
                        // Display the first 500 characters of the response string.
                        //textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.getMessage());
                //textView.setText("That didn't work!");
            }
        }){

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                Map<String,String> rawParameters = new Hashtable<String, String>();
                JSONObject msg = new JSONObject();
                try {
                    JSONObject ob = new JSONObject();
                    ob.put("title", title);
                    ob.put("body", message);
                    ob.put("channel_id", "coronavirus");
                    ob.put("image", "coronavirus");
                    Log.d(TAG, ob.toString());
                    msg.put("notification",ob);
                    msg.put("to", token);
                    rawParameters.put("notification", ob.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                rawParameters.put("to", token);
                Log.d(TAG, msg.toString());
                return msg.toString().getBytes();
            };

            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AAAAAVonNY8:APA91bGJJ6W629ZfdorEtiTDssz8C39mc9a4LFUxbDtourqN-OotX9cxkkSSpJ_bemZuyX7KBvC-1pMVa6UpPTiKOFrTwfl8fBVfnreDWwhJibp8Lp_HdpioEQgO4haWl0sqWydbQ8n2");
                return headers;
            }
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("registration_ids", "num1");
                params.put("data", "num2");
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        Log.d(TAG, "SENDING MESSAGE");
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
        Log.d(TAG, s);
    }

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        super.onSendError(s, e);
        Log.e(TAG, s);
        Log.e(TAG, e.getMessage());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //todo update token to server
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification not = remoteMessage.getNotification();
            Log.d(TAG, "Message Notification Body: " + not.getBody());
            Log.d(TAG, "Message Notification channel: " + not.getChannelId());
            Log.d(TAG, "Message Notification icon: " + not.getIcon());
            Utils.createNotification(getApplicationContext(), not.getChannelId(), not.getTitle(), not.getBody(), not.getIcon());
        }
    }
}
