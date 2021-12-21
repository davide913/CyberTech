package it.unive.cybertech.database.Groups;

import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Groups.Exception.NoGroupFoundException;
import it.unive.cybertech.database.Profile.User;

public class Group {
    private final static String table = "groups";
    private String id;
    private String name;
    private String description;
    private DocumentReference owner;
    private ArrayList<DocumentReference> members;
    private ArrayList<DocumentReference> messages;
    private ArrayList<DocumentReference> activities;

    public Group() {}

    private Group(String id, String name, String description, DocumentReference owner,
                 ArrayList<DocumentReference> members, ArrayList<DocumentReference> messages,
                  ArrayList<DocumentReference> activities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members = members;
        this.messages = messages;
        this.activities = activities;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public DocumentReference getOwner() {
        return owner;
    }

    private void setOwner(DocumentReference owner) {
        this.owner = owner;
    }

    public ArrayList<DocumentReference> getMembers() {
        return members;
    }

    private void setMembers(ArrayList<DocumentReference> members) {
        this.members = members;
    }

    public ArrayList<DocumentReference> getMessages() {
        return messages;
    }

    private void setMessages(ArrayList<DocumentReference> messages) {
        this.messages = messages;
    }

    public ArrayList<DocumentReference> getActivities() {
        return activities;
    }

    private void setActivities(ArrayList<DocumentReference> activities) {
        this.activities = activities;
    }

    public ArrayList<User> getMaterializedMembers() throws ExecutionException, InterruptedException {
        ArrayList<User> arr = new ArrayList<>();

        for (DocumentReference doc : members) {
            arr.add(User.getUserById(doc.getId()));
        }

        return arr;
    }

    public ArrayList<Chat> getMaterializedMessages() throws ExecutionException, InterruptedException {
        ArrayList<Chat> arr = new ArrayList<>();

        for (DocumentReference doc : messages) {
            arr.add(Chat.getChatById(doc.getId()));
        }

        return arr;
    }

    public ArrayList<Activity> getMaterializedActivities() throws ExecutionException, InterruptedException {
        ArrayList<Activity> arr = new ArrayList<>();

        for (DocumentReference doc : activities) {
            arr.add(Activity.getActivityById(doc.getId()));
        }

        return arr;
    }

    public static Group CreateGroup(String name, String description, User creator) throws ExecutionException, InterruptedException {
        DocumentReference userRef = getReference("users", creator.getId());

        Map<String, Object> myGroup = new HashMap<>();
        myGroup.put("name", name);
        myGroup.put("description", description);
        myGroup.put("owner", userRef);

        DocumentReference addedDocRef = Database.addToCollection(table, myGroup);

        Group group = new Group(addedDocRef.getId(), name, description, userRef, new ArrayList<DocumentReference>(),
                    new ArrayList<DocumentReference>(), new ArrayList<DocumentReference>());

        group.addMember(creator);

        return group;
    }

    public static Group getGroupById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Group group = null;

        if (document.exists()) {
            group = document.toObject(Group.class);
            group.setId(document.getId());

            if(group.members == null)
                group.members = new ArrayList<DocumentReference>();

            if(group.activities == null)
                group.activities = new ArrayList<DocumentReference>();

            if(group.messages == null)
                group.messages = new ArrayList<DocumentReference>();

            return group;
        } else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    private Task<Void> deleteGroupAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean deleteGroup() {
        try {
            Task<Void> t = deleteGroupAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateDescriptionAsync(String description) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("description", description);
        } else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean updateDescription(String description) {
        try {
            Task<Void> t = updateDescriptionAsync(description);
            Tasks.await(t);
            this.description = description;
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateNameAsync(String name) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("name", name);
        } else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean updateName(String name) {
        try {
            Task<Void> t = updateNameAsync(name);
            Tasks.await(t);
            this.name = name;
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> addMessageAsync(@NonNull DocumentReference message) throws Exception {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("messages", FieldValue.arrayUnion(message));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean addMessage(@NonNull Chat message) throws Exception {
        try {
            DocumentReference messDoc = getReference("chat", message.getId());
            Task<Void> t = addMessageAsync(messDoc);
            Tasks.await(t);
            this.messages.add(messDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> removeMessageAsync(@NonNull DocumentReference message) throws Exception {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("members", FieldValue.arrayRemove(message));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean removeMessage(@NonNull Chat message) throws Exception {
        try {
            DocumentReference messDoc = getReference("chat", message.getId());
            Task<Void> t = removeMessageAsync(messDoc);
            Tasks.await(t);
            this.messages.remove(message);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> addMemberAsync(@NonNull DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("members", FieldValue.arrayUnion(user));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean addMember(@NonNull User user) {
        try {
            DocumentReference userDoc = getReference("users", user.getId());
            Task<Void> t = addMemberAsync(userDoc);
            Tasks.await(t);
            this.members.add(userDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> removeMemberAsync(@NonNull DocumentReference user) throws Exception {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("members", FieldValue.arrayRemove(user));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean removeMember(@NonNull User user) throws Exception {
        try {
            if(!user.getId().equals(owner.getId())) {
                DocumentReference userDoc = getReference("users", user.getId());
                Task<Void> t = removeMemberAsync(userDoc);
                Tasks.await(t);
                this.members.remove(userDoc);
                return true;
            }
            return false;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> addActivityAsync(@NonNull DocumentReference user) throws Exception {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("activities", FieldValue.arrayUnion(user));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean addActivity(@NonNull Activity activity) throws Exception {
        try {
            DocumentReference actDoc = getReference("activity", activity.getId());
            Task<Void> t = addActivityAsync(actDoc);
            Tasks.await(t);
            this.activities.add(actDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> removeActivityAsync(@NonNull DocumentReference activity) throws Exception {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("activities", FieldValue.arrayRemove(activity));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean removeActivity(@NonNull Activity activity) throws Exception {
        try {
            DocumentReference actDoc = getReference("activity", activity.getId());
            Task<Void> t = removeActivityAsync(actDoc);
            Tasks.await(t);
            this.activities.remove(actDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Group> getPositiveGroups(User user) throws ExecutionException, InterruptedException {
        ArrayList<Group> arr = new ArrayList<>();
        FirebaseFirestore db = getInstance();
        DocumentReference userDoc = getReference("users", user.getId());

        Task<QuerySnapshot> future = db.collection(table).whereArrayContains("members", userDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents) {
            Group g = getGroupById(doc.getId());
            ArrayList<User> members = g.getMaterializedMembers();

            for (User u : members) {
                if (u.getPositiveSince() != null) {
                    arr.add(g);
                    break;
                }
            }
        }

        return arr;
    }

    //TODO vedere se si puo fare con una query
    public ArrayList<Activity> getPositiveActivities() throws ExecutionException, InterruptedException {
        ArrayList<Activity> result = new ArrayList<>();
        ArrayList<Activity> activities = getMaterializedActivities();

        for (Activity activity: activities) {
            ArrayList<User> participants = activity.getMaterializedParticipants();

            for (User user : participants) {
                if(user.getPositiveSince() !=null){
                    result.add(activity);
                    break;
                }
            }
        }

        return result;
    }

}
