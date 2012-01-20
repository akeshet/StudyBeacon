package com.teamblobby.studybeacon.datastructures;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Super class which defines the interface that CourseInfo implementations will follow.
 * Implementations may either store course data locally within the object (such as {@link CourseInfoSimple})
 * or in a local SQLite database (not yet implemented) or in a cache of data that was pulled from the 
 * MIT Stellar API (not yet implemented).
 *
 */
public abstract class CourseInfo implements Parcelable,CourseListable{
	
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
	
	public int listableType() {
		return this.getStarred() ? 0 : 1;
	}
	
	// Implementations of parcelable interface for android friendly serialization
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getCourseName()); // first write course name
		dest.writeBooleanArray(new boolean[] {this.getStarred(),this.getNotify()}); //then write array of starred/notify
	}
	
	public static final Parcelable.Creator<CourseInfo> CREATOR = 
									new Parcelable.Creator<CourseInfo>() {
		
			public CourseInfo createFromParcel(Parcel in){
				String courseName = in.readString();
				boolean[] arrayStarNotify = new boolean[2];
				in.readBooleanArray(arrayStarNotify);
				
				return new CourseInfoSimple(courseName, arrayStarNotify[0], arrayStarNotify[1]);
				
			}

			public CourseInfo[] newArray(int size) {
				return new CourseInfo[size];
			}
			
		};
}