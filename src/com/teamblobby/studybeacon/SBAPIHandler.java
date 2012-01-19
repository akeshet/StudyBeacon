package com.teamblobby.studybeacon;

import java.util.ArrayList;

import android.app.Activity;

import com.teamblobby.studybeacon.datastructures.Beacon;

public interface SBAPIHandler {
	public void onQuery(ArrayList<Beacon> beacons);
	public Activity getActivity();
}
