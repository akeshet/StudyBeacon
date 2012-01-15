package com.teamblobby.studybeacon;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class SBMyCoursesActivity extends ListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.mycourses);
    	
    	// Load the courses list and add them one by one
    	String[] courses = Global.getCourses();
    	ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.mycoursesrow, R.id.mcrCourseNameTextView, courses);
    			
    	setListAdapter(aa);
    	
    }
	
}
