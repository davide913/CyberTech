package it.unive.cybertech.database.Groups;

import static it.unive.cybertech.database.Connection.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Groups.Exception.NoActivityFoundException;
import it.unive.cybertech.database.Groups.Exception.NoChatFoundException;
import it.unive.cybertech.database.Profile.User;

public class Chat {
    private String id;
    private Timestamp dateTime;
    private DocumentReference sender;
    private String message;

    public Chat(){}

    private Chat(String id, Timestamp dateTime, DocumentReference sender, String message) {
        this.id = id;
        this.dateTime = dateTime;
        this.sender = sender;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public Date getDateTimeD() {
        return dateTime.toDate();
    }

    private void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public DocumentReference getSender() {
        return sender;
    }

    public User getSenderUser() throws ExecutionException, InterruptedException {
        return User.getUserById(sender.getId());
    }

    private void setSender(DocumentReference sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public static Chat createChat(Date date, User user, String message) throws ExecutionException, InterruptedException {
        Timestamp t = new Timestamp(date);
        DocumentReference userRef = getReference("users", user.getId());

        Map<String, Object> myChat = new HashMap<>();
        myChat.put("sender", userRef);
        myChat.put("dateTime", t);
        myChat.put("message", message);

        DocumentReference addedDocRef = Database.addToCollection("chat", myChat);

        return new Chat(addedDocRef.getId(), t, userRef, message);
    }

    private Task<Void> deleteChatAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("chat", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync("chat", id);
        else
            throw new NoChatFoundException("No chat found with this id: " + id);
    }

    public boolean deleteChat() {
        try {
            Task<Void> t = deleteChatAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoChatFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
