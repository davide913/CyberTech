package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.*;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Geoquerable;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;

/**
 * Class use to describe a quarantine assistance's instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 * The class extend Geoquerable to query the quarantine assistance by their position.
 *
 * @author Davide Finesso
 */
public class QuarantineAssistance extends Geoquerable {
    public final static String table = "quarantineAssistance";
    private DocumentReference assistanceType;
    private String description;
    private DocumentReference inCharge;
    private boolean isInCharge;
    private Timestamp deliveryDate;
    private GeoPoint location;
    private String geohash;
    private String title;
    private String id;

    /**
     * Materialize field for increase the performance.
     *
     * @author Davide Finesso
     */
    private AssistanceType materializeAssistanceType;
    private User materializeInCharge;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public QuarantineAssistance() {}

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
    private QuarantineAssistance(DocumentReference assistanceType, String description, Timestamp deliveryDate,
                                GeoPoint location, String geohash, String title, String id) {
        this.assistanceType = assistanceType;
        this.description = description;
        this.isInCharge = false;
        this.deliveryDate = deliveryDate;
        this.location = location;
        this.geohash = geohash;
        this.title = title;
        this.id = id;
    }

    public DocumentReference getAssistanceType(){
        return assistanceType;
    }

    private void setAssistanceType(DocumentReference assistanceType) {
        this.assistanceType = assistanceType;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public DocumentReference getInCharge(){
        return inCharge;
    }

    private void setInCharge(DocumentReference inCharge) {
        this.inCharge = inCharge;
    }

    public String getGeohash() {
        return geohash;
    }

    private void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public Date getDeliveryDateToDate() {
        return deliveryDate.toDate();
    }

    private void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public GeoPoint getLocation() {
        return location;
    }

    private void setLocation(GeoPoint location) {
        this.location = location;
    }

    private boolean isInCharge() {
        return isInCharge;
    }

    private void setIsInCharge(boolean isInCharge) {
        this.isInCharge = isInCharge;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    /**
     * The method return the field device materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public AssistanceType obtainMaterializeAssistanceType() throws ExecutionException, InterruptedException {
        if(materializeAssistanceType ==  null)
            materializeAssistanceType = AssistanceType.obtainAssistanceTypeById(assistanceType.getId());

        return materializeAssistanceType;
    }

    /**
     * The method return the field device materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public User obtainMaterializeInCharge() throws ExecutionException, InterruptedException {
        if(inCharge == null)
            return null;

        if(materializeInCharge == null)
            materializeInCharge = User.obtainUserById(inCharge.getId());

        return materializeInCharge;
    }

    /**
     * The protected method add to the database a new quarantine assistance and return it.
     *
     * @author Davide Finesso
     */
    protected static QuarantineAssistance createQuarantineAssistance(@NonNull AssistanceType assistanceType, String title,
                                                                  String description, Date date, double latitude, double longitude) throws ExecutionException, InterruptedException {
        DocumentReference AssTypeRef = getReference(AssistanceType.table, assistanceType.getId());
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));

        Timestamp t = new Timestamp(date);

        Map<String, Object> myQuarantine = new HashMap<>();
        myQuarantine.put("description", description);
        myQuarantine.put("deliveryDate", t);
        myQuarantine.put("assistanceType", AssTypeRef);
        myQuarantine.put("location", geoPoint);
        myQuarantine.put("geohash", geohash);
        myQuarantine.put("title", title);
        myQuarantine.put("isInCharge", false);

        DocumentReference addedDocRef = Database.addToCollection(table, myQuarantine);

        return new QuarantineAssistance(AssTypeRef, description, t, geoPoint, geohash, title, addedDocRef.getId());
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> deleteQuarantineAssistanceAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
        else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id);
    }

    /**
     * The method is use to delete a quarantine assistance from the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean deleteQuarantineAssistance() {
        try {
            Task<Void> t = deleteQuarantineAssistanceAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoLendingInProgressFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * The method return the quarantine assistance with that id. If there isn't a quarantine assistance with that id it throw an exception.
     *
     * @author Davide Finesso
     */
    public static QuarantineAssistance obtainQuarantineAssistanceById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            QuarantineAssistance quarantineAssistance = document.toObject(QuarantineAssistance.class);
            quarantineAssistance.setId(document.getId());

            return quarantineAssistance;
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id);
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateAssistanceType_QuarantineAssistanceAsync(@NonNull AssistanceType assistanceType) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            DocumentReference docRefAssistance = getReference(AssistanceType.table, assistanceType.getId());
            return docRef.update("assistanceType", docRefAssistance);

        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id);
    }

    /**
     * The method is use to update a quarantine assistance field assistance type to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateAssistanceType_QuarantineAssistance(@NonNull AssistanceType assistanceType) {
        try {
            Task<Void> t = this.updateAssistanceType_QuarantineAssistanceAsync(assistanceType);
            Tasks.await(t);
            this.setAssistanceType(getReference(AssistanceType.table, assistanceType.getId()));
            this.materializeAssistanceType = assistanceType;
            return true;
        } catch (ExecutionException | InterruptedException | NoQuarantineAssistanceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateInCharge_QuarantineAssistanceAsync(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if(user == null) {
            docRef.update("isInCharge", false);
            return docRef.update("inCharge", FieldValue.delete());
        }

        DocumentReference userDoc = getReference("users", user.getId());
        DocumentSnapshot documentUser = getDocument(userDoc);

        if (document.exists() && documentUser.exists()) {
            docRef.update("isInCharge", true);
            return docRef.update("inCharge", userDoc);
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id + " Or no user found with this id: " + user.getId());
    }

    /**
     * The method is use to update a quarantine assistance field in charge to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateInCharge_QuarantineAssistance(User user)  {
        try {
            Task<Void> t = this.updateInCharge_QuarantineAssistanceAsync(user);
            Tasks.await(t);
            if(user != null) {
                this.setInCharge(getReference(User.table, user.getId()));
                this.setIsInCharge(true);
                this.materializeInCharge = user;
            }
            else{
                this.setInCharge(null);
                this.setIsInCharge(false);
                this.materializeInCharge = null;
            }
            return true;
        } catch (ExecutionException | InterruptedException | NoQuarantineAssistanceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * The method is use to update a quarantine assistance field description to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateDescription(@NonNull String description) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            Tasks.await(docRef.update("description", description));
            this.setDescription(description);
            return true;
        } else
            return false;
    }

    /**
     * The method is use to update a quarantine assistance field title to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateTitle(@NonNull String title) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            Tasks.await(docRef.update("title", title));
            this.setTitle(title);
            return true;
        } else
            return false;
    }

    /**
     * This method return a quarantine assistance with field in charge equals to an user passed as a parameter. If there isn't quarantine assistance with that user as in charge, the method return null.
     *
     * @author Davide Finesso
     */
    public static QuarantineAssistance obtainQuarantineAssistanceByInCharge(User user) throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> future = getInstance().collection(table)
                .whereEqualTo("isInCharge", true)
                .whereEqualTo("inCharge", getReference(User.table, user.getId())).get();

        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if(documents.size() != 0) {
            QuarantineAssistance quarantineAssistance = documents.get(0).toObject(QuarantineAssistance.class);
            quarantineAssistance.setId(documents.get(0).getId());

            return quarantineAssistance;
        }
        return null;
    }

    /**
     * This method return all joinable quarantine assistance. It's possible to add some filter by the parameter of the method.
     * The result is also sort by date.
     *
     * @author Davide Finesso
     */
    public static List<QuarantineAssistance> obtainJoinableQuarantineAssistance( AssistanceType type, GeoPoint position,
                                                                                  double radiusInKm ) throws ExecutionException, InterruptedException {
        ArrayList<QuarantineAssistance> arr = new ArrayList<>();

        Query query = getInstance().collection(table)
                .whereEqualTo("isInCharge", false);

        if(type != null)
            query = query.whereEqualTo("assistanceType", getReference(AssistanceType.table, type.getId()));

        List<DocumentSnapshot> documents;

        if(position == null) {
            Task<QuerySnapshot> future = query.get();
            Tasks.await(future);
            documents = future.getResult().getDocuments();
        }
        else{
            documents = getGeoQueries(query, radiusInKm * 1000,
                    new GeoLocation(position.getLatitude(), position.getLongitude()));
        }

        for (DocumentSnapshot doc : documents) {
            QuarantineAssistance assistance = null;

            assistance = doc.toObject(QuarantineAssistance.class);
            assistance.id = doc.getId();
            arr.add(assistance);
        }

        arr.sort(new Comparator<QuarantineAssistance>() {
            @Override
            public int compare(QuarantineAssistance o1, QuarantineAssistance o2) {
                return o2.getDeliveryDateToDate().compareTo(o1.getDeliveryDateToDate());
            }
        });

        return arr;
    }

    /**
     * This method return the owner of this quarantine assistance.
     * The result is also sort by date.
     *
     * @author Davide Finesso
     */
    public User obtainRequestOwner() throws ExecutionException, InterruptedException {
        DocumentReference doc = getReference(table, this.id);

        Task<QuerySnapshot> t = getInstance().collection(User.table)
                                .whereArrayContains("quarantineAssistance", doc).get();
        Tasks.await(t);
        List<DocumentSnapshot> documents = t.getResult().getDocuments();

        if(documents.isEmpty())
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance find with this id: " + id);

        return User.obtainUserById(documents.get(0).getId());
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
        QuarantineAssistance that = (QuarantineAssistance) o;
        return Objects.equals(id, that.id);
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
