package edu.augustana.osleventsandroid;

import android.util.Log;
import android.widget.TextView;

import com.example.osleventsandroid.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WeeklyDateFilter implements EventFilter {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MMM d");

    private Calendar weekStartDay;
    private Calendar weekEndDay;

    public WeeklyDateFilter(Calendar startDay) {
        startDay.set(Calendar.HOUR, 0);
        startDay.set(Calendar.MINUTE, 0);
        startDay.set(Calendar.SECOND, 0);

        this.weekStartDay = startDay;

        calculateWeekEndDay();

    }

    private void calculateWeekEndDay() {
        weekEndDay = Calendar.getInstance();

        //Calculates number of days to add to get the last day of the week. Needed for first week
        //  if it starts on a day other than Monday.
        int dateIncrease = 8 - weekStartDay.get(Calendar.DAY_OF_WEEK);
        if (weekStartDay.get(Calendar.DAY_OF_WEEK) == 1) {
            dateIncrease = 0;
        }
        weekEndDay.set(Calendar.DATE, weekStartDay.get(Calendar.DATE));
        weekEndDay.set(Calendar.MONTH, weekStartDay.get(Calendar.MONTH));
        weekEndDay.set(Calendar.YEAR, weekStartDay.get(Calendar.YEAR));
        weekEndDay.add(Calendar.DATE, dateIncrease);
        weekEndDay.set(Calendar.HOUR, 11);
        weekEndDay.set(Calendar.MINUTE, 59);
        weekEndDay.set(Calendar.SECOND, 59);
    }


    @Override
    public boolean applyFilter(Event event) {
        return (event.getCalStart().compareTo(weekStartDay) > 0 && event.getCalStart().compareTo(weekEndDay) < 0);
    }

    public String getCurrentWeekLabel() {
        String beginDateLabel;
        if (isFilteringCurrentWeek()) {
            beginDateLabel = "Now";
        } else {
            beginDateLabel = DATE_FORMATTER.format(weekStartDay.getTime());
        }
        String endDateLabel = DATE_FORMATTER.format(weekEndDay.getTime());
        return beginDateLabel + " - " + endDateLabel;
    }

//    public Calendar getWeekStartDay() {
//        return weekStartDay;
//    }
//
//    public Calendar getWeekEndDay() {
//        return weekEndDay;
//    }

//    public void setWeekStartDay(Calendar weekStartDay) {
//        this.weekStartDay = weekStartDay;
//    }

    public void moveToNextWeek() {
        weekStartDay.set(Calendar.DATE, weekEndDay.get(Calendar.DATE));
        weekStartDay.set(Calendar.MONTH, weekEndDay.get(Calendar.MONTH));
        weekStartDay.set(Calendar.YEAR, weekEndDay.get(Calendar.YEAR));
        weekStartDay.add(Calendar.DATE, 1);
        calculateWeekEndDay();

    }

    public void moveToPreviousWeek() {
        Calendar todayDate = Calendar.getInstance();

        weekStartDay.add(Calendar.DATE, -7);
        if (weekStartDay.compareTo(todayDate) < 0) {
            weekStartDay.set(Calendar.DATE, todayDate.get(Calendar.DATE));
            weekStartDay.set(Calendar.MONTH, todayDate.get(Calendar.MONTH));
            weekStartDay.set(Calendar.YEAR, todayDate.get(Calendar.YEAR));
        }
        calculateWeekEndDay();
    }

    public boolean isFilteringCurrentWeek() {
        Calendar todayDate = Calendar.getInstance();
        return (weekStartDay.compareTo(todayDate) <= 0);

    }

}
