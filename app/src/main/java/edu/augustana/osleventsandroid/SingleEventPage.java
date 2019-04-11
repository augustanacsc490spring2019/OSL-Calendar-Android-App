package edu.augustana.osleventsandroid;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
        txtName.setText(event.getName());
        txtLocation.setText(event.getLocation());
        txtDate.setText(event.getStartDate());
        txtTime.setText(event.getStartTime()+" - "+event.getEndTime());
        txtOrganization.setText(event.getOrganization());
        txtDescription.setText(event.getDescription());
        img.setImageBitmap(BitmapFactory.decodeByteArray(event.getImg(),0,event.getImg().length));


        Button btn_calendar = (Button) findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Event event = (Event) getIntent().getSerializableExtra("choosenEvent");
                    Calendar startTime = event.getCalStart();
                    //TODO: make sure when it connects to firebase, the month is displayed correctly

                    //  code used from https:stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            //need to change this to start time and end time still, date isnt working
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTime.getTimeInMillis()+event.getDuration()*60*1000)
                            .putExtra(CalendarContract.Events.TITLE, event.getName())
                            .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription())
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation())
                            .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
                    startActivity(intent);

            }
        });

    }
}
