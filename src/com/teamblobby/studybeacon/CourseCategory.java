package com.teamblobby.studybeacon;

import android.os.Parcel;
import android.os.Parcelable;

import com.teamblobby.studybeacon.datastructures.*;

public class CourseCategory implements CourseListable, Parcelable {
	
	public CourseCategory(String nameIn) {
		this.name = nameIn;
	}
	
	private String name;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String nameIn){
		this.name = nameIn;
	}
	
	@Override
	public String toString(){
		return this.getName();
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

}
