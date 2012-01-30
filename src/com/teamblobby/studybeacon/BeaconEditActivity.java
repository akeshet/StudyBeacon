package com.teamblobby.studybeacon;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.android.maps.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.teamblobby.studybeacon.datastructures.*;
import com.teamblobby.studybeacon.network.APIClient;
import com.teamblobby.studybeacon.network.ActivityAPIHandler;
import com.teamblobby.studybeacon.ui.QRButton;
import com.teamblobby.studybeacon.ui.TextClickToEdit;
import com.teamblobby.studybeacon.ui.TitleBar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.util.*;


public class BeaconEditActivity extends Activity {

	private static final String SMS_URI_PREFIX = "smsto:";
	private static final int DEFAULT_WORKINGON_SPINNER_POSITION = 0;
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
	protected TextClickToEdit expiresTimeTV;
	protected Spinner workingOnSpinner;
	protected TextView contact;
	protected EditText phone;
	protected EditText email;
	protected EditText details;
	protected Button beaconActionButton;
	protected Button beaconSecondaryActionButton;
	protected TextView locationTV;

	// This represents the beacon we are making.
	protected BeaconInfo mBeacon;

	private ArrayAdapter<CourseInfo> courseAdapter;
	private ArrayAdapter<DurationSpinnerItem> expiresAdapter;
	private ArrayAdapter<String> workingOnAdapter;
	private UserLocator userLocator;
	private ProgressDialog currentDialog;


	private DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
	protected boolean expiresEdited = false;
	private String expiresTimeFormatted;
	private static final double DISTANCE_CUTOFF_JOIN_WARNING_METERS = 100.;
	private double distanceToBeacon = 0.;
	private QRButton qrButton;
	
	private class MyApiHandler extends ActivityAPIHandler {
		@Override
		public Activity getActivity() {
			return BeaconEditActivity.this;
		}
		
		@Override
		protected void handleFailure(APIClient.APICode code, Throwable e) {
			String messageText = null;
			switch (code) {
			case CODE_ADD:
				messageText = new String("Failed to add beacon");
				Global.setCurrentBeacon(null);
				Global.updateBeaconRunningNotification();
				break;
			case CODE_EDIT:
				messageText = new String("Failed to save beacon");
				// TODO What do we do here?
				break;
			case CODE_JOIN:
				messageText = new String("Failed to join beacon");
				Global.setCurrentBeacon(null);
				Global.updateBeaconRunningNotification();
				break;
			case CODE_LEAVE:
				messageText = new String("Failed to leave beacon -- trying to re-sync with server");
				APIClient.sync(myAPIHandler, BeaconEditActivity.this);
				break;
			case CODE_SYNC:
				messageText = new String("Failed to re-sync with server.");
				// TODO What do we do?
				break;
			default:
				// Shouldn't get here ... complain?
			}
			Toast.makeText(BeaconEditActivity.this, messageText, Toast.LENGTH_SHORT).show();
			currentDialog.dismiss();
			// For CODE_LEAVE, we have started a sync; now show a dialog must be after the above dismissal
			if (code == APIClient.APICode.CODE_LEAVE)
				showDialog("Trying to re-sync with server...");
		}
		
		@Override
		protected void handleSuccess(APIClient.APICode code, Object response) {
			BeaconInfo beacon = null;
			String messageText = null;
			switch (code) {
			case CODE_ADD:
				beacon = (BeaconInfo) response;
				messageText = new String("Beacon added successfully");
				break;
			case CODE_EDIT:
				beacon = (BeaconInfo) response;
				messageText = new String("Beacon updated");
				break;
			case CODE_JOIN:
				beacon = (BeaconInfo) response;
				messageText = new String("Beacon joined successfully");
				break;
			case CODE_LEAVE:
				messageText = new String("Beacon left successfully");
				break;
			case CODE_SYNC:
				beacon = (BeaconInfo) response;
				messageText = new String("Resynced with server successfully");
				break;
			default:
				// TODO Shouldn't get here ... complain?
			}
			Toast.makeText(BeaconEditActivity.this, messageText, Toast.LENGTH_SHORT).show();
			Global.setCurrentBeacon(beacon);
			Global.updateBeaconRunningNotification();
			currentDialog.dismiss();
			// go back home
			// TODO Set a result code? SBMapActivity will need to get new data.
			finish();
		}	
	}
	
	private MyApiHandler myAPIHandler = new MyApiHandler();

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		APIClient.cancel(this);
	}

	private void loadUIEls() {
		beaconTitleTV    = (TextView) findViewById(R.id.titleText);
		courseSpinner    = (Spinner)  findViewById(R.id.courseSpinner);
		expiresTV        = (TextView) findViewById(R.id.expiresTV);
		expiresSpinner   = (Spinner)  findViewById(R.id.expiresSpinner);
		//expiresTimeTV    = (TextView) findViewById(R.id.expiresTimeTV);
		workingOnSpinner = (Spinner)  findViewById(R.id.workingOnSpinner);
		contact          = (TextView) findViewById(R.id.contactTV);
		phone            = (EditText) findViewById(R.id.phone);
		email            = (EditText) findViewById(R.id.email);
		details          = (EditText) findViewById(R.id.detailsEdit);
		beaconActionButton = (Button) findViewById(R.id.beaconActionButton);
		beaconSecondaryActionButton = (Button) findViewById(R.id.beaconSecondaryActionButton);
		locationTV = (TextView) 	  findViewById(R.id.locationTV);
		qrButton = (QRButton) findViewById(R.id.qrbutton);
		
		qrButton.updateButton(Global.getCurrentBeacon());
		
		// Set the spinners up
		courseAdapter =
				new ArrayAdapter<CourseInfo>(this,
						android.R.layout.simple_spinner_item,
						Global.getMyCourseInfos());

		courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		courseSpinner.setAdapter(courseAdapter);

		// set up the array for the expires spinner
		List<DurationSpinnerItem> expiresList = new ArrayList<DurationSpinnerItem>();
		String[] expireTimes = this.getResources().getStringArray(R.array.expiresTimes);
		int[] expireMinutes = this.getResources().getIntArray(R.array.expiresMinutes);
		
		for ( int j=0; j<expireTimes.length; j++ ){
			expiresList.add(new DurationSpinnerItem(expireTimes[j], expireMinutes[j]));
		}
		
		expiresAdapter = new ArrayAdapter<DurationSpinnerItem>(
											this,
											android.R.layout.simple_spinner_item,
											expiresList);
		
		expiresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expiresSpinner.setAdapter(expiresAdapter);

		List<String> workingOnList = new ArrayList<String>();
		workingOnList.addAll(Arrays.asList(getResources().getStringArray(R.array.workingOnList)));

		workingOnAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, workingOnList);
		workingOnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		workingOnSpinner.setAdapter(workingOnAdapter);
		workingOnSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View itemView,
					int position, long id) {
				int count = workingOnAdapter.getCount();
				if ( position == count-1 ) { // last element
					customWorkingOnAlert(count-1).show();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		expiresSpinner.setSelection(Global.res.getInteger(R.integer.expiresDefaultIndex));

	}
	
	public void enterMyNumber(View v) {
		TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		String myNumber = tm.getLine1Number();
		if (myNumber != null)
			phone.setText(PhoneNumberUtils.formatNumber(myNumber));
	}
	
	public void enterMyEmail(View v){
		Account[] accounts = AccountManager.get(this).getAccounts();
		if ( accounts[0] != null )
			email.setText(accounts[0].name);
	}
	
	private Builder customWorkingOnAlert(final int index){
		final EditText input = new EditText(this);
		return new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.workingOn))
					.setView(input)
					.setPositiveButton(getResources().getString(R.string.workingOnOK), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if ( input.getText().toString().equals("")){
								workingOnSpinner.setSelection(DEFAULT_WORKINGON_SPINNER_POSITION);
								return; // nothing to do!
							}
							String text = input.getText().toString();
							addToWorkingOn(index, text);
						}
					})
					.setNegativeButton(getResources().getString(R.string.workingOnCancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// return spinner to first element
							workingOnSpinner.setSelection(DEFAULT_WORKINGON_SPINNER_POSITION);
						}
					});
	}
	
	private class DurationSpinnerItem {
		private int minutes;
		private String displayString;
		
		@Override
		public String toString() {
			return this.getDisplayString();
		}

		public int getMinutes() {
			return minutes;
		}

		public String getDisplayString() {
			return displayString;
		}

		DurationSpinnerItem(String displayString,int minutes){
			this.minutes=minutes;
			this.displayString=displayString;
		}
		
		
	}

	protected void setCourseSpinnerItem(String course) {
		if (course != null) {
			// Set the course spinner's selected element
			int courseIndex = -1;
			int count = courseAdapter.getCount();
			for ( int j=0; j<count; j++){
				if (courseAdapter.getItem(j).getName().equals(course)){
					courseIndex = j;
					break;
				}
			}
			if ( courseIndex == -1 ){
				// didn't find it, add it
				CourseInfo dummyCourse = new CourseInfoSimple(course);
				courseAdapter.add(dummyCourse);
				courseIndex = courseAdapter.getPosition(dummyCourse);
			}
			courseSpinner.setSelection(courseIndex);
		}
	}

	protected int newDurationFromField() {
		return ((DurationSpinnerItem) expiresSpinner.getSelectedItem()).getMinutes();
	}

	protected BeaconInfo newBeaconFromFields(GeoPoint location) {
		String courseName = ((CourseInfo) courseSpinner.getSelectedItem()).getName();

		//GeoPoint loc = userLocator.getLocation(); // grab the user's location
		Log.d(TAG,"loc: late6="+location.getLatitudeE6()+" longe6="+location.getLongitudeE6());

		return new BeaconInfoSimple(-1, // don't have a BeaconId yet
				courseName,
				location,
				-1, // don't have a # of visitors yet
				(String)workingOnSpinner.getSelectedItem(),
				details.getText().toString(),
				phone.getText().toString(),
				email.getText().toString(),
				new Date(),
				new Date()
				);
	}
	
	private Runnable locationAcquiredCallback = new Runnable() {
		public void run() {
			switch (mode) {
			case MODE_NEW:
				locationTV.setText(R.string.locationAcquired);
				break;
			case MODE_VIEW:
				// Calculate the distance to the beacon
				Location userLocation = userLocator.getLocation(); // This can not fail, since we are being called
				// TODO Can we ever get here without a beacon?
				GeoPoint beaconGeoPoint = mBeacon.getLoc();
				Location beaconLoc = new Location("dummy provider");
				beaconLoc.setLongitude(beaconGeoPoint.getLongitudeE6()/1000000.0);
				beaconLoc.setLatitude(beaconGeoPoint.getLatitudeE6()/1000000.0);
				distanceToBeacon = userLocation.distanceTo(beaconLoc);
				String distStr = distanceFormat(distanceToBeacon);
				locationTV.setText("Approximately " + distStr + " away.");
				break;
			default:
				// This should never happen.
			}
			
		}
	};

	protected String distanceFormat(double dist) {
		// I really only want one digit of precision on the answer.
		double nDig = Math.floor(Math.log10(dist));
		double roundDist = Math.pow(10, nDig)*Math.round(dist * Math.pow(10, -nDig));
		
		if (roundDist <1000) {
			return (new DecimalFormat("#")).format(roundDist) + "m";
		} else
			return (new DecimalFormat("#")).format(Math.round(roundDist/1000.)) + "km";
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
		userLocator = new UserLocator(locationAcquiredCallback);
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
			showDialog("Creating beacon...");
			// needs working on from fields
			APIClient.add(mActivity.newBeaconFromFields(userLocator.getGeoPoint()), mActivity.newDurationFromField(),
					myAPIHandler, mActivity);
		}
	}

	protected int editDurationFromField() {
		if ( expiresEdited ){
			return newDurationFromField();
		} else {
			return APIClient.DURATION_UNCHANGED;
		}
	}

	protected BeaconInfo editBeaconFromFields() {
		String courseName = ((CourseInfo) courseSpinner.getSelectedItem()).getName();

		return new BeaconInfoSimple(
				Global.getCurrentBeacon().getBeaconId(),
				courseName,
				new GeoPoint(0,0),
				-1, // don't have a # of visitors yet
				(String)workingOnSpinner.getSelectedItem(),
				details.getText().toString(),
				phone.getText().toString(),
				email.getText().toString(),
				new Date(),
				new Date()
				);
	}

	private void setUpForEdit(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Add logic if already at a beacon
		// Set title text
		beaconTitleTV.setText(R.string.editBeacon);
		

		mBeacon = Global.getCurrentBeacon();
		String errorText = null;
		// Check to make sure we're actually at a good beacon
		if (mBeacon == null)
			errorText = new String("Not at a beacon");
		else if (mBeacon.getExpires().before(new Date()))
			errorText = new String("Beacon already expired");
		
		if (errorText != null) {
			Toast.makeText(this, "Beacon already expired", Toast.LENGTH_SHORT).show();
			Global.setCurrentBeacon(null);
			Global.updateBeaconRunningNotification();
			finish();
			return;
		}
		
		loadBeaconData(); // do this first

		// Don't let the class be editable
		//courseSpinner.setEnabled(false);
		String courseText = courseSpinner.getSelectedItem().toString();
		int beacNum = mBeacon.getVisitors();
		if ( beacNum == 1 ){
			courseText += " (1 person)";
		} else {
			courseText += " ("+beacNum+" people)";
		}

		convertToTextClickToEdit(courseSpinner,courseText,true); // true hides the edit button
		convertToTextClickToEdit(workingOnSpinner,workingOnSpinner.getSelectedItem().toString(),false);
			//s.setEnabled(false);

		// Change the "expires" text
		expiresTV.setText(R.string.expiresAt);
		//expiresSpinner.setVisibility(View.GONE);
		//expiresTimeTV.setVisibility(View.VISIBLE);
		expiresTimeTV = convertToTextClickToEdit(expiresSpinner,expiresTimeFormatted,false,new Runnable() { // make the edit button also change the expires text
			public void run() {
				expiresTV.setText(R.string.expiresIn);
				expiresEdited = true;
			}
		});
		
		
		locationTV.setVisibility(View.GONE);

		beaconActionButton.setText(R.string.saveBeacon);
		// Set the drawable on the action button
		beaconActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.beacon_edit, 0, 0, 0);

		beaconActionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				APIClient.edit(editBeaconFromFields(), editDurationFromField(), myAPIHandler,
						BeaconEditActivity.this);
				showDialog("Updating beacon...");
			}
		});

		beaconSecondaryActionButton.setVisibility(View.VISIBLE);
		beaconSecondaryActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.beacon_leave, 0, 0, 0);
		// The secondary button is the leave button
		beaconSecondaryActionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mBeacon != null){
					APIClient.leave(mBeacon.getBeaconId(), myAPIHandler,
							BeaconEditActivity.this);
					showDialog("Leaving beacon...");
				}else
					Toast.makeText(BeaconEditActivity.this,
							"Something went wrong -- I don't know which beacon you're viewing",
							Toast.LENGTH_SHORT).show();
			}
		});

		
		View phoneLayout = findViewById(R.id.phoneLayout);
		View emailLayout = findViewById(R.id.emailLayout);
		String text;
		boolean numberGiven = false;
		if ( mBeacon.getTelephone().equals("") ){
			text = "No phone number given.";
		} else {
			text = phone.getText().toString();
			numberGiven = true;
		}
		TextClickToEdit phoneC2E = convertToTextClickToEdit(phoneLayout, text, false);
		Linkify.addLinks(phoneC2E.getTextView(), Linkify.PHONE_NUMBERS);
		if ( numberGiven )
			phoneC2E.enableSmsButton(SMS_URI_PREFIX+PhoneNumberUtils.stripSeparators(phone.getText().toString()));

		if ( mBeacon.getEmail().equals("") ){
			text = "No email address given.";
		} else {
			text = email.getText().toString();
		}
		TextClickToEdit emailC2E = convertToTextClickToEdit(emailLayout, text, false);
		Linkify.addLinks(emailC2E.getTextView(), Linkify.EMAIL_ADDRESSES);

		// show QR button
		qrButton.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
			qrButton.handleResult(scanResult);
		}
	}

	private void setUpForView(Bundle savedInstanceState, Intent startingIntent) {
		// Set title text
		beaconTitleTV.setText(R.string.beaconDetails);

		mBeacon = startingIntent.getParcelableExtra(EXTRA_BEACON); // do this first
		loadBeaconData();

		// Turn the spinners into textClickToEdits

		String courseText = courseSpinner.getSelectedItem().toString();
		int beacNum = mBeacon.getVisitors();
		if ( beacNum == 1 ){
			courseText += " (1 person)";
		} else {
			courseText += " ("+beacNum+" people)";
		}

		convertToTextClickToEdit(courseSpinner,courseText,true);
		convertToTextClickToEdit(workingOnSpinner,workingOnSpinner.getSelectedItem().toString(),true);

		// Change the "expires" text
		expiresTV.setText(R.string.expiresAt);
		//expiresSpinner.setVisibility(View.GONE);
		//expiresTimeTV.setVisibility(View.VISIBLE);
		expiresTimeTV = convertToTextClickToEdit(expiresSpinner,expiresTimeFormatted,true);
		
		locationTV.setVisibility(View.VISIBLE);

		EditText ets[] = {phone, email, details};
		for (EditText e : ets) {
			e.setFocusable(false);
		}

		// make the details have a different hint if nothing was given
		details.setHint(R.string.detailHintView);

		beaconActionButton.setText(R.string.joinBeacon);
		// Set the drawable on the action button
		beaconActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.beacon_join, 0, 0, 0);

		beaconActionButton.setOnClickListener(new JoinBeaconClickListener());

		if ( mBeacon == null )
			return;

		// don't show contact details if they weren't filled in
		View phoneLayout = findViewById(R.id.phoneLayout);
		View emailLayout = findViewById(R.id.emailLayout);
		if ( mBeacon.getTelephone().equals("") ){
			phoneLayout.setVisibility(View.GONE);
		} else {
			TextClickToEdit phoneC2E = convertToTextClickToEdit(phoneLayout, phone.getText().toString(), true);
			Linkify.addLinks(phoneC2E.getTextView(), Linkify.PHONE_NUMBERS);
			phoneC2E.enableSmsButton(SMS_URI_PREFIX+PhoneNumberUtils.stripSeparators(phone.getText().toString()));
		}

		if ( mBeacon.getEmail().equals("") ){
			emailLayout.setVisibility(View.GONE);
		} else {
			TextClickToEdit emailC2E = convertToTextClickToEdit(emailLayout, email.getText().toString(), true);
			Linkify.addLinks(emailC2E.getTextView(), Linkify.EMAIL_ADDRESSES);
		}

		if ( mBeacon.getTelephone().equals("") && mBeacon.getEmail().equals("") )
			contact.setVisibility(View.GONE);
		
		// start getting the user's location
		userLocator = new UserLocator(locationAcquiredCallback);
		userLocator.startLocating();
	}

	protected final class JoinBeaconClickListener implements OnClickListener {

		Builder maybeJoinBuilder = new AlertDialog.Builder(BeaconEditActivity.this)
		    .setPositiveButton(R.string.join, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int which) {
		    		dialog.dismiss();
		    		joinBeacon(); // Join beacon anyway at user's insistence
		    	}
		    })
		    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int which) {
		    		dialog.dismiss();
		    	}
		    });

		AlertDialog noLocationDialog;
		AlertDialog tooFarDialog;

		public JoinBeaconClickListener() {
			tooFarDialog = maybeJoinBuilder
					.setMessage("You do not appear to be close to this beacon. Are you sure you want to join?")
					.create();
			noLocationDialog = maybeJoinBuilder
					.setMessage("I can't tell where you are. Are you sure you want to join?")
					.create();
		}

		public void onClick(View v) {
			if (mBeacon != null) {  // If we have a defined beacon

				if (userLocator!=null && userLocator.isReady()) {
					// the distance has been calculated by locationAcquiredCallback

					if (distanceToBeacon>DISTANCE_CUTOFF_JOIN_WARNING_METERS) {
						// we are outside the cutoff distance
						// So ask if they really want to do this
						tooFarDialog.show();
					}
					else {  // We are within the cutoff distance, so join the beacon.
						joinBeacon();
					}
				}
				else {				// if we don't have a defined location or locator
									// then ask if they really want to join
					noLocationDialog.show();
				}
			} else					// if we don't have a defined beacon, complain to user
				Toast.makeText(BeaconEditActivity.this,
						"Something went wrong -- I don't know which beacon you're viewing",
						Toast.LENGTH_SHORT).show();
		}

		private void joinBeacon() {
			APIClient.join(mBeacon.getBeaconId(), myAPIHandler,
					BeaconEditActivity.this);
			showDialog("Joining beacon...");
		}
	}

	private TextClickToEdit convertToTextClickToEdit(final View s, String text, boolean hideButton) {
		return convertToTextClickToEdit(s, text, hideButton, new Runnable() {public void run(){}});
	}
	
	private TextClickToEdit convertToTextClickToEdit(final View s, String text, boolean hideButton, final Runnable callback) {
		LinearLayout layout = (LinearLayout) s.getParent();
		// find out where the spinner is
		int spinnerIndex = -1;
		for ( int j=0; j<layout.getChildCount(); j++ ){
			if ( s.equals(layout.getChildAt(j)) ){
				spinnerIndex = j;
				break;
			}
		}
		
		if ( spinnerIndex == -1 ){
			Log.e(TAG, "Crap, didn't find the spinner");
			return null;
		}
		
		// make the spinner disappear
		s.setVisibility(View.GONE);
		
		// new text view
		final TextClickToEdit textClick = new TextClickToEdit(this);
		textClick.setText(text);
		
		// make the button reverse the process
		textClick.setButtonClickListener(new OnClickListener() {
			public void onClick(View v) {
				textClick.setVisibility(View.GONE);
				s.setVisibility(View.VISIBLE);
				callback.run();
			}
		});
		
		// hide the button if asked
		if ( hideButton )
			textClick.hideButton();
		
		//stick it in
		layout.addView(textClick, spinnerIndex);
		return textClick;
	}

	private void loadBeaconData() {
		// TODO What do we do if somebody did not call this properly?
		

		if (mBeacon == null) // FAILURE
			return;

		// Load the course name
		setCourseSpinnerItem(mBeacon.getCourseName());
		phone.setText(mBeacon.getTelephone());
		email.setText(mBeacon.getEmail());
		setWorkingOn(mBeacon.getWorkingOn());
		details.setText(mBeacon.getDetails());
		expiresTimeFormatted = df.format(mBeacon.getExpires());
	}

	private void setWorkingOn(String workingOn) {
		int position = workingOnAdapter.getPosition(workingOn);
		if ( position == -1 ){ // -1 means it didn't find it
			// add it to the spinner
			position = workingOnAdapter.getCount()-1;
			addToWorkingOn(position, workingOn);
		}
		workingOnSpinner.setSelection(position);
	}

	protected void showDialog(String message) {
		currentDialog = ProgressDialog.show(BeaconEditActivity.this, "", message,
				// I don't know what indeterminate is
				true,
				// make cancelable
				true, new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				APIClient.cancel(BeaconEditActivity.this);
			}
		});
	}
	
	protected void addToWorkingOn(final int index, String text) {
		workingOnAdapter.insert(text, index);
		workingOnAdapter.notifyDataSetChanged();
	}

}
