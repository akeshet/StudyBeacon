package com.teamblobby.studybeacon;

import com.teamblobby.studybeacon.network.APIHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class BeaconUriActivity extends Activity implements APIHandler{
	
	private static final String TAG = "BeaconUriActivity";
	private int beaconId = -1;
	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    Uri inputUri = getIntent().getData();
	    
	    if ( inputUri.getAuthority() == "beacon" ) {
	    	// good, fetch beacon
	    	beaconId = Integer.parseInt(inputUri.getPath());
	    	dialog = ProgressDialog.show(this, "", "Getting beacon data");	    				
	    	
	    	
	    	
	    } else {
	    	// bad, unknown
	    	Log.e(TAG, "error, didn't understant uri authority");
	    	finish();
	    }
	}

	public Activity getActivity() {
		return this;
	}

	public void onSuccess(APICode code, Object response) {
		// TODO Auto-generated method stub
		
	}

	public void onFailure(APICode code, Throwable e) {
		// TODO Auto-generated method stub
		
	}

}
