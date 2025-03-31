package com.example.avamemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MemoDataSource {
    private SQLiteDatabase database;
    private MemoDBHelper dbHelper;

    public MemoDataSource(Context context){
        dbHelper = new MemoDBHelper(context);
    }

    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public boolean insert(memo m){
        boolean didSucceed = false;
        try{
            ContentValues initialValues = new ContentValues();

            initialValues.put("name", m.getName());
            initialValues.put("mText", m.getMText());
            initialValues.put("priority", m.getPriority());
            initialValues.put("date", String.valueOf(m.getDate().getTimeInMillis()));

            didSucceed = database.insert("memo", null, initialValues) > 0;
        }
        catch (Exception e){
            //Do nothing - will return false if there is an exception
        }
        return didSucceed;
    }
    public boolean update(memo m){
        boolean didSucceed = false;
        try{
            Long rowId = Long.valueOf(m.getMemoID());
            ContentValues updateValues = new ContentValues();

            updateValues.put("name", m.getName());
            updateValues.put("mText", m.getMText());
            updateValues.put("priority", m.getPriority());
            updateValues.put("date", String.valueOf(m.getDate().getTimeInMillis()));

            didSucceed = database.update("memo", updateValues, "_id=" + rowId, null) > 0;
        }
        catch (Exception e){
            //Do nothing - will return false if there is an exception
        }
        return didSucceed;
    }

    //come back and add getMemoName and all the code that comes after
    // Retrieve all memos from the database
    public List<memo> getAllMemos() {
        //This creates an empty list where all retrieved memos will be stored.
        List<memo> memos = new ArrayList<>();
        /// Query the database to retrieve all memos
        /// The query method returns a Cursor object that contains the results of the query.
        /// The Cursor object is used to iterate through the results.
        /// The query() function asks the database for all records from the memo table.

        Cursor cursor = database.query("memo", new String[]{"_id", "name", "mText", "priority", "date"},
                null, null, null, null, null);

        /// If cursor is not empty and contains at least one memo, we start processing.
        if (cursor != null && cursor.moveToFirst()) {

            /// Loop through the cursor and create memo objects for each record
            /// This runs through all the memos in the database one by one.
            do {
                /// This creates a new memo to store data retrieved from the database.
                memo m = new memo();
                m.setMemoID(cursor.getInt(0));  // Get memo ID (1st column) UNO (MHHH BUT IT'S ACTUALLY zeeee-rooooo
                m.setName(cursor.getString(1));  // Get memo title (2nd column) DOS
                m.setText(cursor.getString(2));  // Get memo description (3rd column) TRES
                m.setPriority(cursor.getString(3));  // Get priority (4th column) CUATRO!!!!!!!!

                /// Get the date from the database and convert it to a Calendar object 🔢➡️🗓️
                /// The date is stored as a String, so we convert it into a number using Long.parseLong().
                /// setTimeInMillis() updates the Calendar object to match the stored date.
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(Long.parseLong(cursor.getString(4)));
                m.setDate(date);
                /// This ensures the database connection is properly closed.
                memos.add(m);

                /// This loop keeps running as long as there are more memos in the database.
                /// Once we reach the last memo, the loop ends.
            } while (cursor.moveToNext());
            ///After we’re done reading all memos, we must close it so the app doesn't freaking crash on us...🫤
            cursor.close();
        }
        /// The method returns a list of all stored memos. I waNT TO SEE THE MEMOS! 😭
        return memos;
    }
}
