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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Groups.Exception.NoChatFoundException;
import it.unive.cybertech.database.Profile.User;

/**
 * Class use to describe a chat instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 *
 * @author Davide Finesso
 */
public class Chat {
    public final static String table = "chat";
    private String id;
    private Timestamp dateTime;
    private DocumentReference sender;
    private String message;

    /**
     * Materialize field for increase the performance.
     *
     * @author Davide Finesso
     */
    private User senderMaterialized;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public Chat(){}

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
    private Chat(String id, Timestamp dateTime, DocumentReference senderDoc, String message, User sender) {
        this.id = id;
        this.dateTime = dateTime;
        this.sender = senderDoc;
        this.message = message;
        this.senderMaterialized = sender;
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

    public Date getDateTimeToDate() {
        return dateTime.toDate();
    }

    private void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public DocumentReference getSender() {
        return sender;
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

    /**
     * The method return the field sender materialize, if is null the method get it from database.
     *
     * @author Davide Finesso
     */
    public User obtainSenderUser() throws ExecutionException, InterruptedException {
        if(senderMaterialized == null)
            senderMaterialized = User.obtainUserById(sender.getId());

        return senderMaterialized;
    }

    /**
     * The method add to the database a new chat and return it.
     *
     * @author Davide Finesso
     */
    public static Chat createChat(Date date, User sender, String message) throws ExecutionException, InterruptedException {
        Timestamp t = new Timestamp(date);
        DocumentReference userRef = getReference(User.table, sender.getId());

        Map<String, Object> myChat = new HashMap<>();
        myChat.put("sender", userRef);
        myChat.put("dateTime", t);
        myChat.put("message", message);

        DocumentReference addedDocRef = Database.addToCollection(table, myChat);

        return new Chat(addedDocRef.getId(), t, userRef, message, sender);
    }

    /**
     * The protected method return the chat with that id. If there isn't a chat with that id it throw an exception.
     *
     * @author Davide Finesso
     */
    protected static Chat obtainChatById(String id) throws ExecutionException, InterruptedException {
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> deleteChatAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
        else
            throw new NoChatFoundException("No chat found with this id: " + id);
    }

    /**
     * The method is use to delete a chat from the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean deleteChat() {
        try {
            Task<Void> t = deleteChatAsync();
            Tasks.await(t);
            this.setId(null);
            return true;
        } catch (ExecutionException | InterruptedException | NoChatFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Compare their id because are unique.
     *
     * @author Davide Finesso
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id);
    }

    /**
     * Return the hash by the unique field id.
     *
     * @author Davide Finesso
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
