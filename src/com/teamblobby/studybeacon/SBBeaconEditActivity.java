package com.teamblobby.studybeacon;

import com.teamblobby.studybeacon.datastructures.BeaconInfoSimple;
import com.teamblobby.studybeacon.datastructures.CourseInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SBBeaconEditActivity extends Activity {
	
	// Here is the interface for intents to use
	public static final String COURSE_STR = "Course";
	private static final String BEACON_STR = "beacon";

	public static final String ACTION_NEW = "com.blobby.studybeacon.BeaconEditActivity.new";
	public static final String ACTION_EDIT = "com.blobby.studybeacon.BeaconEditActivity.edit";
	public static final String ACTION_VIEW = "com.blobby.studybeacon.BeaconEditActivity.view";
	
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beacon);
		
		loadUIEls();
		
		// This is the intent that started us
		Intent startingIntent = getIntent();
		String startingAction = startingIntent.getAction();
		
		// Figure out what to do
		if (startingAction == ACTION_VIEW) {
			mode = OperationMode.MODE_VIEW;
		} else if (startingAction == ACTION_EDIT) {
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
		beaconTitleTV    = (TextView) findViewById(R.id.beaconTitleTV);
		courseSpinner    = (Spinner)  findViewById(R.id.courseSpinner);
		expiresTV        = (TextView) findViewById(R.id.expiresTV);
		expiresSpinner   = (Spinner)  findViewById(R.id.expiresSpinner);
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

	private void setUpForNew(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Auto-generated method stub
		// Set title text
		beaconTitleTV.setText(R.string.newBeacon);
		
		// If a course has been selected in the intent, try to set the spinner
		setCourseSpinnerItem(startingIntent.getStringExtra(COURSE_STR));
		
	}

	protected void setCourseSpinnerItem(String course) {
		if (course != null) {
			// Set the course spinner's selected element
			// TODO -- does this work?
			int courseIndex = courseAdapter.getPosition(course);
			courseSpinner.setSelection(courseIndex);
		}
	}

	private void setUpForEdit(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Auto-generated method stub
		// Set title text
		beaconTitleTV.setText(R.string.editBeacon);		
		
		beaconActionButton.setText(R.string.editBeacon);
		
		loadBeaconData(startingIntent);
		
	}

	private void setUpForView(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Auto-generated method stub
		// Set title text
		beaconTitleTV.setText(R.string.beaconDetails);
		// Disable the elements' editability
		Spinner spinners[] = {courseSpinner, expiresSpinner, workingOnSpinner};
		for (Spinner s : spinners)
			s.setEnabled(false);
		// Change the "expires" text
		expiresTV.setText(R.string.expiresAt);
		// TODO -- replace the expires in spinner with a text field that shows the expiration time
		
		EditText ets[] = {phone, email, details};
		for (EditText e : ets)
			e.setEnabled(false);
		
		
		loadBeaconData(startingIntent);
		
		beaconActionButton.setVisibility(View.INVISIBLE);
		
	}

	private void loadBeaconData(Intent startingIntent) {
		// TODO Auto-generated method stub
		
		// TODO What do we do if somebody did not call this properly?
		mBeacon = startingIntent.getParcelableExtra(BEACON_STR);
		
		if (mBeacon == null) // FAILURE
			return;
		
		// Load the course name
		setCourseSpinnerItem(mBeacon.getCourseName());
		phone.setText(mBeacon.getTelephone());
		email.setText(mBeacon.getEmail());
		details.setText(mBeacon.getDetails());
		
	}

}
