package dev.tw1t1.a10234_final_project_m;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationReceiver implements OnMapReadyCallback {
    private DatabaseReference databaseRef;
    private GoogleMap mMap;
    private LocationData lastLocation;

    public void startListeningForLocationUpdates() {
        databaseRef = FirebaseDatabase.getInstance().getReference("locations").child("device1");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationData locationData = dataSnapshot.getValue(LocationData.class);
                if (locationData != null) {
                    updateUI(locationData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (lastLocation != null) {
            updateMapLocation(lastLocation);
        }
    }

    private void updateUI(LocationData locationData) {
        lastLocation = locationData;
        if (mMap != null) {
            updateMapLocation(locationData);
        }
    }

    private void updateMapLocation(LocationData locationData) {
        LatLng location = new LatLng(locationData.latitude, locationData.longitude);
        mMap.clear(); // Clear previous markers
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("device1")
                .snippet("Latest location");

        final Marker marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15)); // Zoom level 15
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Show info window (which displays the title) by default
        mMap.animateCamera(CameraUpdateFactory.newLatLng(location), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if (marker != null) {
                    marker.showInfoWindow();
                }
            }

            @Override
            public void onCancel() {
                // Handle cancellation if needed
            }
        });
    }

    public void setMap(GoogleMap map) {
        this.mMap = map;
        if (lastLocation != null) {
            updateMapLocation(lastLocation);
        }
    }

}