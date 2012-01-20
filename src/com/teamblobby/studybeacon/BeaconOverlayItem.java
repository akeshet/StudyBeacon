package com.teamblobby.studybeacon;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.teamblobby.studybeacon.datastructures.BeaconInfo;

public class BeaconOverlayItem extends OverlayItem {

	BeaconInfo mBeacon;
	
	public BeaconOverlayItem(GeoPoint point, String title, String snippet, BeaconInfo beacon) {
		super(point, title, snippet);
		mBeacon = beacon;
	}
	
	public BeaconOverlayItem(BeaconInfo beacon) {
		super(beacon.getLoc(), beacon.getCourseName(),
				// The snippet:
				Integer.toString(beacon.getVisitors()) + " studying");
		mBeacon = beacon;
	}

	public BeaconInfo getBeacon() {
		return mBeacon;
	}
	
}
