package com.teamblobby.studybeacon;

import java.text.DateFormat;
import java.util.Date;

import com.google.android.maps.*;
import com.teamblobby.studybeacon.datastructures.*;
import com.teamblobby.studybeacon.network.APIClient;
import com.teamblobby.studybeacon.network.APIHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.text.util.*;


public class BeaconEditActivity extends Activity implements APIHandler {

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
	protected TextView contact;
	protected EditText phone;
	protected EditText email;
	protected EditText details;
	protected Button beaconActionButton;
	protected Button beaconSecondaryActionButton;

	// This represents the beacon we are making.
	protected BeaconInfoSimple mBeacon;

	private ArrayAdapter<String> courseAdapter;
	private ArrayAdapter<CharSequence> expiresAdapter;
	private ArrayAdapter<CharSequence> workingOnAdapter;
	private UserLocator userLocator;
	private ProgressDialog currentDialog;


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
		expiresTimeTV    = (TextView) findViewById(R.id.expiresTimeTV);
		workingOnSpinner = (Spinner)  findViewById(R.id.workingOnSpinner);
		contact          = (TextView) findViewById(R.id.contactTV);
		phone            = (EditText) findViewById(R.id.phone);
		email            = (EditText) findViewById(R.id.email);
		details          = (EditText) findViewById(R.id.detailsEdit);
		beaconActionButton = (Button) findViewById(R.id.beaconActionButton);
		beaconSecondaryActionButton = (Button) findViewById(R.id.beaconSecondaryActionButton);

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
		// Set the drawable on the action button
		beaconActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.newbeaconicon, 0, 0, 0);

		// start getting the user's location
		userLocator = new UserLocator();
		userLocator.startLocating();
	}

	protected final class NewBeaconClickListener implements OnClickListener {

		protected BeaconEditActivity mActivity;

		public NewBeaconClickListener(BeaconEditActivity sbBeaconEditActivity) {

			mActivity = sbBeaconEditActivity;
		}

		public void onClick(View v) {
			if ( !mActivity.userLocator.isReady() ) {
				Toast.makeText(mActivity, R.string.stillLocating, Toast.LENGTH_SHORT).show(); //inform we are still locating.
				Log.d(TAG, "canceled, still locating");
				return;
			}
			currentDialog = ProgressDialog.show(mActivity, "", "Creating beacon...");
			APIClient.add(mActivity.beaconFromFields(), mActivity.durationFromField(), mActivity);
		}
	}

	private void setUpForEdit(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Add logic if already at a beacon
		// Set title text
		beaconTitleTV.setText(R.string.editBeacon);		

		beaconActionButton.setText(R.string.saveBeacon);
		// Set the drawable on the action button
		beaconActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.beacon_edit, 0, 0, 0);

		beaconSecondaryActionButton.setVisibility(View.VISIBLE);
		beaconSecondaryActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.beacon_leave, 0, 0, 0);
		// The secondary button is the leave button
		beaconSecondaryActionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mBeacon != null){
					APIClient.leave(mBeacon.getBeaconId(), BeaconEditActivity.this);
					currentDialog = ProgressDialog.show(BeaconEditActivity.this, "", "Leaving beacon...");
				}else
					Toast.makeText(BeaconEditActivity.this,
							"Something went wrong -- I don't know which beacon you're viewing",
							Toast.LENGTH_SHORT).show();
			}
		});

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
		for (EditText e : ets) {
			e.setFocusable(false);
		}

		// don't show contact details if they weren't filled in
		if ( phone.getText().toString().equals("") )
			phone.setVisibility(View.GONE);

		if ( email.getText().toString().equals("") )
			email.setVisibility(View.GONE);

		if ( phone.getText().toString().equals("") && email.getText().toString().equals("") )
			contact.setVisibility(View.GONE);

		// make the details have a different hint if nothing was given
		details.setHint(R.string.detailHintView);

		loadBeaconData(startingIntent);

		beaconActionButton.setText(R.string.joinBeacon);
		// Set the drawable on the action button
		beaconActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.beacon_join, 0, 0, 0);

		beaconActionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO check user's location
				if (mBeacon != null) {
					APIClient.join(mBeacon.getBeaconId(), BeaconEditActivity.this);
					currentDialog = ProgressDialog.show(BeaconEditActivity.this, "", "Joining beacon...");
				} else
					Toast.makeText(BeaconEditActivity.this,
							"Something went wrong -- I don't know which beacon you're viewing",
							Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void loadBeaconData(Intent startingIntent) {
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

		if (this.mode == OperationMode.MODE_VIEW) {
			Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
			Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
		}
	}

	////////////////////////////////////////////////////////////////
	// The following are for implementing SBAPIHandler

	public Activity getActivity() {
		// TODO Auto-generated method stub
		return this;
	}

	public void onSuccess(APICode code, Object response) {
		BeaconInfo beacon = null;
		String messageText = null;
		switch (code) {
		case CODE_ADD:
			beacon = (BeaconInfo) response;
			messageText = new String("Beacon added successfully");
			break;
		case CODE_JOIN:
			beacon = (BeaconInfo) response;
			messageText = new String("Beacon joined successfully");
			break;
		case CODE_LEAVE:
			messageText = new String("Beacon left successfully");
			break;
		default:
			// Shouldn't get here ... complain?
		}
		Toast.makeText(this, messageText, Toast.LENGTH_SHORT).show();
		Global.setCurrentBeacon(beacon);
		currentDialog.dismiss();
		// go back home
		Global.goHome(this);
	}

	public void onFailure(APICode code, Throwable e) {
		String messageText = null;
		switch (code) {
		case CODE_ADD:
			messageText = new String("Failed to add beacon");
			Global.setCurrentBeacon(null);
			break;
		case CODE_JOIN:
			messageText = new String("Failed to join beacon");
			Global.setCurrentBeacon(null);
			break;
		case CODE_LEAVE:
			messageText = new String("Failed to leave beacon -- out of sync with server");
			// TODO -- We need to resync with server, but that does not yet exist
			break;
		default:
			// Shouldn't get here ... complain?
		}
		Toast.makeText(this, messageText, Toast.LENGTH_SHORT).show();
		currentDialog.dismiss();
	}

}
