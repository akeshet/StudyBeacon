package com.teamblobby.studybeacon.datastructures;

/**
 * 
 * Implementation of {@link CourseInfo} which stores course related data
 * locally within this object.
 *
 */
public class CourseInfoSimple extends CourseInfo {

	public CourseInfoSimple(String courseName)  {
		this(courseName, false, false);
	}
	
	public CourseInfoSimple(String courseName,boolean starred){
		this(courseName,starred,false);
	}

	public CourseInfoSimple(String courseName, boolean starred, boolean notify) {
		setCourseName(courseName);
		setStarred(starred);
		setNotify(notify);
	}
	
	public CourseInfoSimple(CourseInfo copyMe) {
		this(copyMe.getCourseName(), copyMe.getStarred(), copyMe.getNotify());
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
