package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.teamblobby.studybeacon.datastructures.*;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SBCourseResourceActivity extends ListActivity {

	private static final String TAG = "SBCourseResourceListActivity";
	
	private List<CourseListable> availableCourses;
	private List<CourseInfo> currentCourses;
	private ArrayAdapter<CourseListable> arrayAdapter;
	private int DATA_CHANGED_REQUEST_CODE = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // initial value of setresult is CANCELED (no data changed occured)
	    this.setResult(Activity.RESULT_CANCELED);
	    Log.d(TAG,"result defaulted to canceled");
	    
	    this.setContentView(R.layout.mycourses);
	    // set the title text
	    ((TextView) this.findViewById(R.id.titleText))
	    	.setText(Global.res.getString(R.string.courseResourceTitleText));
	    
	    ((TextView) this.findViewById(android.R.id.empty))
    	.setText(Global.res.getString(R.string.courseResourseLoadText));
	    
	    // remove the footer button
	    this.findViewById(R.id.coursesFooter).setVisibility(View.GONE);
	    
	    currentCourses = Global.getMyCourseInfos();
	    
	    // This is where we load the courses, otherwise get from savedBundle
	    if (savedInstanceState != null)
	    	this.availableCourses =
	    	    savedInstanceState.getParcelableArrayList("availableCourses");
	    
	    if (this.availableCourses == null) {
	    	// create empty list and call to load courses
	    	this.availableCourses = new ArrayList<CourseListable>(); // this is something else
	    	String categoryName = null;
	    	if ( this.getIntent().getExtras() != null )
	    		categoryName = this.getIntent().getExtras().getString("category");
	    	if ( categoryName == null ) categoryName = CourseLoadTask.ROOT_CATEGORY;
	    	(new CourseLoadTask(this)).execute(categoryName); // executes AsyncTask
	    }
	    
		this.arrayAdapter = new CourseListAdapter(this, 
										 R.layout.mycoursesrow, 
										 R.id.mcrCourseNameTextView, 
										 this.availableCourses);
		this.setListAdapter(this.arrayAdapter);
		
		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> listView, View itemView, int position,
					long rowId) {
				CourseListable courseListItem = (CourseListable) listView.getAdapter().getItem(position);
				
				if ( courseListItem.listableType() == CourseListable.TYPE_CATEGORY ) { 
					Intent i = new Intent(SBCourseResourceActivity.this,
							              SBCourseResourceActivity.class);
					i.putExtra("category", courseListItem.getName());
					startActivityForResult(i, DATA_CHANGED_REQUEST_CODE);
				}
			}
		
		});
	    
	}
	
	private void setStarredCourses() {
		
		for ( int j = 0; j < this.availableCourses.size(); j++ ){
				//course.setStarred(Arrays.asList(CourseInfo.getCourseNames(
				//					this.currentCourses)).contains(course.getName()));
			if ( CourseInfo.getCourseNames(this.currentCourses).contains(this.availableCourses.get(j).getName()) ){
				this.availableCourses.get(j).setStarred(true);
				
				this.availableCourses.set(j, new CourseInfoSqlite(CourseInfoSqlite.MYCOURSES_TABLE, (CourseInfo) this.availableCourses.get(j)));
				Log.d(TAG,"added object "+this.availableCourses.get(j)+" as "+this.availableCourses.get(j).getClass());
			}
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("availableCourses",
				       (ArrayList<CourseListable>) this.availableCourses);
		super.onSaveInstanceState(outState);
	}
	
	private class CourseListAdapter extends ArrayAdapter<CourseListable> {

		public CourseListAdapter(Context context, int resource,
				int textViewResourceId, List<CourseListable> objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View viewToReturn =  super.getView(position, convertView, parent);

			CheckBox notifyCheckBox = (CheckBox) viewToReturn.findViewById(R.id.mcrStarButton);
			notifyCheckBox.setOnCheckedChangeListener(null);
			notifyCheckBox.setVisibility(View.VISIBLE);
			
			
			// remove notify and new beacon buttons
			viewToReturn.findViewById(R.id.mcrNotificationButton).setVisibility(View.GONE);
			viewToReturn.findViewById(R.id.mcrNewBeaconButton).setVisibility(View.GONE);
			
			// set starred based on what's in the CourseInfo object
			final CourseListable courseInfo = this.getItem(position);
			
			
			// here split depending on if it's a course or course category			
			switch (courseInfo.listableType()) {
				case CourseListable.TYPE_COURSE_STARRED:
				case CourseListable.TYPE_COURSE_UNSTARRED:
					notifyCheckBox.setChecked(courseInfo.getStarred());
					// TODO add onCheck listener to checkbox
					notifyCheckBox.setOnCheckedChangeListener(new CourseCheckedListener(position,(CourseInfo) courseInfo));
					break;
				case CourseListable.TYPE_CATEGORY:
					notifyCheckBox.setVisibility(View.INVISIBLE);
					break;
			}
			
			//Log.d(TAG,"getting course View for "+courseInfo.getName());
			return viewToReturn;
		}
	}
	
	private class CourseCheckedListener implements OnCheckedChangeListener{

		private int position;
		private CourseInfo courseInfo;
		
		public CourseCheckedListener(int positionIn, CourseInfo courseInfoIn){
			this.position = positionIn;
			this.courseInfo = courseInfoIn;
		}
		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			this.courseInfo.setStarred(isChecked);
			SBCourseResourceActivity activity = SBCourseResourceActivity.this;
			if ( isChecked ) {
				activity.availableCourses.set(this.position, new CourseInfoSqlite(CourseInfoSqlite.MYCOURSES_TABLE, this.courseInfo));
			}
			activity.arrayAdapter.notifyDataSetChanged();
			activity.setResult(Activity.RESULT_OK);
			Log.d(TAG,"data changed, result set to OK");
		}
	}
    
    private class CourseLoadTask extends AsyncTask<String, Void, List<CourseListable>> {
    	
		private static final String ROOT_CATEGORY = "_root";
		private SBCourseResourceActivity callingActivity;
    	
    	public CourseLoadTask(SBCourseResourceActivity activity) {
    		this.callingActivity = activity;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Toast.makeText(this.callingActivity,
    				Global.res.getString(R.string.loadingcoursestoast),
    				Toast.LENGTH_SHORT).show();
    	}
    	
		@Override
		protected List<CourseListable> doInBackground(String... category) {
			Log.v(TAG, "Loading Course Resources");
			final String[] pulledCourseList;
			// TODO load the resources from MIT somehow
			try {
				Thread.sleep(500); //simulate load time
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			List<CourseListable> courses = new ArrayList<CourseListable>();
			if (category[0].equals(ROOT_CATEGORY)) {
				pulledCourseList = new String[] {"1","2","3","4","5","6","7","8"};
				for (String course : pulledCourseList) {
					courses.add(new CourseCategory(course));
					}
			} else {
				pulledCourseList = new String[] {"1.334","2.217","6.570","8.101","8.901"};
				for (String course : pulledCourseList) {
					courses.add(new CourseInfoSimple(course));
				}
			}
			
			Log.v(TAG,"load finished");
			
			return courses;
		}
		
		@Override
		protected void onPostExecute(List<CourseListable> fetchedCourses) {
			Log.v(TAG, "Post Load Action");
			this.callingActivity.availableCourses.clear();
			this.callingActivity.availableCourses.addAll(fetchedCourses);
			Log.d(TAG,this.callingActivity.availableCourses.toString());
			this.callingActivity.setStarredCourses();
			this.callingActivity.arrayAdapter.notifyDataSetChanged();
		}
		
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if we requested data change, and got OK, then set our result to OK
		if (requestCode == DATA_CHANGED_REQUEST_CODE){
			this.setResult(resultCode);
			Log.d(TAG,"due to returned result, this result set to "+resultCode);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
    
}
