package com.teamblobby.studybeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SBBeaconEditActivity extends Activity {
	
	public enum OperationMode {
		MODE_NEW,
		MODE_EDIT,
		MODE_VIEW
	}
	
	protected OperationMode mode;
	
	Spinner courseSpinner;
	Spinner expiresSpinner;
	Spinner workingOnSpinner;
	EditText detailsEdit;
	Button actionButton;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beacon);
		
		loadUIEls();
		
		// This is the intent that started us
		Intent startingIntent = getIntent();
		String startingAction = startingIntent.getAction();
		
		// Figure out what to do
		if (startingAction == Intent.ACTION_VIEW) {
			mode = OperationMode.MODE_VIEW;
		} else if (startingAction == Intent.ACTION_EDIT) {
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
		courseSpinner    = (Spinner)  findViewById(R.id.courseSpinner);
		expiresSpinner   = (Spinner)  findViewById(R.id.expiresSpinner);
		workingOnSpinner = (Spinner)  findViewById(R.id.workingOnSpinner);
		detailsEdit      = (EditText) findViewById(R.id.detailsEdit);
		actionButton     = (Button)   findViewById(R.id.actionButton);
	}

	private void setUpForNew(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Auto-generated method stub
		
	}

	private void setUpForEdit(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Auto-generated method stub
		
	}

	private void setUpForView(Bundle savedInstanceState, Intent startingIntent) {
		// TODO Auto-generated method stub
		
	}

}
