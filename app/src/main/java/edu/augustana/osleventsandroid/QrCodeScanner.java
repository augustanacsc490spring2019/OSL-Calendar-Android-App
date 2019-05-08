package edu.augustana.osleventsandroid;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

// Source: https://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application

/**
 * Class for scanning QR code and get code result
 */
public class QrCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    //Data fields
    private ZXingScannerView mScannerView;

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
     * @param rawResult the result code from the QR code
     */
    @Override
    public void handleResult(Result rawResult) {

       final Result result=rawResult;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Open URL");
        builder.setMessage("Check into the event? You will be redirected to URL.");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("QR Code", result.getText());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onResume();
            }

        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}
