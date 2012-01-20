package com.teamblobby.studybeacon;

import java.util.ArrayList;

import android.app.Activity;

import com.teamblobby.studybeacon.datastructures.*;

public interface SBAPIHandler {
	public void onQuerySuccess(ArrayList<BeaconInfo> beacons);
	public void onQueryFailure();
	public Activity getActivity();
}
