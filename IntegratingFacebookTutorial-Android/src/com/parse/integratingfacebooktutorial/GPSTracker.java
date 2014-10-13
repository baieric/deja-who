package com.parse.integratingfacebooktutorial;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
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

	private static final boolean testing = true; // set to true if you want app to consider yourself a passer-by for testing purposes
	
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private Location location;
    private ParseGeoPoint geoPoint;
    
    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final int MIN_TIME_BW_UPDATES = 1000*60*1; //1 minute
    private static final double MAX_DISTANCE = 0.04; // max distance in kilometers for users to pass by each other
    private static final int MINIMUM_HOURS = 1; // minimum hours before user can pass by the same user again
    private static final int LOCATION_LAST_UPDATED = 15; // minimum minutes that user's last update must be to connect with others

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
    	ParseUser currentUser = ParseUser.getCurrentUser();
    	if(l != null && currentUser != null){
	    	geoPoint = new ParseGeoPoint(l.getLatitude(), l.getLongitude());
	    	currentUser.put("location", geoPoint);
	    	Date date = new Date();
	    	currentUser.put("locationUpdatedAt", date);
	    	currentUser.saveInBackground(new SaveCallback() {
	    	      @Override
	    	      public void done(ParseException e) {
	    	    	  Log.d("GPSTracker", "updated user");
	    	    	  findNearbyUsers();
	    	      }
		    });
    	}
    }
    
    private void findNearbyUsers(){
    	ParseQuery<ParseUser> query = ParseUser.getQuery();
    	query.whereWithinKilometers("location", geoPoint, MAX_DISTANCE);
    	
    	Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, LOCATION_LAST_UPDATED * -1);
		Date date = cal.getTime();
		query.whereGreaterThanOrEqualTo("locationUpdatedAt", date);
    	
    	query.findInBackground(new FindCallback<ParseUser>(){
			@Override
			public void done(List<ParseUser> objects, ParseException e) {
				Log.d("GPSTracker", "Found " + objects.size() + " users nearby");
				for(ParseUser user : objects){
					if(!user.hasSameId(ParseUser.getCurrentUser()) || testing){
						processRelationship(user);
					}
				}
			}
    		
    	});
    }
    
    private void processRelationship(final ParseUser user){
    	ParseQuery<Relationship> query1 = Relationship.createQuery();
        query1.whereEqualTo("user1", user);
        query1.whereEqualTo("user2", user);
        ParseQuery<Relationship> query2 = Relationship.createQuery();
        query2.whereEqualTo("user1", ParseUser.getCurrentUser());
        query2.whereEqualTo("user2", ParseUser.getCurrentUser());
        List<ParseQuery<Relationship>> queries = new ArrayList<ParseQuery<Relationship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Relationship> mainQuery = ParseQuery.or(queries);
        mainQuery.include("user1");
        mainQuery.include("user2");

        mainQuery.getFirstInBackground(new GetCallback<Relationship>() {
			@Override
			public void done(Relationship object, ParseException e) {
				if(object == null){
					object = new Relationship();
					object.put("user1", ParseUser.getCurrentUser());
					object.put("user2", user);
					object.put("numEncounters", 0);
					object.put("user1Interested", 0);
					object.put("user2Interested", 0);
				}else{
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.HOUR, MINIMUM_HOURS * -1);
					Date date = cal.getTime();
					if(object.getDate("lastMetAt").after(date)){
						return;
					}
				}
				object.increment("numEncounters");
				object.put("unread", true);
				Date date = new Date();
				object.put("lastMetAt", date);
				object.saveInBackground();
				Log.d("GPSTracker", "updated relationship");
			}
        });
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