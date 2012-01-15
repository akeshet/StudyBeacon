package com.teamblobby.studybeacon;

import java.util.List;
import java.util.Set;

import com.google.android.maps.*;

import android.os.Bundle;
import android.text.TextUtils.StringSplitter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


public class SBMapActivity extends MapActivity {
	
	protected Spinner courseSpinner;
	protected int courseSpinnerId;
	
	public final static String COURSES_STR = "courses";
	
	private MapView mapView;
	private MapController mapViewController;
	private MyLocationOverlay myLocOverlay;
	private List<Overlay> overlays;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        setUpMapView();
	    
	    // Set the default view?
	    
	    // Find the spinner
	    courseSpinnerId = R.id.mapcoursespinner;
	    courseSpinner = (Spinner) findViewById(courseSpinnerId);
	    
	    loadCourses();
	    
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
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    // get the mapview controller, set the default map location on MIT campus
	    mapViewController = mapView.getController();
	    mapViewController.setCenter(new GeoPoint( Global.res.getInteger(R.integer.mapDefaultLatE6),Global.res.getInteger(R.integer.mapDefaultLongE6)));
	    mapViewController.setZoom(Global.res.getInteger(R.integer.mapDefaultZoom));
	    
	    // get the overlays
	    overlays = mapView.getOverlays();
	    // Add a MyLocationOverlay to it
	    myLocOverlay = new MyLocationOverlay(this,mapView);
	    overlays.add(myLocOverlay);
	}
    
    protected void loadCourses() {
    	ArrayAdapter<CharSequence> courseSpinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
    	
    	courseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	String delim = getString(R.string.coursedelim);
    	
    	String courseList = Global.prefs.getString(COURSES_STR, "6.570"+delim+"8.901");

    	String courses[] = courseList.split(delim);
    	
    	courseSpinnerAdapter.add(getString(R.string.allcourses));
    	for( String course : courses){
    		courseSpinnerAdapter.add(course);
    	}
    	courseSpinnerAdapter.add(getString(R.string.editcourses));
    	
    	courseSpinner.setAdapter(courseSpinnerAdapter);
    }

	@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}
}