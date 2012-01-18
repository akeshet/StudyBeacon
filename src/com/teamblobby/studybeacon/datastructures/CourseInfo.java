package com.teamblobby.studybeacon.datastructures;

/**
 * 
 * Super class which defines the interface that CourseInfo implementations will follow.
 * Implementations may either store course data locally within the object (such as {@link CourseInfoSimple})
 * or in a local SQLite database (not yet implemented) or in a cache of data that was pulled from the 
 * MIT Stellar API (not yet implemented).
 *
 */
public abstract class CourseInfo {
	
	public abstract String getCourseName();
	public abstract void setCourseName(String courseName);
		
	public abstract boolean getStarred();
	public abstract void setStarred(boolean starred);

	public abstract boolean getNotify();
	public abstract void setNotify(boolean notify);
	
	@Override
	public String toString() {
		return this.getCourseName();
	}
	
	public static String[] getCourseNames(CourseInfo[] inArray){
		String[] outArray = new String[inArray.length];
		for(int j=0; j<inArray.length; j++){
			outArray[j]=inArray[j].getCourseName();
		}
		return outArray;
	}
}