package edu.augustana.osleventsandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osleventsandroid.R;

import org.w3c.dom.Text;

public class SingleEventPage extends AppCompatActivity {
    private TextView txtLocation;
    private TextView txtDateTime;
    private TextView txtType;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event_page);
        this.txtLocation=(TextView) findViewById(R.id.txt_location);
        this.txtDateTime=(TextView)  findViewById(R.id.txt_datetime);
        this.txtType=(TextView) findViewById(R.id.txt_eventype);
        this.img=(ImageView) findViewById(R.id.img);
        Event event = (Event) getIntent().getSerializableExtra("choosenEvent");
        txtLocation.setText(event.getLocation());
        txtDateTime.setText(event.getFormatedDate()+" "+event.getFormatedTime());
        txtType.setText(event.getType());
        img.setImageResource(event.getImgid());
    }

    public void notifyBtnAction(View v){

    }

    public void addToCalBtnAction(View v){

    }
}
