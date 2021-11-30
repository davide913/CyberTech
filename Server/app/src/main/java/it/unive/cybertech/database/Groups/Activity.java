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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Groups.Exception.NoActivityFoundException;
import it.unive.cybertech.database.Profile.User;

//TESTATED
//TODO modificare tutti gli array con documentreference e fare un metodo che "materializza" gli oggetti
public class Activity {
    private String id;
    private DocumentReference owner;
    private String name;
    private String description;
    private String place;
    private Timestamp date;
    private ArrayList<DocumentReference> participants;

    public Activity(){}

    private Activity(String id, DocumentReference owner, String name, String description, String place, Timestamp date, ArrayList<DocumentReference> participants) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.place = place;
        this.date = date;
        this.participants = participants;
    }

    public DocumentReference getOwner() {
        return owner;
    }

    private void setOwner(DocumentReference owner) {
        this.owner = owner;
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

    public String getPlace() {
        return place;
    }

    private void setPlace(String place) {
        this.place = place;
    }

    public Timestamp getDate() {
        return date;
    }

    public Date getDateTimeD(){
        return date.toDate();
    }

    private void setDate(Timestamp date) {
        this.date = date;
    }

    public ArrayList<DocumentReference> getParticipants() {
        return participants;
    }

    public ArrayList<User> getMaterializedParticipants() throws ExecutionException, InterruptedException {
        ArrayList<User> arr = new ArrayList<>();

        for (DocumentReference doc : participants) {
            arr.add(User.getUserById(doc.getId()));
        }

        return arr;
    }

    private void setParticipants(ArrayList<DocumentReference> participants) {
        this.participants = participants;
    }

    public static Activity createActivity(String name, String description, String place, Date date, User owner) throws ExecutionException, InterruptedException {
        Timestamp t = new Timestamp(date);
        DocumentReference userDoc = Database.getReference("users", owner.getId());

        Map<String, Object> activity = new HashMap<>();
        activity.put("name", name);
        activity.put("description", description);
        activity.put("place", place);
        activity.put("date", t);
        activity.put("owner", userDoc);

        DocumentReference addedDocRef = Database.addToCollection("activity", activity);

        return new Activity(addedDocRef.getId(), userDoc, name, description, place, t, new ArrayList<DocumentReference>());
    }

    public static Activity getActivityById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("activity", id);
        DocumentSnapshot document = getDocument(docRef);

        Activity activity = null;

        if (document.exists()) {
            activity = document.toObject(Activity.class);
            activity.setId(document.getId());

            if(activity.getParticipants()==null)
                activity.participants = new ArrayList<>();
            
            return activity;
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    private Task<Void> deleteActivityAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("activity", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync("activity", id);
        else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    public boolean deleteActivity() {
        try {
            Task<Void> t = deleteActivityAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateDescriptionAsync(String description) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("activity", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("description", description);
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    public boolean updateDescription(String description) {
        try {
            Task<Void> t = updateDescriptionAsync(description);
            Tasks.await(t);
            this.description = description;
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updatePlaceAsync(String place) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("activity", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("place", place);
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    public boolean updatePlace(String place) {
        try {
            Task<Void> t = updatePlaceAsync(place);
            Tasks.await(t);
            this.place = place;
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateDateAsync(Timestamp timestamp) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("activity", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("date", timestamp);
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    public boolean updateDate(Date date) {
        try {
            Timestamp timestamp = new Timestamp(date);
            Task<Void> t = updateDateAsync(timestamp);
            Tasks.await(t);
            this.date = timestamp;
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> addPartecipantAsync(@NonNull DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("activity", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("participants", FieldValue.arrayUnion(user));
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    public boolean addPartecipant(@NonNull User user) {
        try {
            DocumentReference userDoc = getReference("users", user.getId());
            Task<Void> t = addPartecipantAsync(userDoc);
            Tasks.await(t);
            this.participants.add(userDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> removePartecipantAsync(@NonNull DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("activity", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("participants", FieldValue.arrayRemove(user));
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    public boolean removePartecipant(@NonNull User user) {
        try {
            DocumentReference userDoc = getReference("users", user.getId());
            Task<Void> t = removePartecipantAsync(userDoc);
            Tasks.await(t);
            this.participants.remove(userDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
