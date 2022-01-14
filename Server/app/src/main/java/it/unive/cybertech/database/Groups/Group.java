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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Groups.Exception.NoGroupFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;
import it.unive.cybertech.database.Profile.User;

/**
 * Class use to describe a group instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 * firebase required a get and set to serialize and deserialize the object; for don't mix our "getter" with the firebase deserialization we call the method obtain
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
     */
    private User ownerMaterialized;
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
        this.ownerMaterialized = owner;
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
        if(ownerMaterialized == null)
            ownerMaterialized = User.obtainUserById(owner.getId());
        return ownerMaterialized;
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
     * The method return the field messages materialize order by date time. If the field is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<Chat> obtainMaterializedMessages() throws ExecutionException, InterruptedException {
        if(messagesMaterialized == null) {
            messagesMaterialized = new ArrayList<>();

            for (DocumentReference doc : messages) {
                messagesMaterialized.add(Chat.obtainChatById(doc.getId()));
            }

            messagesMaterialized.sort(new Comparator<Chat>() {
                @Override
                public int compare(Chat o1, Chat o2) {
                    return o1.getDateTime().compareTo(o2.getDateTime());
                }
            });
        }

        return messagesMaterialized;
    }

    /**
     * The method return the field activities materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<Activity> obtainMaterializedActivities(boolean caching) throws ExecutionException, InterruptedException {
        if(activitiesMaterialized == null || !caching) {
            activitiesMaterialized = new ArrayList<>();

            for (DocumentReference doc : activities) {
                activitiesMaterialized.add(Activity.obtainActivityById(doc.getId()));
            }
        }

        return activitiesMaterialized;
    }

    /**
     * The method return the field activities materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<Activity> obtainMaterializedActivities() throws ExecutionException, InterruptedException {
        return obtainMaterializedActivities(true);
    }

    /**
     * The method add to the database a new group and return it.
     *
     * @author Davide Finesso
     */
    public static Group createGroup(@NonNull String name,@NonNull String description,@NonNull User creator) throws ExecutionException, InterruptedException {
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
     * The method return the group with that id. If there isn't a group with that id it throw an exception.
     *
     * @author Davide Finesso
     * @throws NoGroupFoundException if a group with that id doesn't exist
     */
    public static Group obtainGroupById(@NonNull String id) throws ExecutionException, InterruptedException, NoGroupFoundException {
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

    /**
     * The method is use to delete a group, all his activities and message as well. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean deleteGroup() {
        try {
            for (Activity activity : obtainMaterializedActivities())
                activity.deleteActivity();
            for(Chat chat : obtainMaterializedMessages())
                chat.deleteChat();

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

    /**
     * The method is use to update a group field description to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateDescription(@NonNull String description) {
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

    /**
     * The private method is use to update a group field owner to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    private boolean updateOwner(@NonNull DocumentReference user) {
        try {
            Task<Void> t = updateOwnerAsync(user);
            Tasks.await(t);
            this.owner = user;
            this.ownerMaterialized = User.obtainUserById(user.getId());
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

    /**
     * The method is use to update a group field name to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateName(@NonNull String name) {
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

    /**
     * The method is use to add a group message to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * The method is use to remove a group message to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean removeMessage(@NonNull Chat message) throws Exception {
        try {
            DocumentReference messDoc = getReference(Chat.table, message.getId());
            Task<Void> t = removeMessageAsync(messDoc);
            Tasks.await(t);
            this.messages.remove(message);
            if(this.messagesMaterialized != null)
                this.obtainMaterializedMessages().remove(message);

            message.deleteChat();
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

    /**
     * The method is use to add a group member to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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
     * The method is use to remove a group member to the database. It check if the remove user is the owner of the group and in case it change the owner, if there isn't any more member in the group the method delete it.
     * Later the method check if the deleted user was the owner of some activities and it change or delete the activity as described before.
     * It return a boolean value that describe if the operation was done.
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

                    if(activity.obtainMaterializedParticipants().isEmpty())
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

    /**
     * The method is use to add a group activity to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * The method is use to remove a group activity to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean removeActivity(@NonNull Activity activity){
        try {
            DocumentReference actDoc = getReference(Activity.table, activity.getId());
            Task<Void> t = removeActivityAsync(actDoc);
            Tasks.await(t);
            this.activities.remove(actDoc);
            if(this.activitiesMaterialized != null)
                this.obtainMaterializedActivities().remove(activity);

            activity.deleteActivity();
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the method is use to get all groups from database.
     *
     * @author Davide Finesso
     */
    public static List<Group> obtainAllGroups() throws ExecutionException, InterruptedException {
        ArrayList<Group> result = new ArrayList<>();

        Task<QuerySnapshot> future = getInstance().collection(table).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents )
            result.add(obtainGroupById(doc.getId()));

        return result;
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
        Group group = (Group) o;
        return Objects.equals(id, group.id);
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
