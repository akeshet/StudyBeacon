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
    
    public void starCheckClicked(View view) {
    	Log.d("SBMyCoursesActivity", "starCheckClicked");
    	
    	CheckBox me = (CheckBox) view;
    	String text = getCourseNameFromClickedView(view);
    	
    	String temp = "checked";
    	if (!me.isChecked())
    		temp = "unchecked";
    	Toast.makeText(this, "You " + temp + " the star for course " + text, Toast.LENGTH_SHORT).show();    	
    }
    
    public void notifyCheckClicked(View view) {
    	Log.d("SBMyCoursesActivity", "notifyCheckClicked");
    	
    	CheckBox me = (CheckBox) view;
    	String text = getCourseNameFromClickedView(view);
    	
    	String temp = "checked";
    	if (!me.isChecked())
    		temp = "unchecked";
    	Toast.makeText(this, "You " + temp + " the notification button for course " + text, Toast.LENGTH_SHORT).show();    	
    }

	private String getCourseNameFromClickedView(View view) {
		View parent = (View) view.getParent();
    	TextView textView = (TextView) parent.findViewById(R.id.mcrCourseNameTextView);
    	String text = (String) textView.getText();
		return text;
	}
    
	
}
