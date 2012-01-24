package com.teamblobby.studybeacon;

import java.util.List;
import java.util.Random;

import android.app.Application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

import com.teamblobby.studybeacon.datastructures.*;

public class Global extends Application {

	// This is the name of the shared prefs file
	public final static String PREFS = "prefs";

	private static final String TAG = "Global";

	public final static String COURSES_STR = "courses";

	// Shared Data
	public static SharedPreferences prefs;
	public static Resources res;

	// Singleton reference to the application
	public static Application application;

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

		Global.application = this;

		//Handler uiHandler = new Handler();

		//Global.getVersionInfo(mContext, uiHandler);

		//Global.updateData(mContext, uiHandler);
	}

	public static List<String> getCourses() {

		return CourseInfo.getCourseNames(getMyCourseInfos());
	}

	/**
	 * This is a placeholder function. Eventually it will query the local sqlite database for courses, and return course infos which
	 * point at that database. 
	 * 
	 * Before fetching this list from the table, old rows in the table which are no longer starred will be flushed from the table.
	 */
	public static List<CourseInfo> getMyCourseInfos() {

		CourseInfoSqlite.flushUnstarred(CourseInfoSqlite.MYCOURSES_TABLE);		
		List<CourseInfo>  courseInfos= CourseInfoSqlite.fetchTable(CourseInfoSqlite.MYCOURSES_TABLE);

		return courseInfos;
	}
	
	private static Random random = new Random();
	
	public static String getMyIdString() {
		//return Secure.getString(application.getContentResolver(), Secure.ANDROID_ID);
		return Long.toHexString(random.nextLong());
	}

	public static void goHome(Context c) {
		Intent i = new Intent(c, SBMapActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(i);
	}

	/**
	 * Returns the current beacon, if we are checked into an existing beacon.
	 * Otherwise, returns null.
	 * @return
	 */
	public static BeaconInfoSqlite getCurrentBeacon() {
		return BeaconInfoSqlite.getCurrentBeacon();
	}

	/**
	 * Sets the current beacon (in sqlite database) to the passed beacon.
	 * (by making a copy of the passed beacon and inserting into the database).
	 * Sets current beacon to none if passed null.
	 * @param copyMe
	 */
	public static void setCurrentBeacon(BeaconInfo beacon) {
		BeaconInfoSqlite.setCurrentBeacon(beacon);
	}
}
