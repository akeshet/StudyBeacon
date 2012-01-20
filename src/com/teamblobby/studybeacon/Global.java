package com.teamblobby.studybeacon;

import java.util.ArrayList;

import android.app.Application;

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
	
	public static String[] getCourses() {
    	String delim = res.getString(R.string.coursedelim);
    	String courseList = Global.prefs.getString(COURSES_STR, "6.570"+delim+"8.901");
    	
    	return courseList.split(delim);
	}
	
	/**
	 * This is a placeholder function. Eventually it will query the local sqlite database for courses, and return course infos which
	 * point at that database. Changes to those courseinfos (from, for instance, mycoursesactivity) will then be reflected in the database.
	 * 
	 */
	public static CourseInfo[] getCourseInfos() {
		String [] courseNames = Global.getCourses();
		for (String courseName : courseNames) {
			//CourseInfoSimple info = new CourseInfoSimple(courseName,true);
			CourseInfo info = new CourseInfoSqlite(CourseInfoSqlite.MYCOURSES_TABLE, courseName, true, false);
		}
		
		ArrayList<CourseInfoSqlite>  courseInfos= CourseInfoSqlite.fetchTable(CourseInfoSqlite.MYCOURSES_TABLE);
		
		return courseInfos.toArray(new CourseInfo[]{});
	}
	
	public String myIdString() {
		return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	}
	
}
