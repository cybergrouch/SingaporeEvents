package com.paypal.first.singaporeevents;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.plus.model.people.Person;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class EventsListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        EventUtility.querySingaporeEvents(new Predicate<List<Event>>() {
            @Override
            public boolean apply(@Nullable List<Event> eventsList) {
                ListView eventListView = (ListView) findViewById(R.id.listView);
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
}
