package edu.augustana.osleventsandroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osleventsandroid.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SingleEventPage extends AppCompatActivity {
    private TextView txtName;
    private TextView txtLocation;
    private TextView txtDate;
    private TextView txtTime;
    private TextView txtOrganization;
    private TextView txtDescription;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event_page);
        this.txtName=(TextView)  findViewById(R.id.txt_name);
        this.txtLocation=(TextView) findViewById(R.id.txt_location);
        this.txtDate=(TextView)  findViewById(R.id.txt_date);
        this.txtTime=(TextView)  findViewById(R.id.txt_time);
        this.txtOrganization=(TextView) findViewById(R.id.txt_organization);
        this.txtDescription=(TextView)  findViewById(R.id.txt_description);

        this.img=(ImageView) findViewById(R.id.img);
        Event event = (Event) getIntent().getSerializableExtra("choosenEvent");
        txtName.setText("Name: "+event.getName());
        txtLocation.setText("Location: "+event.getLocation());
        txtDate.setText("Date: "+event.getStartDate());
        txtTime.setText("Time: "+event.getStartTime()+"-"+event.getEndTime());
        txtOrganization.setText("Organization: "+event.getOrganization());
        txtDescription.setText("Description: "+event.getDescription());
        img.setImageResource(R.drawable.augustanatest);


        Button btn_calendar = (Button) findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Event event = (Event) getIntent().getSerializableExtra("choosenEvent");
                Date dateOfEvent = new Date();
                if (Build.VERSION.SDK_INT >= 14) {
                    Calendar startTime = Calendar.getInstance();
                    //TODO: make sure when it connects to firebase, the month is displayed correctly
                    startTime.set(dateOfEvent.getYear(),dateOfEvent.getMonth(),dateOfEvent.getDate(),
                            dateOfEvent.getHours(),dateOfEvent.getMinutes());
                    //  code used from https:stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            //need to change this to start time and end time still, date isnt working

                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis())

                            .putExtra(CalendarContract.Events.TITLE, event.getName())
                            .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription())
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation())
                            .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
                    startActivity(intent);
                } else {
                    Calendar cal = Calendar.getInstance();
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("beginTime", cal.getTimeInMillis());
                   // intent.putExtra("allDay", true);
                    intent.putExtra("rrule", "FREQ=YEARLY");
                    intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                    //intent.putExtra("title", "A Test Event from android app");
                    startActivity(intent);
                }

            }
        });

    }
}
