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

import java.util.List;

import it.unive.cybertech.database.Material.Type;
import it.unive.cybertech.database.Profile.Chat;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
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

        //String date = "13012022";
        //Chat chat = Chat.createChat("MLA4AELjJTfGundss458Q3Es3eC2", "s5M1ZmoqwPO3XplqWBw0KhqwWyi1");
        //Chat chat = Chat.obtainChatById("L7uUlzbpKiFYLkNe9zv0");
        //User u = User.obtainUserById("MLA4AELjJTfGundss458Q3Es3eC2");
        //chat.sendMessage(u, "dfgdfgdfg");
        QuarantineAssistance q = QuarantineAssistance.obtainQuarantineAssistanceById("D1vABnDdtzjAJBeWXKbH");
        Chat c = q.obtainMaterializeChat();
        c.test();
        /*User u = User.obtainUserById("nWZXf0hT5qSVURKdS3UkT0Te63E3");

        List<QuarantineAssistance> arr = u.obtainMaterializedQuarantineAssistance();

        Log.d("size", ""+arr.size());

        u.removeQuarantineAssistance(arr.get(0));*/

    }
}