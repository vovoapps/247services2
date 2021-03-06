package com.dcservicez.a247services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.dcservicez.a247services.Prefs.Prefs;
import com.dcservicez.a247services.debugs.Debug;
import com.dcservicez.a247services.objects.MyLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.IOException;

public class search_service extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Debug debug;
    Prefs prefs;
    String service_type;
    private GoogleApiClient googleApiClient;
    boolean isSP=false;

    boolean isMapRead=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_service);
        Log.e("GMAP","activity created");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Log.e("GMAP","mpa fragment set");
        mapFragment.getMapAsync(this);

        debug=new Debug(this);
        prefs=new Prefs(this);
        Log.e("GMAP","prefs set");

        if(getIntent()!=null){
            service_type=getIntent().getExtras().getString("service_type");
            if(service_type==null){
                isSP=true;
                String id=getIntent().getExtras().getString("user_id");
                debug.print(id);
                FirebaseDatabase.getInstance().getReference("Users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        final double latitude=Double.parseDouble(dataSnapshot.child("location").child("latitude").getValue().toString());
                        final double longtidue=Double.parseDouble(dataSnapshot.child("location").child("longitude").getValue().toString());
                        if(isMapRead){
                            debug.print(longtidue+"map is ready"+latitude);
                            Transformation transformation = new RoundedTransformationBuilder()
                                    .borderColor(Color.parseColor("#6776c0ff"))
                                    .borderWidthDp(10)
                                    .cornerRadiusDp(30)
                                    .oval(false)
                                    .build();







                            Picasso.get().load(dataSnapshot.child("profile_url").getValue().toString()).resize(110,110).centerCrop().transform(transformation).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                                  mMap.clear();
                                    mMap.addMarker(new MarkerOptions().icon(icon).title(dataSnapshot.getKey().toString()).position(new LatLng(latitude,longtidue)));

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtidue), 11));
                                    Log.e("GMAP", "animate camera");
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(new LatLng(latitude, longtidue))      // Sets the center of the map to location user
                                            .zoom(10)                   // Sets the zoom
                                            .build();                   // Creates a CameraPosition from the builder
                                    Log.e("GMAP", "animate pos");
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


        }



    }

    @SuppressLint("MissingPermission")
    private Location getMyLocation() {

        try {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
            }
            return myLocation;
        } catch (Exception e) {
            Log.e("GMAP",e.toString());
            e.printStackTrace();
            return null;
        }
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        isMapRead=true;
        mMap = googleMap;
        Log.e("GMAP","map ready");
        mMap.getUiSettings().setRotateGesturesEnabled(false);


        // Add a marker in Sydney and move the camera

//        mMap.getMyLocation();
                mMap.setMyLocationEnabled(true);
        Log.e("GMAP","enabled");



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!isSP) {
                    Log.i("srarch_activity", marker.getTitle());
                    Log.e("go sp", "show");
                    Intent i = new Intent(search_service.this, Sp_Profile.class);
                    i.putExtra("user_id", marker.getSnippet());
                    startActivity(i);

                }

                return false;
            }
        });

//        mMap.setMaxZoomPreference(50.0f);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location l) {

                if(!isSP) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(), l.getLongitude()), 11));
                    Log.e("GMAP", "animate camera");
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(l.getLatitude(), l.getLongitude()))      // Sets the center of the map to location user
                            .zoom(13)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    Log.e("GMAP", "animate pos");
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    update_location(l);

                }




            }
        });

    }

    public void check_service_near_me(Location location,final String service_type){
        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child("service_provider_location").child(service_type);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot data:dataSnapshot.getChildren()) {

                    Log.i("Search_Service",data.getKey().toString());

                    FirebaseDatabase.getInstance().getReference("Users").child(data.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                            if(dataSnapshot.child("profile_url").getValue()!=null) {
                                String profile_url = dataSnapshot.child("profile_url").getValue().toString();

                                boolean isActive = Boolean.parseBoolean(dataSnapshot.child("service").child("active").getValue().toString());
                                if (!isActive) {

                                    Log.e("GMAP", "Check");

                                    return;

                                }

                                Log.i("Search_Service", dataSnapshot.getValue().toString());


                                Transformation transformation = new RoundedTransformationBuilder()
                                        .borderColor(Color.parseColor("#6776c0ff"))
                                        .borderWidthDp(10)
                                        .cornerRadiusDp(30)
                                        .oval(false)
                                        .build();


                                Log.e("Search_service", profile_url);
                                Log.e("Search_service", service_type);
                                Log.e("Search_service", data.getKey().toString());

                                if (profile_url != null)
                                    Picasso.get().load(profile_url).resize(110, 110).centerCrop().transform(transformation).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                           final BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

                                            FirebaseDatabase.getInstance().getReference("Users").child(data.getKey()).child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    mMap.addMarker(new MarkerOptions().snippet(data.getKey().toString()).icon(icon).title(dataSnapshot.getValue().toString()).position(new LatLng(Float.parseFloat(data.child("latitude").getValue().toString()), Float.parseFloat(data.child("longitude").getValue().toString()))));

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i("dataabse get error",databaseError.toString());
                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     }
    public void update_location(Location location){
        if(prefs.sverc_type().isEmpty() && !isSP){
            check_service_near_me(location,service_type);
            return;

        }
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        MyLocation myLocation=new MyLocation(location.getLatitude(),location.getLongitude());

//        ref.child("service_provider_location").child(prefs.sverc_type()).child(prefs.email()).child("Latitude").setValue(location.getLatitude());
        ref.child("service_provider_location").child(prefs.sverc_type()).child(prefs.email()).setValue(myLocation);

        ref.child(prefs.email()).child("location").setValue(myLocation);
//        ref.child("service_provider_location").child(prefs.sverc_type()).child(prefs.email()).child("Longitude").setValue(location.getLongitude());


    }
}
