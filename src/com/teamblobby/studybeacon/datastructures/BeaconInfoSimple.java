package com.teamblobby.studybeacon.datastructures;

import java.util.Date;

import com.google.android.maps.GeoPoint;

public class BeaconInfoSimple extends BeaconInfo {

	private int beaconId;
	private String courseName;
	private GeoPoint loc;
	private int visitors;
	private String details;
	private String contact;
	private Date created;
	private Date expires;

	public BeaconInfoSimple(int beaconId, String courseName, GeoPoint loc,
			int visitors, String details, String contact, Date created,
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
				copyMe.getDetails(),
				copyMe.getContact(),
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
	public String getDetails() {
		return details;
	}

	@Override
	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String getContact() {
		return contact;
	}

	@Override
	public void setContact(String contact) {
		this.contact = contact;
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
