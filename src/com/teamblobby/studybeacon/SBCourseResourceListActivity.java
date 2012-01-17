package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
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
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mycourses);
	    // set the title text
	    ((TextView) this.findViewById(R.id.coursesTitleText)).setText("MIT Course List");
	    
	    String[] currentCourses = Global.getCourses();
	    final List<String> availableCourses = new ArrayList<String> ();
	    
		final ArrayAdapter<String> courseListAdapter = 
				new ArrayAdapter<String>(SBCourseResourceListActivity.this, 
										 R.layout.mycoursesrow, 
										 R.id.mcrCourseNameTextView, 
										 availableCourses);
    	
    	this.setListAdapter(courseListAdapter);
	    
	    // initiate call to load courses
	    (new AsyncTask<Void, Void, Void>() {
	    	@Override
	    	protected void onPreExecute() {
	    		Toast.makeText(SBCourseResourceListActivity.this, "Loading Courses", Toast.LENGTH_SHORT).show();    
	    	}
	    	
			@Override
			protected Void doInBackground(Void... params) {
				Log.v(TAG, "Loading Course Resources");
				// TODO load the resources from MIT somehow
				try {
					Thread.sleep(3000); //simulate load time
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.v(TAG,"load finished");
				return null;
				
			}
			
			@Override
			protected void onPostExecute(Void v) {
				Log.v(TAG, "Post Load Action");
				// update list contents
				availableCourses.add("1.334");
				availableCourses.add("2.217");
				availableCourses.add("6.570");
				availableCourses.add("8.101");
				availableCourses.add("8.901");
				courseListAdapter.notifyDataSetChanged();
		    }
	    }).execute(); // executes the anonymous class implementing AsyncTask
	    
	    
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
}
