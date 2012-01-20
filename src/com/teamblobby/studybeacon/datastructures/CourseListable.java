package com.teamblobby.studybeacon.datastructures;

import android.os.Parcelable;

public interface CourseListable extends Parcelable{

	public static int TYPE_COURSE_STARRED = 0;
	public static int TYPE_COURSE_UNSTARRED = 1;
	public static int TYPE_CATEGORY = 2;
	
	public void setStarred(boolean starred);

	public boolean getStarred();
	
	public String getName();
	
	public int listableType();
}
