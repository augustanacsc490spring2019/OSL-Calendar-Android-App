package edu.augustana.osleventsandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.osleventsandroid.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.EventViewHolder>{
    List<Event> events;
    Context parentActivity;

    public EventRecyclerAdapter(List<Event> events, Context parentActivity) {
        this.events = new ArrayList<>(events);
        this.parentActivity = parentActivity;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_listview_event_layout, viewGroup, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder viewHolder, int position) {
        final Event event = events.get(position);
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
                    Picasso.with(parentActivity).load(uri.toString()).into(viewHolder.img,
                            new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    viewHolder.relLayout.invalidate();
                                }

                                @Override
                                public void onError() {

                                }
                            });

                    Log.d("FindEvents", "Loading Image for: " + viewHolder.txtTitle.getText() + "\nURI: " + uri.toString());
                }
            });
        } else {
            Picasso.with(parentActivity).load(viewHolder.imgURL).into(viewHolder.img);
        }
        viewHolder.relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentActivity, SingleEventPage.class);
                intent.putExtra("chosenEvent", event);
                parentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
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
