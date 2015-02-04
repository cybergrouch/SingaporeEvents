package com.paypal.first.singaporeevents;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wpantoja on 2/4/15.
 */
public class EventListAdapter extends ArrayAdapter<Event> {

    List<Event> events = null;
    int layoutResource = 0;
    Context context;

    public EventListAdapter(Context context, int resource, List<Event> events) {
        super(context, resource, events);
        this.events = events;
        this.layoutResource = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResource, null);
        }
        Event event = events.get(position);
        TextView eventNameView = (TextView) view.findViewById(R.id.eventName);
        TextView eventAddressView = (TextView) view.findViewById(R.id.eventAddress);
        TextView eventDateTime = (TextView) view.findViewById(R.id.eventDate);

        eventNameView.setText(event.name.or(""));

        eventAddressView.setText(event.address.or(""));

        if (!event.dateTime.isPresent()) {
            eventDateTime.setText("");
        } else {
            eventDateTime.setText(String.format("%s %s",
                    DateFormat.getDateFormat(context).format(event.dateTime.get()),
                    DateFormat.getTimeFormat(context).format(event.dateTime.get())));
        }
        return view;
    }
}
