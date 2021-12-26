package it.unive.cybertech.database;

import static it.unive.cybertech.database.Database.getDocument;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Date;

import it.unive.cybertech.database.Groups.Group;
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

        Group group = Group.getGroupById("GomAtcIw32fv13spLHE3");

        Log.d("id", group.getId());

        User u = User.getUserById("oYFnMCvE3efkOrbMCS8NBJI5Ph83");

        Collection<User> us= u.activitiesUsers();



        Log.d("date", new Date().toString());
    }
}