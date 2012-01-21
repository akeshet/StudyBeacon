package com.teamblobby.studybeacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.teamblobby.studybeacon.datastructures.*;

public class SBMyCoursesActivity extends ListActivity {
	
	private static final String TAG = "SBMyCoursesActivity";
	static final int DATA_CHANGE_REQUEST_CODE = 0;
	private CourseAdapter adapter;
	private List<CourseInfo> courseInfos;
	ListView myListView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.mycourses);
    	
    	// add footer button
    	Button footerButton = (Button) this.findViewById(R.id.addClassesButton);
    	//footerButton.setText(Global.res.getString(R.string.addclass));
    	footerButton.setOnClickListener(new AddCourseClickListener());
    	
    	myListView = this.getListView();
    	//myListView.addFooterView(footerButton);
    	this.courseInfos = new ArrayList<CourseInfo>();
     	this.courseInfos.addAll(Global.getMyCourseInfos());
    	
     	this.adapter = new CourseAdapter(this,R.layout.mycoursesrow,R.id.mcrCourseNameTextView,
     			this.courseInfos);
     	
    	myListView.setAdapter(this.adapter);
    	
    }
    
    protected class CourseAdapter extends ArrayAdapter<CourseInfo>{

		public CourseAdapter(Context context, int resource,
				int textViewResourceId, List<CourseInfo> courseInfos) {
			super(context, resource, textViewResourceId, courseInfos);
		}
    	
		public View getView(int position, View convertView, ViewGroup parent) {
			View viewToReturn = super.getView(position, convertView, parent);
			
			final CourseInfo courseInfo = this.getItem(position);
			
			CheckBox notifyCheckBox = (CheckBox) viewToReturn.findViewById(R.id.mcrNotificationButton);
			notifyCheckBox.setOnCheckedChangeListener(null); // This is necessary in case we are recycling a previous "convertView"
															// so that its event hander gets un-hooked BEFORE we change the checked state
															// of the button (on the next line)
			notifyCheckBox.setChecked(courseInfo.getNotify());
			notifyCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					courseInfo.setNotify(isChecked);
				}
			});
			
			CheckBox starCheckBox = (CheckBox) viewToReturn.findViewById(R.id.mcrStarButton);
			starCheckBox.setOnCheckedChangeListener(null);
			starCheckBox.setChecked(courseInfo.getStarred());
			starCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					courseInfo.setStarred(isChecked);
				}
			});
			
			ImageButton newBeaconButton = (ImageButton) viewToReturn.findViewById(R.id.mcrNewBeaconButton);
			newBeaconButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent i = new Intent(SBMyCoursesActivity.this, SBBeaconEditActivity.class);
					i.setAction(SBBeaconEditActivity.ACTION_NEW);
					// Set the default class
					i.putExtra(SBBeaconEditActivity.COURSE_STR, courseInfo.getName());
					SBMyCoursesActivity.this.startActivity(i);
				}
			});
			
			return viewToReturn;
			
		};
    }
    
	private class AddCourseClickListener implements OnClickListener {
		public void onClick(View view){
			// launch the course resource activity
			Intent i = new Intent(SBMyCoursesActivity.this,SBCourseResourceActivity.class);
			SBMyCoursesActivity.this.startActivityForResult(i, DATA_CHANGE_REQUEST_CODE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SBMyCoursesActivity.DATA_CHANGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			// RESULT_OK means data has changed
			this.courseInfos.clear();
			this.courseInfos.addAll(Global.getMyCourseInfos());
			this.adapter.notifyDataSetChanged();
			Log.d(TAG, "data refreshed due to activity result");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	
}
