package com.teamblobby.studybeacon.datastructures;

import java.util.Date;

import com.google.android.maps.GeoPoint;

// This is immutable once you create it. Do we want that?
// You can change it later if this is bad.
public class Beacon {
	public final String course;
	public final GeoPoint loc;
	public final int visitors;
	public final String details;
	public final String contact;
	public final Date created;
	public final Date expires;
	
	public Beacon(String _course, GeoPoint _loc, int _visitors,
			String _details, String _contact,
			Date _created, Date _expires) {
		course = _course;
		loc = _loc;
		visitors = _visitors;
		details = _details;
		contact = _contact;
		created = _created;
		expires = _expires;
	}
	
}
