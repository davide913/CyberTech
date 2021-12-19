package it.unive.cybertech.database;

import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Device;
import it.unive.cybertech.database.Profile.LendingInProgress;
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

        User u = User.getUserById("s5M1ZmoqwPO3XplqWBw0KhqwWyi1");

        /*LendingInProgress lending = LendingInProgress.
                createLendingInProgress(Material.getMaterialById("u1j3jyJI6vKMlN4vsH6p"),
                        new Date(2022, 1, 5));


        u.addLending(lending);*/

        ArrayList<LendingInProgress> lendings = u.getMaterializedLendingInProgress();

        Log.d("shish","" + lendings.size());



        //Log.d("date", new Date().toString());
    }
}