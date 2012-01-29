package com.teamblobby.studybeacon.ui;

import java.util.Arrays;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.teamblobby.studybeacon.BeaconUriActivity;
import com.teamblobby.studybeacon.R;
import com.teamblobby.studybeacon.datastructures.BeaconInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class QRButton extends ImageView {
	private Context context;

	public QRButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.qrIntentMaker = context instanceof Activity?
				new IntentIntegrator((Activity) context):
					null; // TODO maybe this doesn't work?
		setup(context);
	}
	
	public QRButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.qrIntentMaker = context instanceof Activity?
				new IntentIntegrator((Activity) context):
					null; // TODO maybe this doesn't work?
		setup(context);
	}
	
	public QRButton(Context context) {
		super(context);
		this.qrIntentMaker = context instanceof Activity?
				new IntentIntegrator((Activity) context):
					null; // TODO maybe this doesn't work?
		setup(context);
	}

	private static final String TAG = "QRButton";
	final IntentIntegrator qrIntentMaker;
	
	private void setup(Context context) {
		this.setImageDrawable(getResources().getDrawable(R.drawable.qricon));
		this.updateButton(null);
		
		if ( this.qrIntentMaker == null )
			Log.e(TAG,"context isn't activity, this is bad.");

		this.context = context;

	}
	
	public void updateButton(final BeaconInfo beacon){
		// if the beacon is null, make a scan type button
		if ( beacon == null ) {
			this.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// initiate scan
					qrIntentMaker.initiateScan(Arrays.asList("QR_CODE"));
				}
			});
		} else {
			this.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// show QR code
					qrIntentMaker.shareText("beacon://beacon/"+beacon.getBeaconId());
				}
			});
		} 
	}

	public void handleResult(IntentResult scanResult) {

		// If no scanResult or no contents, then they hit back.
		if (scanResult == null || scanResult.getContents() == null)
			return;

		Uri uri = Uri.parse(scanResult.getContents());
		
		if (uri == null || !uri.getScheme().equals("beacon") ){
			Log.d(TAG, uri.toString());
			Toast.makeText(context, "Didn't understand QR code", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Log.d(TAG, context.toString());
		
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		i.putExtra(BeaconUriActivity.EXTRA_AUTOJOIN, true);
		context.startActivity(i);
		
	}

}
