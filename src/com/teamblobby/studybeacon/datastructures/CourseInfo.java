package com.teamblobby.studybeacon.datastructures;

import java.util.ArrayList;
import java.util.List;

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

	public abstract String getName();
	public abstract void setName(String courseName);

	public abstract boolean getStarred();
	public abstract void setStarred(boolean starred);

	public abstract boolean getNotify();
	public abstract void setNotify(boolean notify);

	public abstract String getDescription();
	public abstract void setDescription(String description);

	public abstract String getPrettyName();
	public abstract void setPrettyName(String prettyName);


	@Override
	public String toString() {
		return this.getPrettyName();
	}

	public static List<String> getCourseNames(List<CourseInfo> courses){
		List<String> courseNames = new ArrayList<String>();

		for(CourseInfo course : courses)
			courseNames.add(course.getName());

		return courseNames;
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
		dest.writeString(this.getName()); // first write course name
		dest.writeBooleanArray(new boolean[] {this.getStarred(),this.getNotify()}); //then write array of starred/notify
		dest.writeString(this.getDescription()); // then write description and pretty name.
		dest.writeString(this.getPrettyName());
	}

	public static final Parcelable.Creator<CourseInfo> CREATOR = 
			new Parcelable.Creator<CourseInfo>() {

		public CourseInfo createFromParcel(Parcel in){
			String courseName = in.readString();
			boolean[] arrayStarNotify = new boolean[2];
			in.readBooleanArray(arrayStarNotify);
			String desc = in.readString();
			String prettyName = in.readString();

			return new CourseInfoSimple(courseName, arrayStarNotify[0], arrayStarNotify[1], desc, prettyName);

		}

		public CourseInfo[] newArray(int size) {
			return new CourseInfo[size];
		}

	};
}