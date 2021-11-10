package Groups;

import Profile.Users;
import com.google.cloud.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class Activity {
    private String ID;
    private String Name;
    private String Description;
    private String Place;
    private Timestamp DateTime;
    private ArrayList<Users> Participants;

    public Activity(){}

    public Activity(String ID, String name, String description, String place, Timestamp dateTime, ArrayList<Users> participants) {
        this.ID = ID;
        Name = name;
        Description = description;
        Place = place;
        DateTime = dateTime;
        Participants = participants;
    }

    public String getID() {
        return ID;
    }

    private void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    private void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    private void setDescription(String description) {
        Description = description;
    }

    public String getPlace() {
        return Place;
    }

    private void setPlace(String place) {
        Place = place;
    }

    public Timestamp getDateTime() {
        return DateTime;
    }

    public Date getDateTimeD(){
        return DateTime.toDate();
    }

    private void setDateTime(Timestamp dateTime) {
        DateTime = dateTime;
    }

    public ArrayList<Users> getParticipants() {
        return Participants;
    }

    private void setParticipants(ArrayList<Users> participants) {
        Participants = participants;
    }
}
