package it.unive.cybertech.database;

import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getInstance;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Profile.Device;

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

        FirebaseFirestore db = getInstance();

        //QuarantineAssistance quarantineAssistance = QuarantineAssistance.getQuarantineAssistanceByInCharge(User.getUserById("davide.finesso@hotmail.it"));

        //Device.createDevice("token", "deviceid");
        //Device.createDevice("token2", "deviceid2");

        Device device = Device.createDevice("token22222222222", "deviceid");

        device.deleteDevice();



        Log.d("date", new Date().toString());
    }
}