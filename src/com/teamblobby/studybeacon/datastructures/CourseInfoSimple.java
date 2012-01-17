package com.teamblobby.studybeacon.datastructures;

public class CourseInfoSimple extends CourseInfo {

	public CourseInfoSimple(String courseName)  {
		this(courseName, false, false);
	}

	public CourseInfoSimple(String courseName, boolean starred, boolean notify) {
		this.courseName = courseName;
		this.starred = starred;
		this.notify = notify;
	}

	private String courseName;

	@Override
	public String getCourseName() {
		return this.courseName;
	}

	@Override
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	private boolean starred;

	@Override
	public boolean getStarred() {
		return this.starred;
	}

	@Override
	public void setStarred(boolean starred) {
		this.starred = starred;
	}

	private boolean notify;

	@Override
	public boolean getNotify() {
		return this.notify;
	}

	@Override
	public void setNotify(boolean notify) {
		this.notify = notify;	
	}

}
