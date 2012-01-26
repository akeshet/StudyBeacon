package com.teamblobby.studybeacon.datastructures;

import java.util.Date;

import com.google.android.maps.GeoPoint;

public class BeaconInfoSimple extends BeaconInfo {

	private int beaconId;
	private String courseName;
	private GeoPoint loc;
	private int visitors;
	private String workingOn;
	private String details;
	private String telephone;
	private String email;
	private Date created;
	private Date expires;

	public BeaconInfoSimple(int beaconId, String courseName, GeoPoint loc,
			int visitors, String workingOn, String details, String telephone, String email,
			Date created, Date expires) {		

		setBeaconId(beaconId);
		setCourseName(courseName);
		setLoc(loc);
		setVisitors(visitors);
		setWorkingOn(workingOn);
		setDetails(details);
		setTelephone(telephone);
		setEmail(email);
		setCreated(created);
		setExpires(expires);
	}

	/**
	 * Copy constructor. Fragile -- must be edited if we later introduce new fields.
	 * Consider implementing by parcelling/unparcelling, at some point, maybe.
	 * @param copyMe
	 */
	public BeaconInfoSimple(BeaconInfo copyMe) {
		this(copyMe.getBeaconId(),
				copyMe.getCourseName(),
				copyMe.getLoc(),
				copyMe.getVisitors(),
				copyMe.getWorkingOn(),
				copyMe.getDetails(),
				copyMe.getTelephone(),
				copyMe.getEmail(),
				copyMe.getCreated(),
				copyMe.getExpires());
	}

	@Override
	public int getBeaconId() {
		return beaconId;
	}

	@Override
	protected void setBeaconId(int beaconId) {
		this.beaconId = beaconId;
	}

	@Override
	public String getCourseName() {
		return courseName;
	}

	@Override
	public void setCourseName(String courseName) {
		this.courseName = courseName;

	}

	@Override
	public GeoPoint getLoc() {
		return loc;
	}

	@Override
	public void setLoc(GeoPoint loc) {
		this.loc = loc;
	}

	@Override
	public int getVisitors() {
		return visitors;
	}

	@Override
	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}

	@Override
	public String getWorkingOn() {
		return workingOn;
	}

	@Override
	public void setWorkingOn(String workingOn) {
		this.workingOn = workingOn;
	}

	@Override
	public String getDetails() {
		return details;
	}

	@Override
	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String getTelephone() {
		return telephone;
	}

	@Override
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public Date getExpires() {
		return expires;
	}

	@Override
	public void setExpires(Date expires) {
		this.expires = expires;
	}

}
