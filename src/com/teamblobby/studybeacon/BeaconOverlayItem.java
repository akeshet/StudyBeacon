package com.teamblobby.studybeacon;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.teamblobby.studybeacon.datastructures.BeaconInfo;

public class BeaconOverlayItem extends OverlayItem {

	BeaconInfo mBeacon;
	
	public BeaconOverlayItem(GeoPoint point, String title, String snippet, BeaconInfo beacon) {
		super(point, title, snippet);
		mBeacon = beacon;
		mBeacon.setLoc(point);
	}

	private static String getShortTitle(BeaconInfo beacon) {
		return beacon.getCourseName() + " | x" + Integer.toString(beacon.getVisitors());
	}

	private static String getShortSnippet(BeaconInfo beacon) {
		return  null;
	}

	public BeaconOverlayItem(BeaconInfo beacon) {
		this(beacon.getLoc(), getShortTitle(beacon),
				getShortSnippet(beacon), beacon);
	}

	public BeaconInfo getBeacon() {
		return mBeacon;
	}
	
}
