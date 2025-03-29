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
        List<memo> memos = new ArrayList<>();
        Cursor cursor = database.query("memo", new String[]{"_id", "name", "mText", "priority", "date"},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                memo m = new memo();
                m.setMemoID(cursor.getInt(0));
                m.setName(cursor.getString(1));
                m.setText(cursor.getString(2));
                m.setPriority(cursor.getString(3));

                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(Long.parseLong(cursor.getString(4)));
                m.setDate(date);

                memos.add(m);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return memos;
    }
}
