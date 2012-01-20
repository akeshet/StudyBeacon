package com.teamblobby.studybeacon.datastructures;

import java.util.ArrayList;

import com.teamblobby.studybeacon.*;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

/**
 * 
 * CourseInfoSqlite presents the same interface as CourseInfo
 * but is backed by a SQLite database entry. Internally, the CourseInfoSqlite
 * object keeps a primary key and table name, which it uses to update the
 * database entry from setter methods. It also keeps a cached CourseInfoSimple
 * which it uses to return values from getter methods (and which it updates from setter
 * methods).
 *
 * All instances of CourseInfoSqlite share one static database handle. This handle is created on demand
 * in a CourseInfoSqlite constructor, if when first needed.
 */
public class CourseInfoSqlite extends CourseInfo {

	// ***** Database related code

	private static final int DB_VERSION = 4;
	public static final String TAG = "CourseInfoSqlite";

	public static final String MYCOURSES_TABLE = "mycourses";
	public static final String ALLCOURSES_TABLE = "allcourses";

	private static final String DB_NAME = "courseInfoDB";

	private static final String COLUMN_ID = "id";
	private static final String COLUMN_NAME = "courseName";
	private static final String COLUMN_STAR = "courseStarred";
	private static final String COLUMN_NOTIFY = "courseNotify";
	private static final String [] COLUMNS_ALLVALUES = {COLUMN_NAME, COLUMN_STAR, COLUMN_NOTIFY};

	private static final String DB_COLUMNS_DESCRIPTION =  
			"(" + COLUMN_ID + " integer primary key, "
					+ COLUMN_NAME + " text not null, "
					+ COLUMN_STAR + " integer, "
					+ COLUMN_NOTIFY + " integer)";

	private static final String [] TABLE_NAMES = {MYCOURSES_TABLE, ALLCOURSES_TABLE};

	private static SQLiteDatabase database;

	private long id;
	String where;
	private String tableName;

	private CourseInfoSimple cachedInfo;


	private static void openIfNecessaryDB() {
		if (database==null) {
			DatabaseOpener opener = 
					new DatabaseOpener(Global.application.getApplicationContext(), 
							DB_NAME, null, DB_VERSION, DB_COLUMNS_DESCRIPTION, TABLE_NAMES);

			database = opener.getWritableDatabase();
		}
	}

	private void createWhereString() {
		this.where = "id='" + this.id + "'";
	}

	private void setRowValues(ContentValues values) {
		database.update(this.tableName, values, where, null);
	}

	/**
	 * Returns an ArrayList of CourseInfoSqlite objects representing all
	 * of the database entries in the specified * @param table
	 * @return
	 */
	public static ArrayList<CourseInfoSqlite> fetchTable(String tableName) {
		openIfNecessaryDB();
		Cursor cursor=database.query(tableName, new String [] {COLUMN_ID}, null, null, null, null, null);

		ArrayList<CourseInfoSqlite> returnList = new ArrayList<CourseInfoSqlite>(cursor.getCount());

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			returnList.add(new CourseInfoSqlite(tableName, cursor.getLong(0)));
			cursor.moveToNext();
		}

		return returnList;
	}

	/**
	 * 
	 * Removes un-starred CourseInfos from specified table.
	 * @param tableName
	 */
	public static void flushUnstarred(String tableName) {
		openIfNecessaryDB();
		database.execSQL("delete from " + tableName + " where + " + COLUMN_STAR + " = 0");
	}

	// ***** Constructors 

	/** 
	 * Creates a new database in table @param tableName , using @param cachedInfo as 
	 * the cached value and as the initial value of database row.
	 */
	private CourseInfoSqlite(String tableName, CourseInfoSimple cachedInfo) {
		createNewEntry(tableName, cachedInfo);
	}

	private void createNewEntry(String tableName, CourseInfoSimple cachedInfo) {
		this.tableName = tableName;
		this.cachedInfo = cachedInfo;

		openIfNecessaryDB();
		ContentValues cValues = makeCValues(cachedInfo);
		this.id = database.insert(tableName, null, cValues);
		createWhereString();
	}

	private ContentValues makeCValues(CourseInfo courseInfo) {
		ContentValues cValues = new ContentValues();
		cValues.put(COLUMN_NAME, courseInfo.getName());
		cValues.put(COLUMN_STAR, courseInfo.getStarred());
		cValues.put(COLUMN_NOTIFY, courseInfo.getNotify());
		return cValues;
	}



	/**
	 * Creates a new database entry in given table, and with given course info parameters.
	 * @param tableName
	 * @param courseName
	 * @param starred
	 * @param notify
	 */
	public CourseInfoSqlite(String tableName, String courseName, boolean starred, boolean notify) {
		this(tableName, new CourseInfoSimple(courseName, starred, notify));
	}

	/** If no courseInfo exists in the table with the given course name, then creates 
	 * a new database entry in the given table, with copy of course info from 
	 * copyMe.
	 * 
	 * Otherwise, initialized a database backed courseInfo which hooks into the existing
	 * row, and updates the row's values to match the passed CourseInfo.
	 * @param tableName
	 * @param copyMe
	 */
	public CourseInfoSqlite(String tableName, CourseInfo copyMe) {
		openIfNecessaryDB();
		// Query for existing rows with matching coursename
		Cursor cursor = database.query(tableName, new String[] {COLUMN_ID}, 
				COLUMN_NAME+"=?", 
				new String [] {copyMe.getName()}, null, null, null);

		if (cursor.getCount()==0) {
			// If not in database, create new database entry
			createNewEntry(tableName, new CourseInfoSimple(copyMe));
		}
		else {
			cursor.moveToFirst();
			this.id = cursor.getLong(0);
			this.cachedInfo = new CourseInfoSimple(copyMe);
			this.tableName = tableName;
			this.createWhereString();
			ContentValues values = this.makeCValues(cachedInfo);
			database.update(tableName, values, where, null);
		}
		cursor.close();
	}

	/**
	 * Create a new CourseInfoSqlite object from an already existing database entry.
	 * @param tableName Name of the table for existing entry.
	 * @param id  Primary key id of the existing entry.
	 */
	public CourseInfoSqlite(String tableName, long id) {
		openIfNecessaryDB();
		this.id = id;
		this.createWhereString();
		this.tableName = tableName;

		Cursor cursor = database.query(tableName, COLUMNS_ALLVALUES, where, null, null, null, null);
		cursor.moveToFirst();
		this.cachedInfo = new CourseInfoSimple(cursor.getString(0),
				cursor.getInt(1)!=0,
				cursor.getInt(2)!=0);
		cursor.close();
	}



	// ***** Gettors / settors

	@Override
	public String getName() {
		return cachedInfo.getName();
	}

	@Override
	public void setName(String courseName) {
		cachedInfo.setName(courseName);
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, courseName);
		setRowValues(values);
	}

	@Override
	public boolean getStarred() {
		return cachedInfo.getStarred();
	}

	@Override
	public void setStarred(boolean starred) {
		cachedInfo.setStarred(starred);
		ContentValues values = new ContentValues();
		values.put(COLUMN_STAR, starred);
		setRowValues(values);
	}

	@Override
	public boolean getNotify() {
		return cachedInfo.getNotify();
	}

	@Override
	public void setNotify(boolean notify) {
		cachedInfo.setNotify(notify);
		ContentValues values = new ContentValues();
		values.put(COLUMN_NOTIFY, notify);
		setRowValues(values);
	}
}
