package com.paypal.first.singaporeevents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * Created by wpantoja on 2/4/15.
 */
public class EventMapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Event event = (Event) bundle.getSerializable("event");
        EventUtility.queryEventLocation(event, new Predicate<Optional<Event>>() {
            @Override
            public boolean apply(@Nullable Optional<Event> eventOptional) {
                if (eventOptional.isPresent() && eventOptional.get().location.isPresent()) {
                    Event event = eventOptional.get();
                    Pair<Double, Double> location = event.location.get();

                    mMap.setBuildingsEnabled(true);
                    mMap.setIndoorEnabled(true);
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    LatLng markerLoc = new LatLng(location.first, location.second);
                    final CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(markerLoc)
                            .zoom(14)                   // Sets the zoom
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.addMarker(new MarkerOptions().position(markerLoc).title(event.name.or("Unnamed Event")).snippet(event.address.or("Unknown Address")));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        });
    }
}
