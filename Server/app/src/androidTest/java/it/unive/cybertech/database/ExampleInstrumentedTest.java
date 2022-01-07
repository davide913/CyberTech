package it.unive.cybertech.database;

import static it.unive.cybertech.database.Database.getDocument;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest{
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Database.initialize(appContext);

        //scrivi qui sotto

        List<QuarantineAssistance> arr = QuarantineAssistance
                .obtainJoinableQuarantineAssistance(null, new GeoPoint(37,-122),15);

        double distanceInM = GeoFireUtils
                .getDistanceBetween(new GeoLocation(37,-122), new GeoLocation(37.4219983,-122.084));

        Log.d("size", ""+distanceInM/1000);

    }
}