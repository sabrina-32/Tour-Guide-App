package com.example.dell.tourguide2;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    Button Post;

    String UserLocation, SearachLocation, type, area,radius;
    private GoogleMap mMap;
    ArrayList<PostingSupport> UserArrayList = new ArrayList<>();
    DatabaseReference mDatabase;
    private Address hostAddress;
    public static List<PostingSupport> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Post = findViewById(R.id.goto_recycleView);
        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this,RecyclerViewActivity.class));
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        UserLocation = getIntent().getStringExtra("place").toString().trim();
        type = getIntent().getStringExtra("type").toString().trim();

       // if(radius.isEmpty())
            radius = "10";
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d("Search_Tag", type);
        Log.d("Search_Tag", UserLocation);
        Log.d("Search_Tag", type);
        Log.d("Search_Tag", type);

        mDatabase.child(type).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PostingSupport gpi = dataSnapshot.getValue(PostingSupport.class);

                assert  gpi!=null;

                Geocoder geocoder = new Geocoder(MapActivity.this);
                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();
                SearachLocation = gpi.getArea();

                try {
                    addressList = geocoder.getFromLocationName(SearachLocation, 6);
                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            Address userAddress = addressList.get(i);

                            LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                            float results[] = new float[100];
                            Location.distanceBetween(userAddress.getLatitude(), userAddress.getLongitude(), hostAddress.getLatitude(), hostAddress.getLongitude(), results);
                            /*
                            markerOptions.position(latLng);
                            markerOptions.title(type);
                            markerOptions.snippet(Integer.toString(fee));

                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(Search_trial.this);
                            mMap.setInfoWindowAdapter(customInfoWindow);
                            Marker m = mMap.addMarker(markerOptions);
                            m.setTag(rent_add);
                            m.showInfoWindow();

                            */
                            if (results[0] / 1000 <= Float.valueOf(radius)) {
                                arrayList.add(gpi);
                                markerOptions.position(latLng);
                                markerOptions.title(type);
                                markerOptions.snippet(gpi.getArea());
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(MapActivity.this);

                                mMap.setInfoWindowAdapter(customInfoWindow);
                                Marker m = mMap.addMarker(markerOptions);
                                m.setTag(gpi);
                                m.showInfoWindow();
                            }

                        }

                    }

                } catch (Exception e) {
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = null;
        MarkerOptions markerOptions = new MarkerOptions();
        CircleOptions circleOptions = new CircleOptions();
        mMap = googleMap;
        try {
            addressList = geocoder.getFromLocationName(UserLocation, 10);

            if (addressList != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    Address userAddress = addressList.get(i);
                    hostAddress = userAddress;
                    LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                    markerOptions.position(latLng);
                    markerOptions.title(UserLocation);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                    circleOptions.center(latLng);
                    circleOptions.radius(Integer.valueOf(radius)*1000);
                    circleOptions.strokeColor(Color.CYAN);
                    circleOptions.fillColor(0x4D000080);

                    // mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    mMap.addCircle(circleOptions);


                }

            }

        } catch (Exception e) {

        }

    }

}