package edu.augustana.osleventsandroid;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Event implements Serializable, Comparable<Event> {
    private String name;
    private String location;
    private Date date;
    private String organization;
    private String type;
    private ArrayList<String> tags;
    private int imgid;

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }

    public String getFormatedDate() {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        //return simpleDateFormat.format(date);
        return date.getMonth() + "-" + date.getDate() + "-" + date.getYear();
    }

    public String getFormatedTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date);
    }

    public Event(String name, String location, Date date, String organization, String type, ArrayList<String> tags, int imgid) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.organization = organization;
        this.type = type;
        this.tags = tags;
        this.imgid = imgid;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    @Override
    public int compareTo(Event o) {
        return this.getName().compareTo(o.getName());
    }

}
    class DateSorter implements Comparator<Event>
    {
        public int compare(Event o1, Event o2)
        {
            return o1.getDate().compareTo(o2.getDate());
        }
    }

    class EventTypeSorter implements Comparator<Event>
    {
        public int compare(Event o1, Event o2)
        {
            return o1.getType().compareTo(o2.getType());
        }
    }

    class LocationSorter implements Comparator<Event>
    {
        public int compare(Event o1, Event o2)
        {
            return o1.getLocation().compareTo(o2.getLocation());
        }
    }

