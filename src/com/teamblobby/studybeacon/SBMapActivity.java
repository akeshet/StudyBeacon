package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.maps.*;
import com.teamblobby.studybeacon.datastructures.*;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SBMapActivity extends MapActivity implements SBAPIHandler
{

	public final static int REQUESTCODE_RETURNED_FROM_MYCOURSES = 1;

	public final static String TAG = "SBMapActivity";

	private static final int REQUEST_NEW_BEACON = 0;
	
	protected Spinner courseSpinner;
	private ArrayAdapter<String> courseSpinnerAdapter;

	protected int courseSpinnerId;

	protected Button myClassesButton;

	private MapView mapView;
	private MapController mapViewController;

	protected HashMap<Integer, BeaconInfo> mBeacons;

	private List<Overlay> overlays;
	private MyLocationOverlay myLocOverlay;
	/** Hash from course string to itemized overlays */
	private HashMap<String,BeaconItemizedOverlay> beacItemOverlays; 

	private Drawable beaconD = Global.res.getDrawable(R.drawable.beacon);

	private List<String> courses;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map);

		// set up an onClickListener handler for classesButton
		myClassesButton = (Button) findViewById(R.id.myClassesButton);
		myClassesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(SBMapActivity.this, SBMyCoursesActivity.class);
				startActivityForResult(i, REQUESTCODE_RETURNED_FROM_MYCOURSES);
			}
		});

		this.setUpMapView(savedInstanceState);

		// Find the spinner
		courseSpinnerId = R.id.mapCourseSpinner;
		courseSpinner = (Spinner) findViewById(courseSpinnerId);

		this.loadCourses(savedInstanceState);

		this.setUpBeacons(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"onResume()");
		myLocOverlay.enableMyLocation();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG,"onPause()");
		myLocOverlay.disableMyLocation();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_RETURNED_FROM_MYCOURSES:
			loadCourses(null);
			break;
		}
	}

	protected void setUpMapView(Bundle savedInstanceState) {
		// TODO use savedInstanceState if possible
		// add zoom
		this.mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		// get the mapview controller, set the default map location on MIT campus
		this.mapViewController = mapView.getController();

		// get the overlays
		this.overlays = mapView.getOverlays();
		// Add a MyLocationOverlay to it
		this.myLocOverlay = new MyLocationOverlay(this,mapView);

		overlays.add(myLocOverlay);

		// Nic is so smart! -Leo
		if (savedInstanceState == null) this.setMapPosition(); 

	}

	protected void setMapPosition() {
		// begin at MIT
		mapViewController.setCenter(new GeoPoint( Global.res.getInteger(R.integer.mapDefaultLatE6),Global.res.getInteger(R.integer.mapDefaultLongE6)));
		mapViewController.setZoom(Global.res.getInteger(R.integer.mapDefaultZoom));
		// show user's position
		myLocOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapViewController.animateTo(myLocOverlay.getMyLocation());
			}});
	}

	private void setUpBeacons(Bundle savedInstanceState) {
		// TODO use savedInstanceState
		mBeacons = new HashMap<Integer, BeaconInfo>();

		beacItemOverlays = new HashMap<String,BeaconItemizedOverlay>();

		//	    beacItemOverlay.addOverlay(new BeaconOverlayItem(mapView.getMapCenter(), "test", "123",null));

		//	    overlays.add(beacItemOverlay);

		startQuery();
	}

	private void startQuery() {
		// find the view bounds
		int deltaLat = mapView.getLatitudeSpan(), deltaLon = mapView.getLongitudeSpan();
		GeoPoint ctr = mapView.getMapCenter();
		int LatE6Min, LatE6Max, LonE6Min, LonE6Max;
		LatE6Min = ctr.getLatitudeE6() - deltaLat/2;
		LatE6Max = ctr.getLatitudeE6() + deltaLat/2;
		LonE6Min = ctr.getLongitudeE6() - deltaLon/2;
		LonE6Max = ctr.getLongitudeE6() + deltaLon/2;

		// Try to populate the beacons asynchronously
		// TODO don't use courses, but rather the selection from the spinner.
		// If the selection is All, then use courses.
		
		List<String> queryCourses;
		String selected = (String)courseSpinner.getSelectedItem();
		if (selected.equals(Global.res.getString(R.string.allCourses))) {
			queryCourses = courses;
		} else {
			queryCourses = new ArrayList<String>();
			queryCourses.add(selected);
		}
		
		APIClient.query(LatE6Min, LatE6Max, LonE6Min, LonE6Max, queryCourses, this);
	}

	protected void loadCourses(Bundle savedInstanceState) {
		// TODO use savedInstanceState if possible.
		// Specifically, keep track of what the selection was in the spinner
		// and automatically set that.

		courses = Global.getCourses();
		
		courseSpinnerAdapter =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
						Global.getCourses());
		
		courseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		courseSpinnerAdapter.insert(getString(R.string.allCourses),0);
		courseSpinnerAdapter.add(getString(R.string.editCourses));

		// TODO Add a handler for detecting "All" and "Edit"

		courseSpinner.setAdapter(courseSpinnerAdapter);
		
	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

	public void newBeaconClicked(View view) {
		Intent intent = new Intent(this, SBBeaconEditActivity.class);
		intent.setAction(SBBeaconEditActivity.ACTION_NEW);
		// check what the spinner is set to
		// TODO
		String selected = (String)courseSpinner.getSelectedItem();
		if (! selected.equals(Global.res.getString(R.string.allCourses))) {
			intent.putExtra(SBBeaconEditActivity.EXTRA_COURSE, selected);
		}
		startActivityForResult(intent, REQUEST_NEW_BEACON);
	}

    ////////////////////////////////////////////////////////////////
	// The following methods are for implementing SBAPIHandler

	public Activity getActivity() {
		return this;
	}

	/**
	 *  This function is responsible for taking the data from
	 *  the query and inserting it into the data structures.
	 *  TODO: Rewrite this.
	 */
	public void onQuerySuccess(ArrayList<BeaconInfo> beacons) {
		// TODO Put the beacons on the map
		Log.d(TAG,"onQuery()");
		for (BeaconInfo beacon : beacons) {

			Boolean addOverlay = false;

			String courseName = beacon.getCourseName();

			BeaconInfo old = this.mBeacons.put(beacon.getBeaconId(), beacon);

			BeaconItemizedOverlay courseOverlay;

			if (old != null) {
				// this is updated info about a beacon we already knew about
				// remove from the overlay that has it
				// Since they have the same ID, courseName is the same
				this.beacItemOverlays.get(courseName).removeByBeaconId(old.getBeaconId());

			}

			// Check if there is an overlay for this beacon's course
			if ( this.beacItemOverlays.containsKey(courseName)) {
				// One already exists
				courseOverlay = this.beacItemOverlays.get(courseName);
			} else {
				// Need to create one
				courseOverlay = new BeaconItemizedOverlay(beaconD, this, this.mapView);
				this.beacItemOverlays.put(courseName, courseOverlay);
				// Make sure to add it later
				addOverlay = true;
			} 

			// Add a beacon to the itemized.
			BeaconOverlayItem item = new BeaconOverlayItem(beacon);

			courseOverlay.addOverlay(item);

			if (addOverlay) {
				this.overlays.add(courseOverlay);
			}

			//			String snippet = Integer.toString(beacon.getVisitors()) + " visitors";
			//			OverlayItem item = new OverlayItem(beacon.getLoc(), beacon.getCourseName(), snippet);
			//			beacItemOverlay.addOverlay(item);
		}
			
		mapView.invalidate();
		
	}

	public void onQueryFailure(Throwable t) {
		// TODO Complain about failure
		Toast.makeText(this, "Failed to load beacons from server", Toast.LENGTH_SHORT).show();
	}

	public void onAddSuccess(BeaconInfo beacon) {
		// TODO Auto-generated method stub
		
	}

	public void onAddFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		// This should never be called
	}

}