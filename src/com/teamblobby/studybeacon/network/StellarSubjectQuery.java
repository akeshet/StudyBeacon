package com.teamblobby.studybeacon.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.teamblobby.studybeacon.datastructures.*;

public class StellarSubjectQuery extends StellarQuery {

	private static final String STELLAR_DESCRIPTION_JSON_KEY = "title";
	private static final String STELLAR_PRETTYNAME_JSON_KEY = "name";
	private static final String STELLAR_NAME_JSON_KEY = "masterId";
	private static final String command = "subjectList";
	private static final String TAG = "StellarSubjectQuery";
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
	protected List<CourseListable> extractCourses(JSONArray jsonArray) throws JSONException {

		int length = jsonArray.length();
		List<CourseListable> returnSubjects = new ArrayList<CourseListable>();
		JSONObject jsonObj;
		String nameId;
		String prettyName;
		String description;
		
		for ( int j=0; j<length; j++ ){
			jsonObj = jsonArray.getJSONObject(j);
			nameId = jsonObj.getString(STELLAR_NAME_JSON_KEY);
			prettyName = jsonObj.getString(STELLAR_PRETTYNAME_JSON_KEY);
			description = jsonObj.getString(STELLAR_DESCRIPTION_JSON_KEY);
			returnSubjects.add(new CourseInfoSimple(nameId, description, prettyName));
		}
		Log.d(TAG, returnSubjects.toString());
		return returnSubjects;
	}

}
