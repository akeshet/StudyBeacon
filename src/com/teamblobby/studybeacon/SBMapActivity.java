package com.teamblobby.studybeacon;

import java.util.List;
import java.util.Set;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.os.Bundle;
import android.text.TextUtils.StringSplitter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


public class SBMapActivity extends MapActivity {
	
	protected Spinner courseSpinner;
	protected int courseSpinnerId;
	
	public final static String COURSES_STR = "courses";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // add zoom
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    // Set the default view?
	    
	    // Find the spinner
	    courseSpinnerId = R.id.mapcoursespinner;
	    courseSpinner = (Spinner) findViewById(courseSpinnerId);
	    
	    loadCourses();
	    
    }
    
    protected void loadCourses() {
    	ArrayAdapter<CharSequence> courseSpinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
    	
    	courseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	String courseList = Global.prefs.getString(COURSES_STR, "6.570:8.901");

    	String courses[] = courseList.split(":");
    	
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