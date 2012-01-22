package com.teamblobby.studybeacon;

import java.util.ArrayList;

import android.app.Activity;

import com.teamblobby.studybeacon.datastructures.*;

public interface SBAPIHandler {
	public Activity getActivity();
	public void onQuerySuccess(ArrayList<BeaconInfo> beacons);
	public void onQueryFailure(Throwable arg0);
	public void onAddSuccess(BeaconInfo beacon);
	public void onAddFailure(Throwable arg0);
}
