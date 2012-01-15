package com.teamblobby.studybeacon;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Global extends Application {

	// This is the name of the shared prefs file
	public final static String PREFS = "prefs";

	private static final String TAG = "Global";
	
	// Shared Data
	public static SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG,"onCreate()");
		//mContext = this; 
		// load Mobile Web Domain preferences
		try {
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
		}
		catch (RuntimeException e) {
			Log.d(TAG,"error getting prefs: " + e.getMessage() + "\n" + e.getStackTrace());
		}
		
		// res = this.getResources();
		
		//Handler uiHandler = new Handler();

		//Global.getVersionInfo(mContext, uiHandler);

		//Global.updateData(mContext, uiHandler);
	}
	
}
