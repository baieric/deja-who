package com.parse.integratingfacebooktutorial;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private Location location;
    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final int MIN_TIME_BW_UPDATES = 1000*60*1; //1 minute

    LocationManager manager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
    	Log.d("GPSTracker", "initialized");
        getLocation();
        return START_STICKY;
    }

    public Location getLocation() {
    	try{
    		manager = (LocationManager) this.getBaseContext().getSystemService(LOCATION_SERVICE);
    		isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    		isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    		
    		if(!isGPSEnabled && !isNetworkEnabled){
    			//no network provider is enabled
    		}else{
    			if(isGPSEnabled){
    				Log.d("GPSTracker", "GPS enabled");
    				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    							MIN_TIME_BW_UPDATES,
        						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    				if(manager != null){
    					location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    				}
    			}else if(isNetworkEnabled){
    				Log.d("GPSTracker", "Network enabled");
    				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
    						MIN_TIME_BW_UPDATES,
    						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    				if(manager != null){
    					location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    				}
    			}else{
    				Log.d("GPSTracker", "Failed to find location");
    			}
    			
    		}
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	return location;
    }

    @Override
    public void onLocationChanged(Location l) {
    	if(l != null){
	    	ParseUser currentUser = ParseUser.getCurrentUser();
	    	ParseGeoPoint geoPoint = new ParseGeoPoint(l.getLatitude(), l.getLongitude());
	    	currentUser.put("location", geoPoint);
	    	Date date = new Date();
	    	currentUser.put("locationUpdatedAt", date);
	    	currentUser.saveInBackground(new SaveCallback() {
	    	      @Override
	    	      public void done(ParseException e) {
	    	    	  Log.d("GPSTracker", "updated user");
	    	      }
		    });
    	}
    }

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}