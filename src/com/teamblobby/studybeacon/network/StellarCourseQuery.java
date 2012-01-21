package com.teamblobby.studybeacon.network;

import org.json.JSONArray;
import org.json.JSONException;

public class StellarCourseQuery extends StellarQuery {


	private static final String command = "courses";

	@Override
	protected String getCommand() {
		return command;
	}

	@Override
	protected String getFullURL() {
		return this.getCommandURL();
	}

	@Override
	protected String[] extractNames(JSONArray jsonArray) throws JSONException {
		
		int length = jsonArray.length();
		String[] returnStrings = new String[length];
		
		for ( int j=0; j<length; j++){
			returnStrings[j] = jsonArray.getJSONObject(j).getString("short");
		}
		
		return returnStrings;
	}

}
