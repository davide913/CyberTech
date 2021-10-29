import Connection.Connection;
import Profile.User;
import com.google.cloud.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        Connection.initializeConnection();

        /*User s = User.createUser("davide", "finesso", "M", "rosmini", "abano", "italy",
                        new GeoPoint(1.4,1.5),true );

        User u = User.getUserById(s.getId());

        System.out.println(u.getName());

        s = User.createUser("davide", "finesso", "M", "rosmini", "abano", "italy",
                new GeoPoint(1.4,1.5),true );

        u = User.getUserById(s.getId());

        System.out.println(u.getName());*/

        //User.deleteUser("4HeuqZvyZxqSkUGshiXF");

        System.out.println(User.updatePositiveSince("S2BaLtNi3Zja76BMWGXH", new Date(2021-1900, 10,7)));



        //User u = User.getUserById("S2BaLtNi3Zja76BMWGXH");

        //System.out.println(u.getPositiveSince());
    }
}
