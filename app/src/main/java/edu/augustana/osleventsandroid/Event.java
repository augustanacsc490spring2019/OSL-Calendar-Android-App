package edu.augustana.osleventsandroid;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/*
    This class houses all the information for an event. It allows you to get information on a
    particular event, and contains custom compare methods for the events.
 */
public class Event implements Serializable, Comparable<Event> {

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("h:mm a");
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("EEE MMM d, yyyy");


    private String eventID;
    private String description;
    private int duration;
    private String imgid; // Note: this field IS assigned when firebase creates the object
    private String location;
    private String name;
    private String organization;
    private String startDate;
    private String tags;
    private String webLink;
    private HashMap<String, Boolean> favoritedBy = new HashMap<>();


    @SuppressWarnings({"unused"}) // used by Firebase deserialization
    public Event() {
    }

    //Getters and setters

    public String getDescription() {
        return description;
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

    public String getStartDate() {
        return DATE_FORMATTER.format(getCalStart().getTime());
    }

    public String getImgid() {
        return imgid;
    }

    public String getWebLink() { return webLink; }

    public HashMap<String, Boolean> getFavoritedBy() { return favoritedBy; }

    public String getEventID() { return eventID; }

    public void setEventID(String eventID) { this.eventID = eventID; }

    /**
     *
     * @return string format of event start time
     */
    public String getStartTimeText() {
        return TIME_FORMATTER.format(getCalStart().getTime());
    }

    /**
     *
     * @return string format of the event end time
     */
    public String getEndTimeText() {
        Calendar end= getCalStart();
        end.add(Calendar.MINUTE, duration);
        return TIME_FORMATTER.format(end.getTime());
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
        int year = Integer.parseInt(startDate.substring(0, 4));
        int month = Integer.parseInt(startDate.substring(5, 7));
        int day = Integer.parseInt(startDate.substring(8, 10));
        int hour = Integer.parseInt(startDate.substring(11, 13));
        int min = Integer.parseInt(startDate.substring(14, 16));
        Calendar startTime = Calendar.getInstance();
        startTime.set(year, month-1, day, hour, min);
        return startTime;

    }

    /**
     * Classes for sorting events by location, date and group
     */

    public static class DateSorter implements Comparator<Event> {
        public int compare(Event o1, Event o2) {
            return o1.getCalStart().compareTo(o2.getCalStart());
        }
    }
}


