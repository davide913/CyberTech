package it.unive.cybertech.database;

import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import it.unive.cybertech.database.Material.Type;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.Chat;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.Sex;
import it.unive.cybertech.database.Profile.User;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Database.initialize(appContext);

        //scrivi qui sotto

        User davide = User.obtainUserById("BQbdfmklqXM1veEGezdPXmmvN0B2");
        davide.deleteAllMyQuarantineAssistance();
        /*User davide = User.createUser("davide", "davide", "davide", Sex.male, new Date(10,10,10),
                "via rss", "abano", "italy", 10,10, true);

        User u = User.obtainUserById("davide.finesso@hotmail.it");


        //u.addQuarantineAssistance(AssistanceType.obtainAssistanceTypes().get(0), "title Davide1",
        //        "description Davide1",new Date(10,10,10), 10,10);

        davide.addDevice("tokenDavide", "deviceIDDavide");

        davide.addQuarantineAssistance(AssistanceType.obtainAssistanceTypes().get(0), "title Davide",
                "description Davide",new Date(10,10,10), 10,10);

        davide.obtainMaterializedQuarantineAssistance().get(0).updateInCharge_QuarantineAssistance(u);

        davide.deleteUser();*/
    }
}