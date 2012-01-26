package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.maps.*;
import com.teamblobby.studybeacon.datastructures.*;
import com.teamblobby.studybeacon.network.APIClient;
import com.teamblobby.studybeacon.network.APIHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class SBMapActivity extends MapActivity implements APIHandler
{

	public final static int REQUESTCODE_RETURNED_FROM_MYCOURSES = 1;

	public final static String TAG = "SBMapActivity";

	private static final int REQUESTCODE_RETURNED_FROM_BEACON = 0;
	
	protected Spinner courseSpinner;
	private ArrayAdapter<String> courseSpinnerAdapter;

	protected int courseSpinnerId;

	protected Button myClassesButton;
	protected ImageButton beaconButton;

	private SBMapView mapView;

	// A map from BeaconId to BeaconInfo. Makes it easy to replace a beacon we already know of.
	protected HashMap<Integer, BeaconInfo> mBeacons;
	private List<BeaconInfo> dirtyBeacons;
	private HashMap<String,BeaconItemizedOverlay> beacItemizedOverlays;

	private final static Drawable beaconD = Global.res.getDrawable(R.drawable.beacon);

	private List<String> courses;

	protected int filterLastSelected = 0;
	
	///////////////////////////////////////////////////////

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map);

		// Find the UI elements
		// Find the spinner
		courseSpinnerId = R.id.mapCourseSpinner;
		courseSpinner = (Spinner) findViewById(courseSpinnerId);
		
		// set up an onClickListener handler for classesButton
		myClassesButton = (Button) findViewById(R.id.myClassesButton);
		myClassesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startMyCoursesActivity();
			}
		});

		beaconButton = (ImageButton) findViewById(R.id.newBeaconButton);
		
		this.loadCourses(savedInstanceState);
		// Must come after loadCourses
		updateBeaconButton();

		this.setUpMapView(savedInstanceState);

		this.setUpBeacons(savedInstanceState);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		checkFirstRun();

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"onResume()");
		mapView.resume();
		// TODO -- Do we want to do this?
		startQuery();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG,"onPause()");
		mapView.pause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		// TODO Save the state of mBeacons
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		// This doesn't seem to ever get called
		mapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_RETURNED_FROM_MYCOURSES:
			if (resultCode == MyCoursesActivity.RESULT_COURSES_CHANGED) {
				loadCourses(null);
				updateBeaconButton();
				startQuery();
			}
			break;
		
		case REQUESTCODE_RETURNED_FROM_BEACON:
			updateBeaconButton();
		}
	}
	
	private void updateBeaconButton() {
		if (Global.atBeacon()) {
			Log.d(TAG, "We are at a beacon.");
			beaconButton.setImageResource(R.drawable.beacon_edit);
			beaconButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editBeaconClicked(v);
				}
			});
			beaconButton.setEnabled(true);
		}
		else {
			Log.d(TAG, "We are not at a beacon.");
			if (courses.size() > 0) {
				beaconButton.setImageResource(R.drawable.newbeaconicon);
				beaconButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						newBeaconClicked(v);
					}
				});
				beaconButton.setEnabled(true);
			} else {
				beaconButton.setImageResource(R.drawable.newbeaconicon_gray);
				beaconButton.setEnabled(false);
			}
		}

	}

	protected void setUpMapView(Bundle savedInstanceState) {
		// TODO use savedInstanceState if possible
		// add zoom
		this.mapView = (SBMapView) findViewById(R.id.mapView);
		mapView.setActivity(this);
		mapView.setBuiltInZoomControls(true);

		// Nic is so smart! -Leo
		if (savedInstanceState == null) mapView.setDefaultMapPosition();

	}

	private void setUpBeacons(Bundle savedInstanceState) {
		// TODO use savedInstanceState
		mBeacons = new HashMap<Integer, BeaconInfo>();
		dirtyBeacons = new ArrayList<BeaconInfo>();
		// Don't fill beacItemOverlay unless you already have some beacons
		// If we had some beacons from savedInstanceState, we would want to do that here
		beacItemizedOverlays = new HashMap<String, BeaconItemizedOverlay>();

		setBeaconOverlays();

		startQuery();
	}

	private void setBeaconOverlays() {

		// First, go through the list of dirtyBeacons and
		// remove any of them from the list of beacItemizedOverlays
//		for (BeaconInfo dirtyBeacon : dirtyBeacons) {
//			BeaconItemizedOverlay courseOverlay = beacItemizedOverlays.get(dirtyBeacon.getClass());
//			courseOverlay.removeByBeaconId(dirtyBeacon.getBeaconId());
//		}
//		dirtyBeacons.clear();

		// Second, make sure that the BeaconItemizedOverlays contain everything they're supposed to
		for (Map.Entry<Integer, BeaconInfo> entry : mBeacons.entrySet()) {
			BeaconInfo beacon = entry.getValue();
			String course = beacon.getCourseName();
			// Put it in the appropriate beacItemizedOverlay
			BeaconItemizedOverlay courseOverlay;

			if (! beacItemizedOverlays.containsKey(course)) {
				courseOverlay = new BeaconItemizedOverlay(beaconD, this, mapView);
				beacItemizedOverlays.put(course,courseOverlay);
			} else {
				courseOverlay = beacItemizedOverlays.get(course);
			}

			BeaconOverlayItem item = courseOverlay.getByBeaconId(beacon.getBeaconId());

			if (item == null) {
				courseOverlay.addOverlay(new BeaconOverlayItem(beacon));
			} else {
				item.setBeacon(beacon); // This might update, or do nothing!
			}


		}


		// Ok, beacItemizedOverlays ought to be up to date.
		// Figure out which one(s) to display.
		List<Overlay> overlays = mapView.getOverlays();
		Boolean wantAllCourses = wantAllCourses();
		boolean dirtied = false;
		String selected = (String)courseSpinner.getSelectedItem();
		for (Map.Entry<String, BeaconItemizedOverlay> entry : beacItemizedOverlays.entrySet()) {
			String course = entry.getKey();
			BeaconItemizedOverlay beacItemizedOverlay = entry.getValue();
			Boolean currentlyShown = overlays.contains(beacItemizedOverlay);
			Boolean shouldBeShown = (wantAllCourses
					|| course.equals(selected));

			if (shouldBeShown && !currentlyShown) {
				dirtied = true;
				overlays.add(beacItemizedOverlay);
			} else if (currentlyShown && !shouldBeShown) {
				dirtied = true;
				overlays.remove(beacItemizedOverlay);
			}
		}

		if (dirtied)
			mapView.invalidate();

	}

	private void checkFirstRun() {
		if ( Global.isFirstTimeAtActivity(TAG))
			(new AlertDialog.Builder(this)).setMessage(R.string.welcomeMapMessage)
			.setCancelable(false).setPositiveButton(R.string.addCourses,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(SBMapActivity.this, CourseResourceActivity.class);
					startActivityForResult(i, REQUESTCODE_RETURNED_FROM_MYCOURSES);


					(new AlertDialog.Builder(SBMapActivity.this)).setMessage(R.string.createBeaconTutorial1)
					.setCancelable(false).setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							(new AlertDialog.Builder(SBMapActivity.this)).setMessage(R.string.createBeaconTutorial2)
							.setCancelable(false).setPositiveButton(R.string.OK,
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();

						}
					}).show();

				}
			}).show();
	}

	protected Boolean wantAllCourses() {
		// TODO Reimplement this when the course spinner is fixed
		String selected = (String)courseSpinner.getSelectedItem();
		return selected.equals(Global.res.getString(R.string.allCourses));
	}

	void startQuery() {
		// find the view bounds
		int deltaLat = mapView.getLatitudeSpan(), deltaLon = mapView.getLongitudeSpan();
		GeoPoint ctr = mapView.getMapCenter();
		int LatE6Min, LatE6Max, LonE6Min, LonE6Max;
		LatE6Min = ctr.getLatitudeE6() - deltaLat/2;
		LatE6Max = ctr.getLatitudeE6() + deltaLat/2;
		LonE6Min = ctr.getLongitudeE6() - deltaLon/2;
		LonE6Max = ctr.getLongitudeE6() + deltaLon/2;

		// Try to populate the beacons asynchronously
		// TODO don't use courses, but rather the selection from the spinner.
		// If the selection is All, then use courses.
		
		List<String> queryCourses;
		String selected = (String)courseSpinner.getSelectedItem();
		if (selected.equals(Global.res.getString(R.string.allCourses))) {
			queryCourses = courses;
		} else {
			queryCourses = new ArrayList<String>();
			queryCourses.add(selected);
		}
		
		APIClient.query(LatE6Min, LatE6Max, LonE6Min, LonE6Max, queryCourses, this);
	}

	protected void loadCourses(Bundle savedInstanceState) {
		// TODO use savedInstanceState if possible.
		// Specifically, keep track of what the selection was in the spinner
		// and automatically set that.

		courses = Global.getCourses();

		courseSpinnerAdapter =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
						Global.getCourses());

		courseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		courseSpinnerAdapter.insert(getString(R.string.allCourses),0);
		courseSpinnerAdapter.add(getString(R.string.editCourses));

		courseSpinner.setAdapter(courseSpinnerAdapter);
		courseSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentAdapterView, View view,
					int position, long id) {
				if ( position == parentAdapterView.getAdapter().getCount()-1 ) {
					//go back to all
					parentAdapterView.setSelection(filterLastSelected);
					// this is the edit item, so spawn my courses activity.
					startMyCoursesActivity();
				}
				filterLastSelected  = position;
				SBMapActivity.this.setBeaconOverlays();
				// TODO Check if Edit ... was clicked.
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

	public void newBeaconClicked(View view) {
		Intent intent = new Intent(this, BeaconEditActivity.class);
		intent.setAction(BeaconEditActivity.ACTION_NEW);
		// check what the spinner is set to
		// TODO
		String selected = (String)courseSpinner.getSelectedItem();
		if (! selected.equals(Global.res.getString(R.string.allCourses))) {
			intent.putExtra(BeaconEditActivity.EXTRA_COURSE, selected);
		}
		startActivityForResult(intent, REQUESTCODE_RETURNED_FROM_BEACON);
	}
	
	public void editBeaconClicked(View view) {
		Intent intent = new Intent(this, BeaconEditActivity.class);
		intent.setAction(BeaconEditActivity.ACTION_EDIT);
		//intent.putExtra(BeaconEditActivity.EXTRA_BEACON, Global.getCurrentBeacon());
		startActivityForResult(intent, REQUESTCODE_RETURNED_FROM_BEACON);
	}
	
    ////////////////////////////////////////////////////////////////
	// The following methods are for implementing SBAPIHandler

	public Activity getActivity() {
		return this;
	}

	/**
	 *  This function is responsible for taking the data from
	 *  the query and inserting it into the data structures.
	 */
	@SuppressWarnings("unchecked")
	public void onSuccess(APICode code, Object result) {
		switch (code) {
		case CODE_QUERY:
			ArrayList<BeaconInfo> beacons = (ArrayList<BeaconInfo>) result;
			boolean dirtied = false;
			// TODO Put the beacons on the map
			Log.d(TAG,"onSuccess()");
			// Add them to mBeacons.
			for (BeaconInfo beacon : beacons) {
				BeaconInfo oldBeacon = mBeacons.put(beacon.getBeaconId(), beacon);
				if (oldBeacon == null) {
					dirtied = true;
				} else {
					// Compare the two to see if anything has changed
					if (! oldBeacon.equals(beacon)) {
						dirtied = true;
						// Put the old one in a list to be deleted
						//dirtyBeacons.add(oldBeacon);
						// Check if we have new info on our present beacon
						if (Global.atBeacon()
								&& (Global.getCurrentBeacon().getBeaconId() == beacon.getBeaconId())) {
							Log.d(TAG,"Updating present beacon info");
							Global.setCurrentBeacon(beacon);
							Global.updateBeaconRunningNotification();
						}
					}
				}
			}

			if (dirtied) {
				Log.d(TAG,"dirtied");
				setBeaconOverlays();
			}
			break;
		default:
			// Shouldn't ever get here. Complain?
		}
	}

	public void onFailure(APICode code, Throwable t) {
		// TODO Complain about failure
		switch (code) {
		case CODE_QUERY:
			Toast.makeText(this, "Failed to load beacons from server", Toast.LENGTH_SHORT).show();
			break;
		default:
			// Shouldn't ever get here. Complain?
		}
	}

	protected void startMyCoursesActivity() {
		Intent i = new Intent(SBMapActivity.this, MyCoursesActivity.class);
		startActivityForResult(i, REQUESTCODE_RETURNED_FROM_MYCOURSES);
	}

}