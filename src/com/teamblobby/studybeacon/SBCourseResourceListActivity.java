package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SBCourseResourceListActivity extends ListActivity {

	private static final String TAG = "SBCourseResourceListActivity";
	
	private ArrayAdapter<String> courseListAdapter;
	
	private coursePickerTask picker; 
	
	private List<String> availableCourses;
	private String[] currentCourses;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mycourses);
	    // set the title text
	    ((TextView) this.findViewById(R.id.coursesTitleText)).setText("MIT Course List");
	    
	    currentCourses = Global.getCourses();
	    final ArrayList<HashMap<String, Object>> availableCourses = 
	    		new ArrayList<HashMap<String,Object>>();
	    
		final ArrayAdapter<HashMap<String, Object>> courseListAdapter = 
				new ArrayAdapter<HashMap<String, Object>>(SBCourseResourceListActivity.this, 
										 R.layout.mycoursesrow, 
										 R.id.mcrCourseNameTextView, 
										 availableCourses);
    	
    	setListAdapter(courseListAdapter);
	    
	    // initiate call to load courses
	    picker = new coursePickerTask(this);
    	
    	
    	picker.execute(); // executes the anonymous class implementing AsyncTask
	    
	    
	}
	
	public void addCourse(String s) {
		availableCourses.add(s);
	}
	
	public void notifyDataSetChanged() {
		courseListAdapter.notifyDataSetChanged();
	}
	
    public void starCheckClicked(View view) {
    	Log.d(TAG, "starCheckClicked");
    	
    	CheckBox me = (CheckBox) view;
    	String text = getCourseNameFromClickedView(view);
    	
    	String temp = "checked";
    	if (!me.isChecked())
    		temp = "unchecked";
    	Toast.makeText(this, "You " + temp + " the star for course " + text, Toast.LENGTH_SHORT).show();    	
    }
    
    private String getCourseNameFromClickedView(View view) {
		View parent = (View) view.getParent();
    	TextView textView = (TextView) parent.findViewById(R.id.mcrCourseNameTextView);
    	String text = (String) textView.getText();
		return text;
	}
    
    
    public class coursePickerTask extends AsyncTask<Void, Boolean, Boolean> {
    	
    	private SBCourseResourceListActivity a;
    	
    	private String[] courseList;
    	
    	
    	public coursePickerTask(SBCourseResourceListActivity ctrA) {
    		a = ctrA;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Toast.makeText(a, "Loading Courses", Toast.LENGTH_SHORT).show();    
    	}
    	
		@Override
		protected Boolean doInBackground(Void... arg0) {
			Log.v(TAG, "Loading Course Resources");
			// TODO load the resources from MIT somehow
			try {
				Thread.sleep(3000); //simulate load time
				courseList = new String[]{"1.334","2.217","6.570","8.101","8.901"};
				// update list contents
				for (String course : courseList)
					a.addCourse(course);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.v(TAG,"load finished");
			return true;
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Log.v(TAG, "Post Load Action");
			
			a.notifyDataSetChanged();
	    }
		
    }
}
