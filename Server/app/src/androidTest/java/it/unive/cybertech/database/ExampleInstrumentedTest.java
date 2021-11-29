package it.unive.cybertech.database;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Material.Type;
import it.unive.cybertech.database.Profile.RentMaterial;

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

        //Log.d("prima chiamata di log:", "sono qui");


        //User u = User.getUserById("davide.finesso@hotmail.it");

        //ArrayList<AssistanceType> assistances = AssistanceType.getAssistanceTypes();

        ArrayList<Type> types = Type.getMaterialTypes();

        Material m = Material.getMaterialById("PSrmt3MH9kKWeLVIdQfI");
        Material m1 = Material.getMaterialById("DADbMms4X9q4FtliexmZ");

        RentMaterial r =  RentMaterial.getRentMaterialById("wBWVJnLe5I44aMrrdhu6");

        r.updateRentMaterial(m);
        //errore
        Log.d("log", r.getMaterial().getDescription());

        r.deleteRentMaterial();





    }
}