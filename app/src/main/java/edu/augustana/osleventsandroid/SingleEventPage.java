package edu.augustana.osleventsandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.osleventsandroid.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.TreeMap;

/*
    Displays detailed information of a single event
 */
public class SingleEventPage extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 0;

    private Event event;
    private TextView txtName;
    private TextView txtLocation;
    private TextView txtDate;
    private TextView txtTime;
    private TextView txtGroup;
    private TextView txtTags;
    private TextView txtDescription;
    private ImageView img;
    private TreeMap map;
    private CheckBox favoriteCheckBox;
    private DatabaseReference database;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_single_event_page);
        this.txtName = (TextView) findViewById(R.id.txt_name);
        this.txtLocation = (TextView) findViewById(R.id.txt_location);
        this.txtDate = (TextView) findViewById(R.id.txt_date);
        this.txtTime = (TextView) findViewById(R.id.txt_time);
        this.txtGroup = (TextView) findViewById(R.id.txt_organization);
        this.txtTags = (TextView) findViewById(R.id.txt_tags);
        this.txtDescription = (TextView) findViewById(R.id.txt_description);
        map = new TreeMap();

        this.img = (ImageView) findViewById(R.id.img);
        event = (Event) getIntent().getSerializableExtra("chosenEvent");
        txtName.setText(event.getName());
        txtLocation.setText(event.getLocation());
        txtDate.setText(event.getStartDate());
        txtTime.setText(event.getStartTimeText() + " - " + event.getEndTimeText());
        txtGroup.setText(event.getOrganization());
        if (event.getTags() == null) {
            txtTags.setVisibility(View.GONE);
            findViewById(R.id.lbl_tags).setVisibility(View.GONE);
        } else {
            txtTags.setText(event.getTags());
        }
        txtDescription.setText(event.getDescription());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Images").child(event.getImgid()+".jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
             @Override
             public void onSuccess(Uri uri) {
                 Picasso.with(SingleEventPage.this).load(uri.toString()).into(img);
             }
         });

        Button btn_calendar = (Button) findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCalendar();
            }
        });

        makeTheme();
        favoriteCheckBox = findViewById(R.id.favorite_checkBox);
        database = FirebaseDatabase.getInstance().getReference("/current-events");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();
        user = userEmail.substring(0, userEmail.indexOf('@'));

        boolean favorited = event.getFavoritedBy().containsKey(user);
        favoriteCheckBox.setChecked(favorited);

        TextView webLink = (TextView) findViewById(R.id.txt_webLink);
        if ((event.getWebLink()).equals("")) {
            TextView linkLabel = (TextView) findViewById(R.id.lbl_webLink);
            linkLabel.setVisibility(View.GONE);
            webLink.setVisibility(View.GONE);
        } else {
            webLink.setText(Html.fromHtml("<a href=\""+ event.getWebLink() + "\">Click for more details.</a>"));
            webLink.setClickable(true);
            webLink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    //Click functionality for Favorites Check Box
    public void favoritesClick(View view) {
        if (favoriteCheckBox.isChecked()) {
            //Currently changing this event has no effect since it is just a deserialized copy
            // of the original event, AND all the events get recreated from Firebase anyway?
            event.getFavoritedBy().put(user,true);
            database.child(event.getEventID()).child("favoritedBy").child(user).setValue(true);
        } else {
            event.getFavoritedBy().remove(user);
            database.child(event.getEventID()).child("favoritedBy").child(user).removeValue();
        }
    }

    //Add the event to the user's google calendar
    public void addToCalendar() {
        Calendar startTime = event.getCalStart();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(SingleEventPage.this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted.   Should we show an explanation?  (if they denied once before)
            if (ActivityCompat.shouldShowRequestPermissionRationale(SingleEventPage.this,
                    Manifest.permission.READ_CALENDAR)) {
                AlertDialog alertDialog = new AlertDialog.Builder(SingleEventPage.this).create();
                alertDialog.setTitle("Calendar Permission");
                alertDialog.setMessage("If you allow calendar read permissions, this app can prevent you from adding the same event twice.  If you deny calendar reading permissions, it will still open the calendar to create an event.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(SingleEventPage.this,
                                        new String[]{Manifest.permission.READ_CALENDAR},
                                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);
                            }
                        });
                alertDialog.show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(SingleEventPage.this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);
            }
        } else {
            // check if an event with the same name is happening at the same time already in the user's calendar
            long startMillis = startTime.getTimeInMillis();
            long endMillis = startTime.getTimeInMillis() + event.getDuration() * 60 * 1000;
            String[] proj = new String[]{CalendarContract.Instances._ID, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.EVENT_ID, CalendarContract.Instances.TITLE};
            Cursor cursor = CalendarContract.Instances.query(getContentResolver(), proj, startMillis, endMillis, "\"" + event.getName() + "\"");

            boolean isEventAlreadyAdded = cursor != null && cursor.moveToFirst() && cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE)).equalsIgnoreCase(event.getName());
            if (!isEventAlreadyAdded) {
                launchAddToCalendarIntent();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(SingleEventPage.this).create();
                alertDialog.setTitle("Event already in calendar");
                alertDialog.setMessage("This event should have already been added.");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Edit event",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                launchAddToCalendarIntent();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();
            }
        }
    }

    public void addToCalPopUp() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();
        final String user = userEmail.substring(0, userEmail.indexOf('@'));
        final DatabaseReference database= FirebaseDatabase.getInstance().getReference("/user-favorites");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean eventExists = false;
                for (DataSnapshot test : snapshot.getChildren()) {
                    if (test.equals(event)) {
                        Log.d("My Events", "Event already exists");
                        eventExists = true;
                    }
                }
                if (!eventExists) {
                    database.child(user).setValue(event);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        database.setValue(event);
    }

    public void launchAddToCalendarIntent() {
        //TODO: make sure when it connects to firebase, the month is displayed correctly
        //  code used from https:stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getCalStart().getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getCalStart().getTimeInMillis() + event.getDuration() * 60 * 1000)
                .putExtra(CalendarContract.Events.TITLE, event.getName())
                .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation())
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    addToCalendar();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    launchAddToCalendarIntent();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void makeTheme(){
        LinearLayout single_layout = findViewById(R.id.single_layout);
        ScrollView single_bottom_layout = findViewById(R.id.single_bottom_layout);
        TextView time = findViewById(R.id.txt_time);
        time.setTextColor(Theme.getTextColor());
        TextView txt_name= findViewById(R.id.txt_name);
        txt_name.setTextColor(Theme.getTextColor());
        TextView lbl_location = findViewById(R.id.lbl_location);
        lbl_location.setTextColor(Theme.getTextColor());
        TextView lbl_Date = findViewById(R.id.lbl_Date);
        lbl_Date.setTextColor(Theme.getTextColor());
        TextView lbl_Time = findViewById(R.id.lbl_Time);
        lbl_Time.setTextColor(Theme.getTextColor());
        TextView lbl_org = findViewById(R.id.lbl_org);
        lbl_org.setTextColor(Theme.getTextColor());
        TextView lbl_Descrip= findViewById(R.id.lbl_Descrip);
        lbl_Descrip.setTextColor(Theme.getTextColor());
        TextView txt_location = findViewById(R.id.txt_location);
        txt_location.setTextColor(Theme.getTextColor());
        TextView txt_date = findViewById(R.id.txt_date);
        txt_date.setTextColor(Theme.getTextColor());
        TextView txt_organization = findViewById(R.id. txt_organization);
        txt_organization.setTextColor(Theme.getTextColor());
        TextView txt_description = findViewById(R.id.txt_description);
        txt_description.setTextColor(Theme.getTextColor());
        TextView txt_tags = findViewById(R.id.txt_tags);
        txt_tags.setTextColor(Theme.getTextColor());
        TextView lbl_tags = findViewById(R.id.lbl_tags);
        lbl_tags.setTextColor(Theme.getTextColor());
        TextView lbl_weblink = findViewById(R.id.lbl_webLink);
        lbl_weblink.setTextColor(Theme.getTextColor());
        Button btn_calendar = findViewById(R.id.btn_calendar);
        btn_calendar.setBackgroundColor(Theme.getButtonColor());
        btn_calendar.setTextColor(Theme.getTextColor());
        single_bottom_layout.setBackgroundColor(Theme.getBackgroundColor());
        single_layout.setBackgroundColor(Theme.getBackgroundColor());
    }
}

