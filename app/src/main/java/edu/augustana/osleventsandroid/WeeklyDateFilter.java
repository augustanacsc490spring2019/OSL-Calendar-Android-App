package edu.augustana.osleventsandroid;

import android.util.Log;
import android.widget.TextView;

import com.example.osleventsandroid.R;

import java.util.Calendar;

public class WeeklyDateFilter implements EventFilter {

    private Calendar weekStartDay;

    public WeeklyDateFilter(Calendar weekStartDay) {
        this.weekStartDay = weekStartDay;
    }


    @Override
    public boolean applyFilter(Event event) {
        Calendar weekEndDay = getWeekEndDay();
        if (event.getCalStart().compareTo(weekStartDay) > 0 && event.getCalStart().compareTo(weekEndDay) < 0) {
            return true;
        }
        return false;
    }

    public void setCurrentWeek(TextView currentWeek, Calendar weekEndDay) {
        String currentWeekText = (weekStartDay.get(Calendar.MONTH)+1) + "/" + weekStartDay.get(Calendar.DAY_OF_MONTH) + "/"
                + weekStartDay.get(Calendar.YEAR) + " - " + (weekEndDay.get(Calendar.MONTH)+1) + "/"
                + weekEndDay.get(Calendar.DAY_OF_MONTH) + "/" + weekEndDay.get(Calendar.YEAR);
        currentWeek.setText(currentWeekText);
    }

    public Calendar getWeekEndDay() {
        Calendar weekEndDay = Calendar.getInstance();
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

        return weekEndDay;
    }

    public void setWeekStartDay(Calendar weekStartDay) {
        this.weekStartDay = weekStartDay;
        Log.d("DateFilter", "New Date: " + this.weekStartDay.getTime());
        Log.d("DateFilter", "New Date: " + weekStartDay.getTime());
    }
}
