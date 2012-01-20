package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.teamblobby.studybeacon.datastructures.*;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class SBCourseResourceActivity extends ListActivity {

	private static final String TAG = "SBCourseResourceListActivity";
	
	private List<CourseListable> availableCourses;
	private CourseInfo[] currentCourses;
	private ArrayAdapter<CourseListable> arrayAdapter;
	
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
	    	this.availableCourses = new ArrayList<CourseListable>(); // this is something else
	    	(new CourseLoadTask(this)).execute(); // executes AsyncTask
	    }
	    
		this.arrayAdapter = new CourseListAdapter(SBCourseResourceActivity.this, 
										 R.layout.mycoursesrow, 
										 R.id.mcrCourseNameTextView, 
										 this.availableCourses);
		this.setListAdapter(this.arrayAdapter);
	    
	}
	
	private void setStarredCourses() {
		
		for (CourseListable course : this.availableCourses){
				course.setStarred(Arrays.asList(CourseInfo.getCourseNames(
									this.currentCourses)).contains(course.toString()));
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
			
			// remove notify and new beacon buttons
			viewToReturn.findViewById(R.id.mcrNotificationButton).setVisibility(View.GONE);
			viewToReturn.findViewById(R.id.mcrNewBeaconButton).setVisibility(View.GONE);
			
			// set starred based on what's in the CourseInfo object
			final CourseListable courseInfo = this.getItem(position);
			
			CheckBox notifyCheckBox = (CheckBox) viewToReturn.findViewById(R.id.mcrStarButton);
			
			// here split depending on if it's a course or course category
			if (CourseInfo.class.isInstance(courseInfo)) {
				notifyCheckBox.setChecked(courseInfo.getStarred());
			} else{
				notifyCheckBox.setVisibility(View.GONE);
				ImageView folderIcon = (ImageView) viewToReturn.findViewById(R.id.mcrFolderIcon);
				folderIcon.setVisibility(View.VISIBLE);
			}
				 
			
			Log.d(TAG,"getting course View for "+courseInfo.toString());
			return viewToReturn;
		}
	}
    
    private class CourseLoadTask extends AsyncTask<Void, Boolean, Bundle> {
    	
		private static final String COURSES = "courses";
		private SBCourseResourceActivity callingActivity;
    	
    	public CourseLoadTask(SBCourseResourceActivity ctrA) {
    		this.callingActivity = ctrA;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		Toast.makeText(this.callingActivity,
    				Global.res.getString(R.string.loadingcoursestoast),
    				Toast.LENGTH_SHORT).show();
    	}
    	
		@Override
		protected Bundle doInBackground(Void... arg0) {
			Log.v(TAG, "Loading Course Resources");
			final String[] pulledCourseList;
			// TODO load the resources from MIT somehow
			try {
				Thread.sleep(3000); //simulate load time
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (true){
				pulledCourseList = new String[] {"1","2","3","4","5","6","7","8"};
			} else {
				pulledCourseList = new String[] {"1.334","2.217","6.570","8.101","8.901"};
			}
			
			Log.v(TAG,"load finished");
			
			List<CourseListable> courses = new ArrayList<CourseListable>();
			
			for (String course : pulledCourseList) {
				courses.add(new CourseCategory(course));
				//courses.add(new CourseInfoSimple(course));
			}
			Bundle courseBundle = new Bundle();
			courseBundle.putParcelableArrayList(COURSES, (ArrayList<CourseListable>) courses);
			return courseBundle;
		}
		
		@Override
		protected void onPostExecute(Bundle fetchedCourses) {
			Log.v(TAG, "Post Load Action");
			this.callingActivity.availableCourses.clear();
			this.callingActivity.availableCourses.addAll(
					(Collection<? extends CourseListable>) 
					fetchedCourses.getParcelableArrayList(COURSES)
				);
			Log.d(TAG,this.callingActivity.availableCourses.toString());
			this.callingActivity.setStarredCourses();
			this.callingActivity.arrayAdapter.notifyDataSetChanged();
		}
		
    }
}
