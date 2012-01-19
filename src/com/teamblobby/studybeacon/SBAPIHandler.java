package com.teamblobby.studybeacon;

import java.util.ArrayList;

import android.app.Activity;

import com.teamblobby.studybeacon.datastructures.*;

public interface SBAPIHandler {
	public void onQuery(ArrayList<BeaconInfo> beacons);
	public Activity getActivity();
}
