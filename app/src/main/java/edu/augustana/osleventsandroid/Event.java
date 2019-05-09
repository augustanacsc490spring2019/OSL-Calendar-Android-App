package edu.augustana.osleventsandroid;


import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.text.SimpleDateFormat;
/*
    This class houses all the information for an event. It allows you to get information on a
    particular event, and contains custom compare methods for the events.
 */
public class Event implements Serializable, Comparable<Event> {
    private String name;
    private String location;
    private String date;
    private int duration;
    private String group;
    private String tags;
    private String description;
    private byte[] img;
    private FirebaseStorage storage;

    public Event(String name, String location, String date, int duration, String group, String tags, String description, byte[] img) throws IOException {
        this.name = name;
        this.location = location;
        this.date = date;
        this.group = group;
        this.duration = duration;
        this.tags = tags;
        this.img = img;
        this.description = description;
    }
//Getters and setters
    public byte[] getImg() {
        return img;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        String[] calDatetime = date.split(" ");
        return calDatetime[0];
    }

    /**
     *
     * @return string format of event start time
     */
    public String getStartTime() {
        Calendar start=getCalStart();
        SimpleDateFormat df = new SimpleDateFormat("h:mm a");
        return df.format(start.getTime());
    }

    /**
     *
     * @return string format of the event end time
     */
    public String getEndTime() {
        Calendar end=getCalStart();
        end.add(Calendar.MINUTE, duration);
        SimpleDateFormat df = new SimpleDateFormat("h:mm a");
        return df.format(end.getTime());
    }

    public String getGroup() {
        return group;
    }

    public String getTags() {
        return tags;
    }

    /**
     * Compare event titles for sorting
     */
    @Override
    public int compareTo(Event o) { return this.getName().toLowerCase().compareTo(o.getName().toLowerCase()); }

    public int getDuration() {
        return duration;
    }

    /**
     * Converts Date from string to calander
     * @return Calander object of date
     */
    public Calendar getCalStart() {
        int month = Integer.parseInt(date.substring(0, 2)) - 1;
        int day = Integer.parseInt(date.substring(3, 5));
        int year = Integer.parseInt(date.substring(6, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int min = Integer.parseInt(date.substring(14, 16));
        Calendar startTime = Calendar.getInstance();
        startTime.set(year, month, day, hour, min);
        return startTime;

    }
}

/**
 * Classes for sorting events by location, date and group
 */
class LocationSorter implements Comparator<Event> {
    public int compare(Event o1, Event o2) {
        return o1.getLocation().compareTo(o2.getLocation());
    }
}

class DateSorter implements Comparator<Event> {
    public int compare(Event o1, Event o2) {
        return o1.getCalStart().compareTo(o2.getCalStart());
    }
}

class GroupSorter implements Comparator<Event> {
    public int compare(Event o1, Event o2) {
        return o1.getGroup().toLowerCase().compareTo(o2.getGroup().toLowerCase());
    }
}