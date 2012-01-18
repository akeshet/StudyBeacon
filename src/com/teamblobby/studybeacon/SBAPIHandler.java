package com.teamblobby.studybeacon;

import java.util.ArrayList;

import com.teamblobby.studybeacon.datastructures.Beacon;

public interface SBAPIHandler {
	public void onQuery(ArrayList<Beacon> beacons);
}
