package com.paypal.first.singaporeevents;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

/**
 * Created by wpantoja on 2/4/15.
 */
public class EventUtility {

    private static final String URL = "https://www.eventbrite.sg/d/singapore--singapore/events/";

    private static final String TAG = EventUtility.class.getName();

    public static void querySingaporeEvents(final Predicate<List<Event>> callback) {
        createAsyncTask(callback).execute();
    }

    private static AsyncTask<Void, Void, List<Event>> createAsyncTask(final Predicate<List<Event>> callback) {
        return new AsyncTask<Void, Void, List<Event>>() {
            @Override
            protected List<Event> doInBackground(Void... params) {
                Document doc;
                try {
                    doc = Jsoup.connect(URL).get();
                } catch (IOException e) {
                    Log.w(TAG, "Unable to connect to backend", e);
                    return Collections.emptyList();
                }

                Elements elements = doc.getElementsByAttributeValue("itemtype", "http://data-vocabulary.org/Event");

                return Lists.newArrayList(Iterators.transform(elements.iterator(), new Function<Element, Event>() {
                    @Nullable
                    @Override
                    public Event apply(@Nullable Element element) {
                        Element event = element.select("a div.event-card__description").get(0);
                        String name = event.select("h3").get(0).text();
                        String dateTime = event.select("ul li span[itemprop=startDate]").get(0).attr("datetime");
                        String location = event.select("ul li span[itemprop=location]").get(0).text();
                        return Event.createEvent(name, dateTime, location);
                    }
                }));
            }

            @Override
            protected void onPostExecute(List<Event> events) {
                super.onPostExecute(events);
                callback.apply(events);
            }
        };
    }
}
