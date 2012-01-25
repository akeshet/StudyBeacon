package com.teamblobby.studybeacon.network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.loopj.android.http.*;
import com.teamblobby.studybeacon.Global;
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


	protected static BeaconInfo parseJSONObjBeaconInfo(JSONObject bObj) throws JSONException, ParseException {
		GeoPoint point = new GeoPoint(bObj.getInt(LAT_STR), bObj.getInt(LON_STR));

		Date created = df.parse(bObj.getString(CREATED_STR));
		Date expires = df.parse(bObj.getString(EXPIRES_STR));

		return new BeaconInfoSimple(bObj.getInt(BEACID_STR), bObj.getString(COURSE_STR),
				point, bObj.getInt(COUNT_STR),
				bObj.getString(DETAILS_STR),
				bObj.getString(TELEPHONE_STR),bObj.getString(EMAIL_STR),
				created, expires);
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

	public static void query(int LatE6Min, int LatE6Max, int LonE6Min, int LonE6Max, List<String> queryCourses,
			final APIHandler handler) {
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

		get(QUERY_URL, params, new QueryJsonHandler(handler));

	}

	protected static class QueryJsonHandler extends JsonHttpResponseHandler {

		protected final APIHandler handler;

		public QueryJsonHandler(final APIHandler handler) {
			// TODO Auto-generated constructor stub
			this.handler = handler;
		}

		@Override
		public void onSuccess(JSONArray response) {

			// the response should be an array of objects

			final ArrayList<BeaconInfo> beacons = new ArrayList<BeaconInfo>();

			try {
				for(int i =0; i < response.length(); i++)
					beacons.add(parseJSONObjBeaconInfo(response.getJSONObject(i)));

				// Call the handler's function
				handler.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						handler.onSuccess(APIHandler.APICode.CODE_QUERY, beacons);
					}
				});
			}
			catch (final Exception e) {
				// TODO
				e.printStackTrace();
				handler.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						handler.onFailure(APIHandler.APICode.CODE_QUERY, e);
					}
				});
			}

		}

		@Override
		public void onFailure(final Throwable arg0) {
			// TODO Do we do this first or last?
			super.onFailure(arg0);
			handler.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					handler.onFailure(APIHandler.APICode.CODE_QUERY,arg0);
				}
			});
		}
	}

	////////////////////////////////////////////////////////////////
	// Interface for doing an add

	public final static String ADD_URL = "add.py";

	public final static String DURATION_STR = "Duration";
	public final static String DEVID_STR    = "DeviceId";

	public static void add(BeaconInfo beacon, int duration, final APIHandler handler) {
		RequestParams params = new RequestParams();

		params.put(COURSE_STR,    beacon.getCourseName());
		params.put(LAT_STR,       Integer.toString(beacon.getLoc().getLatitudeE6()));
		params.put(LON_STR,       Integer.toString(beacon.getLoc().getLongitudeE6()));
		params.put(DEVID_STR,     Global.getMyIdString());
		params.put(DURATION_STR,  Integer.toString(duration));
		params.put(TELEPHONE_STR, beacon.getTelephone() );
		params.put(EMAIL_STR,     beacon.getEmail() );
		params.put(DETAILS_STR,   beacon.getDetails());

		Log.d(TAG,"add string " + params.toString());

		post(ADD_URL, params, new AddJsonHandler(handler));

	}

	protected static class AddJsonHandler extends JsonHttpResponseHandler {

		protected final APIHandler handler;

		public AddJsonHandler(APIHandler handler) {
			this.handler = handler;
		}

		@Override
		public void onSuccess(JSONObject bObj) {
			// TODO Auto-generated method stub
			super.onSuccess(bObj);

			try {
				final BeaconInfo beacon = parseJSONObjBeaconInfo(bObj);
				handler.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						handler.onSuccess(APIHandler.APICode.CODE_ADD,beacon);
					}
				});
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				handler.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						handler.onFailure(APIHandler.APICode.CODE_ADD,e);
					}
				});
			}
		}

		@Override
		public void onFailure(final Throwable arg0) {
			// TODO Auto-generated method stub
			super.onFailure(arg0);
			handler.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					handler.onFailure(APIHandler.APICode.CODE_ADD,arg0);
				}
			});
		}

	}
	
	////////////////////////////////////////////////////////////////
	// Interface for doing a join

	public final static String JOIN_URL = "join.py";


	public static void join(int BeaconId, final APIHandler handler) {

		RequestParams params = new RequestParams();

		params.put(DEVID_STR, Global.getMyIdString());
		params.put(BEACID_STR, Integer.toString(BeaconId));

		get(JOIN_URL, params, new JoinJsonHandler(handler));

	}

	protected static class JoinJsonHandler extends JsonHttpResponseHandler {

		protected final APIHandler handler;
		
		public JoinJsonHandler(APIHandler handler) {
			// TODO Auto-generated constructor stub
			this.handler = handler;
		}

		@Override
		public void onSuccess(JSONObject bObj) {
			// TODO Auto-generated method stub
			super.onSuccess(bObj);

			try {
				final BeaconInfo beacon = parseJSONObjBeaconInfo(bObj);
				handler.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						handler.onSuccess(APIHandler.APICode.CODE_JOIN,beacon);
					}
				});
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				handler.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						handler.onFailure(APIHandler.APICode.CODE_JOIN,e);
					}
				});
			}
		}

		@Override
		public void onFailure(final Throwable arg0) {
			// TODO Auto-generated method stub
			super.onFailure(arg0);
			handler.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					handler.onFailure(APIHandler.APICode.CODE_JOIN,arg0);
				}
			});
		}

	};


	////////////////////////////////////////////////////////////////
	// Interface for doing a leave

	public final static String LEAVE_URL = "leave.py";


	public static void leave(int BeaconId, final APIHandler handler) {

		RequestParams params = new RequestParams();

		params.put(DEVID_STR, Global.getMyIdString());
		params.put(BEACID_STR, Integer.toString(BeaconId));

		get(LEAVE_URL, params, new LeaveHTTPHandler(handler));

	}

	public static class LeaveHTTPHandler extends AsyncHttpResponseHandler {

		protected final APIHandler handler;

		public LeaveHTTPHandler(APIHandler handler) {
			this.handler = handler;
		}

		@Override
		public void onSuccess(String arg0) {
			// TODO Auto-generated method stub
			super.onSuccess(arg0);
			Log.d(TAG,"Got leave success, response was "+ arg0);
			handler.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					handler.onSuccess(APIHandler.APICode.CODE_LEAVE,null);
				}
			});
		}

		@Override
		public void onFailure(final Throwable arg0) {
			// TODO Auto-generated method stub
			super.onFailure(arg0);
			handler.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					handler.onFailure(APIHandler.APICode.CODE_LEAVE,arg0);
				}
			});
		}

	}

}
