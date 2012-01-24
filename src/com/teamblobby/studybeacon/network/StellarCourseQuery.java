package com.teamblobby.studybeacon.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamblobby.studybeacon.datastructures.*;

public class StellarCourseQuery extends StellarQuery {

	private static final String STELLAR_DESCRIPTION_JSON_KEY = "name";
	private static final String STELLAR_NAME_JSON_KEY = "short";
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
	protected List<CourseListable> extractCourses(JSONArray jsonArray) throws JSONException {
		
		int length = jsonArray.length();
		List<CourseListable> returnCourseList = new ArrayList<CourseListable>();
		
		String nameId;
		String description;
		JSONObject jsonObj;
		for ( int j=0; j<length; j++){
			jsonObj = jsonArray.getJSONObject(j);
			nameId = jsonObj.getString(STELLAR_NAME_JSON_KEY);
			description = jsonObj.getString(STELLAR_DESCRIPTION_JSON_KEY);
			returnCourseList.add(new CourseCategory(nameId, description));
		}
		
		return returnCourseList;
	}

}
