package com.teamblobby.studybeacon.datastructures;

import android.os.Parcel;
import android.os.Parcelable;

public class CourseCategory implements CourseListable, Parcelable {
	
	public CourseCategory(String nameIn, String descriptionIn) {
		this.name = nameIn;
		this.description = descriptionIn;
	}
	
	public CourseCategory(String nameIn) {
		this(nameIn,new String());
	}
	
	private String name;
	private String description;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String nameIn){
		this.name = nameIn;
	}
	
	@Override
	public String toString(){
		return this.getPrettyName();
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getName());
	}
	
	public Parcelable.Creator<CourseCategory> CREATOR = new Parcelable.Creator<CourseCategory>() {

		public CourseCategory createFromParcel(Parcel source) {
			
			return new CourseCategory(source.readString());
		}

		public CourseCategory[] newArray(int size) {
			return new CourseCategory[size];
		}
	}; 

	public CourseInfo getCourseInfo() {
		return null;
	}

	public void setStarred(boolean starred) {
		return; //do nothing for course category
	}

	public boolean getStarred() {
		return false; //return false for course category
	}

	public int listableType() {
		return CourseListable.TYPE_CATEGORY;
	}

	public String getPrettyName() {
		return "Course "+this.getName(); // TODO maybe make string resource
	}

	public String getDescription() {
		return this.description;
	}

}
