package edu.augustana.osleventsandroid;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Event {
    private String name;
    private String location;
    private LocalDateTime date;
    private String organization;
    private String type;
    private ArrayList<String> tags;

    public Event(String name, String location, LocalDateTime date, String organization, String type, ArrayList<String> tags) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.organization = organization;
        this.type = type;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
