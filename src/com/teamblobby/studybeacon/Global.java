package com.teamblobby.studybeacon;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

public class Global extends Application {

	// This is the name of the shared prefs file
	public final static String PREFS = "prefs";

	private static final String TAG = "Global";
	
	public final static String COURSES_STR = "courses";
	
	// Shared Data
	public static SharedPreferences prefs;
	public static Resources res;

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
		
		res = this.getResources();
		
		//Handler uiHandler = new Handler();

		//Global.getVersionInfo(mContext, uiHandler);

		//Global.updateData(mContext, uiHandler);
	}
	
	public static String[] getCourses() {
    	String delim = res.getString(R.string.coursedelim);
    	String courseList = Global.prefs.getString(COURSES_STR, "6.570"+delim+"8.901");
    	return courseList.split(delim);
	}
	
}
