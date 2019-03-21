package edu.augustana.osleventsandroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osleventsandroid.R;

import java.util.Calendar;

public class SingleEventPage extends AppCompatActivity {
    private TextView txtLocation;
    private TextView txtDateTime;
    private TextView txtType;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event_page);
        this.txtLocation=(TextView) findViewById(R.id.txt_location);
        this.txtDateTime=(TextView)  findViewById(R.id.txt_datetime);
        this.txtType=(TextView) findViewById(R.id.txt_eventype);
        this.img=(ImageView) findViewById(R.id.img);
        Event event = (Event) getIntent().getSerializableExtra("choosenEvent");
        txtLocation.setText(event.getLocation());
        txtDateTime.setText(event.getFormatedDate()+" "+event.getFormatedTime());
        txtType.setText(event.getType());
        img.setImageResource(event.getImgid());


        Button btn_calendar = (Button) findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Event event = (Event) getIntent().getSerializableExtra("choosenEvent");
                if (Build.VERSION.SDK_INT >= 14) {
                    //  code used from https:stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            //need to change this to start time and end time still, date isnt working
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getDate().getTime())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getFormatedDate())
                            .putExtra(CalendarContract.Events.TITLE, event.getName())
                            .putExtra(CalendarContract.Events.DESCRIPTION, event.getType())
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
