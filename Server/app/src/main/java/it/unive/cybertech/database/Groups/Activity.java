package it.unive.cybertech.database.Groups;

import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Groups.Exception.NoActivityFoundException;
import it.unive.cybertech.database.Groups.Exception.NoGroupFoundException;
import it.unive.cybertech.database.Profile.User;

/**
 * Class use to describe an activity instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 *
 * @author Davide Finesso
 */
public class Activity {
    public final static String table = "activity";
    private String id;
    private DocumentReference owner;
    private String name;
    private String description;
    private String place;
    private Timestamp date;
    private ArrayList<DocumentReference> participants;

    /**
     * Materialize field for increase the performance.
     *
     * @author Davide Finesso
     */
    private User ownerMaterialized;
    private ArrayList<User> participantsMaterialized;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public Activity(){}

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
    private Activity(String id, DocumentReference ownerDoc, String name, String description, String place,
                     Timestamp date, ArrayList<DocumentReference> participants, User owner) {
        this.id = id;
        this.owner = ownerDoc;
        this.name = name;
        this.description = description;
        this.place = place;
        this.date = date;
        this.participants = participants;
        this.ownerMaterialized = owner;
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

    public Date obtainDateToDate(){
        return date.toDate();
    }

    private void setDate(Timestamp date) {
        this.date = date;
    }

    public List<DocumentReference> getParticipants() {
        return participants;
    }

    /**
     * The method return the field participants materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<User> obtainMaterializedParticipants() throws ExecutionException, InterruptedException {
        if(participantsMaterialized == null) {
            participantsMaterialized = new ArrayList<>();

            for (DocumentReference doc : participants) {
                participantsMaterialized.add(User.obtainUserById(doc.getId()));
            }
        }

        return participantsMaterialized;
    }

    /**
     * The method return the field owner materialize, if is null the method get it from database.
     *
     * @author Davide Finesso
     */
    public User obtainOwnerMaterialized() throws ExecutionException, InterruptedException {
        if(ownerMaterialized == null)
            ownerMaterialized = User.obtainUserById(owner.getId());

        return ownerMaterialized;
    }

    /**
     * The method add to the database a new activity and return it.
     *
     * @author Davide Finesso
     */
    public static Activity createActivity(String name, String description, String place, Date date, User owner) throws ExecutionException, InterruptedException {
        Timestamp t = new Timestamp(date);
        DocumentReference userDoc = Database.getReference(User.table, owner.getId());

        Map<String, Object> activity = new HashMap<>();
        activity.put("name", name);
        activity.put("description", description);
        activity.put("place", place);
        activity.put("date", t);
        activity.put("owner", userDoc);

        DocumentReference addedDocRef = Database.addToCollection(table, activity);

        return new Activity(addedDocRef.getId(), userDoc, name, description, place, t, new ArrayList<>(), owner);
    }

    /**
     * The protected method return the activity with that id. If there isn't a activity with that id it throw an exception.
     *
     * @author Davide Finesso
     */
    public static Activity obtainActivityById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Activity activity = null;

        if (document.exists()) {
            activity = document.toObject(Activity.class);
            activity.setId(document.getId());

            if(activity.getParticipants() == null)
                activity.participants = new ArrayList<>();
            
            return activity;
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> deleteActivityAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
        else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    /**
     * The method is use to delete a activity from the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    /**
     * The protected method is use to update an activity field owner to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    protected boolean updateOwner(User user) {
        try {
            DocumentReference docRef = getReference(User.table, user.getId());
            Task<Void> t = updateOwnerAsync(docRef);
            Tasks.await(t);
            this.setOwner(docRef);
            this.ownerMaterialized = user;
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
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
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    /**
     * The method is use to update an activity field description to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updatePlaceAsync(String place) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("place", place);
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    /**
     * The method is use to update an activity field place to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateDateAsync(Timestamp timestamp) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("date", timestamp);
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    /**
     * The method is use to update an activity field date to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> addParticipantAsync(@NonNull DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("participants", FieldValue.arrayUnion(user));
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    /**
     * The method is use to add an activity participant to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean addParticipant(@NonNull User user) {
        try {
            DocumentReference userDoc = getReference(User.table, user.getId());
            Task<Void> t = addParticipantAsync(userDoc);
            Tasks.await(t);
            this.participants.add(userDoc);
            if(this.participantsMaterialized != null)
                this.obtainMaterializedParticipants().add(user);
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> removeParticipantAsync(@NonNull DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("participants", FieldValue.arrayRemove(user));
        } else
            throw new NoActivityFoundException("No activity found with this id: " + id);
    }

    /**
     * The method is use to remove an activity participant to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean removeParticipant(@NonNull User user) {
        try {
            DocumentReference userDoc = getReference(User.table, user.getId());
            Task<Void> t = removeParticipantAsync(userDoc);
            Tasks.await(t);
            this.participants.remove(userDoc);
            if(this.participantsMaterialized != null)
                this.obtainMaterializedParticipants().remove(user);
            return true;
        } catch (ExecutionException | InterruptedException | NoActivityFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * The method is use to get the membership group of the passed activity. The method can throw an exception if there is any group with that activity.
     *
     * @author Davide Finesso
     */
    public static Group obtainGroupFromActivity(Activity activity) throws ExecutionException, InterruptedException, NoGroupFoundException {
        DocumentReference actDoc = getReference(table, activity.getId());

        Task<QuerySnapshot> future = getInstance().collection(Group.table)
                .whereArrayContains("activities", actDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if(documents.isEmpty())
            throw new NoGroupFoundException("no group found where this activity ( "+activity.getId()+" ) is present");
        return Group.obtainGroupById(documents.get(0).getId());
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
        Activity activity = (Activity) o;
        return Objects.equals(id, activity.id);
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
