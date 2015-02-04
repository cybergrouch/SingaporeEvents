package com.paypal.first.singaporeevents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.common.base.Predicate;

import java.util.List;

import javax.annotation.Nullable;


public class EventsListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = EventsListActivity.class.getName();

    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        final ListView eventListView = (ListView) findViewById(R.id.listView);
        eventListView.setOnItemClickListener(this);

        eventList = EventUtility.querySingaporeEvents(new Predicate<List<Event>>() {
            @Override
            public boolean apply(@Nullable List<Event> eventsList) {
                EventListAdapter adapter = new EventListAdapter(EventsListActivity.this, R.layout.event_layout, eventsList);
                eventListView.setAdapter(adapter);
                return Boolean.TRUE;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event event = eventList.get(position);
        if (event.address.isPresent()) {
            Intent intent = new Intent(this, EventMapActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        }
    }
}
