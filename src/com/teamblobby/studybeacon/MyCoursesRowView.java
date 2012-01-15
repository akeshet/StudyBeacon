package com.teamblobby.studybeacon;

import android.util.*;
import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyCoursesRowView extends android.widget.LinearLayout {

	Activity a;
	
	ImageButton starButton;
	ImageButton newBeaconButton;
	ImageButton notificationButton;
	TextView    courseNameTextView;
	
	public MyCoursesRowView(Context context,  AttributeSet attributeSet) {
		super(context);
		
		a = (Activity) context;
		
		LayoutInflater inf = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inf.inflate(R.layout.mycoursesrow, this);
		
		starButton = (ImageButton) this.findViewById(R.id.mcrStarButton);
		newBeaconButton = (ImageButton) this.findViewById(R.id.mcrNewBeaconButton);
		notificationButton = (ImageButton) this.findViewById(R.id.mcrNotificationButton);
		courseNameTextView = (TextView) this.findViewById(R.id.mcrCourseNameTextView);
		
		
	}

}
