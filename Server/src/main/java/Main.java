import Connection.Connection;
import Profile.User;
import com.google.cloud.firestore.*;

import java.util.*;

class City{
    private String name;
    private String country;
    private String nation;
    private boolean capital;
    private long population;
    private List<String> a;

    public City(String name, String country, String nation, boolean capital, long population, List<String> a) {
        this.name = name;
        this.country = country;
        this.nation = nation;
        this.capital = capital;
        this.population = population;
        this.a = a;
    }
}

public class Main {

    public static void main(String[] args) throws Exception {
        Connection.initializeConnection();

        User s = User.createUser("davide", "finesso", "M", "rosmini", "abano", "italy",
                        new GeoPoint(1.4,1.5),true );

        User u = User.getUserById(s.getId());

        System.out.println(u.getId());
    }
}
