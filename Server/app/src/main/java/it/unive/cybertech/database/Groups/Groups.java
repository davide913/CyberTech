package it.unive.cybertech.database.Groups;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class Groups {
    private String ID;
    private String Name;
    private String Description;
    private DocumentReference Owner;
    private ArrayList<Members> MembersField;
    private ArrayList<Chat> Messages;
    private ArrayList<Activity> Activities;

    public Groups() {}

    public Groups(String ID, String name, String description, DocumentReference owner,
                  ArrayList<Members> members, ArrayList<Chat> messages, ArrayList<Activity> activities) {
        this.ID = ID;
        Name = name;
        Description = description;
        Owner = owner;
        MembersField = members;
        Messages = messages;
        Activities = activities;
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

    public DocumentReference getOwner() {
        return Owner;
    }

    private void setOwner(DocumentReference owner) {
        Owner = owner;
    }

    public ArrayList<Members> getMembers() {
        return MembersField;
    }

    private void setMembers(ArrayList<Members> members) {
        MembersField = members;
    }

    public ArrayList<Chat> getMessages() {
        return Messages;
    }

    private void setMessages(ArrayList<Chat> messages) {
        Messages = messages;
    }

    public ArrayList<Activity> getActivities() {
        return Activities;
    }

    private void setActivities(ArrayList<Activity> activities) {
        Activities = activities;
    }


}
