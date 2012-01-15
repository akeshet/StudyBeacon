package com.teamblobby.studybeacon;

import java.util.List;
import java.util.Set;

import com.google.android.maps.*;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils.StringSplitter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


public class SBMapActivity extends MapActivity {
	
	protected Spinner courseSpinner;
	protected int courseSpinnerId;
	
	protected Button myClassesButton;
	
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
	    
	    // Find the spinner
	    courseSpinnerId = R.id.mapCourseSpinner;
	    courseSpinner = (Spinner) findViewById(courseSpinnerId);
	    
	    loadCourses();
	    
	    // set up an onClickListener handler for classesButton
	    myClassesButton = (Button) findViewById(R.id.myClassesButton);
	    myClassesButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		Intent i = new Intent(SBMapActivity.this, SBMyCoursesActivity.class);
	    		startActivity(i);
	    	}
	    });
	    
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
	    mapView = (MapView) findViewById(R.id.mapView);
	    mapView.setBuiltInZoomControls(true);
	    
	    // get the mapview controller, set the default map location on MIT campus
	    mapViewController = mapView.getController();
	    mapViewController.setCenter(new GeoPoint( Global.res.getInteger(R.integer.mapDefaultLatE6),Global.res.getInteger(R.integer.mapDefaultLongE6)));
	    mapViewController.setZoom(Global.res.getInteger(R.integer.mapDefaultZoom));
	    
	    // get the overlays
	    overlays = mapView.getOverlays();
	    // Add a MyLocationOverlay to it
	    myLocOverlay = new MyLocationOverlay(this,mapView);
	    // TODO possibly add a Runnable with myLocOverlay.runOnFirstFix(r)
	    // which should re-center the map on user's location
	    overlays.add(myLocOverlay);
	}
    
    protected void loadCourses() {
    	ArrayAdapter<CharSequence> courseSpinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);

    	courseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	String courses[] = Global.getCourses();
    	
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
}