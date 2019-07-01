package edu.augustana.osleventsandroid;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osleventsandroid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FindEvents extends AppCompatActivity {

    final int QRCODE = 0;
    private TextView mTextMessage;
    private ListView eventslv;
    private RelativeLayout settingsView;
    private BottomNavigationView navigation;
    private ArrayList<Event> events;
    private ArrayList<Event> filteredEvents = new ArrayList<>();
    private ArrayList<Event> dateFilteredEvents = new ArrayList<>();
    private DatabaseReference database;
    private RelativeLayout progressBar;
    private MenuItem item;
    private SearchView searchBar;
    private RadioButton checkedRadioButton;
    private LinearLayout linear_layout;
    private RelativeLayout relative_layout;
    private RelativeLayout relLayout;
    private RadioGroup radioGroup;
    private LinearLayout dateToolbar;
    private WeeklyDateFilter dateFilter;
    private TextView currentWeek;
    private Button prevWeek;
    private Button nextWeek;
    private Calendar todayDate;
    private Calendar weekStartDate;
    private Calendar weekEndDate;


    private RecyclerView eventsView;
    private StaggeredGridLayoutManager gridLayoutManager;

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
        progressBar=(RelativeLayout) findViewById(R.id.progressLayout);
        dateToolbar=(LinearLayout) findViewById(R.id.date_toolbar);
        progressBar.setVisibility(View.VISIBLE);
        database=FirebaseDatabase.getInstance().getReference();
        settingsView=(RelativeLayout) findViewById(R.id.settingsView);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setBackgroundColor(Theme.getButtonColor());
        eventsView = findViewById(R.id.events_view);
        if (eventsView != null) {
            //to enable optimization of Recycler View
            eventsView.setHasFixedSize(true);
        }
        gridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager linearManager = new LinearLayoutManager(FindEvents.this, LinearLayoutManager.VERTICAL, false);
        eventsView.setLayoutManager(gridLayoutManager);
        eventsView.setLayoutManager(linearManager);
        currentWeek = (TextView) findViewById(R.id.current_week);
        prevWeek = (Button) findViewById(R.id.previous_week);
        nextWeek = (Button) findViewById(R.id.next_week);
        weekStartDate = Calendar.getInstance();
        todayDate = Calendar.getInstance();
        weekStartDate.set(Calendar.HOUR, 0);
        weekStartDate.set(Calendar.MINUTE, 0);
        weekStartDate.set(Calendar.SECOND, 0);
        todayDate.set(Calendar.HOUR, 0);
        todayDate.set(Calendar.MINUTE, 0);
        todayDate.set(Calendar.SECOND, 0);
        dateFilter = new WeeklyDateFilter(weekStartDate);
        weekEndDate = dateFilter.getWeekEndDay();
        prevWeek.setVisibility(View.GONE);

        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekStartDate.set(Calendar.DATE, weekEndDate.get(Calendar.DATE));
                weekStartDate.set(Calendar.MONTH, weekEndDate.get(Calendar.MONTH));
                weekStartDate.set(Calendar.YEAR, weekEndDate.get(Calendar.YEAR));
                weekStartDate.add(Calendar.DATE, 1);
                dateFilter.setWeekStartDay(weekStartDate);
                weekEndDate = dateFilter.getWeekEndDay();
                dateFilter.setCurrentWeek(currentWeek, weekEndDate);
                prevWeek.setVisibility(View.VISIBLE);
                filterByDate();
            }
        });

        prevWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        createAdapter();

        Log.d("FindEvents", "OnCreate finished");
    }

    private void createAdapter() {
        Query query = database.child("/current-events").orderByChild("startDate");
        // TODO: easy way out, just listen ONCE and not update in real time
        //  (harder way: listen for child changes, and make incremental changes to
        //    update the adapter appropriately...)
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    events.add(child.getValue(Event.class));
                }
                progressBar.setVisibility(View.GONE);
                filterByDate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filterByDate() {
        dateFilteredEvents.clear();
        for (int i = 0; i < events.size(); i++) {
            if (dateFilter.applyFilter(events.get(i))) {
                dateFilteredEvents.add(events.get(i));
            }
        }
        dateFilter.setCurrentWeek(currentWeek, weekEndDate);
        eventsView.setAdapter(new EventRecyclerAdapter(dateFilteredEvents, FindEvents.this));
    }


    public void moveToSearch() {
        settingsView.setVisibility(View.GONE);
        //eventslv.setVisibility(View.VISIBLE);
        //searchBar.setVisibility(View.VISIBLE);
        eventsView.setVisibility(View.VISIBLE);
        dateToolbar.setVisibility(View.VISIBLE);
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
        //eventslv.setVisibility(View.GONE);
        settingsView.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        eventsView.setVisibility(View.GONE);
        dateToolbar.setVisibility(View.GONE);
        startThemeListener();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QRCODE) {
            navigation.setSelectedItemId(R.id.navigation_search);
            if (resultCode == RESULT_OK) {
                String code=data.getStringExtra("QR Code");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(code)));
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
    //prevents user from hitting back button on bottom of phone and taking them back to the sign in page
    @Override
    public void onBackPressed(){

    }

    /**
     * Adds a drop down for sorting in the top right action bar
     * @param menu
     * @return boolean
     */
    //https://www.viralandroid.com/2016/03/how-to-add-spinner-dropdown-list-to-android-actionbar-toolbar.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_options, menu);
        MenuItem searchBarItem = menu.findItem(R.id.app_bar_search);
        searchBar = (SearchView) MenuItemCompat.getActionView(searchBarItem);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() >= 3) {
                    SearchMultiFieldFilter filter = new SearchMultiFieldFilter(query);
                    filterDisplayedEvents(filter);
                }
                return false;
            }
        });

        //resets the events view if they close out of the search bar
        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                eventsView.setAdapter(new EventRecyclerAdapter(dateFilteredEvents,FindEvents.this));
                return false;
            }
        });
        
        return true;
    }

    public void filterDisplayedEvents(EventFilter filter) {
        Log.d("FILTER", "events: " + events);
        filteredEvents.clear();
            for (int i = 0; i < events.size(); i++) {
                Event currentEvent = events.get(i);

                if (filter.applyFilter(currentEvent)) {
                    filteredEvents.add(currentEvent);
                }
        }
        updateDisplayingEvents();
    }
    public void updateDisplayingEvents() {
        Log.d("UPDATE_DISPLAY", "displayed events: " + filteredEvents);
        eventsView.setAdapter(new EventRecyclerAdapter(filteredEvents,FindEvents.this));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                //eventslv.setVisibility(View.GONE);
                settingsView.setVisibility(View.VISIBLE);
                //searchBar.setVisibility(View.GONE);
                eventsView.setVisibility(View.GONE);
                dateToolbar.setVisibility(View.GONE);
                startThemeListener();
                return true;
        }
        return true;
    }

    public void signOutbtn(View v){
        finish();
    }

    public void startThemeListener(){
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        checkedRadioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        relative_layout = findViewById(R.id.settingsView);
        relLayout = findViewById(R.id.relLayout);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId)
            {

                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                // This will get the radiobutton that has changed in its check state
                // This puts the value (true/false) into the variable
               if(checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme1).getId()) {
                   // If the radiobutton that has changed in check state is now checked...
                   Theme.whiteTheme();
               }else if(checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme2).getId()){
                   Theme.darkTheme();
               }else if(checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme3).getId()){
                   Theme.seaBlueTheme();
               }else if(checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme4).getId()){
                   Theme.twilightPurpleTheme();
               }else if(checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme5).getId()){
                   Theme.augieTheme();
               }
               FrameLayout constraintLayout = findViewById(R.id.container);
               TextView themeTitle = findViewById(R.id.themeTitle);
               themeTitle.setTextColor(Theme.getTextColor());
                TextView settingsTitle = findViewById(R.id.settingsTitle);
                settingsTitle.setTextColor(Theme.getTextColor());
                Button signOutBttn = findViewById(R.id.signOutBttn);
                signOutBttn.setBackgroundColor(Theme.getButtonColor());
                RadioButton theme1  = findViewById(R.id.theme1);
                theme1.setTextColor(Theme.getTextColor());

                RadioButton theme2  = findViewById(R.id.theme2);
                theme2.setTextColor(Theme.getTextColor());
                RadioButton theme3  = findViewById(R.id.theme3);
                theme3.setTextColor(Theme.getTextColor());
                RadioButton theme4  = findViewById(R.id.theme4);
                theme4.setTextColor(Theme.getTextColor());
                RadioButton theme5 = findViewById(R.id.theme5);
                theme5.setTextColor(Theme.getTextColor());
                BottomNavigationView navigation = findViewById(R.id.navigation);
                navigation.setBackgroundColor(Theme.getButtonColor());

                //ListView listOfEvents = findViewById(R.id.listViewEvents);

                constraintLayout.setBackgroundColor(Theme.getBackgroundColor());
                relative_layout.setBackgroundColor(Theme.getBackgroundColor());
                //relLayout.setBackgroundColor(Theme.getBackgroundColor());

            }
        });
    }


}
