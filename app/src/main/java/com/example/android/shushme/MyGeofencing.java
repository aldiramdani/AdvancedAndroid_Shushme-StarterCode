package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyGeofencing implements ResultCallback {

    private List<Geofence> mGeogenceList;
    private Context mContext;
    private GoogleApiClient mClient;

    public MyGeofencing(Context context, GoogleApiClient client) {
        this.mContext = context;
        this.mClient = client;
        mGeogenceList = new ArrayList<>();
    }

    public void registerGeofences(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeogenceList);

        Intent intent = new Intent(mContext, MyGeofencingReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(mContext,0,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);

        try{
            LocationServices.GeofencingApi
                    .addGeofences(mClient, builder.build(),pIntent)
                    .setResultCallback(this);
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    public void updateGeofenceList(PlaceBuffer places){
        mGeogenceList = new ArrayList<>();
         for (Place place : places){
             LatLng l = place.getLatLng();
             Geofence geofence = new Geofence.Builder()
                     .setRequestId(place.getId())
                     .setExpirationDuration(24 * 60 ^ 60 * 1000)
                     .setCircularRegion(l.latitude,l.longitude,50)
                     .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                     .build();
             mGeogenceList.add(geofence);
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.d("MyGeofencing",result.getStatus().toString());
    }
}
