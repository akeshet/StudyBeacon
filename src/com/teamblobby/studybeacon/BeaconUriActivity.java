package com.teamblobby.studybeacon;

import com.teamblobby.studybeacon.datastructures.BeaconInfo;
import com.teamblobby.studybeacon.network.APIClient;
import com.teamblobby.studybeacon.network.APIHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BeaconUriActivity extends Activity implements APIHandler{
	
	public static final String EXTRA_AUTOJOIN = "com.teamblobby.studybeacon.BeaconUri.Activity.AUTO_JOIN";
	private static final String TAG = "BeaconUriActivity";
	private int beaconId = -1;
	private ProgressDialog dialog;
	private boolean autoJoin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    Uri inputUri = getIntent().getData();
	    autoJoin = getIntent().getBooleanExtra(EXTRA_AUTOJOIN, false);
	    
	    boolean thingsHappening = false;
	    
	    if ( ( inputUri != null ) && inputUri.getAuthority().equals("beacon") ) {
	    	// good, fetch beacon
	    	
	    	beaconId = (int) ContentUris.parseId(inputUri);
	    	Log.d(TAG,"beaconid="+beaconId);
	    	if ( autoJoin ){
	    		dialog = ProgressDialog.show(this, "", "Joining beacon...");
	    		Log.d(TAG,"sending api client call to join");
	    		APIClient.join(beaconId, this);
	    	} else {
	    		dialog = ProgressDialog.show(this, "", "Getting beacon data...");
	    		Log.d(TAG,"sending api client call to getbeacon");
	    		APIClient.getBeacon(beaconId, this);
	    	}
	    	
	    	thingsHappening = true;
	  
	    	
	    } else {
	    	// bad, unknown
	    	Log.e(TAG, "error, didn't understant uri authority");
	    	finish();
	    }
	    
	    if ( !thingsHappening )
	    	finish();
	}

	public Activity getActivity() {
		return this;
	}

	public void onSuccess(APICode code, Object response) {
		dialog.dismiss();
		switch (code) {
		case CODE_GETBEACON:
			if (response == null){
				Toast.makeText(this, "Invalid beacon", Toast.LENGTH_SHORT).show();
				
			} else {
				Intent intent = new Intent(this, BeaconEditActivity.class);
				intent.setAction(BeaconEditActivity.ACTION_VIEW);
				intent.putExtra(BeaconEditActivity.EXTRA_BEACON, (BeaconInfo) response);
				this.startActivity(intent);
			}
			break;
		case CODE_JOIN:
			Global.setCurrentBeacon((BeaconInfo) response);
			Toast.makeText(this, "Beacon joined successfully", Toast.LENGTH_SHORT).show();
			Global.updateBeaconRunningNotification();
			break;
		default:
			break;
		}
		
		finish();
	}

	public void onFailure(APICode code, Throwable e) {
		dialog.dismiss();
		Toast.makeText(this, "Problem communicating with server.", Toast.LENGTH_SHORT).show();
		finish();
	}

}
