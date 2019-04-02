package edu.augustana.osleventsandroid;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osleventsandroid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class FindEvents extends AppCompatActivity {

    final int QRCODE = 0;
    private TextView mTextMessage;
    private ListView eventslv;
    private RelativeLayout settingsView;
    private BottomNavigationView navigation;
    private ArrayList<Event> events;
    private CustomLVAdapter customLVAdapter;
    private DatabaseReference database;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    moveToSearch();
                    return true;
                case R.id.navigation_scanQR:
                    moveToQR();
                    return true;
                case R.id.navigation_settings:
                    moveToSettings();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_events);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        database=FirebaseDatabase.getInstance().getReference("current-events");


        eventslv=(ListView) findViewById(R.id.listViewEvents);
        settingsView=(RelativeLayout) findViewById(R.id.settingsView);
        events=new ArrayList<Event>();
        databaseListener();
        //ArrayList<String> tag=new ArrayList<String>();
       // tag.add("bingo");
//        Event event1=new Event("Bingo", "Gavle 3",new Date(2019, 3, 30, 7, 30), "OSL", "OSL",tag,"win bingo",R.drawable.augustanatest );
//        Event event2=new Event("Comedy Show", "Gavle 1",new Date(2019, 3, 21, 6, 00), "OSL", "OSL",  tag,"hahah", R.drawable.augustanatest );
//        Event event3=new Event("Symphonic Band Concert", "Centeniall Hall",new Date(2019, 3, 30, 7, 30), "Music", "Arts", tag,"music", R.drawable.augustanatest );
//        Event event4=new Event("Movie", "Olin Auditorium",new Date(2019, 3, 21, 6, 00), "OSL", "OSL", tag,"movie",R.drawable.augustanatest );
//        Event event5=new Event("PepsiCo", "PepsiCo",new Date(2019, 3, 30, 7, 30), "OSL", "OSL", tag,"Gainzzzz",R.drawable.augustanatest );
//        Event event6=new Event("PepsiCo", "PepsiCo",new Date(2019, 3, 21, 6, 00), "OSL", "OSL", tag,"Gainzzz",R.drawable.augustanatest );
//        events.add(event1);
//        events.add(event2);
//        events.add(event3);
//        events.add(event4);
//        events.add(event5);
//        events.add(event6);
        //Collections.sort(events);
       // Collections.sort(events, new DateSorter());

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void moveToSearch() {
        settingsView.setVisibility(View.GONE);
        eventslv.setVisibility(View.VISIBLE);
    }

    // Source: https://stackoverflow.com/questions/42275906/how-to-ask-runtime-permissions-for-camera
    public void moveToQR() {
        if (checkPermission()) {
            startActivityForResult(new Intent(FindEvents.this, QrCodeScanner.class), QRCODE);
        } else {
            requestPermission();
        }
    }

    public void moveToSettings() {
        eventslv.setVisibility(View.GONE);
        settingsView.setVisibility(View.VISIBLE);
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
//            case R.id.sort:
//                // User chose the "Sort" item, show the app settings UI...
//                System.out.println("Do Sort");
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QRCODE) {
            navigation.setSelectedItemId(R.id.navigation_search);
            if (resultCode == RESULT_OK) {
                System.out.println(data);
            }
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FindEvents.this, QrCodeScanner.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(FindEvents.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void databaseListener(){
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                   // Event event= snapshot.getValue(Event.class);
                    String name=snapshot.child("name").getValue().toString();
                    String startDate=snapshot.child("startDate").getValue().toString();
                    int duration=Integer.parseInt(snapshot.child("duration").getValue().toString());
                    String location=snapshot.child("location").getValue().toString();
                    String organization=snapshot.child("organization").getValue().toString();
                    String tags=snapshot.child("tags").getValue().toString();
                    String description=snapshot.child("description").getValue().toString();
                    String imgid=snapshot.child("imgid").getValue().toString();
                    Event event=new Event(name,location, startDate, duration, organization, tags, description,imgid);
                    events.add(event);
                }
                customLVAdapter=new CustomLVAdapter(FindEvents.this, events);
                eventslv.setAdapter(customLVAdapter);
                eventslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView adapter, View v, int position, long arg3) {
                        Event choosenEvent = events.get(position);
                        Intent intent = new Intent(FindEvents.this, SingleEventPage.class);
                        intent.putExtra("choosenEvent",choosenEvent);
                        startActivity(intent);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
