package it.unive.cybertech.assistenza;

public class RequestInfo {
    private int id;
    private String title;
    private String location;
    private String date;
    private String text;


    public RequestInfo(int id, String title, String text, String location, String date) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.location = location;
        this.date = date;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
