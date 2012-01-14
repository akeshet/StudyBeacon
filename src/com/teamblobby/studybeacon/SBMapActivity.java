package com.teamblobby.studybeacon;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.os.Bundle;

public class SBMapActivity extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // add zoom
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
    }

	@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}
}