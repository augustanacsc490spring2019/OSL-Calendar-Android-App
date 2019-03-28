package edu.augustana.osleventsandroid;

import java.io.Serializable;
import java.text.DateFormat;
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
        this.duration=duration;
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
        String[] calDatetime=date.split(" ");
        return calDatetime[0];
    }

    public String getStartTime() {
        String[] calDatetime=date.split(" ");
        int hour=Integer.parseInt(calDatetime[1].substring(0,2));
        int min=Integer.parseInt(calDatetime[1].substring(3,5));
        String ampm="";

        if(hour>=12){
            ampm="PM";
        }else {
            ampm="AM";
        }

        if(hour==0){
            hour=12;
        }else if(hour!=12){
            hour=hour%12;
        }
        String hourStr=hour+"";
        String minStr=min+"";

        if(min<10){
            minStr="0"+min;
        }
        return hour+":"+minStr+" "+ampm;
    }

    public String getEndTime(){
        String[] calDatetime=date.split(" ");
        int hour=Integer.parseInt(calDatetime[1].substring(0,2))+duration/60;
        int min=Integer.parseInt(calDatetime[1].substring(3,5))+duration%60;
        if(min>=60){
            hour=hour+min/60;
            min=min%60;
        }
        hour=hour%24;
        String ampm="";
        if(hour>=12){
            ampm="PM";
        }else {
            ampm="AM";
        }

        if(hour==0){
            hour=12;
        }else if(hour!=12){
            hour=hour%12;
        }
        String minStr=min+"";

        if(min<10){
            minStr="0"+min;
        }
        return hour+":"+minStr+" "+ampm;
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

