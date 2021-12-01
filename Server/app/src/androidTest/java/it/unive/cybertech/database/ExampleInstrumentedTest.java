package it.unive.cybertech.database;

import static it.unive.cybertech.database.Connection.Database.getInstance;
import static it.unive.cybertech.database.Profile.QuarantineAssistance.getJoinableQuarantineAssistance;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Chat;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Material.Type;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.RentMaterial;
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

        User u = User.getUserById("davide.finesso@hotmail.it");
        //User u1 = User.getUserById("davide.finesso@hotmail.it");

        Activity a = Activity.getActivityById("GAlx1uQ3lceEQpslHj7T");

        //a.addPartecipant(u);

        //Activity b = Activity.getActivityById("GAlx1uQ3lceEQpslHj7T");

        ArrayList<User> arr = a.getMaterializedParticipants();

        //a.addPartecipant(u);

        //Activity b = Activity.getActivityById("GAlx1uQ3lceEQpslHj7T");

    }
}