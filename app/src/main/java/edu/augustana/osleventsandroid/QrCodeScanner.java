package edu.augustana.osleventsandroid;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

// Source: https://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application

/**
 * Class for scanning QR code and get code result
 */
public class QrCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    //Data fields
    private ZXingScannerView mScannerView;
    private String eventTitle;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        // Set the scanner view as the content view
        setContentView(mScannerView);
        //for displaying back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * Code for back button
     * @param item MenuItem
     * @return boolean
     */
    //https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * For back button
     * @param menu MenuItem
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Starts camera for scanning
     */
    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    /**
     * Pauses camera
     */
    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    /**
     * Action once qr code has been scanned
     * @param rawResult the result data from the QR code
     */
    @Override
    public void handleResult(Result rawResult) {
        final Result result=rawResult;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String userEmail = mAuth.getCurrentUser().getEmail();
        final String user = userEmail.substring(0, userEmail.indexOf('@'));

        // currently the QR code contains a whole URL of the form:
        //    https://osl-events-app.firebaseapp.com/event?id=-Lfzqf1JrBve4PYB9m0Y&name=Team+C+Event
        // we just want to extract the event ID out of it.
        String url = result.getText();
        final String eventID = url.substring(url.lastIndexOf("id=") + 3, url.lastIndexOf("&name="));

        // below we read the official event name from the database,
        // and if that's successful, then we add the user to the list of attendees in the DB
        final DatabaseReference eventDBRef = FirebaseDatabase.getInstance().getReference("current-events/" + eventID);
        eventDBRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final String eventTitle = (String) snapshot.getValue();
                eventDBRef.child("users").child(user).setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                displayConfirmationDialog(user, eventTitle, true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("QRCodeScanner", "Database Write Error: " + e);
                                displayConfirmationDialog(user, eventTitle, false);
                            }
                        });


                Log.d("QRCodeScanner", "snapshot" + eventTitle);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("QRCodeScanner", "Database Read Error: " + databaseError);
                displayConfirmationDialog(user, eventTitle, false);
            }
        });




        // TODO: instead of opening web browser, we want to:
        //  a) extract the ID from the url
        //  b) create a new entry in firebase at /current-events/EVENTID/users/emailstart
        //  c) on successful completion of part b, pull the name of the event FROM firebase,
        //     and tell the user they were checked into event named X.
        //  d) go back to the main activity?


    }

    private void displayConfirmationDialog(String userName, String eventTitle, boolean successful) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        String title;
        String message;
        if (successful) {
            title = "Checked In";
            message = "You have checked into " + eventTitle + " as " + userName;
        } else {
            title = "Check In Failed";
            message = "You were unable to check into " + eventTitle + ". Check your internet connection and try again.";
        }
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(QrCodeScanner.this, FindEvents.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
