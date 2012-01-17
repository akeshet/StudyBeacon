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
    	footerButton.setOnClickListener(new addCourseClickListener());
    	
    	myListView = this.getListView();
    	myListView.addFooterView(footerButton);
    	
     	CourseInfo [] courseInfos = (CourseInfo[]) Global.getCourseInfos();
    	
    	ArrayAdapter<CourseInfo> ara = new ArrayAdapter<CourseInfo>(this, R.layout.mycoursesrow, R.id.mcrCourseNameTextView, courseInfos) {
    		public View getView(int position, View convertView, ViewGroup parent) {
    			View ans = super.getView(position, convertView, parent);
    			
    			final CourseInfo courseInfo = this.getItem(position);
    			
    			CheckBox notifyCheckBox = (CheckBox) ans.findViewById(R.id.mcrNotificationButton);
    			notifyCheckBox.setOnCheckedChangeListener(null); // This is necessary in case we are recycling a previous "convertView"
    															// so that its event hander gets un-hooked BEFORE we change the checked state
    															// of the button (on the next line)
    			notifyCheckBox.setChecked(courseInfo.getNotify());
    			notifyCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						courseInfo.setNotify(isChecked);
					}
				});
    			
    			CheckBox starCheckBox = (CheckBox) ans.findViewById(R.id.mcrStarButton);
    			starCheckBox.setOnCheckedChangeListener(null);
    			starCheckBox.setChecked(courseInfo.getStarred());
    			starCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						courseInfo.setStarred(isChecked);
					}
				});
    			
    			ImageButton newBeaconButton = (ImageButton) ans.findViewById(R.id.mcrNewBeaconButton);
    			newBeaconButton.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						Intent i = new Intent(SBMyCoursesActivity.this, SBBeaconEditActivity.class);
						SBMyCoursesActivity.this.startActivity(i);
					}
				});
    			
    			return ans;
    			
    		};
    	};
    	
    	myListView.setAdapter(ara);
    	
    	
    	
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
    
    public void notifyCheckClicked(View view) {
    	Log.d(TAG, "notifyCheckClicked");
    	
    	CheckBox me = (CheckBox) view;
    	String text = getCourseNameFromClickedView(view);
    	
    	String temp = "checked";
    	if (!me.isChecked())
    		temp = "unchecked";
    	Toast.makeText(this, "You " + temp + " the notification button for course " + text, Toast.LENGTH_SHORT).show();    	
    }
    
    public void newBeaconClicked(View view) {
    	// for now, just start the new beacon activity directly
    	// with empty data so we can play with the interface
    	Intent i = new Intent(this, SBBeaconEditActivity.class);
    	startActivity(i);
    }

	private String getCourseNameFromClickedView(View view) {
		View parent = (View) view.getParent();
    	TextView textView = (TextView) parent.findViewById(R.id.mcrCourseNameTextView);
    	String text = (String) textView.getText();
		return text;
	}
    
	private class addCourseClickListener implements OnClickListener {
		public void onClick(View view){
			// launch the course resource activity
			Intent i = new Intent(SBMyCoursesActivity.this,SBCourseResourceListActivity.class);
			startActivity(i);
		}
	}
	
}
