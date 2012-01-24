package com.teamblobby.studybeacon.datastructures;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.teamblobby.studybeacon.Global;

public class BeaconInfoSqlite extends BeaconInfo {

	// ***** Database related code

	private static final int DB_VERSION = 3;
	public static final String TAG = "BeaconInfoSqlite";

	public static final String MYBEACONS_TABLE = "mybeacons";
	public static final String ALLBEACONS_TABLE = "allbeacons";

	private static final String DB_NAME = "beaconInfoDB";

	private static final String COLUMN_ID = "id";
	private static final String COLUMN_BEACONID = "beaconId";
	private static final String COLUMN_COURSENAME = "courseName";
	private static final String COLUMN_LOC_LAT = "locLat";
	private static final String COLUMN_LOC_LONG = "locLong";
	private static final String COLUMN_VISITORS = "visitors";
	private static final String COLUMN_DETAILS = "details";
	private static final String COLUMN_TELEPHONE = "telephone";
	private static final String COLUMN_EMAIL = "email";
	private static final String COLUMN_CREATED = "created";
	private static final String COLUMN_EXPIRES = "expires";

	private static final String [] COLUMNS_ALLVALUES = {
		COLUMN_ID ,
		COLUMN_BEACONID,
		COLUMN_COURSENAME, 
		COLUMN_LOC_LAT ,
		COLUMN_LOC_LONG,
		COLUMN_VISITORS,
		COLUMN_DETAILS,
		COLUMN_TELEPHONE,
		COLUMN_EMAIL,
		COLUMN_CREATED ,
		COLUMN_EXPIRES
	};

	private static final String DB_COLUMNS_DESCRIPTION =  
			"(" + COLUMN_ID + " integer primary key, " +
					COLUMN_BEACONID + " integer, " +
					COLUMN_COURSENAME + " text not null, " + 
					COLUMN_LOC_LAT + " integer, " +
					COLUMN_LOC_LONG + " integer, "+
					COLUMN_VISITORS + " integer, "+
					COLUMN_DETAILS + " text, " +
					COLUMN_TELEPHONE + " text, " +
					COLUMN_EMAIL + " text, " +
					COLUMN_CREATED + " text, " +
					COLUMN_EXPIRES + " text)";

	private static final String [] TABLE_NAMES = {MYBEACONS_TABLE, ALLBEACONS_TABLE};

	private static SQLiteDatabase database;

	private static void openIfNecessaryDB() {
		if (database==null) {
			DatabaseOpener opener = 
					new DatabaseOpener(Global.application.getApplicationContext(), 
							DB_NAME, null, DB_VERSION, DB_COLUMNS_DESCRIPTION, TABLE_NAMES);

			database = opener.getWritableDatabase();
		}
	}

	private long id;
	String where;
	private String tableName;

	private BeaconInfoSimple cachedInfo;


	private void createWhereString() {
		this.where = "id='" + this.id + "'";
	}

	private void setRowValues(ContentValues values) {
		database.update(this.tableName, values, where, null);
	}

	/**
	 * Returns an ArrayList of BeaconInfoSqlite objects representing all
	 * of the database entries in the specified * @param table
	 * @return
	 */
	public static ArrayList<BeaconInfoSqlite> fetchTable(String tableName) {
		openIfNecessaryDB();
		Cursor cursor=database.query(tableName, new String [] {COLUMN_ID}, null, null, null, null, null);

		ArrayList<BeaconInfoSqlite> returnList = new ArrayList<BeaconInfoSqlite>(cursor.getCount());

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			returnList.add(new BeaconInfoSqlite(tableName, cursor.getLong(0)));
			cursor.moveToNext();
		}

		return returnList;
	}


	// ***** Constructors

	/** 
	 * Creates a new database in table @param tableName , using @param cachedInfo as 
	 * the cached value and as the initial value of database row.
	 */
	private BeaconInfoSqlite(String tableName, BeaconInfoSimple cachedInfo) {
		this.tableName = tableName;
		this.cachedInfo = cachedInfo;

		openIfNecessaryDB();
		ContentValues cValues = new ContentValues();
		cValues.put(COLUMN_BEACONID, cachedInfo.getBeaconId());
		cValues.put(COLUMN_COURSENAME, cachedInfo.getCourseName());
		GeoPoint loc = cachedInfo.getLoc();
		cValues.put(COLUMN_LOC_LAT, loc.getLatitudeE6());
		cValues.put(COLUMN_LOC_LONG, loc.getLongitudeE6());
		cValues.put(COLUMN_VISITORS, cachedInfo.getVisitors());
		cValues.put(COLUMN_DETAILS, cachedInfo.getDetails());
		cValues.put(COLUMN_TELEPHONE, cachedInfo.getTelephone());
		cValues.put(COLUMN_EMAIL, cachedInfo.getEmail());
		cValues.put(COLUMN_CREATED, cachedInfo.getCreated().toGMTString());
		cValues.put(COLUMN_EXPIRES, cachedInfo.getExpires().toGMTString());

		this.id = database.insert(tableName, null, cValues);
		createWhereString();
	}




	/** Creates a new database entry in the given table, with copy of info from 
	 * copyMe.
	 * @param tableName
	 * @param copyMe
	 */
	public BeaconInfoSqlite(String tableName, BeaconInfo copyMe) {
		this(tableName, new BeaconInfoSimple(copyMe));
	}

	/**
	 * Create a new BeaconInfoSqlite object from an already existing database entry.
	 * @param tableName Name of the table for existing entry.
	 * @param id  Primary key id of the existing entry.
	 */
	public BeaconInfoSqlite(String tableName, long id) {
		openIfNecessaryDB();
		this.id = id;
		this.createWhereString();
		this.tableName = tableName;

		Cursor cursor = database.query(tableName, COLUMNS_ALLVALUES, where, null, null, null, null);
		cursor.moveToFirst();
		this.cachedInfo = new BeaconInfoSimple(
				cursor.getInt(0),
				cursor.getString(1),
				new GeoPoint(cursor.getInt(2), cursor.getInt(3)),
				cursor.getInt(4),
				cursor.getString(5),
				cursor.getString(6),
				cursor.getString(7),
				new Date(cursor.getString(8)),
				new Date(cursor.getString(9))
				);
		cursor.close();
	}



	// ***** Gettors / settors


	@Override
	public int getBeaconId() {
		return cachedInfo.getBeaconId();
	}

	@Override
	protected void setBeaconId(int beaconId) {
		cachedInfo.setBeaconId(beaconId);
		ContentValues v = new ContentValues();
		v.put(COLUMN_BEACONID, beaconId);
		setRowValues(v);
	}

	@Override
	public String getCourseName() {
		return cachedInfo.getCourseName();
	}

	@Override
	public void setCourseName(String courseName) {
		cachedInfo.setCourseName(courseName);
		ContentValues v = new ContentValues();
		v.put(COLUMN_COURSENAME, courseName);
		setRowValues(v);
	}

	@Override
	public GeoPoint getLoc() {
		return cachedInfo.getLoc();
	}

	@Override
	public void setLoc(GeoPoint loc) {
		cachedInfo.setLoc(loc);
		ContentValues v = new ContentValues();
		v.put(COLUMN_LOC_LAT, loc.getLatitudeE6());
		v.put(COLUMN_LOC_LONG, loc.getLongitudeE6());
		setRowValues(v);
	}

	@Override
	public int getVisitors() {
		return cachedInfo.getVisitors();
	}

	@Override
	public void setVisitors(int visitors) {
		cachedInfo.setVisitors(visitors);
		ContentValues v = new ContentValues();
		v.put(COLUMN_VISITORS, visitors);
		setRowValues(v);
	}

	@Override
	public String getDetails() {
		return cachedInfo.getDetails();
	}

	@Override
	public void setDetails(String details) {
		cachedInfo.setDetails(details);
		ContentValues v = new ContentValues();
		v.put(COLUMN_DETAILS, details);
		setRowValues(v);}

	@Override
	public String getTelephone() {
		return cachedInfo.getTelephone();
	}

	@Override
	public void setTelephone(String telephone) {
		cachedInfo.setTelephone(telephone);
		ContentValues v = new ContentValues();
		v.put(COLUMN_TELEPHONE, telephone);
		setRowValues(v);

	}

	@Override
	public String getEmail() {
		return cachedInfo.getEmail();
	}

	@Override
	public void setEmail(String email) {
		cachedInfo.setEmail(email);
		ContentValues v = new ContentValues();
		v.put(COLUMN_EMAIL, email);
		setRowValues(v);

	}

	@Override
	public Date getCreated() {
		return cachedInfo.getCreated();
	}

	@Override
	public void setCreated(Date created) {
		cachedInfo.setCreated(created);
		ContentValues v = new ContentValues();
		v.put(COLUMN_CREATED, created.toGMTString());
		setRowValues(v);
	}

	@Override
	public Date getExpires() {
		return cachedInfo.getExpires();
	}

	@Override
	public void setExpires(Date expires) {
		cachedInfo.setExpires(expires);
		ContentValues v = new ContentValues();
		v.put(COLUMN_EXPIRES, expires.toGMTString());
		setRowValues(v);

	}
	
	/**
	 * Returns true if we are at a beacon (ie My Beacons table is non empty).
	 * @return
	 */
	public static boolean atBeacon() {
		openIfNecessaryDB();
		Cursor cursor = database.query(MYBEACONS_TABLE, new String [] {COLUMN_ID}, 
				null, null, null, null, null);

		int count = cursor.getCount();
		
		cursor.close();
		
		if (count>0)
			return true;
		
		return false;
	}

	/**
	 * Returns a (should-be-unique) beacon info for the beacon we are at,
	 * if we are checked into one. Otherwise returns null.
	 * @return
	 */
	public static BeaconInfoSqlite getCurrentBeacon() {
		openIfNecessaryDB();
		Cursor cursor = database.query(MYBEACONS_TABLE, new String [] {COLUMN_ID}, 
				null, null, null, null, null);

		int count = cursor.getCount();

		if (count==0) {
			cursor.close();
			return null;
		}

		if (count>1)
			Log.e(TAG, "Found more than 1 row in My Beacons table, in getCurrentBeacon method. Returning 1st one, but this should not happen.");

		cursor.moveToFirst();

		long id = cursor.getInt(0);		
		cursor.close();
		
		return new BeaconInfoSqlite(MYBEACONS_TABLE, id);
	}
	
	/**
	 * Clears the My Beacons table, and inserts passed beacon as the current beacon.
	 * If beacon is null, does not insert an entry, and simply clears the My Beacons table.
	 * @param beacon
	 */
	public static void setCurrentBeacon(BeaconInfo beacon) {
		openIfNecessaryDB();
		database.delete(MYBEACONS_TABLE, null, null);
		
		if (beacon!=null)
			new BeaconInfoSqlite(MYBEACONS_TABLE, beacon); // create db entry
	}

}
