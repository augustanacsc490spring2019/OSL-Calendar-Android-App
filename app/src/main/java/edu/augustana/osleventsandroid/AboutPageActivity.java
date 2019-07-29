package edu.augustana.osleventsandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.osleventsandroid.R;

public class AboutPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        //Link for phone number
        TextView phoneNumber = (TextView) findViewById(R.id.phone_text);
        Linkify.addLinks(phoneNumber, Linkify.ALL);

        Button backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(back);
    }

    private Button.OnClickListener back = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
