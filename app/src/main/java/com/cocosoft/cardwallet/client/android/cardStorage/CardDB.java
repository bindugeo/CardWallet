package com.cocosoft.cardwallet.client.android.cardStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// CardDb class that encapsulates the database functions of this application
public class CardDB
{

    // define constants
    public static final String KEY_ROWID = "CardID";
    public static final String KEY_CompName= "CompName";
    public static final String KEY_Details = "Details";
    public static final String KEY_Format = "Format";
   // public static final String KEY_Mobile01 = "Mobile01";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "loyaltyCardDB";
    private static final String DATABASE_TABLE = "Cards";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table Cards (CardID integer primary key autoincrement, CompName text not null, Details text not null, Format text not null);";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public CardDB(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // creates the db if it does not exist
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        // called when the db needs upgrading
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }

    //open db
    public CardDB open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //close db
    public void close()
    {
        DBHelper.close();
    }

    //add a record
    public long insertCards(String CompName, String Details, String Fmt)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CompName, CompName);
        initialValues.put(KEY_Details, Details);
        initialValues.put(KEY_Format, Fmt);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //delete a record
    public boolean deleteCards(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //get all record
    public Cursor getAllCards()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_CompName,KEY_Details,KEY_Format}, null, null, null, null, KEY_CompName);
    }

    //get a record
    public Cursor getCards(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,KEY_CompName,KEY_Details, KEY_Format},
                        KEY_ROWID + "=" + rowId,
                        null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //record update
    public boolean updateCards(long rowId, String FirstName, String LastName, String emailID, String Mobile01)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_CompName, FirstName);
        args.put(KEY_Details, LastName);
        args.put(KEY_Format, emailID);
        return db.update(DATABASE_TABLE, args,
                KEY_ROWID + "=" + rowId, null) > 0;
    }
}