package com.teamblobby.studybeacon.datastructures;

import com.teamblobby.studybeacon.*;
import android.content.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class CourseInfoSqlite extends CourseInfo {

	private static final int DB_VERSION = 1;
	public static final String TAG = "CourseInfoSqlite";
	
	public static final String MYCOURSES_TABLE = "mycourses";
	public static final String ALLCOURSES_TABLE = "allcourses";
	
	private static final String DB_NAME = "courseInfoDB";
	
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_NAME = "courseName";
	private static final String COLUMN_STAR = "courseStarred";
	private static final String COLUMN_NOTIFY = "courseNotify";
	
	private static SQLiteDatabase database;
	
	private static class CourseInfoDBOpener extends SQLiteOpenHelper {

		public static final String DBCreateColumns =  
				"courses (" + COLUMN_ID + " integer primary key, "
						+ COLUMN_NAME + " text not null, "
						+ COLUMN_STAR + " integer, "
						+ COLUMN_NOTIFY + " integer)";

		public CourseInfoDBOpener(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Creating CourseInfo database.");
			db.execSQL("create table " + MYCOURSES_TABLE + DBCreateColumns);
			db.execSQL("create table " + ALLCOURSES_TABLE + DBCreateColumns);
			Log.d(TAG, "CourseInfo database created.");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "In onUpgrade. Not upgrading database, creating new one.");
			db.execSQL("drop table " + MYCOURSES_TABLE);
			db.execSQL("drop table " + ALLCOURSES_TABLE);
			Log.d(TAG, "Dropped old tables. Now running onCreate.");
			onCreate(db);
		}

	}

	private String tableName;
	private CourseInfoSimple cachedInfo;
	private long id;
	String where;
	
	private static void openIfNecessaryDB() {
		if (database==null) {
			CourseInfoDBOpener opener = 
					new CourseInfoDBOpener(Global.application.getApplicationContext(), 
							DB_NAME, null, DB_VERSION);
			database = opener.getWritableDatabase();
		}
	}
	
	private CourseInfoSqlite(String tableName, CourseInfoSimple cachedInfo) {
		this.tableName = tableName;
		this.cachedInfo = cachedInfo;
		
		openIfNecessaryDB();
		ContentValues cValues = new ContentValues();
		cValues.put(COLUMN_NAME, cachedInfo.getCourseName());
		cValues.put(COLUMN_STAR, cachedInfo.getStarred());
		cValues.put(COLUMN_NOTIFY, cachedInfo.getNotify());
		this.id = database.insert(tableName, null, cValues);
		this.where = "id='" + this.id + "'";
	}
	
	public CourseInfoSqlite(String tableName, String courseName, boolean starred, boolean notify) {
		this(tableName, new CourseInfoSimple(courseName, starred, notify));
	}
	
	public CourseInfoSqlite(String tableName, CourseInfo copyMe) {
		this(tableName, new CourseInfoSimple(copyMe));
	}
	
	private void setRowValues(ContentValues values) {
		database.update(this.tableName, values, where, null);
	}

	
	@Override
	public String getCourseName() {
		return cachedInfo.getCourseName();
	}

	@Override
	public void setCourseName(String courseName) {
		cachedInfo.setCourseName(courseName);
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
