package it.unive.cybertech.assistenza;

import static android.view.View.GONE;
import static it.unive.cybertech.utils.CachedUser.user;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.ChatAdapter;
import it.unive.cybertech.database.Profile.Chat;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.messages.MessageService;
import it.unive.cybertech.utils.Showables;
import it.unive.cybertech.utils.Utils;

/**
 * This class represent a chat between the positive user that have made the assistance request and the user in charge
 * The chat is in real-time
 * Note: at the moment the chat is visualized day by day
 *
 * @author Mattia Musone
 * */
public class ChatActivity extends AppCompatActivity implements Utils.ItemClickListener {

    private RecyclerView list;
    private ChatAdapter adapter;
    private List<Chat.Message> items;
    private String id;
    private Chat chat;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        id = getIntent().getStringExtra("ID");
        //if the chat id is not provided we cannot proceed, so close this activity (rude way)
        if (id == null)
            finish();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.assistance_chat);
        items = new ArrayList<>();
        list = findViewById(R.id.assistance_chat_list);
        loader = findViewById(R.id.assistance_chat_loader);
        FloatingActionButton send = findViewById(R.id.assistance_chat_send_message);
        EditText message = findViewById(R.id.assistance_chat_message);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(items);
        adapter.setClickListener(this);
        list.setAdapter(adapter);
        //Bind the send message button click
        send.setOnClickListener(v -> {
            //Send the message only if the user has written something
            if (message.length() > 0) {
                send.setEnabled(false);
                String messageStr = message.getText().toString();
                Utils.executeAsync(() -> chat.sendMessage(user, messageStr), new Utils.TaskResult<Chat.Message>() {
                    @Override
                    public void onComplete(Chat.Message result) {
                        send.setEnabled(true);
                        message.setText("");
                        adapter.add(result);
                        list.scrollToPosition(items.size() - 1);
                        //When a message is sent, send a notification to the other user
                        Utils.executeAsync(() -> chat.obtainOtherUser(user), new Utils.TaskResult<User>() {
                            @Override
                            public void onComplete(User result) {
                                MessageService.sendMessageToUserDevices(result, MessageService.NotificationType.assistance_chat, user.getName() + " " + getString(R.string.user_asked_for_assistance), messageStr, getApplication());
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        send.setEnabled(true);
                        Showables.showShortToast(getString(R.string.error_sending_chat_message), getApplicationContext());
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get the chat for this day
        Utils.executeAsync(() -> Chat.obtainChatById(id), new Utils.TaskResult<Chat>() {
            @Override
            public void onComplete(Chat result) {
                chat = result;
                //Bind a listener to the database changes in order to add new messages to the real-time chat
                chat.setListener(message -> {
                    if (!items.contains(message)) {
                        adapter.add(message);
                        list.scrollToPosition(items.size() - 1);
                    }
                });
                Utils.executeAsync(() -> chat.obtainMessageListByDay(Timestamp.now()), new Utils.TaskResult<List<Chat.Message>>() {
                    @Override
                    public void onComplete(List<Chat.Message> result) {
                        items = result;
                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();
                        list.scrollToPosition(items.size() - 1);
                        loader.setVisibility(GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        loader.setVisibility(GONE);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}