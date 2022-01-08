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

/**
 * Class use to describe a user's instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set.
 * The class extend Geoquerable to query the users by their position.
 *
 * @author Davide Finesso
 */
public class Group {
    public final static String table = "groups";
    private String id;
    private String name;
    private String description;
    private DocumentReference owner;
    private ArrayList<DocumentReference> members;
    private ArrayList<DocumentReference> messages;
    private ArrayList<DocumentReference> activities;

    /**
     * Materialize field for increase the performance.
     *
     * @author Davide Finesso
     */
    private User materializedOwner;
    private ArrayList<User> membersMaterialized;
    private ArrayList<Chat> messagesMaterialized;
    private ArrayList<Activity> activitiesMaterialized;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public Group() {}

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
    private Group(String id, String name, String description, DocumentReference ownerDoc,
                 ArrayList<DocumentReference> members, ArrayList<DocumentReference> messages,
                  ArrayList<DocumentReference> activities, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = ownerDoc;
        this.members = members;
        this.messages = messages;
        this.activities = activities;
        this.materializedOwner = owner;
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

    public List<DocumentReference> getMembers() {
        return members;
    }

    private void setMembers(ArrayList<DocumentReference> members) {
        this.members = members;
    }

    public List<DocumentReference> getMessages() {
        return messages;
    }

    private void setMessages(ArrayList<DocumentReference> messages) {
        this.messages = messages;
    }

    public List<DocumentReference> getActivities() {
        return activities;
    }

    private void setActivities(ArrayList<DocumentReference> activities) {
        this.activities = activities;
    }

    /**
     * The method return the field owner materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public User obtainMaterializedOwner() throws ExecutionException, InterruptedException {
        if(materializedOwner == null)
            materializedOwner = User.obtainUserById(owner.getId());
        return materializedOwner;
    }

    /**
     * The method return the field members materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<User> obtainMaterializedMembers() throws ExecutionException, InterruptedException {
        if(membersMaterialized == null) {
            membersMaterialized = new ArrayList<>();

            for (DocumentReference doc : members) {
                membersMaterialized.add(User.obtainUserById(doc.getId()));
            }
        }

        return membersMaterialized;
    }

    /**
     * The method return the field messages materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<Chat> obtainMaterializedMessages() throws ExecutionException, InterruptedException {
        if(messagesMaterialized == null) {
            messagesMaterialized = new ArrayList<>();

            for (DocumentReference doc : messages) {
                messagesMaterialized.add(Chat.getChatById(doc.getId()));
            }
        }

        return messagesMaterialized;
    }

    /**
     * The method return the field activities materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<Activity> obtainMaterializedActivities() throws ExecutionException, InterruptedException {
        if(activitiesMaterialized == null) {
            activitiesMaterialized = new ArrayList<>();

            for (DocumentReference doc : activities) {
                activitiesMaterialized.add(Activity.getActivityById(doc.getId()));
            }
        }

        return activitiesMaterialized;
    }

    /**
     * The method add to the database a new group and return it.
     *
     * @author Davide Finesso
     */
    public static Group createGroup(String name, String description, User creator) throws ExecutionException, InterruptedException {
        DocumentReference userRef = getReference(User.table, creator.getId());

        Map<String, Object> myGroup = new HashMap<>();
        myGroup.put("name", name);
        myGroup.put("description", description);
        myGroup.put("owner", userRef);

        DocumentReference addedDocRef = Database.addToCollection(table, myGroup);

        Group group = new Group(addedDocRef.getId(), name, description, userRef, new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), creator);

        group.addMember(creator);

        return group;
    }

    /**
     * The method return the group with that id. If there isn't a user with that id it throw an exception.
     *
     * @author Davide Finesso
     */
    public static Group obtainGroupById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Group group = null;

        if (document.exists()) {
            group = document.toObject(Group.class);
            group.setId(document.getId());

            if(group.members == null)
                group.members = new ArrayList<>();

            if(group.activities == null)
                group.activities = new ArrayList<>();

            if(group.messages == null)
                group.messages = new ArrayList<>();

            return group;
        } else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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
            for (Activity activity: obtainMaterializedActivities())
                activity.deleteActivity();
            Task<Void> t = deleteGroupAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateOwnerAsync(DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("owner", user);
        } else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    private boolean updateOwner(@NonNull DocumentReference user) {
        try {
            Task<Void> t = updateOwnerAsync(user);
            Tasks.await(t);
            this.owner = user;
            this.materializedOwner = User.obtainUserById(user.getId());
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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
            DocumentReference messDoc = getReference(Chat.table, message.getId());
            Task<Void> t = addMessageAsync(messDoc);
            Tasks.await(t);
            this.messages.add(messDoc);
            if(this.messagesMaterialized != null)
                this.obtainMaterializedMessages().add(message);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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
            DocumentReference messDoc = getReference(Chat.table, message.getId());
            Task<Void> t = removeMessageAsync(messDoc);
            Tasks.await(t);
            this.messages.remove(message);
            if(this.messagesMaterialized != null)
                this.obtainMaterializedMessages().remove(message);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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
            DocumentReference userDoc = getReference(User.table, user.getId());
            Task<Void> t = addMemberAsync(userDoc);
            Tasks.await(t);
            this.members.add(userDoc);
            if(this.membersMaterialized != null)
                this.obtainMaterializedMembers().add(user);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> removeMemberAsync(@NonNull DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("members", FieldValue.arrayRemove(user));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    public boolean removeMember(@NonNull User user){
        try {
            if(user.getId().equals(owner.getId())){
                Tasks.await(removeMemberAsync(owner));
                this.members.remove(owner);

                if(this.members.isEmpty()) {
                    this.deleteGroup();
                    return true;
                }
                else
                    this.updateOwner(this.members.get(0));

            }
            else{
                DocumentReference userDoc = getReference(User.table, user.getId());

                Tasks.await(removeActivityAsync(userDoc));
                this.members.remove(userDoc);
            }

            if(this.membersMaterialized != null)
                this.obtainMaterializedMembers().remove(user);

            for (Activity activity : obtainMaterializedActivities() ) {
                if(activity.getOwner().getId().equals(user.getId())) {

                    if(activity.getMaterializedParticipants().isEmpty())
                        activity.deleteActivity();

                    else {
                        User substitute = User.obtainUserById(activity.getParticipants().get(0).getId());
                        activity.removeParticipant(substitute);
                        activity.updateOwner(substitute);
                    }
                }
                else
                    activity.removeParticipant(user);
            }

            return true;

        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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
            DocumentReference actDoc = getReference(Activity.table, activity.getId());
            Task<Void> t = addActivityAsync(actDoc);
            Tasks.await(t);
            this.activities.add(actDoc);
            if(this.activitiesMaterialized != null)
                this.obtainMaterializedActivities().add(activity);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> removeActivityAsync(@NonNull DocumentReference activity) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("members", FieldValue.arrayRemove(activity));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean removeActivity(@NonNull Activity activity){
        try {
            DocumentReference actDoc = getReference(Activity.table, activity.getId());
            Task<Void> t = removeActivityAsync(actDoc);
            Tasks.await(t);
            this.activities.remove(actDoc);
            if(this.activitiesMaterialized != null)
                this.obtainMaterializedActivities().remove(activity);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*@Deprecated
    public static List<Group> obtainPositiveGroups(User user) throws ExecutionException, InterruptedException {
        ArrayList<Group> arr = new ArrayList<>();
        FirebaseFirestore db = getInstance();
        DocumentReference userDoc = getReference(User.table, user.getId());

        Task<QuerySnapshot> future = db.collection(table).whereArrayContains("members", userDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents) {
            Group g = obtainGroupById(doc.getId());
            List<User> members = g.obtainMaterializedMembers();

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
    @Deprecated
    public List<Activity> obtainPositiveActivities() throws ExecutionException, InterruptedException {
        ArrayList<Activity> result = new ArrayList<>();
        List<Activity> activities = obtainMaterializedActivities();

        for (Activity activity: activities) {
            List<User> participants = activity.getMaterializedParticipants();

            for (User user : participants) {
                if(user.getPositiveSince() != null){
                    result.add(activity);
                    break;
                }
            }
        }

        return result;
    }*/

    /**
     * the method is use to get all groups from database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    public static List<Group> obtainAllGroups() throws ExecutionException, InterruptedException {
        ArrayList<Group> result = new ArrayList<>();

        Task<QuerySnapshot> future = getInstance().collection(table).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc: documents )
            result.add(obtainGroupById(doc.getId()));

        return result;
    }

}
