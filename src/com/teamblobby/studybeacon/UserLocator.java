package com.teamblobby.studybeacon;

import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;

class UserLocator {

	private static final String TAG = "UserLocator";
	private static final int TENTOTHESIX = 1000000;
	private Location location;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Runnable callback;
	private boolean isReady = false;

	public UserLocator(Runnable obtainedLocationCallback) {
		//this.timeout = timeout;
		this.callback = obtainedLocationCallback;
		// set up location manager.
		this.locationManager = (LocationManager) Global.application.getSystemService(Context.LOCATION_SERVICE);
		// make location listener
		this.locationListener = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {} //TODO
			
			public void onProviderEnabled(String provider) {} //TODO
			
			public void onProviderDisabled(String provider) {} //TODO
			
			public void onLocationChanged(Location location) {UserLocator.this.receiveLocation(location);}
		};
	}
	
	public UserLocator() {
		// no-arg doesn't need callback
		this(new Runnable(){public void run() {return;}}); //blank callback that doesn't do anything
	}
	
	public GeoPoint getLocation() {
		return this.isReady() ? new GeoPoint((int) (this.location.getLatitude()*TENTOTHESIX), (int)(this.location.getLongitude()*TENTOTHESIX)) : null;
	}
	
	public boolean isReady() {
		return this.isReady;
	}
	
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
