package com.paypal.first.singaporeevents;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by wpantoja on 2/4/15.
 */
public class EventUtility {

    private static final String URL = "https://www.eventbrite.sg/d/singapore--singapore/events/";

    private static final String TAG = EventUtility.class.getName();

    public static List<Event> querySingaporeEvents(final Predicate<List<Event>> callback) {
        try {
            return createAsyncTask(callback).execute().get();
        } catch (Exception e) {
            Log.e(TAG, "Error querying Singapore Events", e);
            return Collections.emptyList();
        }
    }

    public static Optional<Event> queryEventLocation(final Event event, final Predicate<Optional<Event>> callback) {
        try {
            return createLocationQueryTask(callback).execute(event).get();
        } catch (Exception e) {
            Log.e(TAG, "Error querying location for Singapore Event", e);
            return Optional.absent();
        }
    }

    public static AsyncTask<Event, Void, Optional<Event>> createLocationQueryTask(final Predicate<Optional<Event>> callback) {

        return new AsyncTask<Event, Void, Optional<Event>>() {

            private static final String REQUEST_FORMAT = "http://maps.google.com.sg/maps/api/geocode/json?address=%s&sensor=false";

            @Override
            protected Optional<Event> doInBackground(Event... events) {
                // write code to do network call
                Event event = events[0];

                if (event.location.isPresent()) {
                    return Optional.fromNullable(event);
                }

                StringBuffer response = new StringBuffer();


                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = null;
                try {
                    httpGet = new HttpGet(String.format(REQUEST_FORMAT, URLEncoder.encode(event.address.get(), "UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Error encoding URL parameter");
                }

                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response.append(s);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error on executing remote call", e);
                }


                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray resArray = (JSONArray) jsonObject.get("results");
                    JSONObject geometry = (JSONObject) ((JSONObject) resArray.get(0)).get("geometry");
                    JSONObject location = (JSONObject) geometry.get("location");
                    double latitude = location.getDouble("lat");
                    double longitude = location.getDouble("lng");
                    Pair<Double, Double> locationPair = Pair.create(latitude, longitude);
                    event.location = Optional.fromNullable(locationPair);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing result", e);
                }

                return Optional.fromNullable(event);
            }

            @Override
            protected void onPostExecute(Optional<Event> event) {
                super.onPostExecute(event);
                callback.apply(event);
            }
        };
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
