package com.teamblobby.studybeacon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.loopj.android.http.*;
import com.teamblobby.studybeacon.datastructures.*;

import org.json.*;

public class APIClient {
	
	public static final String TAG = "APIClient";
	
    public static final String BASE_URL = "http://leostein.scripts.mit.edu/StudyBeacon/";
	
    private static AsyncHttpClient client = new AsyncHttpClient();
	
    
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	
    
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	    client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
	    return BASE_URL + relativeUrl;
	}
	
	
    ////////////////////////////////
	// Interface to doing a query.
	
	public final static String QUERY_URL = "query.py";
	
	// These strings are for the format of the query
	public final static String COURSE_STR = "Course";
	public final static String COURSE_DELIM_STR = ":";
	public final static String LAT_MIN_STR = "LatE6Min";
	public final static String LAT_MAX_STR = "LatE6Max";
	public final static String LON_MIN_STR = "LonE6Min";
	public final static String LON_MAX_STR = "LonE6Max";
	
	// These strings are for the format of the response
	public final static String BEACID_STR = "BeaconId";
	public final static String LAT_STR = "LatE6";
	public final static String LON_STR = "LonE6";
	public final static String DETAILS_STR = "Details";
	public final static String TELEPHONE_STR = "Telephone";
	public final static String EMAIL_STR = "Email";
	public final static String COUNT_STR = "Count";
	public final static String CREATED_STR = "Created";
	public final static String EXPIRES_STR = "Expires";
	
	// TODO Maybe change this to a list or array of CourseInfo instead of course names
	public static void query(int LatE6Min, int LatE6Max, int LonE6Min, int LonE6Max, List<String> queryCourses,
			final SBAPIHandler handler) {
		RequestParams params = new RequestParams();
		
		params.put(LAT_MIN_STR,Integer.toString(LatE6Min));
		params.put(LAT_MAX_STR,Integer.toString(LatE6Max));
		params.put(LON_MIN_STR,Integer.toString(LonE6Min));
		params.put(LON_MAX_STR,Integer.toString(LonE6Max));
		
		if (queryCourses == null)
			return;
		
		String courseList = TextUtils.join(COURSE_DELIM_STR, queryCourses); 
		
		params.put(COURSE_STR, courseList);
				
		Log.d(TAG,"Query string " + params.toString());
		
		get(QUERY_URL, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray response) {

				// the response should be an array of objects
				
				final ArrayList<BeaconInfo> beacons = new ArrayList<BeaconInfo>();
				
				try {
				
				for(int i =0; i < response.length(); i++) {
					
					JSONObject bObj = response.getJSONObject(i);
					
					GeoPoint point = new GeoPoint(bObj.getInt(LAT_STR), bObj.getInt(LON_STR));
					
					Date created = df.parse(bObj.getString(CREATED_STR));
					Date expires = df.parse(bObj.getString(EXPIRES_STR));
					
					beacons.add(new BeaconInfoSimple(bObj.getInt(BEACID_STR), bObj.getString(COURSE_STR),
							point, bObj.getInt(COUNT_STR),
							bObj.getString(DETAILS_STR),
							// TODO FIX THIS; USE BOTH TEL AND EMAIL
							bObj.getString(TELEPHONE_STR),bObj.getString(EMAIL_STR),
							created, expires));
					
				}
					
				// Call the handler's function
				handler.getActivity().runOnUiThread(new Runnable() {

					public void run() {
						handler.onQuerySuccess(beacons);
					}
					
				});
				
				}
				
				catch (Exception e) {
					// TODO do something here??
					Log.e(TAG,e.getMessage());
				}
				
			}

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Do we do this first or last?
				super.onFailure(arg0);
				handler.getActivity().runOnUiThread(new Runnable() {
					
					public void run() {
						handler.onQueryFailure();
					}
				});
			}
		});
		
	}
	
}
