package it.unive.cybertech.database;

import static it.unive.cybertech.database.Database.getDocument;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.Sex;
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

        /*User.createUser("shish", "davi","lll", Sex.female, new Date(1999-1900,1,10), "via delle banane", "abano",
                "italy", 10,10,true);

        User u = User.getUserById("shish");

        u.deleteUser();*/

        List<QuarantineAssistance> arr = QuarantineAssistance.getJoinableQuarantineAssistance(null,
                new GeoPoint( 37,-122), 50);



    }
}