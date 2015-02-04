package com.paypal.first.singaporeevents;

import android.util.Log;
import android.util.Pair;

import com.google.common.base.Optional;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wpantoja on 2/4/15.
 */
public class Event implements Serializable {
    Optional<String> name = Optional.absent();
    Optional<Date> dateTime = Optional.absent();
    Optional<String> address = Optional.absent();
    Optional<Pair<Double, Double>> location = Optional.absent();

    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final String TAG = Event.class.getName();

    private Event() {
        super();
    }

    public String toString() {
        String format = "Event:{\n\tname:%s\n\tdateTime:%s\n\taddress:%s\n\tlocation:%s\n}";
        return String.format(format, name.or("null"), dateTime.orNull(), address.or("null"), location.orNull());
    }

    private static Optional<Date> parseDate(String dateTime) {
        Optional<Date> date = Optional.absent();
        try {
            date = Optional.fromNullable(DATE_TIME_FORMAT.parse(dateTime));
        } catch (ParseException e) {
            Log.w(TAG, String.format("Unable to parse dateTime parameter: %s", dateTime), e);
        }
        return date;
    }

    public static Event createEvent(String eventName, String dateTime, String address) {
        Event event = new Event();
        event.name = Optional.fromNullable(eventName);
        event.dateTime = parseDate(dateTime);
        event.address = Optional.fromNullable(address);
        return event;
    }
}
