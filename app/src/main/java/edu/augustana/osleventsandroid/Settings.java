package edu.augustana.osleventsandroid;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.osleventsandroid.R;

public class Settings extends AppCompatActivity {

    private RelativeLayout settingsView;
    private RelativeLayout relative_layout;
    private RadioGroup radioGroup;
    private RelativeLayout relLayout;
    private RadioButton checkedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingsView=(RelativeLayout) findViewById(R.id.settingsView2);
        startThemeListener();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void signOutbtn2(View v){

    }

    public void startThemeListener(){
        try {
            radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
            checkedRadioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            relative_layout = findViewById(R.id.settingsView2);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                    RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                    // This will get the radiobutton that has changed in its check state
                    // This puts the value (true/false) into the variable
                    if (checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme12).getId()) {
                        // If the radiobutton that has changed in check state is now checked...
                        Theme.whiteTheme();
                    } else if (checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme22).getId()) {
                        Theme.darkTheme();
                    } else if (checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme32).getId()) {
                        Theme.seaBlueTheme();
                    } else if (checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme42).getId()) {
                        Theme.twilightPurpleTheme();
                    } else if (checkedRadioButton.getId() == radioGroup.findViewById(R.id.theme52).getId()) {
                        Theme.augieTheme();
                    }
                    //ConstraintLayout constraintLayout = findViewById(R.id.container);
                    TextView themeTitle = findViewById(R.id.themeTitle2);
                    themeTitle.setTextColor(Theme.getTextColor());
                    TextView settingsTitle = findViewById(R.id.settingsTitle2);
                    settingsTitle.setTextColor(Theme.getTextColor());
                    Button signOutBttn = findViewById(R.id.signOutBttn2);
                    signOutBttn.setBackgroundColor(Theme.getButtonColor());
                    RadioButton theme1 = findViewById(R.id.theme12);
                    theme1.setTextColor(Theme.getTextColor());

                    RadioButton theme2 = findViewById(R.id.theme22);
                    theme2.setTextColor(Theme.getTextColor());
                    RadioButton theme3 = findViewById(R.id.theme32);
                    theme3.setTextColor(Theme.getTextColor());
                    RadioButton theme4 = findViewById(R.id.theme42);
                    theme4.setTextColor(Theme.getTextColor());
                    RadioButton theme5 = findViewById(R.id.theme52);
                    theme5.setTextColor(Theme.getTextColor());

                    //constraintLayout.setBackgroundColor(Theme.getBackgroundColor());
                    //relative_layout.setBackgroundColor(Theme.getBackgroundColor());

                }
            });
        } catch(Exception e){
            Log.d("Settings", "Error: " + e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return true; }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
