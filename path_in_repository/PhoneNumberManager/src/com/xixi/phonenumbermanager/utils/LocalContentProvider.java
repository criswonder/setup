package com.xixi.phonenumbermanager.utils;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.text.TextUtils;
import android.util.Log;

public class LocalContentProvider extends ContentProvider {
	private static final String TAG = "LocalContentProvider";
    private static final String DATABASE_NAME = "phone_n_m.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CALLLOG_BAK = "calllog_bak";
    private static final String TABLE_CALLLOG_BAK1 = "sms_bak";
    private static HashMap<String, String> sNotesProjectionMap;
    private static final UriMatcher sUriMatcher;
    public static final Uri bak_uri = Uri.parse("content://com.xixi.pnm");
    public static final String authority="com.xixi.pnm";
	private static final int CALL_LOGS = 1;
	private static final int SMS_S = 3;
	private static final int CALL_LOG_ITEM = 2;
    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
        	
        	String str = "CREATE TABLE " + TABLE_CALLLOG_BAK + " (" +
                    Calls._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Calls.NUMBER + " TEXT," +
                    Calls.DATE + " INTEGER," +
                    Calls.DURATION + " INTEGER," +
                    Calls.TYPE + " INTEGER," +
                    Calls.NEW + " INTEGER," +
                    Calls.CACHED_NAME + " TEXT," +
                    Calls.CACHED_NUMBER_TYPE + " INTEGER," +
                    Calls.CACHED_NUMBER_LABEL + " TEXT" +
            ");";
        	String str1 = "CREATE TABLE " + TABLE_CALLLOG_BAK1 + " (" +
        			Calls._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        			 "address TEXT," +
        			"date" + " INTEGER," +
        			"type" + " INTEGER," +
        			"body" + " INTEGER" +

        			");";
            db.execSQL(str);
            db.execSQL(str1);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    private DatabaseHelper mOpenHelper;
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
        case CALL_LOGS:
            qb.setTables(TABLE_CALLLOG_BAK);
            qb.setProjectionMap(sNotesProjectionMap);
            break;
        case SMS_S:
        	  qb.setTables(TABLE_CALLLOG_BAK1);
              qb.setProjectionMap(sNotesProjectionMap);
              break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // If no sort order is specified use the default
        String orderBy=null;
        if (TextUtils.isEmpty(sortOrder)) {
            //orderBy = NotePad.Notes.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
//        case NOTES:
//            return Notes.CONTENT_TYPE;
//        case NOTE_ID:
//            return Notes.CONTENT_ITEM_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
    	String table;
        if(sUriMatcher.match(uri)==CALL_LOGS){
        	table = TABLE_CALLLOG_BAK;
        }else if(sUriMatcher.match(uri)==SMS_S){
        	table = TABLE_CALLLOG_BAK1;
        }else{
        	throw new IllegalArgumentException("Unknown URI " + uri);
        }
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
//        Long now = Long.valueOf(System.currentTimeMillis());

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(table, null, values);
        if (rowId > 0) {
//            Uri noteUri = ContentUris.withAppendedId(bak_uri, rowId);
//            getContext().getContentResolver().notifyChange(noteUri, null);
            return null;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {return -1;}
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
    	return -1;
    }
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
       sUriMatcher.addURI(authority, "callLogs", CALL_LOGS);
       sUriMatcher.addURI(authority, "smss", SMS_S);
       sUriMatcher.addURI(authority, "callLogs/#", CALL_LOG_ITEM);
    }
}
