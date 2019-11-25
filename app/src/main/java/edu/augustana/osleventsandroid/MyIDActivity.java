package edu.augustana.osleventsandroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.osleventsandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MyIDActivity extends AppCompatActivity {

    private ImageView qrImage;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private BottomNavigationView navigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myid);

        //Gets username of the user, by accessing the user's augie email and cutting it off before '@'
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();
        final String user = userEmail.substring(0, userEmail.indexOf('@'));

        qrImage = (ImageView) findViewById(R.id.idQRImage);
        qrgEncoder = new QRGEncoder(user, null, QRGContents.Type.TEXT, qrImage.getWidth());
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e){
            Log.d("QR Code Generator", e.toString());
        }
        qrImage.setImageBitmap(bitmap);
    }
}
