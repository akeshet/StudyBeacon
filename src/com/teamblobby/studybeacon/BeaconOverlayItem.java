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

	private static String buildBalloonTitle(BeaconInfo beacon) {
		return beacon.getCourseName();
	}

	private static String buildBalloonSnippet(BeaconInfo beacon) {
		return Integer.toString(beacon.getVisitors()) + " studying";
	}

	public BeaconOverlayItem(BeaconInfo beacon) {
		super(beacon.getLoc(), buildBalloonTitle(beacon),
				buildBalloonSnippet(beacon));
		mBeacon = beacon;
	}

	public BeaconInfo getBeacon() {
		return mBeacon;
	}
	
}
