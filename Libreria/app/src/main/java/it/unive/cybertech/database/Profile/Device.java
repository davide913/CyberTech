package it.unive.cybertech.database.Profile;

public class Device {
    private String id;

    public Device(){}

    public Device(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Device) {
            Device d = (Device) o;
            return d.id.equals(this.id);
        }
        return false;
    }
}
