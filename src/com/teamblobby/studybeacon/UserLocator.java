package com.teamblobby.studybeacon;

import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * User Locator class. Used for getting a user's current location.
 * This asks the location manager for a single grab of the user's location,
 * not continuous updates.
 * 
 * @author nicolas
 *
 */
class UserLocator {

	private static final String TAG = "UserLocator";
	private static final int TENTOTHESIX = 1000000;
	private Location location;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Runnable callback;
	private boolean isReady = false;

	/**
	 * Constructor with callback. Callback Runnable will be run when the location has been obtained.
	 * @param obtainedLocationCallback Runnable callback to be run when a location is determined.
	 */
	public UserLocator(Runnable obtainedLocationCallback) {
		//this.timeout = timeout;
		this.callback = obtainedLocationCallback;
		// set up location manager.
		this.locationManager = (LocationManager) Global.application.getSystemService(Context.LOCATION_SERVICE);
		// make location listener
		this.locationListener = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			public void onProviderEnabled(String provider) {}
			
			public void onProviderDisabled(String provider) {}
			
			public void onLocationChanged(Location location) {UserLocator.this.receiveLocation(location);}
		};
	}
	
	/**
	 * Constructor with no callback.
	 */
	public UserLocator() {
		// no-arg doesn't need callback
		this(new Runnable(){public void run() {return;}}); //blank callback that doesn't do anything
	}
	
	/**
	 * Returns GeoPoint of obtained location. If the location has not yet been obtained, returns null.
	 */
	public GeoPoint getGeoPoint() {
		return this.isReady() ? new GeoPoint((int) (this.location.getLatitude()*TENTOTHESIX), (int)(this.location.getLongitude()*TENTOTHESIX)) : null;
	}
	
	/**
	 * Returns Location of obtained location. If the location has not yet been obtained, returns null.
	 */
	public Location getLocation() {
		return this.isReady() ? this.location : null;
	}

	/**
	 * Returns true if the location has been obtained, false otherwise.
	 */
	public boolean isReady() {
		return this.isReady;
	}
	
	/**
	 * Should be called when you want to start locating the user. Location will not be obtained unless this is called.
	 */
	public void startLocating() {
		// register for a single update matching criteria
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
		locationManager.requestSingleUpdate(criteria, locationListener, null);
		Log.v(TAG, "Location listener registered");
	}
	
	private void receiveLocation(Location location){
		Log.v(TAG, "Got user's location acc:"+location.getAccuracy());
		//if ( this.location == null || location.getAccuracy() < this.location.getAccuracy() ) //update location if it's better
		this.location = location;
		this.isReady = true;
		this.callback.run();
	}
}
