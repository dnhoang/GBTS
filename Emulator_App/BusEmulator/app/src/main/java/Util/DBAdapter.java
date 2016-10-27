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

    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "GreenBus";
    private static final String TABLE_NAME = "ticketOff";
    private static final String TABLE_CASH = "ticketOffCash";
    static final String KEY_ROWID = "id";
    static final String KEY_CARDID = "cardid";
    static final String KEY_TICKETTYPEID = "tickettypeid";
    static final String KEY_ROUTECODE = "routecode";
    static final String KEY_BOUGHTDATE = "boughtdate";
    static final String DATABASE_CREATE = "create table ticketOff(id integer primary key AUTOINCREMENT, cardid text, tickettypeid text, routecode text, boughtdate text)";
    static final String DATABASE_CASHTABLE = "create table ticketOffCash(id integer primary key AUTOINCREMENT, tickettypeid text, routecode text, boughtdate text)";

    public DBAdapter(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = DATABASE_CREATE;
        String query2 = DATABASE_CASHTABLE;
        db.execSQL(query);
        db.execSQL(query2);
    }

    public boolean isOfflineDataEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String count = "SELECT count(*) FROM " + TABLE_NAME;
            Cursor mcursor = db.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);
            return icount == 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return false;
    }
    public int countOfflineData() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String count = "SELECT count(*) FROM " + TABLE_NAME;
            Cursor mcursor = db.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);
            return icount;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return 0;
    }
    public boolean isOfflineCashDataEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String count = "SELECT count(*) FROM " + TABLE_CASH;
            Cursor mcursor = db.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);
            db.close();
            return icount == 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return false;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASH + ";");
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

//

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

    //  Offline cash ticket
    public void insertOfflineCashTicket(String tickettypeid, String routecode, String boughtdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();

        //initialValues.put(KEY_ROWID, id);
//        initialValues.put(KEY_CARDID, cardid);
        initialValues.put(KEY_TICKETTYPEID, tickettypeid);
        initialValues.put(KEY_ROUTECODE, routecode);
        initialValues.put(KEY_BOUGHTDATE, boughtdate);
        db.insert(TABLE_CASH, null, initialValues);


        db.close();
    }

    public boolean deleteOfflineCashTicket(long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            return db.delete(TABLE_CASH, KEY_ROWID + " =" + rowId, null) > 0;
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
        }

    }

//

    public List<OfflineTicket> getAllOfflineCashTicket() {
        List<OfflineTicket> items = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CASH;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                OfflineTicket item = new OfflineTicket(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }
    //End offline cash ticket


//    public OfflineTicket getOfflineTicket(int id) {
//        SQLiteDatabase db = null;
//        Cursor cursor = null;
//        try {
//            db = this.getReadableDatabase();
//            cursor = db.query(TABLE_NAME, new String[]{KEY_ROWID,
//                            KEY_CARDID, KEY_TICKETTYPEID, KEY_ROUTECODE, KEY_BOUGHTDATE},
//                    KEY_ROWID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
//            if (cursor != null) {
//                cursor.moveToFirst();
//            } else {
//                return null;
//            }
//
//            OfflineTicket item = new OfflineTicket(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
//            return item;
//        } catch (Exception e) {
//            return null;
//        } finally {
//            cursor.close();
//            db.close();
//        }
//
//
//    }

}
