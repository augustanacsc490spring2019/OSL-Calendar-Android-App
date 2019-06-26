package edu.augustana.osleventsandroid;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.osleventsandroid.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class TestFirebaseUIActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    ScaleAnimation shrinkAnim;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private TextView tvNoMovies;

    //Get reference to database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = database.getReference();

    private static final String userID = "53";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_firebaseui);

        //Initialize RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        tvNoMovies = (TextView) findViewById(R.id.tv_no_movies);

        //scale animation to shrink floating action bar
        shrinkAnim = new ScaleAnimation(1.15f, 0f, 1.5f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        if (mRecyclerView != null) {
            //to enable optimization of Recycler View
            mRecyclerView.setHasFixedSize(true);
        }
        //Using staggered grid patter in Recycler View
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager linearManager = new LinearLayoutManager(TestFirebaseUIActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearManager);

        Log.d("TestFirebaseUIActivity", "Adapter created");
        mRecyclerView.setAdapter(createAdapter(1));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int numItems = mRecyclerView.getAdapter().getItemCount();
                mRecyclerView.setAdapter(createAdapter(numItems + 1));
                mRecyclerView.scrollToPosition(numItems - 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fab.getVisibility() == View.GONE) {
            //fab.setVisibility(View.VISIBLE);
        }
    }



    private FirebaseRecyclerAdapter createAdapter(int numItems) {
        Query query = mDatabaseReference.child("current-events").orderByChild("startDate").limitToFirst(numItems);
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();
        //Say Hello to our new FirebaseUI android Element, i.e., FirebaseRecyclerAdapter
        FirebaseRecyclerAdapter<Event, EventViewHolder> adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options){
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.custom_listview_event_layout, viewGroup, false);

                return new EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final EventViewHolder viewHolder, int position, @NonNull Event event) {
                viewHolder.txtTitle.setText(event.getName());
                viewHolder.txtLocation.setText(event.getLocation());
                viewHolder.txtDate.setText(event.getStartDate());
                viewHolder.txtDuration.setText(event.getStartTimeText() + "-" + event.getEndTimeText());
                viewHolder.txtOrganization.setText(event.getOrganization());
                if (viewHolder.imgURL == null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Images").child(event.getImgid()+".jpg");
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            viewHolder.imgURL = uri.toString();
                            Picasso.with(TestFirebaseUIActivity.this).load(uri.toString()).into(viewHolder.img);
                            Log.d("TestFirebaseUIActivity", "Loading Image for: " + viewHolder.txtTitle.getText() + "\nURI: " + uri.toString());
                        }
                    });
                } else {
                    Picasso.with(TestFirebaseUIActivity.this).load(viewHolder.imgURL).into(viewHolder.img);
                }
            }


        };

        adapter.startListening();
        return adapter;
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtLocation;
        TextView txtDate;
        TextView txtDuration;
        TextView txtOrganization;
        ImageView img;
        String imgURL;
        RelativeLayout relLayout;

        public EventViewHolder(View v) {
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
