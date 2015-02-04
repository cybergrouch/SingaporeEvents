package com.paypal.first.singaporeevents;

import android.util.Log;

import com.google.common.base.Optional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wpantoja on 2/4/15.
 */
public class Event {
    Optional<String> name;
    Optional<Date> dateTime;
    Optional<String> location;

    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final String TAG = Event.class.getName();

    private Event() {
        super();
    }

    public String toString() {
        String format = "Event:{\n\tname:%s\n\tdateTime:%s\n\tlocation:%s\n}";
        return String.format(format, name, dateTime, location);
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

    public static Event createEvent(String eventName, String dateTime, String location) {
        Event event = new Event();
        event.name = Optional.fromNullable(eventName);
        event.dateTime = parseDate(dateTime);
        event.location = Optional.fromNullable(location);
        return event;
    }
}
