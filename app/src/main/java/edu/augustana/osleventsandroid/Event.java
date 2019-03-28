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
    private String date;
    private int duration;
    private String organization;
    private String tags;
    private String description;
    private String imgid;
    public Event(String name, String location, String date, int duration, String organization, String tags, String description, String imgid) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.organization = organization;
        this.tags = tags;
        this.imgid = imgid;
        this.description=description;
    }
    public String  getImgid() {
        return imgid;
    }

    public void setImgid(String imgid) {
        this.imgid = imgid;
    }

  //  public String getFormatedDate() {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        //return simpleDateFormat.format(date);
        //return date.getMonth() + "-" + date.getDate() + "-" + date.getYear();
  //  }

    //public String getFormatedTime() {
      //  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
      //  return simpleDateFormat.format(date);
    //}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStartDate() {
        //String calDate=date.
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }


    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public int compareTo(Event o) {
        return this.getName().compareTo(o.getName());
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

    class LocationSorter implements Comparator<Event>
    {
        public int compare(Event o1, Event o2)
        {
            return o1.getLocation().compareTo(o2.getLocation());
        }
    }

