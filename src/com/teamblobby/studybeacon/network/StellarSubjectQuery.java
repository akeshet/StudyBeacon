package com.teamblobby.studybeacon.network;

import org.json.JSONArray;
import org.json.JSONException;

public class StellarSubjectQuery extends StellarQuery {

	private static final String command = "subjectList";
	private final String category;
	
	public StellarSubjectQuery(String categoryIn) {
		this.category = categoryIn;
	}

	@Override
	protected String getCommand() {
		return command;
	}

	@Override
	protected String getFullURL() {
		return this.getCommandURL()+"&id="+this.category;
	}

	@Override
	protected String[] extractNames(JSONArray jsonArray) throws JSONException {

		int length = jsonArray.length();
		String[] returnStrings = new String[length];
		
		for ( int j=0; j<length; j++){
			returnStrings[j] = jsonArray.getJSONObject(j).getString("masterId");
		}
		
		return returnStrings;
	}

}
