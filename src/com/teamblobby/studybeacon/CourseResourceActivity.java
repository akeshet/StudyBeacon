package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.List;

import com.teamblobby.studybeacon.datastructures.*;
import com.teamblobby.studybeacon.network.StellarQuery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CourseResourceActivity extends ListActivity {

	private static final String TAG = "CourseResourceListActivity";
	
	private List<CourseListable> availableCourses;
	private List<CourseInfo> currentCourses;
	private ArrayAdapter<CourseListable> arrayAdapter;
	private final static int DATA_CHANGED_REQUEST_CODE = 0;
	
	public final static int RESULT_COURSES_CHANGED = Activity.RESULT_FIRST_USER;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // initial value of setResult is CANCELED (no data changed occurred)
	    this.setResult(Activity.RESULT_CANCELED);
	    Log.d(TAG,"result defaulted to canceled");
	    
	    this.setContentView(R.layout.mycourses);
	    
	    Bundle intentExtras = this.getIntent().getExtras();
	    
	    // set the title text
	    String titleText = Global.res.getString(R.string.courseResourceTitleText); 
	    if (intentExtras != null && intentExtras.getString("titleText") != null)
	    	titleText = intentExtras.getString("titleText");
	    
	    ((TextView) this.findViewById(R.id.titleText))
	    	.setText(titleText);
	    
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
	    	if ( intentExtras != null )
	    		categoryName = intentExtras.getString("category");
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
					Intent i = new Intent(CourseResourceActivity.this,
							              CourseResourceActivity.class);
					i.putExtra("category", courseListItem.getName());
					i.putExtra("titleText", courseListItem.getPrettyName());
					startActivityForResult(i, DATA_CHANGED_REQUEST_CODE);
				} 
			}
		
		});

		if ((intentExtras != null) && intentExtras.containsKey("category"))
			doTutorial();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			final View viewToReturn =  super.getView(position, convertView, parent);

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
					LinearLayout courseTextLayout = (LinearLayout) viewToReturn.findViewById(R.id.mcrTextLayout);
					notifyCheckBox.setChecked(courseInfo.getStarred());
					notifyCheckBox.setOnCheckedChangeListener(new CourseCheckedListener(position,(CourseInfo) courseInfo));
					courseTextLayout.setOnClickListener(new OnClickListener(){public void onClick(View v){
						checkBoxPullRugOut(position, courseInfo, !courseInfo.getStarred());}
					});
					break;
				case CourseListable.TYPE_CATEGORY:
					notifyCheckBox.setVisibility(View.GONE);
					break;
			}
			
			// set the description text
			TextView descTextView = (TextView) viewToReturn.findViewById(R.id.mcrCourseDescriptionView);
			descTextView.setText(courseInfo.getDescription());
			
			//Log.d(TAG,"getting course View for "+courseInfo.getName());
			return viewToReturn;
		}
	}
	
	private class CourseCheckedListener implements OnCheckedChangeListener{

		private int position;
		private CourseListable courseInfo;
		
		public CourseCheckedListener(int positionIn, CourseListable courseListItem){
			this.position = positionIn;
			this.courseInfo = courseListItem;
		}
		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			checkBoxPullRugOut(position, courseInfo, isChecked);
		}
	}
	
	private void checkBoxPullRugOut(int position, CourseListable courseListItem, boolean checked){
		courseListItem.setStarred(checked);
		CourseResourceActivity activity = CourseResourceActivity.this;
		if ( checked ) {
			activity.availableCourses.set(position, new CourseInfoSqlite(CourseInfoSqlite.MYCOURSES_TABLE, (CourseInfo) courseListItem));
		}
		activity.arrayAdapter.notifyDataSetChanged();
		activity.setResult(RESULT_COURSES_CHANGED);
		Log.d(TAG,"data changed, result set to OK");
	}
    
    private class CourseLoadTask extends AsyncTask<String, Void, List<CourseListable>> {
    	
		private static final String ROOT_CATEGORY = "_root";
		private CourseResourceActivity callingActivity;
    	
    	public CourseLoadTask(CourseResourceActivity activity) {
    		this.callingActivity = activity;
    	}
    	
		@Override
		protected List<CourseListable> doInBackground(String... category) {
			String theCategory = category[0];
			Log.v(TAG, "Loading Course Resources");
						
			List<CourseListable> courses = StellarQuery.newQuery(theCategory).execute();
						
			Log.v(TAG,"load finished");
			
			return courses;
		}
		
		@Override
		protected void onPostExecute(List<CourseListable> fetchedCourses) {
			Log.v(TAG, "Post Load Action");
			
			if ( fetchedCourses != null ){
				this.callingActivity.availableCourses.clear();
				this.callingActivity.availableCourses.addAll(fetchedCourses);
				Log.d(TAG,this.callingActivity.availableCourses.toString());
				this.callingActivity.setStarredCourses();
			} else {
				Toast.makeText(CourseResourceActivity.this, R.string.courseResourseServerProblem, Toast.LENGTH_SHORT).show();

			}

			((TextView) this.callingActivity.findViewById(android.R.id.empty)).setText(Global.res.getString(R.string.courseResourseNothing));
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

	private void doTutorial() {
		//if ( !Global.isFirstTimeAtActivity(TAG) )
		//	return;

		switch (Global.getTutorialStep()) {
		case 2:
			(new AlertDialog.Builder(this)).setMessage(R.string.welcomeCourseMessage).setTitle(R.string.welcomeCourseTitle)
			.setCancelable(false).setPositiveButton(R.string.OK,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
			//increment tutorial
			Global.incrementTutorialStep();
			break;

		default:
			break;
		}

	}

}
