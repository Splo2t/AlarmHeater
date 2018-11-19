package com.causoft.heatersetter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingDbOpenHelper {

    private static final String DATABASE_NAME = "settingDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(AlarmDatabase.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+AlarmDatabase.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public SettingDbOpenHelper(Context context){
        this.mCtx = context;
    }

    public SettingDbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        insertColumn("ID0", "");
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }
    public long insertColumn(String userid, String ip){
        ContentValues values = new ContentValues();
        values.put(AlarmDatabase.CreateDB.USERID, userid);
        return mDB.insert(AlarmDatabase.CreateDB._TABLENAME0, null, values);
    }



    // Update DB
    public boolean updateColumn(long id, String userid, String ip){
        ContentValues values = new ContentValues();
        values.put(AlarmDatabase.CreateDB.USERID, userid);
        values.put(AlarmDatabase.CreateDB.TIME, ip);
        return mDB.update(AlarmDatabase.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(AlarmDatabase.CreateDB._TABLENAME0, null, null);
    }

    // Delete DB
    public boolean deleteColumn(long id){
        return mDB.delete(AlarmDatabase.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }
    // Select DB
    public Cursor selectColumns(){
        return mDB.query(AlarmDatabase.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    // sort by column
    public Cursor getSetting(){
        Cursor c = mDB.rawQuery( "SELECT * FROM settingtable;", null);
        return c;
    }



}