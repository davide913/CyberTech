package it.unive.cybertech.database;

import static it.unive.cybertech.database.Database.getDocument;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

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

<<<<<<< HEAD
        Group g = Group.getGroupById("CWhAhAjdsYAfkaRKDFxX");
=======
        User u = User.obtainUserById("s5M1ZmoqwPO3XplqWBw0KhqwWyi1");
        u.obtainMyMaterialsExpiredLending();

        /*User u = User.getUserById("oYFnMCvE3efkOrbMCS8NBJI5Ph83");
>>>>>>> Mattia-nuovo

        User davide = User.getUserById("davide.finesso@hotmail.it");

        //g.addMember(davide);

<<<<<<< HEAD
        g.removeMember(davide);
=======

        Group g = Group.getGroupById("8roUO1MxMI9HVLryDEhG");

        DocumentReference doc = g.getOwner();

        doc.getId().equals(u.getId());

        User u1 = User.getUserById(doc.getId());

        u1.equals(u);

        g.addMember(u);

        List<User> arr = g.getMaterializedMembers();

        g.removeMember(u);

        arr = g.getMaterializedMembers();*/
>>>>>>> Mattia-nuovo





    }
}