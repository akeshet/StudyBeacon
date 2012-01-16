package com.teamblobby.studybeacon;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    
    public void checkClicked(View view) {
    	CheckBox me = (CheckBox) view;
    	Log.d("SBMyCoursesActivity", "checkClicked");
    	View parent = (View) view.getParent();
    	TextView textView = (TextView) parent.findViewById(R.id.mcrCourseNameTextView);
    	String temp = "checked";
    	if (!me.isChecked())
    		temp = "unchecked";
    	Toast.makeText(this, "You " + temp + " the star for course " + textView.getText(), Toast.LENGTH_SHORT).show();    	
    }
    
	
}
