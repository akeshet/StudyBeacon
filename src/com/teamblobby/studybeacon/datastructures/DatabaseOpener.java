package com.teamblobby.studybeacon.datastructures;

import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DatabaseOpener extends SQLiteOpenHelper {

	private final String DBCreateColumns;
	private final String[] tableNames;
	private final String name;
	
	private static final String TAG = "DatabaseOpener";

	public DatabaseOpener(Context context, String name,
			CursorFactory factory, int version, 
			String DBCreateColumns, String [] tableNames) {
		super(context, name, factory, version);
		this.name = name;
		this.tableNames = tableNames;
		this.DBCreateColumns = DBCreateColumns;		
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "Creating " + name + " + database.");
		for (String table : tableNames) {
			createTable(db, table);
		}
		Log.d(TAG, name +" database created.");
	}

	private void createTable(SQLiteDatabase db, String table) {
		db.execSQL("create table " + table + DBCreateColumns);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "In onUpgrade. Not upgrading database, creating new one.");

		for (String table : tableNames) {
			dropTableIfExists(db, table);
		}

		Log.d(TAG, "Dropped old tables. Now running onCreate.");
		onCreate(db);
	}

	private void dropTableIfExists(SQLiteDatabase db, String table) {
		try {
			db.execSQL("drop table " + table);
		} catch (SQLException e) {
			Log.d(TAG, "Exception when trying to drop " + table + " table. Ignoring.");
		}
	}
}
