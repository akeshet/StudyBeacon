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
		setName(courseName);
		setStarred(starred);
		setNotify(notify);
	}
	
	/**
	 * Copy constructor. Fragile -- must be edited if we later introduce new fields.
	 * Consider implementing by parcelling/unparcelling, at some point, maybe.
	 * @param copyMe
	 */
	public CourseInfoSimple(CourseInfo copyMe) {
		this(copyMe.getName(), copyMe.getStarred(), copyMe.getNotify());
	}

	private String courseName;

	@Override
	public String getName() {
		return this.courseName;
	}

	@Override
	public void setName(String courseName) {
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
