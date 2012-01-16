package com.teamblobby.studybeacon;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;

public class SBMyCoursesActivity extends ListActivity {
	
	ListView myListView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.mycourses);
    	
    	// add footer button
    	Button footerButton = new Button(this.getApplicationContext());
    	footerButton.setText("Add New Class");
    	myListView = this.getListView();
    	myListView.addFooterView(footerButton);
    	
    	// Load the courses list and add them one by one
    	String[] courses = Global.getCourses();
    	ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.mycoursesrow, R.id.mcrCourseNameTextView, courses);
    	
    	setListAdapter(aa);
    	
    	
    }
	
}
