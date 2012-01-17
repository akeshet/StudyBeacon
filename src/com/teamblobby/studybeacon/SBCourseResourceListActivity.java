package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	private ArrayList<HashMap<String,Object>> availableCourses;
	private List<String> currentCourses;
	private ArrayAdapter<HashMap<String, Object>> arrayAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    this.setContentView(R.layout.mycourses);
	    // set the title text
	    ((TextView) this.findViewById(R.id.coursesTitleText)).setText("MIT Course List");
	    
	    currentCourses = Arrays.asList(Global.getCourses());
	    availableCourses = new ArrayList<HashMap<String,Object>>();
	    
		this.arrayAdapter = new ArrayAdapter<HashMap<String, Object>>(SBCourseResourceListActivity.this, 
										 R.layout.mycoursesrow, 
										 R.id.mcrCourseNameTextView, 
										 availableCourses);
		this.setListAdapter(this.arrayAdapter);
	    
	    // initiate call to load courses
	    (new coursePickerTask(this)).execute(); // executes AsyncTask
	    
	    
	}
	
	public void addCourse(String s) {
		HashMap<String,Object> courseMap = new HashMap<String,Object>();
		Boolean isCourseInCurrentList = this.currentCourses.contains(s);
		
		courseMap.put("courseName", s);
		courseMap.put("checked", isCourseInCurrentList);
		
		availableCourses.add(courseMap);
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
    
    
    public class coursePickerTask extends AsyncTask<Void, Boolean, String[]> {
    	
    	private SBCourseResourceListActivity callingActivity;
    	
    	public coursePickerTask(SBCourseResourceListActivity ctrA) {
    		this.callingActivity = ctrA;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Toast.makeText(this.callingActivity, "Loading Courses", Toast.LENGTH_SHORT).show();    
    	}
    	
		@Override
		protected String[] doInBackground(Void... arg0) {
			Log.v(TAG, "Loading Course Resources");
			String[] pulledCourseList;
			// TODO load the resources from MIT somehow
			try {
				Thread.sleep(3000); //simulate load time
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			pulledCourseList = new String[]{"1.334","2.217","6.570","8.101","8.901"};
			
			Log.v(TAG,"load finished");
			return pulledCourseList;
			
		}
		
		@Override
		protected void onPostExecute(String[] pulledCourseList) {
			Log.v(TAG, "Post Load Action");
			for (String course : pulledCourseList)
				this.callingActivity.addCourse(course);
			
			this.callingActivity.arrayAdapter.notifyDataSetChanged();
		}
		
    }
}
