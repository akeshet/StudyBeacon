package com.teamblobby.studybeacon.datastructures;

import java.util.Date;
import com.google.android.maps.GeoPoint;

public abstract class BeaconInfo {

	public abstract int getBeaconId();
	protected abstract void setBeaconId(int beaconId);

	public abstract String getCourseName();
	public abstract void setCourseName(String courseName);

	public abstract GeoPoint getLoc();
	public abstract void setLoc(GeoPoint loc);

	public abstract int getVisitors();
	public abstract void setVisitors(int visitors);

	public abstract String getDetails();
	public abstract void setDetails(String details);


	public abstract String getContact();
	public abstract void setContact(String contact);

	public abstract Date getCreated();
	public abstract void setCreated(Date created);

	public abstract Date getExpires();
	public abstract void setExpires(Date expires);

	public BeaconInfo(int beaconId, 
			String courseName, 
			GeoPoint loc,
			int visitors,
			String details, 
			String contact,
			Date created, 
			Date expires) {
			
		setBeaconId(beaconId);
		setCourseName(courseName);
		setLoc(loc);
		setVisitors(visitors);
		setDetails(details);
		setContact(contact);
		setCreated(created);
		setExpires(expires);
	}

}
