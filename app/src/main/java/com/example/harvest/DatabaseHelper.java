package com.example.harvest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COLUMN_DATA = "data";
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_QUERIES = "queries";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_ACTION = "action";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the queries table
        String createTableQuery = "CREATE TABLE " + TABLE_QUERIES + " (" +
                COLUMN_TYPE + " TEXT," +
                COLUMN_ACTION + "TEXT, " +
                COLUMN_TIMESTAMP + " TEXT," +
                COLUMN_DATA + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    public void insertQuery(String payload) {
        // Get the current timestamp in UTC
        String timestamp = String.valueOf(System.currentTimeMillis());

        // Prepare the values to be inserted
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, "queries");
        values.put(COLUMN_ACTION, "insert");
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_DATA, payload);

        // Get a writable database instance
        SQLiteDatabase db = getWritableDatabase();

        // Insert the values into the table
        db.insert(TABLE_QUERIES, null, values);

        // Close the database connection
        db.close();
    }
}
