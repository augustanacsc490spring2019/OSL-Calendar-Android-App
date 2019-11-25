package edu.augustana.osleventsandroid;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osleventsandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class FindEvents extends AppCompatActivity {

    final int QRCODE = 0;
    private RelativeLayout settingsView;
    private RelativeLayout myIDView;
    private BottomNavigationView navigation;
    private ArrayList<Event> events;
    private ArrayList<Event> filteredEvents = new ArrayList<>();
    private ArrayList<Event> dateFilteredEvents = new ArrayList<>();
    private ArrayList<Event> myEvents = new ArrayList<>();
    private DatabaseReference database;
    private RelativeLayout progressBar;
    private SearchView searchBar;
    private RadioButton checkedRadioButton;
    private LinearLayout linear_layout;
    private RelativeLayout relative_layout;
    private RelativeLayout relLayout;
    private RadioGroup radioGroup;
    private LinearLayout dateToolbar;
    private WeeklyDateFilter dateFilter;
    private TextView currentWeekLabel;
    private Button prevWeekBtn;
    private Button nextWeekBtn;
    private Calendar todayDate;
    private MyEventsFilter favoriteEventsFilter;
    private SearchMultiFieldFilter searchFilter;
    private ImageView qrImage;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;


    private RecyclerView eventsView;
    private StaggeredGridLayoutManager gridLayoutManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_all_events:
                    dateFilter.setEnabled(true);
                    favoriteEventsFilter.setEnabled(false);
                    moveToSearch();
                    return true;
                case R.id.navigation_scanQR:
                    moveToQR();
                    return true;
                case R.id.navigation_my_events:
                    dateFilter.setEnabled(false);
                    favoriteEventsFilter.setEnabled(true);
                    dateToolbar.setVisibility(View.GONE);
                    filter();
                    return true;
                case R.id.navigation_myID:
                    settingsView.setVisibility(View.GONE);
                    //eventslv.setVisibility(View.VISIBLE);
                    searchBar.setVisibility(View.GONE);
                    eventsView.setVisibility(View.GONE);
                    dateToolbar.setVisibility(View.GONE);
                    myIDView.setVisibility(View.VISIBLE);

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
        myIDView = findViewById(R.id.myIDView);
        gridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager linearManager = new LinearLayoutManager(FindEvents.this, LinearLayoutManager.VERTICAL, false);
        eventsView.setLayoutManager(gridLayoutManager);
        eventsView.setLayoutManager(linearManager);
        currentWeekLabel = (TextView) findViewById(R.id.current_week);
        prevWeekBtn = (Button) findViewById(R.id.previous_week);
        nextWeekBtn = (Button) findViewById(R.id.next_week);

        todayDate = Calendar.getInstance();
        dateFilter = new WeeklyDateFilter(todayDate);
        prevWeekBtn.setEnabled(false);

        nextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFilter.moveToNextWeek();
                filter();
            }
        });

        prevWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFilter.moveToPreviousWeek();
                filter();
            }
        });

        loadEventsFromFirebase();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();
        String user = userEmail.substring(0, userEmail.indexOf('@'));
        favoriteEventsFilter = new MyEventsFilter(user);
        searchFilter = new SearchMultiFieldFilter("");

        qrImage = (ImageView) findViewById(R.id.idQRImage);
        qrgEncoder = new QRGEncoder(user, null, QRGContents.Type.TEXT, qrImage.getWidth());
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e){
            Log.d("QR Code Generator", e.toString());
        }
        qrImage.setImageBitmap(bitmap);

        Log.d("FindEvents", "OnCreate finished");
    }

    private void loadEventsFromFirebase() {
        Query query = database.child("/current-events").orderByChild("startDate");
        // TODO: easy way out, reset all events whenever any event changes
        //  (but that can make the user's list refresh unexpectedly?)
        //  (harder way: listen for child changes, and make incremental changes to
        //    update the adapter appropriately...)
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Event e = child.getValue(Event.class);
                    e.setEventID(child.getKey());
                    events.add(e);
                }
                progressBar.setVisibility(View.GONE);
                filter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filter() {
        filteredEvents.clear();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (dateFilter.applyFilter(event) && favoriteEventsFilter.applyFilter(event) && searchFilter.applyFilter(event)) {
                filteredEvents.add(event);
            }
        }
        prevWeekBtn.setEnabled(!dateFilter.isFilteringCurrentWeek());
        currentWeekLabel.setText(dateFilter.getCurrentWeekLabel());
        eventsView.setAdapter(new EventRecyclerAdapter(filteredEvents, FindEvents.this));
    }

    public void moveToSearch() {
        settingsView.setVisibility(View.GONE);
        //eventslv.setVisibility(View.VISIBLE);
        //searchBar.setVisibility(View.VISIBLE);
        eventsView.setVisibility(View.VISIBLE);
        dateToolbar.setVisibility(View.VISIBLE);
        filter();
    }

    // Source: https://stackoverflow.com/questions/42275906/how-to-ask-runtime-permissions-for-camera
    public void moveToQR() {
        if (checkPermission()) {
            startActivityForResult(new Intent(FindEvents.this, QrCodeScanner.class), QRCODE);
        } else {
            requestPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QRCODE) {
            navigation.setSelectedItemId(R.id.navigation_all_events);
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
                    searchFilter = new SearchMultiFieldFilter(query);
                    searchFilter.setEnabled(true);
                    dateFilter.setEnabled(false);
                    dateToolbar.setVisibility(View.GONE);
                    filter();
                }
                return false;
            }
        });

        //resets the events view if they close out of the search bar
        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchFilter.setEnabled(false);
                if (!favoriteEventsFilter.isEnabled()) {
                    dateFilter.setEnabled(true);
                    dateToolbar.setVisibility(View.VISIBLE);
                }
                filter();
                return false;
            }
        });
        
        return true;
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
                //startThemeListener();
                return true;
        }
        return true;
    }

    public void signOutbtn(View v){
        finish();
    }

    public void aboutBtn(View v) {
        Log.d("ABOUT", "Running");
        Intent intent = new Intent(FindEvents.this, AboutPageActivity.class);
        startActivityForResult(intent, 4);
    }

    public void startThemeListener(){
        //radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
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
               /*if(checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme1).getId()) {
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
                theme5.setTextColor(Theme.getTextColor());*/
                BottomNavigationView navigation = findViewById(R.id.navigation);
                navigation.setBackgroundColor(Theme.getButtonColor());

                //ListView listOfEvents = findViewById(R.id.listViewEvents);

                //constraintLayout.setBackgroundColor(Theme.getBackgroundColor());
                relative_layout.setBackgroundColor(Theme.getBackgroundColor());
                //relLayout.setBackgroundColor(Theme.getBackgroundColor());

            }
        });
    }


}
