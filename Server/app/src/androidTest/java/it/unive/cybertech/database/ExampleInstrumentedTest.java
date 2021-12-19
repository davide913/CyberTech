package it.unive.cybertech.database;

import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Device;
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

        //FirebaseFirestore db = getInstance();

        //User u = User.getUserById("davide.finesso@hotmail.it");

        //u.addDevice("token", "deviceId");
        //u.addDevice("token2", "deviceId2 di prova");

        //ArrayList<Device> arr = u.getMaterializedDevices();

        //arr.get(0).updateToken("token numero 2");

        //u.removeDevice(arr.get(0));

        //Material.getRentableMaterials(10,10,50);


        //u.updateLocation("italy", "montegrotto", "via de amicis, 1", 20,20);



        //Task<DocumentReference> s = db.collection("users").document(u.getId()).collection("devices").add(d);

        //ArrayList<Material> arr = Material.getRentableMaterials(45,12,50);

        ArrayList<QuarantineAssistance> q = QuarantineAssistance.getJoinableQuarantineAssistance(null, null, 0);

        //Log.d("date", new Date().toString());
    }
}