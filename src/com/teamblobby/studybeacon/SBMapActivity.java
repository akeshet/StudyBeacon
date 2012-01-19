package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.*;
import com.teamblobby.studybeacon.datastructures.*;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SBMapActivity extends MapActivity implements SBAPIHandler
{

	public final static String TAG = "SBMapActivity";
	
	protected Spinner courseSpinner;
	protected int courseSpinnerId;
	
	protected Button myClassesButton;
	
	private MapView mapView;
	private MapController mapViewController;
	
	private List<Overlay> overlays;
	private MyLocationOverlay myLocOverlay;
	private BeaconItemizedOverlay beacItemOverlay;

	private String courses[];
	
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
	    		startActivity(i);
	    	}
	    });
        
        this.setUpMapView();
        if (savedInstanceState == null) this.setMapPosition(); 
	    
	    // Find the spinner
	    courseSpinnerId = R.id.mapCourseSpinner;
	    courseSpinner = (Spinner) findViewById(courseSpinnerId);
	    
	    this.loadCourses();

	    this.setUpBeacons();
	    
    }

    @Override
    public void onResume() {
    	super.onResume();
    	myLocOverlay.enableMyLocation();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	myLocOverlay.disableMyLocation();
    }
    
	protected void setUpMapView() {
		// add zoom
	    this.mapView = (MapView) findViewById(R.id.mapView);
	    mapView.setBuiltInZoomControls(true);
	    
	    // get the mapview controller, set the default map location on MIT campus
	    this.mapViewController = mapView.getController();
	    
	    // get the overlays
	    this.overlays = mapView.getOverlays();
	    // Add a MyLocationOverlay to it
	    this.myLocOverlay = new MyLocationOverlay(this,mapView);
	    // re-center the map on user's location on first fix
	    
	    overlays.add(myLocOverlay);
	    
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

	private void setUpBeacons() {
		// add overlays for beacons
	    Drawable beaconD = Global.res.getDrawable(R.drawable.beacon);
	    beacItemOverlay = new BeaconItemizedOverlay(beaconD,this);
	    
	    beacItemOverlay.addOverlay(new OverlayItem(mapView.getMapCenter(), "test", "123"));
	    
	    overlays.add(beacItemOverlay);
	    
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
	    APIClient.query(LatE6Min, LatE6Max, LonE6Min, LonE6Max, courses, this);
	}
    
    protected void loadCourses() {
    	ArrayAdapter<CharSequence> courseSpinnerAdapter =
    			new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);

    	courseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	courses = CourseInfo.getCourseNames(Global.getCourseInfos());
    	
    	courseSpinnerAdapter.add(getString(R.string.allCourses));
    	for( String course : courses){
    		courseSpinnerAdapter.add(course);
    	}
    	courseSpinnerAdapter.add(getString(R.string.editCourses));
    	
    	courseSpinner.setAdapter(courseSpinnerAdapter);
    }

	@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}
	
	public void newBeaconClicked(View view) {
		Intent i = new Intent(this, SBBeaconEditActivity.class);
		startActivity(i);
	}

	public void onQuery(ArrayList<Beacon> beacons) {
		// TODO Put the beacons on the map
		Log.d(TAG,"onQuery");
	}

}