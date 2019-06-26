package edu.augustana.osleventsandroid;

import android.Manifest;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import java.io.ByteArrayOutputStream;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osleventsandroid.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class FindEvents extends AppCompatActivity {

    final int QRCODE = 0;
    final int SETTINGSCODE = 3;
    private TextView mTextMessage;
    private ListView eventslv;
    private RelativeLayout settingsView;
    private BottomNavigationView navigation;
    private ArrayList<Event> events;
    private ArrayList<Event> displayedEvents;
    private CustomLVAdapter customLVAdapter;
    private DatabaseReference database;
    private RelativeLayout progressBar;
    private MenuItem item;
    private SearchView searchBar;
    private RadioButton checkedRadioButton;
    private LinearLayout linear_layout;
    private RelativeLayout relative_layout;
    private RelativeLayout relLayout;
    private RadioGroup radioGroup;
    private int selectedSort;

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
        progressBar.setVisibility(View.VISIBLE);
        database=FirebaseDatabase.getInstance().getReference("current-events");
        eventslv=(ListView) findViewById(R.id.listViewEvents);
        settingsView=(RelativeLayout) findViewById(R.id.settingsView);
        events=new ArrayList<Event>();
        selectedSort=0;
        displayedEvents=new ArrayList<Event>();
        customLVAdapter=new CustomLVAdapter(FindEvents.this, displayedEvents);
        databaseListener();
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
        eventsView.setLayoutManager(gridLayoutManager);
    }

    public void moveToSearch() {
        settingsView.setVisibility(View.GONE);
        eventslv.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.VISIBLE);
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
        searchBar.setVisibility(View.GONE);
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
        if (requestCode == SETTINGSCODE) {
            navigation.setSelectedItemId(R.id.navigation_search);
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

    public void databaseListener(){
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    final Event event = snapshot.getValue(Event.class);

                    StorageReference storage = FirebaseStorage.getInstance().getReference().child("Images").child(event.getImgid()+".jpg");
                    final long ONE_MEGABYTE = Integer.MAX_VALUE;
                    storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            event.setImgBytes(bytes);
                            events.add(event);
                            Collections.sort(events, new DateSorter());
                            displayedEvents.clear();
                            displayedEvents.addAll(events);
                            customLVAdapter=new CustomLVAdapter(FindEvents.this, displayedEvents);
                            eventslv.setAdapter(customLVAdapter);
                            progressBar.setVisibility(View.GONE);
                            eventslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView adapter, View v, int position, long arg3) {
                                    Event chosenEvent = displayedEvents.get(position);
                                    Intent intent = new Intent(FindEvents.this, SingleEventPage.class);
                                    byte[] img=chosenEvent.getImgBytes();
                                    while(img.length>(1000*1000)){

                                        // PNG has not losses, it just ignores this field when compressing
                                        final int COMPRESS_QUALITY = 0;

                                        // Get the bitmap from byte array since, the bitmap has the the resize function
                                        Bitmap bitmapImage = (BitmapFactory.decodeByteArray(img, 0, img.length));


                                        // New bitmap with the correct size, may not return a null object
                                        Bitmap mutableBitmapImage = Bitmap.createScaledBitmap(bitmapImage,bitmapImage.getWidth()/2, bitmapImage.getHeight()/2, false);

                                        // Get the byte array from tbe bitmap to be returned
                                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                        mutableBitmapImage.compress(Bitmap.CompressFormat.PNG, 0 , outputStream);

                                        if (mutableBitmapImage != bitmapImage) {
                                            mutableBitmapImage.recycle();
                                        } // else they are the same, just recycle once

                                        bitmapImage.recycle();
                                        chosenEvent.setImgBytes(outputStream.toByteArray());
                                        img=chosenEvent.getImgBytes();
                                        System.out.println(img.length);
                                    }
                                        intent.putExtra("choosenEvent", chosenEvent);
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
               displayedEvents.clear();
                for(int i = 0; i< events.size();i++){
                    Event currentEvent = events.get(i);
                    String lowerCaseQuery = query.toLowerCase();
                    if(currentEvent.getName().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getLocation().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getTags().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getOrganization().toLowerCase().contains(lowerCaseQuery)) {
                        displayedEvents.add(events.get(i));
                    }
                }
                sorting();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {

               displayedEvents.clear();
                for(int i = 0; i< events.size();i++){
                    Event currentEvent = events.get(i);
                    String lowerCaseQuery = query.toLowerCase();
                    if(currentEvent.getName().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getLocation().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getTags().toLowerCase().contains(lowerCaseQuery) ||
                            currentEvent.getOrganization().toLowerCase().contains(lowerCaseQuery)) {
                        displayedEvents.add(events.get(i));
                    }
                }
                System.out.println("\n\n/n/n/n/n/n/");
                System.out.println(displayedEvents);
                sorting();
                return false;
            }
        });

        //resets the events view if they close out of the search bar
        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                displayedEvents.clear();
                displayedEvents.addAll(events);
                sorting();
                return false;
            }
        });
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                eventslv.setVisibility(View.GONE);
                settingsView.setVisibility(View.VISIBLE);
                searchBar.setVisibility(View.GONE);
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
               ConstraintLayout constraintLayout = findViewById(R.id.container);
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

                ListView listOfEvents = findViewById(R.id.listViewEvents);

                constraintLayout.setBackgroundColor(Theme.getBackgroundColor());
                relative_layout.setBackgroundColor(Theme.getBackgroundColor());
                //relLayout.setBackgroundColor(Theme.getBackgroundColor());

            }
        });
    }

    public void sorting() {
        if (selectedSort == 1) {
            Collections.sort(displayedEvents);
            customLVAdapter = new CustomLVAdapter(FindEvents.this, displayedEvents);
            eventslv.setAdapter(customLVAdapter);
        } else if (selectedSort == 2) {
            Collections.sort(displayedEvents, Collections.<Event>reverseOrder());
            customLVAdapter = new CustomLVAdapter(FindEvents.this, displayedEvents);
            eventslv.setAdapter(customLVAdapter);
        } else if (selectedSort == 0) {
            Collections.sort(displayedEvents, new DateSorter());
            customLVAdapter = new CustomLVAdapter(FindEvents.this, displayedEvents);
            eventslv.setAdapter(customLVAdapter);
        } else if (selectedSort == 3) {
            Collections.sort(displayedEvents, new GroupSorter());
            customLVAdapter = new CustomLVAdapter(FindEvents.this, displayedEvents);
            eventslv.setAdapter(customLVAdapter);
        }
    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtLocation;
        TextView txtDate;
        TextView txtDuration;
        TextView txtOrganization;
        ImageView img;
        RelativeLayout relLayout;

        public EventsViewHolder(View v) {
            super(v);
            this.txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            this.txtLocation = (TextView) v.findViewById(R.id.txtLocation);
            this.txtDate = (TextView) v.findViewById(R.id.txtDate);
            this.txtDuration = (TextView) v.findViewById(R.id.txtDuration);
            this.txtOrganization = (TextView) v.findViewById(R.id.txtOrganization);
            this.img = (ImageView) v.findViewById(R.id.eventImg);
            this.relLayout = (RelativeLayout) v.findViewById(R.id.relLayout);
        }
    }
}
