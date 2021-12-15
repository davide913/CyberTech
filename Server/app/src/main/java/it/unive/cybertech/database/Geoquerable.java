package it.unive.cybertech.database;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Geoquerable {

    //Aggiunta il 5/12/2021 funzionante
    protected static List<DocumentSnapshot> getGeoQueries(Query query, double radiusInM,
                                                          GeoLocation center)
            throws ExecutionException, InterruptedException {
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (GeoQueryBounds b : bounds) {
            Query q = query.orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            Task<QuerySnapshot> t = q.get();
            Tasks.await(t);
            tasks.add(t);
        }

        List<DocumentSnapshot> matchingDocs = new ArrayList<>();

        for (Task<QuerySnapshot> task : tasks) {
            List<DocumentSnapshot> snap = task.getResult().getDocuments();
            for (DocumentSnapshot doc : snap) {
                GeoPoint geoPoint = doc.getGeoPoint("location");

                // We have to filter out a few false positives due to GeoHash
                // accuracy, but most will match
                GeoLocation docLocation = new GeoLocation(geoPoint.getLatitude(), geoPoint.getLongitude());
                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                if (distanceInM <= radiusInM) {
                    matchingDocs.add(doc);
                }
            }
        }

        return matchingDocs;
    }
}
