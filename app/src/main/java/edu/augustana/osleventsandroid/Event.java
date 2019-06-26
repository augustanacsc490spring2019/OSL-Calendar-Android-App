package edu.augustana.osleventsandroid;


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
    private String description;
    private int duration;
    private String imgid;
    private String location;
    private String name;
    private String organization;
    private String startDate;
    private String tags;
    private byte[] imgBytes;


    @SuppressWarnings({"unused"}) // used by Firebase deserialization
    public Event() {
    }

    public Event(String name, String location, String date, int duration, String group, String tags, String description, byte[] img) throws IOException {
        this.name = name;
        this.location = location;
        this.startDate = date;
        this.organization = group;
        this.duration = duration;
        this.tags = tags;
        this.imgBytes = img;
        this.description = description;
    }

//Getters and setters
    public byte[] getImgBytes() {
        return imgBytes;
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

    public void setImgBytes(byte[] imgBytes) {
        this.imgBytes = imgBytes;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        String[] calDatetime = startDate.split(" ");
        return calDatetime[0];
    }

    public String getImgid() {
        return imgid;
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

    public String getOrganization() {
        return organization;
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
        int year = Integer.parseInt(startDate.substring(0, 4)) - 1;
        int month = Integer.parseInt(startDate.substring(5, 7));
        int day = Integer.parseInt(startDate.substring(8, 10));
        int hour = Integer.parseInt(startDate.substring(11, 13));
        int min = Integer.parseInt(startDate.substring(14, 16));
        Calendar startTime = Calendar.getInstance();
        startTime.set(year, month, day, hour, min);
        return startTime;

    }
}

/**
 * Classes for sorting events by location, date and group
 */

class DateSorter implements Comparator<Event> {
    public int compare(Event o1, Event o2) {
        return o1.getCalStart().compareTo(o2.getCalStart());
    }
}

class GroupSorter implements Comparator<Event> {
    public int compare(Event o1, Event o2) {
        return o1.getOrganization().toLowerCase().compareTo(o2.getOrganization().toLowerCase());
    }
}