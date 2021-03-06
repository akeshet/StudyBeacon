package com.teamblobby.studybeacon.datastructures;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.maps.GeoPoint;

public abstract class BeaconInfo implements Parcelable {

	protected static final String TAG = "BeaconInfo";

	public abstract int getBeaconId();
	protected abstract void setBeaconId(int beaconId);

	public abstract String getCourseName();
	public abstract void setCourseName(String courseName);

	public abstract GeoPoint getLoc();
	public abstract void setLoc(GeoPoint loc);

	public abstract int getVisitors();
	public abstract void setVisitors(int visitors);

	public abstract String getWorkingOn();
	public abstract void setWorkingOn(String workingOn);

	public abstract String getDetails();
	public abstract void setDetails(String details);

	public abstract String getTelephone();
	public abstract void setTelephone(String telephone);

	public abstract String getEmail();
	public abstract void setEmail(String email);

	public abstract Date getCreated();
	public abstract void setCreated(Date created);

	public abstract Date getExpires();
	public abstract void setExpires(Date expires);

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
		dest.writeString(getWorkingOn());
		dest.writeString(getDetails());
		dest.writeString(getTelephone());
		dest.writeString(getEmail());
		DateFormat df = new SimpleDateFormat();
		dest.writeString(df.format(getCreated()));
		dest.writeString(df.format(getExpires()));

	}

	public final static Parcelable.Creator<BeaconInfo> CREATOR =
			new Parcelable.Creator<BeaconInfo>() {

		public BeaconInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub

			int beaconId = source.readInt(); 
			String courseName = source.readString(); 
			GeoPoint loc = new GeoPoint(source.readInt(), source.readInt());
			int visitors = source.readInt();
			String workingOn = source.readString();
			String details = source.readString();
			String telephone = source.readString();
			String email = source.readString();
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

			return new BeaconInfoSimple(beaconId, courseName, loc, visitors, workingOn, details, telephone, email, created, expires);

		}

		public BeaconInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new BeaconInfo[size];
		}
	};

	public boolean equals(BeaconInfo other) {
		return ((getBeaconId() == other.getBeaconId())
				&& getCourseName().equals(other.getCourseName())
				&& getLoc().equals(other.getLoc())
				&& (getVisitors() == other.getVisitors())
				&& getWorkingOn().equals(other.getWorkingOn())
				&& getDetails().equals(other.getDetails())
				&& getTelephone().equals(other.getTelephone())
				&& getEmail().equals(other.getEmail())
				&& getCreated().equals(other.getCreated())
				&& getExpires().equals(other.getExpires()));
	}

}
