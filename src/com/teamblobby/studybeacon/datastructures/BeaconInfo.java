package com.teamblobby.studybeacon.datastructures;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.maps.GeoPoint;

public abstract class BeaconInfo implements Parcelable {

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
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
	
		dest.writeInt(getBeaconId());
		dest.writeString(getCourseName());
		GeoPoint p = getLoc();
		dest.writeInt(p.getLatitudeE6());
		dest.writeInt(p.getLongitudeE6());
		dest.writeInt(getVisitors());
		dest.writeString(getDetails());
		dest.writeString(getContact());
		dest.writeString(getCreated().toGMTString());
		dest.writeString(getExpires().toGMTString());
		
	}

	public final static Parcelable.Creator<BeaconInfo> CREATOR =
			new Parcelable.Creator<BeaconInfo>() {

				public BeaconInfo createFromParcel(Parcel source) {
					// TODO Auto-generated method stub
					
					int beaconId = source.readInt(); 
					String courseName = source.readString(); 
					GeoPoint loc = new GeoPoint(source.readInt(), source.readInt());
					int visitors = source.readInt();
					String details = source.readString();
					String contact = source.readString();
					DateFormat df = new SimpleDateFormat();
					Date created = new Date();
					Date expires = new Date();
					try {
						created = df.parse(source.readString());
						expires = df.parse(source.readString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return new BeaconInfoSimple(beaconId, courseName, loc, visitors, details, contact, created, expires);
					
				}

				public BeaconInfo[] newArray(int size) {
					// TODO Auto-generated method stub
					return new BeaconInfo[size];
				}
			};
	
}
