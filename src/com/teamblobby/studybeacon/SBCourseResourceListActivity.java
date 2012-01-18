package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.Arrays;

import com.teamblobby.studybeacon.datastructures.*;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

public class SBCourseResourceListActivity extends ListActivity {

	private static final String TAG = "SBCourseResourceListActivity";
	
	private ArrayList<CourseInfo> availableCourses;
	private CourseInfo[] currentCourses;
	private ArrayAdapter<CourseInfo> arrayAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    this.setContentView(R.layout.mycourses);
	    // set the title text
	    ((TextView) this.findViewById(R.id.coursesTitleText))
	    	.setText(Global.res.getString(R.string.courseResourceTitleText));
	    
	    currentCourses = Global.getCourseInfos();
	    
	    // This is where we load the courses, otherwise get from savedBundle
	    if (savedInstanceState != null)
	    	this.availableCourses =
	    	    savedInstanceState.getParcelableArrayList("availableCourses");
	    
	    if (this.availableCourses == null) {
	    	// create empty list and call to load courses
	    	this.availableCourses = new ArrayList<CourseInfo>();
	    	(new coursePickerTask(this)).execute(); // executes AsyncTask
	    }
	    
		this.arrayAdapter = new ArrayAdapter<CourseInfo>(SBCourseResourceListActivity.this, 
										 R.layout.mycoursesrow, 
										 R.id.mcrCourseNameTextView, 
										 availableCourses);
		this.setListAdapter(this.arrayAdapter);
	    
	}
	
	public void addCourse(String s) {
		boolean isCourseInCurrentList = // highly optimized :)
				Arrays.asList(
						CourseInfo.getCourseNames(
								this.currentCourses)).contains(s);
		
		CourseInfo courseInfo = new CourseInfoSimple(s, isCourseInCurrentList);
		
		availableCourses.add(courseInfo);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("availableCourses",
				                        this.availableCourses);
		super.onSaveInstanceState(outState);
	}
    
    public class coursePickerTask extends AsyncTask<Void, Boolean, String[]> {
    	
    	private SBCourseResourceListActivity callingActivity;
    	
    	public coursePickerTask(SBCourseResourceListActivity ctrA) {
    		this.callingActivity = ctrA;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Toast.makeText(this.callingActivity,
    				Global.res.getString(R.string.loadingcoursestoast),
    				Toast.LENGTH_SHORT).show();
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
