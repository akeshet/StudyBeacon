package com.teamblobby.studybeacon;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MyLocationOverlay;
import com.teamblobby.studybeacon.datastructures.BeaconInfo;
import com.teamblobby.studybeacon.datastructures.BeaconInfoSimple;
import android.app.Activity;
import android.content.Intent;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SBBeaconEditActivity extends Activity implements SBAPIHandler {
	
	// Here is the interface for intents to use
	public static final String EXTRA_COURSE = "Course";
	public static final String EXTRA_BEACON = "beacon";

	public static final String ACTION_NEW  = "com.blobby.studybeacon.BeaconEditActivity.new";
	public static final String ACTION_EDIT = "com.blobby.studybeacon.BeaconEditActivity.edit";
	public static final String ACTION_VIEW = "com.blobby.studybeacon.BeaconEditActivity.view";
	public static final String TAG = "SBBeaconEditActivity";

	protected enum OperationMode {
		MODE_NEW,
		MODE_EDIT,
		MODE_VIEW
	}
	
	protected OperationMode mode;
	
	protected TextView beaconTitleTV;
	protected Spinner courseSpinner;
	protected TextView expiresTV;
	protected Spinner expiresSpinner;
	protected TextView expiresTimeTV;
	protected Spinner workingOnSpinner;
	protected EditText phone;
	protected EditText email;
	protected EditText details;
	protected Button beaconActionButton;
	
	// This represents the beacon we are making.
	protected BeaconInfoSimple mBeacon;

	private ArrayAdapter<String> courseAdapter;
	private ArrayAdapter<CharSequence> expiresAdapter;
	private ArrayAdapter<CharSequence> workingOnAdapter;
	private UserLocator userLocator;
	
	
	private DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beacon);
		
		loadUIEls();
		
		// This is the intent that started us
		Intent startingIntent = getIntent();
		String startingAction = startingIntent.getAction();
		
		// Figure out what to do
		if (startingAction.equals(ACTION_VIEW)) {
			mode = OperationMode.MODE_VIEW;
		} else if (startingAction.equals(ACTION_EDIT)) {
			mode = OperationMode.MODE_EDIT;
		} else { // By default, create a new beacon
			mode = OperationMode.MODE_NEW;
		}
		
		switch (mode) {
		case MODE_VIEW:
			setUpForView(savedInstanceState,startingIntent);
			break;
		case MODE_EDIT:
			setUpForEdit(savedInstanceState,startingIntent);
			break;
		case MODE_NEW:
		default:
			setUpForNew(savedInstanceState,startingIntent);
			break;
		}
		
	}

	private void loadUIEls() {
		beaconTitleTV    = (TextView) findViewById(R.id.titleText);
		courseSpinner    = (Spinner)  findViewById(R.id.courseSpinner);
		expiresTV        = (TextView) findViewById(R.id.expiresTV);
		expiresSpinner   = (Spinner)  findViewById(R.id.expiresSpinner);
		expiresTimeTV    = (TextView)  findViewById(R.id.expiresTimeTV);
		workingOnSpinner = (Spinner)  findViewById(R.id.workingOnSpinner);
		phone            = (EditText) findViewById(R.id.phone);
		email            = (EditText) findViewById(R.id.email);
		details          = (EditText) findViewById(R.id.detailsEdit);
		beaconActionButton = (Button) findViewById(R.id.beaconActionButton);
		
		// Set the spinners up
		courseAdapter =
				new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item,
						Global.getCourses());
			
	    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    courseSpinner.setAdapter(courseAdapter);
		
		expiresAdapter = ArrayAdapter.createFromResource(
	            this, R.array.expiresTimes, android.R.layout.simple_spinner_item);
	    expiresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    expiresSpinner.setAdapter(expiresAdapter);
	    
	    workingOnAdapter = ArrayAdapter.createFromResource(
	            this, R.array.workingOnList, android.R.layout.simple_spinner_item);
	    workingOnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    workingOnSpinner.setAdapter(workingOnAdapter);
	    // TODO add a listener for custom
	    
		expiresSpinner.setSelection(Global.res.getInteger(R.integer.expiresDefaultIndex));
	    
	}

	protected void setCourseSpinnerItem(String course) {
		if (course != null) {
			// Set the course spinner's selected element
			int courseIndex = courseAdapter.getPosition(course);
			courseSpinner.setSelection(courseIndex);
		}
	}

	protected int durationFromField() {
		// TODO This is a hack. Fix it.
		String durationText = (String)expiresSpinner.getSelectedItem();
		int spaceIndex = durationText.indexOf(" ");
		// Convert to minutes
		return 60*Integer.parseInt(durationText.substring(0, spaceIndex));
	}

	protected BeaconInfo beaconFromFields() {
		// TODO Auto-generated method stub

		String courseName = (String) courseSpinner.getSelectedItem();

		GeoPoint loc = userLocator.getLocation(); // grab the user's location
		Log.d(TAG,"loc: late6="+loc.getLatitudeE6()+" longe6="+loc.getLongitudeE6());

		return new BeaconInfoSimple(-1, // don't have a BeaconId yet
				courseName,
				loc,
				-1, // don't have a # of visitors yet
				details.getText().toString(),
				phone.getText().toString(),
				email.getText().toString(),
				new Date(),
				new Date() // TODO put this in the future
		);
	}

	private void setUpForNew(Bundle savedInstanceState, Intent startingIntent) {
		// TODO -- Add logic if already at a beacon

		// Set title text
		beaconTitleTV.setText(R.string.newBeacon);

		// If a course has been selected in the intent, try to set the spinner
		setCourseSpinnerItem(startingIntent.getStringExtra(EXTRA_COURSE));

		// Add a listener for the action button
		beaconActionButton.setOnClickListener(new NewBeaconClickListener(this));

		// start getting the user's location
		userLocator = new UserLocator();
		userLocator.startLocating();
	}

	protected static final class NewBeaconClickListener implements OnClickListener {

		protected SBBeaconEditActivity mActivity;

		public NewBeaconClickListener(SBBeaconEditActivity sbBeaconEditActivity) {
			
			mActivity = sbBeaconEditActivity;
		}

		public void onClick(View v) {
			if ( !mActivity.userLocator.isReady() ) {
				Toast.makeText(mActivity, R.string.stilllocating, Toast.LENGTH_SHORT).show(); //inform we are still locating.
				Log.d(TAG, "canceled, still locating");
				return;
			}
			// TODO actually get this from the fields
			APIClient.add(mActivity.beaconFromFields(), mActivity.durationFromField(), mActivity);
		}
	}

	private void setUpForEdit(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Add logic if already at a beacon
		// Set title text
		beaconTitleTV.setText(R.string.editBeacon);		

		beaconActionButton.setText(R.string.editBeacon);

		loadBeaconData(startingIntent);

	}

	private void setUpForView(Bundle savedInstanceState, Intent startingIntent) {
		// Set title text
		beaconTitleTV.setText(R.string.beaconDetails);
		
		// Disable the elements' editability
		Spinner spinners[] = {courseSpinner, expiresSpinner, workingOnSpinner};
		for (Spinner s : spinners)
			s.setEnabled(false);
		
		// Change the "expires" text
		expiresTV.setText(R.string.expiresAt);
		expiresSpinner.setVisibility(View.GONE);
		expiresTimeTV.setVisibility(View.VISIBLE);
		
		EditText ets[] = {phone, email, details};
		for (EditText e : ets)
			e.setEnabled(false);
		
		
		loadBeaconData(startingIntent);
		
		beaconActionButton.setVisibility(View.GONE);
		
	}

	private void loadBeaconData(Intent startingIntent) {
		// TODO Auto-generated method stub

		// TODO What do we do if somebody did not call this properly?
		mBeacon = startingIntent.getParcelableExtra(EXTRA_BEACON);

		if (mBeacon == null) // FAILURE
			return;

		// Load the course name
		setCourseSpinnerItem(mBeacon.getCourseName());
		phone.setText(mBeacon.getTelephone());
		email.setText(mBeacon.getEmail());
		details.setText(mBeacon.getDetails());
		expiresTimeTV.setText(df.format(mBeacon.getExpires()));


	}

	////////////////////////////////////////////////////////////////
	// The following are for implementing SBAPIHandler

	public Activity getActivity() {
		// TODO Auto-generated method stub
		return this;
	}

	public void onQuerySuccess(ArrayList<BeaconInfo> beacons) {
		// TODO Auto-generated method stub
		
	}

	public void onQueryFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onAddSuccess(BeaconInfo beacon) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Beacon added successfully", Toast.LENGTH_SHORT).show();
		// go back home
		Global.goHome(this);
	}

	public void onAddFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Failed to add beacon", Toast.LENGTH_SHORT).show();
	}

}
