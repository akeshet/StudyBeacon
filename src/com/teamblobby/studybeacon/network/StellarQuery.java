package com.teamblobby.studybeacon.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import android.util.Log;

public abstract class StellarQuery {
	private final static String baseURL = "http://m.mit.edu/api/index.php?module=stellar&";
	private final static String ROOT_CATEGORY = "_root";
	private final static HttpClient httpclient = new DefaultHttpClient();
	private static final String TAG = "StellarQuery";
	
	public static StellarQuery newQuery(String category){
		if ( category.equals(ROOT_CATEGORY) ){
			return new StellarCourseQuery();
		} else {
			return new StellarSubjectQuery(category);
		}
	}
	
	protected String getCommandURL(){
		return StellarQuery.baseURL+"command="+this.getCommand();
	}
	
	protected abstract String getFullURL();
	
	protected abstract String getCommand();

	public String[] execute() {
		// create the url
		String URL = this.getFullURL();
		
		// execute request
		String response;
		try {
			response = this.makeRequest(URL);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "crap HTTP request failed");
			return null;
		}
		// get response

		// parse JSON
		JSONArray jsonArray;
		String[] names;
		try {
			jsonArray = new JSONArray(response);

			// extract response 
			names = extractNames(jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "crap JSON parse failed");
			return null;
		}
		
		//not sure how.
		return names; 
	}
	
	protected abstract String[] extractNames(JSONArray jsonArray) throws JSONException ;

	protected String makeRequest(String URL) throws IOException{
		HttpResponse response = httpclient.execute(new HttpGet(URL));
	    StatusLine statusLine = response.getStatusLine();
	    String responseString = null;
	    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        response.getEntity().writeTo(out);
	        out.close();
	        responseString = out.toString();
	        //response.getEntity().getContent().close();
	    } else{
	        //Closes the connection.
	        response.getEntity().getContent().close();
	        throw new IOException(statusLine.getReasonPhrase());
	    }
	    return responseString;
	}
}
