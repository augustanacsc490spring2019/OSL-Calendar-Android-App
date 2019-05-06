package edu.augustana.osleventsandroid;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osleventsandroid.R;

import java.util.ArrayList;

/*
    A custom ListView adapter to display the events in the particular ways we need it to
 */
// from https://www.youtube.com/watch?v=_YF6ocdPaBg
public class CustomLVAdapter extends ArrayAdapter {
    private ArrayList<Event> events;
    private Activity context;

    public CustomLVAdapter(Activity context, ArrayList<Event> events) {
        super(context, R.layout.custom_listview_event_layout, events);
        this.context = context;
        this.events = new ArrayList<Event>(events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView = convertView;
        ViewHolder viewHolder = null;
        if (newView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            newView = layoutInflater.inflate(R.layout.custom_listview_event_layout, null, true);
            viewHolder = new ViewHolder(newView);
            newView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) newView.getTag();
        }
        Event event = events.get(position);
        viewHolder.img.setImageBitmap(BitmapFactory.decodeByteArray(event.getImg(), 0, event.getImg().length));
        viewHolder.txtTitle.setText(event.getName());
        viewHolder.txtLocation.setText(event.getLocation());
        viewHolder.txtDate.setText(event.getStartDate());
        viewHolder.txtDuration.setText(event.getStartTime() + "-" + event.getEndTime());
        viewHolder.txtOrganization.setText(event.getGroup());
        return newView;
    }

    class ViewHolder {
        TextView txtTitle;
        TextView txtLocation;
        TextView txtDate;
        TextView txtDuration;
        TextView txtOrganization;
        ImageView img;

        ViewHolder(View v) {
            this.txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            this.txtLocation = (TextView) v.findViewById(R.id.txtLocation);
            this.txtDate = (TextView) v.findViewById(R.id.txtDate);
            this.txtDuration = (TextView) v.findViewById(R.id.txtDuration);
            this.txtOrganization = (TextView) v.findViewById(R.id.txtOrganization);
            this.img = (ImageView) v.findViewById(R.id.eventImg);

        }
    }
}
