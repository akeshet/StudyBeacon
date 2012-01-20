package com.teamblobby.studybeacon.datastructures;

import android.os.Parcelable;

public interface CourseListable extends Parcelable{

	public void setStarred(boolean starred);

	public boolean getStarred();
}
