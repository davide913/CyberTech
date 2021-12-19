package it.unive.cybertech.database.Groups;

import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getReference;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Groups.Exception.NoChatFoundException;
import it.unive.cybertech.database.Profile.User;

public class Chat {
    public final static String table = "chat";
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

    public static Chat createChat(Date date, User sender, String message) throws ExecutionException, InterruptedException {
        Timestamp t = new Timestamp(date);
        DocumentReference userRef = getReference(User.table, sender.getId());

        Map<String, Object> myChat = new HashMap<>();
        myChat.put("sender", userRef);
        myChat.put("dateTime", t);
        myChat.put("message", message);

        DocumentReference addedDocRef = Database.addToCollection(table, myChat);

        return new Chat(addedDocRef.getId(), t, userRef, message);
    }

    protected static Chat getChatById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Chat chat = null;

        if (document.exists()) {
            chat = document.toObject(Chat.class);
            chat.setId(document.getId());

            return chat;
        } else
            throw new NoChatFoundException("No chat found with this id: " + id);
    }

    private Task<Void> deleteChatAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
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
