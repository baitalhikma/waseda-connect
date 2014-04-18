/**
 * Copyright (c) 2014, Bait Al-Hikma LTD. All rights reserved.
 * 
 * This file is part of Waseda Connect.
 *
 * Waseda Connect is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Waseda Connect is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Waseda Connect. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bai.android.data.connection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MapDataAdapter {
  private static final String DATABASE_NAME = "mapData.sqlite";

  /** names of fixed database tables */
  private static final String TABLE_NAME = "locations";

  private int DATABASE_VERSION = 1;
  private boolean isOpen = false;

  private static final String DATABASE_CREATE_LOCATIONS_TABLE = "create table "+ TABLE_NAME +
      "(_id integer PRIMARY KEY AUTOINCREMENT, " +
      "title varchar(128) not null, " +
      "descr varchar(512), " +
      "lat real not null, " +
      "lon real not null, " +
      "type integer not null);";

  private final Context context;

  private DatabaseHelper DBHelper;
  private SQLiteDatabase db;

  /**
   * constructor
   */
  public MapDataAdapter(Context ctx) {
    this.context = ctx;
    DBHelper = new DatabaseHelper(context);
  }

  /**
   * Extension of the SQLiteOpenHelper to create/upgrade the database.
   */
  private class DatabaseHelper extends SQLiteOpenHelper {
    DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
  }

  /** Opens the database */
  public MapDataAdapter open() throws SQLException {
    db = DBHelper.getWritableDatabase();
    isOpen = true;
    return this;
  }

  public void beginTransaction() {
    db.beginTransaction();
  }

  public void setTransactionSuccessful() {
    db.setTransactionSuccessful();
  }

  public void endTransaction() {
    db.endTransaction();
  }

  /** Closes the database */
  public void close() throws SQLException {
    isOpen = false;
    DBHelper.close();
  }

  public boolean isOpen(){
    return isOpen;
  }

  /** Create a table in the database*/
  public void createTable(String createString){
    db.execSQL(createString);
  }

  /** Returns content from the database tables index*/
  public Cursor getLocations(){
    return db.query(TABLE_NAME, null, null, null, null, null, null);
  }

  //	/** Drop table from the database */
  //	public void dropTable(){
  //		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
  //	}

  public void deleteAll() {
    db.execSQL("DELETE FROM " + TABLE_NAME + ";");
  }

  /** insert a table record with a new location */
  public long insertLocation(String title, String descr, int type, double lat, double lon) {
    ContentValues args = new ContentValues();
    args.put("title", title);
    args.put("descr", descr);
    args.put("type", type);
    args.put("lat", lat);
    args.put("lon", lon);
    return db.insert(TABLE_NAME, null, args);
  }

  public Cursor getLocationByTitle(String title) {
    return db.query(TABLE_NAME, null, "title = '" + title + "'", null, null, null, null);
  }

  public Cursor getLocationById(int id) {
    return db.query(TABLE_NAME, null, "_id = " + id , null, null, null, null);
  }
}