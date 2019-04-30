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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osleventsandroid.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/*
    Uses:
    -Code to navigate the menu screen
    -Displays the events on the device
    -Search or sort events
 */
public class FindEvents extends AppCompatActivity {

    final int QRCODE = 0;
    private ListView eventslv;
    private RelativeLayout settingsView;
    private BottomNavigationView navigation;
    private ArrayList<Event> events;
    private CustomLVAdapter customLVAdapter;
    private DatabaseReference database;
    private RelativeLayout progressBar;
    private MenuItem item;


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
        progressBar = (RelativeLayout) findViewById(R.id.progressLayout);
        progressBar.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance().getReference("current-events");
        eventslv = (ListView) findViewById(R.id.listViewEvents);
        settingsView = (RelativeLayout) findViewById(R.id.settingsView);
        events = new ArrayList<Event>();
        customLVAdapter = new CustomLVAdapter(FindEvents.this, events);
        databaseListener();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void moveToSearch() {
        settingsView.setVisibility(View.GONE);
        eventslv.setVisibility(View.VISIBLE);
        item.setVisible(true);
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
        item.setVisible(false);

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

    public void databaseListener() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            //when an event is added to firebase, the listener is triggered and begins the
            //process to add and display that event on the device
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Event event= snapshot.getValue(Event.class);
                    final String name = snapshot.child("name").getValue().toString();
                    final String startDate = snapshot.child("startDate").getValue().toString();
                    final int duration = Integer.parseInt(snapshot.child("duration").getValue().toString());
                    final String location = snapshot.child("location").getValue().toString();
                    final String organization = snapshot.child("organization").getValue().toString();
                    final String tags = snapshot.child("tags").getValue().toString();
                    final String description = snapshot.child("description").getValue().toString();
                    String imgid = snapshot.child("imgid").getValue().toString();
                    StorageReference storage = FirebaseStorage.getInstance().getReference().child("Images").child(imgid + ".jpg");
                    final long ONE_MEGABYTE = Integer.MAX_VALUE;

                    storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Event event = null;
                            try {
                                event = new Event(name, location, startDate, duration, organization, tags, description, bytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            events.add(event);
                            Collections.sort(events, new DateSorter());
                            customLVAdapter = new CustomLVAdapter(FindEvents.this, events);
                            eventslv.setAdapter(customLVAdapter);
                            progressBar.setVisibility(View.GONE);
                            eventslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView adapter, View v, int position, long arg3) {
                                    Event choosenEvent = events.get(position);
                                    Intent intent = new Intent(FindEvents.this, SingleEventPage.class);
                                    intent.putExtra("choosenEvent", choosenEvent);
                                    startActivity(intent);

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /**
     * Adds a drop down for sorting in the top right action bar
     * Contains code for the search bar
     *
     * @param menu
     * @return boolean
     */
    //https://www.viralandroid.com/2016/03/how-to-add-spinner-dropdown-list-to-android-actionbar-toolbar.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_options, menu);
        item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        String[] spinner_list_item_array = {"A-Z", "Z-A", "Soonest", "Organization"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_spinner_layout, spinner_list_item_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //https://developer.android.com/guide/topics/ui/controls/spinner.html
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //different compare methods are called depending on the user input
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    Collections.sort(events);
                    customLVAdapter = new CustomLVAdapter(FindEvents.this, events);
                    eventslv.setAdapter(customLVAdapter);
                } else if (position == 1) {
                    Collections.sort(events, Collections.<Event>reverseOrder());
                    customLVAdapter = new CustomLVAdapter(FindEvents.this, events);
                    eventslv.setAdapter(customLVAdapter);
                } else if (position == 2) {
                    Collections.sort(events, new DateSorter());
                    customLVAdapter = new CustomLVAdapter(FindEvents.this, events);
                    eventslv.setAdapter(customLVAdapter);
                } else if (position == 3) {
                    Collections.sort(events, new OrganizationSorter());
                    customLVAdapter = new CustomLVAdapter(FindEvents.this, events);
                    eventslv.setAdapter(customLVAdapter);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        //code for the search bar
        MenuItem searchBarItem = menu.findItem(R.id.app_bar_search);
        SearchView searchBar = (SearchView) MenuItemCompat.getActionView(searchBarItem);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<Event> searchedEvents = new ArrayList<Event>();
                for (int i = 0; i < events.size(); i++) {
                    Event currentEvent = events.get(i);
                    String lowerCaseQuery = query.toLowerCase();
                    //searches all events based on name, location, tags, and organization
                    if (currentEvent.getName().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getLocation().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getTags().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getOrganization().toLowerCase().contains(lowerCaseQuery)) {
                        searchedEvents.add(events.get(i));
                    }
                }
                Collections.sort(searchedEvents);
                customLVAdapter = new CustomLVAdapter(FindEvents.this, searchedEvents);
                eventslv.setAdapter(customLVAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                return false;
            }
        });

        //resets the events view if they close out of the search bar
        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Collections.sort(events);
                customLVAdapter = new CustomLVAdapter(FindEvents.this, events);
                eventslv.setAdapter(customLVAdapter);
                return false;
            }
        });

        return true;
    }

    public void signOutbtn(View v) {
        FirebaseAuth.getInstance().signOut();
        finish();
    }

}
