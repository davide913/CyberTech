package it.unive.cybertech.database.Groups;

import static it.unive.cybertech.database.Connection.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Groups.Exception.NoActivityFoundException;
import it.unive.cybertech.database.Groups.Exception.NoGroupFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.database.Profile.User;

public class Group {
    private String id;
    private String name;
    private String description;
    private DocumentReference owner;
    private ArrayList<User> members;
    private ArrayList<Chat> messages;
    private ArrayList<Activity> activities;

    public Group() {}

    private Group(String id, String name, String description, DocumentReference owner,
                 ArrayList<User> members, ArrayList<Chat> messages, ArrayList<Activity> activities) {
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

    public ArrayList<User> getMembers() {
        return members;
    }

    private void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public ArrayList<Chat> getMessages() {
        return messages;
    }

    private void setMessages(ArrayList<Chat> messages) {
        this.messages = messages;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    private void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }

    public static Group CreateGroup(String name, String description, User creator) throws ExecutionException, InterruptedException {
        DocumentReference userRef = getReference("users", creator.getId());

        Map<String, Object> myGroup = new HashMap<>();
        myGroup.put("name", name);
        myGroup.put("description", description);
        myGroup.put("owner", userRef);

        DocumentReference addedDocRef = Database.addToCollection("groups", myGroup);

        return new Group(addedDocRef.getId(), name, description, userRef, new ArrayList<User>(), new ArrayList<Chat>(), new ArrayList<Activity>());
    }

    public static Group getGroupById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("groups", id);
        DocumentSnapshot document = getDocument(docRef);

        Group group = null;

        if (document.exists()) {
            group = document.toObject(Group.class);
            group.setId(document.getId());

            if(group.members == null)
                group.members = new ArrayList<User>();

            if(group.activities == null)
                group.activities = new ArrayList<Activity>();

            if(group.messages == null)
                group.messages = new ArrayList<Chat>();

            return group;
        } else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    private Task<Void> deleteGroupAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("groups", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync("groups", id);
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
        DocumentReference docRef = getReference("groups", this.id);
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
        DocumentReference docRef = getReference("groups", this.id);
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

    private Task<Void> addMessageAsync(@NonNull Chat message) throws Exception {
        DocumentReference docRef = getReference("groups", id);
        DocumentSnapshot document = getDocument(docRef);
        DocumentReference messDoc = getReference("chat", message.getId());

        if (document.exists())
            return docRef.update("messages", FieldValue.arrayUnion(messDoc));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean addMessage(@NonNull Chat message) throws Exception {
        try {
            Task<Void> t = addMessageAsync(message);
            Tasks.await(t);
            this.messages.add(message);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> removeMessageAsync(@NonNull Chat message) throws Exception {
        DocumentReference docRef = getReference("groups", id);
        DocumentSnapshot document = getDocument(docRef);
        DocumentReference userDoc = getReference("chat", message.getId());

        if (document.exists())
            return docRef.update("members", FieldValue.arrayRemove(userDoc));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean removeMessage(@NonNull Chat message) throws Exception {
        try {
            Task<Void> t = removeMessageAsync(message);
            Tasks.await(t);
            this.messages.remove(message);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> addMemberAsync(@NonNull User user) throws Exception {
        DocumentReference docRef = getReference("groups", id);
        DocumentSnapshot document = getDocument(docRef);
        DocumentReference userDoc = getReference("users", user.getId());

        if (document.exists())
            return docRef.update("members", FieldValue.arrayUnion(userDoc));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean addMember(@NonNull User user) throws Exception {
        try {
            Task<Void> t = addMemberAsync(user);
            Tasks.await(t);
            this.members.add(user);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> removeMemberAsync(@NonNull User user) throws Exception {
        DocumentReference docRef = getReference("groups", id);
        DocumentSnapshot document = getDocument(docRef);
        DocumentReference userDoc = getReference("users", user.getId());

        if (document.exists())
            return docRef.update("members", FieldValue.arrayRemove(userDoc));
        else
            throw new NoGroupFoundException("No group found with this id: " + id);
    }

    public boolean removeMember(@NonNull User user) throws Exception {
        try {
            Task<Void> t = removeMemberAsync(user);
            Tasks.await(t);
            this.members.remove(user);
            return true;
        } catch (ExecutionException | InterruptedException | NoGroupFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //TODO finire add e remove di activity

}
