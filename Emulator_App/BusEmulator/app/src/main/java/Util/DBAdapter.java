package Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ducdmse61486 on 9/30/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import sample.dto.OfflineTicket;

/**
 * Created by HoangDN on 9/30/2016.
 */
public class DBAdapter extends SQLiteOpenHelper {

    private static final int DB_VERSION = 7;
    private static final String DB_NAME = "MyDB";
    private static final String TABLE_NAME = "ticketOff";
    static final String KEY_ROWID = "id";
    static final String KEY_CARDID = "cardid";
    static final String KEY_TICKETTYPEID = "tickettypeid";
    static final String KEY_ROUTECODE = "routecode";
    static final String KEY_BOUGHTDATE = "boughtdate";
    static final String DATABASE_CREATE = "create table ticketOff(id integer primary key AUTOINCREMENT, cardid text, tickettypeid text, routecode text, boughtdate text)";

    public DBAdapter(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = DATABASE_CREATE;
        db.execSQL(query);
    }
    public boolean isEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM ticketOff";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return icount==0;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void insertOfflineTicket(String cardid, String tickettypeid, String routecode, String boughtdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();

        //initialValues.put(KEY_ROWID, id);
        initialValues.put(KEY_CARDID, cardid);
        initialValues.put(KEY_TICKETTYPEID, tickettypeid);
        initialValues.put(KEY_ROUTECODE, routecode);
        initialValues.put(KEY_BOUGHTDATE, boughtdate);
        db.insert(TABLE_NAME, null, initialValues);


        db.close();
    }

    public boolean deleteOfflineTicket(long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete(TABLE_NAME, KEY_ROWID + " =" + rowId, null) > 0;
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
        }

    }

    public OfflineTicket getOfflineTicket(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME, new String[]{KEY_ROWID,
                            KEY_CARDID, KEY_TICKETTYPEID, KEY_ROUTECODE, KEY_BOUGHTDATE},
                    KEY_ROWID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return null;
            }

            OfflineTicket item = new OfflineTicket(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            return item;
        } catch (Exception e) {
            return null;
        } finally {
            cursor.close();
            db.close();
        }


    }

    public List<OfflineTicket> getAllOfflineTicket() {
        List<OfflineTicket> items = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                OfflineTicket item = new OfflineTicket(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }


}
//public class DBAdapter {
//    static final String KEY_ROWID = "_id";
//    static final String KEY_CARDID = "cardid";
//    static final String KEY_TICKETTYPEID = "tickettypeid";
//    static final String KEY_ROUTECODE = "routecode";
//    static final String KEY_BOUGHTDATE = "boughtdate";
//
//    static final String TAG = "DBAdapter";
//    static final String DATABASE_NAME = "MyDB";
//    static final String DATABASE_TABLE = "offlineticket";
//    static final int DATABASE_VERSION = 1;
//    static final String DATABASE_CREATE = "create table offlineticket(_id integer primary key autoincrement, cardid text not null, tickettypeid text not null, routecode text not null, boughtdate text not null)";
//
//    final Context context;
//    DatabaseHelper DBHelper;
//    SQLiteDatabase db;
//
//    public DBAdapter(Context ctx) {
//        this.context = ctx;
//        DBHelper = new DatabaseHelper(context);
//    }
//
//    private static class DatabaseHelper extends SQLiteOpenHelper {
//        DatabaseHelper(Context context) {
//            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            try {
//                db.execSQL(DATABASE_CREATE);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(TAG, "Upgrading database from version" + oldVersion + "to"
//                    + newVersion + ",which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS offlineticket");
//            onCreate(db);
//        }
//    }
//
//    public void dropTable() {
//
//    }
//
//    //---opens the database---
//    public DBAdapter open() throws SQLException {
//        db = DBHelper.getWritableDatabase();
//        return this;
//    }
//
//    //---closes the database---
//    public void close() {
//        DBHelper.close();
//    }
//
//    //---insert a contact into the database---
//    public long insertOfflineTicket(String cardid, String tickettypeid, String routecode, String boughtdate) {
//        ContentValues initialValues = new ContentValues();
//        initialValues.put(KEY_CARDID, cardid);
//        initialValues.put(KEY_TICKETTYPEID, tickettypeid);
//        initialValues.put(KEY_ROUTECODE, routecode);
//        initialValues.put(KEY_BOUGHTDATE, boughtdate);
//
//        return db.insert(DATABASE_TABLE, null, initialValues);
//    }
//
//    //---deletes a particular contact---
//    public boolean deleteOfflineTicket(long rowId) {
//        return db.delete(DATABASE_TABLE, KEY_ROWID + " =" + rowId, null) > 0;
//    }
//
//    //---retrieves all the contacts---
//    public Cursor getAllOfflineTicket() {
//        return db.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_CARDID,
//                KEY_TICKETTYPEID, KEY_ROUTECODE, KEY_BOUGHTDATE}, null, null, null, null, null);
//    }
//
//    //---retrieves a particular contact---
//    public Cursor getOfflineTicket(long rowId) throws SQLException {
//        Cursor mCursor =
//                db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
//                                KEY_CARDID, KEY_TICKETTYPEID, KEY_ROUTECODE, KEY_BOUGHTDATE}, KEY_ROWID + " =" + rowId, null,
//                        null, null, null, null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//    }
//
//}


