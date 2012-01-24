package com.teamblobby.studybeacon.datastructures;

/**
 * 
 * Implementation of {@link CourseInfo} which stores course related data
 * locally within this object.
 *
 */
public class CourseInfoSimple extends CourseInfo {

	public CourseInfoSimple(String courseName)  {
		this(courseName, false, false, courseName, courseName);
	}

	public CourseInfoSimple(String courseName,boolean starred){
		this(courseName,starred,false, courseName, courseName);
	}

	public CourseInfoSimple(String courseName, String description, String prettyName) {
		this(courseName, false, false, description, prettyName);
	}

	public CourseInfoSimple(String courseName, boolean starred, boolean notify, String description, String prettyName) {
		setName(courseName);
		setStarred(starred);
		setNotify(notify);
		setDescription(description);
		setPrettyName(prettyName);
	}

	/**
	 * Copy constructor. Fragile -- must be edited if we later introduce new fields.
	 * Consider implementing by parcelling/unparcelling, at some point, maybe.
	 * @param copyMe
	 */
	public CourseInfoSimple(CourseInfo copyMe) {
		this(copyMe.getName(), copyMe.getStarred(), copyMe.getNotify(), copyMe.getDescription(), copyMe.getPrettyName());
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

	private String description;

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;

	}

	private String prettyName;

	@Override
	public String getPrettyName() {
		return prettyName;
	}

	@Override
	public void setPrettyName(String prettyName) {
		this.prettyName = prettyName;
	}

}
