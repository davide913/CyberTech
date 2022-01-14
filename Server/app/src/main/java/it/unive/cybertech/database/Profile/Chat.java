package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Profile.Exception.NoChatFoundException;

/**
 * Class use to describe a chat instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 * firebase required a get and set to serialize and deserialize the object; for don't mix our "getter" with the firebase deserialization we call the method obtain
 *
 * @author Davide Finesso
 */
public class Chat {

    public interface MessageListener {
        void OnNewMessage(Message m);
    }

    public final static String table = "chat";
    private String id;
    private DocumentReference firstUser;
    private DocumentReference secondUser;

    public static class Message {
        private Timestamp dateTime;
        private String senderId;
        private String message;
        private String id;

        public Message() {

        }

        public Message(String id, Timestamp dateTime, String senderId, String message) {
            this.dateTime = dateTime;
            this.senderId = senderId;
            this.message = message;
            this.id = id;
        }

        public static Message fromMap(HashMap<String, Object> map) {
            try {
                return new Message((String) map.get("id"), (Timestamp) map.get("dateTime"), (String) map.get("sender"), (String) map.get("message"));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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

        public String getMessage() {
            return message;
        }

        private void setMessage(String message) {
            this.message = message;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean amITheSender(User user) {
            return user.getId().equals(this.getSenderId());
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
            Message message = (Message) o;
            return Objects.equals(id, message.id);
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

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public Chat() {
    }

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
    private Chat(String id, DocumentReference firstUser, DocumentReference secondUser) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        try {
            DocumentSnapshot senderT = Tasks.await(firstUser.get());
            DocumentSnapshot receiverT = Tasks.await(secondUser.get());
            this.otherUserMaterialized = senderT.toObject(User.class);
            this.otherUserMaterialized = receiverT.toObject(User.class);
        } catch (ExecutionException | InterruptedException e) {
        }
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public DocumentReference getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(DocumentReference firstUser) {
        this.firstUser = firstUser;
    }

    public DocumentReference getSecondUser() {
        return secondUser;
    }

    private void setSecondUser(DocumentReference secondUser) {
        this.secondUser = secondUser;
    }

    /**
     * Materialize field for increase the performance.
     */
    private User otherUserMaterialized;

    /**
     * The method return the field sender materialize, if is null the method get it from database.
     *
     * @author Davide Finesso
     */
    public User obtainOtherUser(User current) throws ExecutionException, InterruptedException {
        if (otherUserMaterialized == null) {
            otherUserMaterialized = User.obtainUserById(firstUser.getId().equals(current.getId()) ? secondUser.getId() : firstUser.getId());
        }
        return otherUserMaterialized;
    }

    public static Chat createChat(@NonNull User first, @NonNull User second) throws ExecutionException, InterruptedException {
        DocumentReference userRef = getReference(User.table, first.getId());
        DocumentReference receiverRef = getReference(User.table, second.getId());

        Map<String, Object> myChat = new HashMap<>();
        myChat.put("firstUser", userRef);
        myChat.put("secondUser", receiverRef);
        DocumentReference addedDocRef = Database.addToCollection(table, myChat);

        return new Chat(addedDocRef.getId(), userRef, receiverRef);
    }

    /**
     * The method add to the database a new chat and return it.
     *
     * @author Davide Finesso
     */
    public Chat.Message sendMessage(@NonNull User sender, @NonNull String message) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Timestamp t = Timestamp.now();
        String chatDayId = new SimpleDateFormat("ddMMyyyy", Locale.ITALY).format(t.toDate());
        String idMessage = String.valueOf(t.toDate().getTime());
        Map<String, Object> myChat = new HashMap<>();
        myChat.put("sender", sender.getId());
        myChat.put("dateTime", t);
        myChat.put("message", message);
        myChat.put("id", idMessage);

        if (document.exists())
            docRef.update(chatDayId, FieldValue.arrayUnion(myChat));
        else
            throw new NoChatFoundException("Chat not found, id: " + id);

        return new Message(idMessage, t, sender.getId(), message);
    }

    public List<Chat.Message> obtainMessageListByDay(@NonNull Timestamp date) throws ExecutionException, InterruptedException {
        List<Chat.Message> list = new ArrayList<>();

        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);
        if (!document.exists())
            throw new NoChatFoundException("Chat not found, id: " + id);
        String chatDayId = new SimpleDateFormat("ddMMyyyy", Locale.ITALY).format(date.toDate());
        try {
            List<HashMap<String, Object>> tmp = (List<HashMap<String, Object>>) document.get(chatDayId);
            for (HashMap<String, Object> h : tmp)
                list.add(Message.fromMap(h));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void setListener(MessageListener listener) {
        DocumentReference docRef = getReference(table, id);
        docRef.addSnapshotListener((value, error) -> {
            String chatDayId = new SimpleDateFormat("ddMMyyyy", Locale.ITALY).format(Timestamp.now().toDate());
            try {
                List<HashMap<String, Object>> tmp = (List<HashMap<String, Object>>) value.get(chatDayId);
                Message m = Message.fromMap(tmp.get(tmp.size() - 1));
                if (m != null)
                    listener.OnNewMessage(m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * The protected method return the chat with that id. If there isn't a chat with that id it throw an exception.
     *
     * @throws NoChatFoundException if a chat with that id doesn't exist
     * @author Davide Finesso
     */
    public static Chat obtainChatById(@NonNull String id) throws ExecutionException, InterruptedException, NoChatFoundException {
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
    protected boolean deleteChat() {
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
