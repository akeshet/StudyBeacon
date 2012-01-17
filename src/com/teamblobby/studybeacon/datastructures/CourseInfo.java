package com.teamblobby.studybeacon.datastructures;

public abstract class CourseInfo {
	
	public abstract String getCourseName();
	public abstract void setCourseName(String courseName);
		
	public abstract boolean getStarred();
	public abstract void setStarred(boolean starred);

	public abstract boolean getNotify();
	public abstract void setNotify(boolean notify);
}
