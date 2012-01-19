package com.teamblobby.studybeacon;

import android.app.ListActivity;
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
	ListView myListView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.mycourses);
    	
    	// add footer button
    	Button footerButton = new Button(this.getApplicationContext());
    	footerButton.setText(Global.res.getString(R.string.addclass));
    	footerButton.setOnClickListener(new CourseClickListener());
    	
    	myListView = this.getListView();
    	myListView.addFooterView(footerButton);
    	
     	CourseInfo [] courseInfos = (CourseInfo[]) Global.getCourseInfos();
    	
    	ArrayAdapter<CourseInfo> courseAdapter = new ArrayAdapter<CourseInfo>(this, R.layout.mycoursesrow, R.id.mcrCourseNameTextView, courseInfos) {
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
						SBMyCoursesActivity.this.startActivity(i);
					}
				});
    			
    			return viewToReturn;
    			
    		};
    	};
    	
    	myListView.setAdapter(courseAdapter);
    	
    	
    	
    }
    
    
    
	private class CourseClickListener implements OnClickListener {
		public void onClick(View view){
			// launch the course resource activity
			Intent i = new Intent(SBMyCoursesActivity.this,SBCourseResourceListActivity.class);
			startActivity(i);
		}
	}
	
}
