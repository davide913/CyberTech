package it.unive.cybertech.database.Groups;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;

import java.util.Date;

public class Chat {
    private String ID;
    private Timestamp DateTime;
    private CollectionReference SenderID;
    private String Message;

    public Chat(){}

    public Chat(String ID, Timestamp dateTime, CollectionReference senderID, String message) {
        this.ID = ID;
        DateTime = dateTime;
        SenderID = senderID;
        Message = message;
    }

    public String getID() {
        return ID;
    }

    private void setID(String ID) {
        this.ID = ID;
    }

    public Timestamp getDateTime() {
        return DateTime;
    }

    public Date getDateTimeD() {
        return DateTime.toDate();
    }

    private void setDateTime(Timestamp dateTime) {
        DateTime = dateTime;
    }

    public CollectionReference getSenderID() {
        return SenderID;
    }

    private void setSenderID(CollectionReference senderID) {
        SenderID = senderID;
    }

    public String getMessage() {
        return Message;
    }

    private void setMessage(String message) {
        Message = message;
    }
}
