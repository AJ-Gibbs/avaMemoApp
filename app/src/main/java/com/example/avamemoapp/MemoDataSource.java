package com.example.avamemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;

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

    //may not use this method
    //check back here later to define whaat this method does

    public ArrayList<String> getMemoName(){
        ArrayList<String> memoNames = new ArrayList<String>();
        try {
            String query = "SELECT name FROM memo";
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                memoNames.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            memoNames = new ArrayList<String>();
        }
        return memoNames;
    }

    public ArrayList<memo> getMemos(String sortField, String sortOrder){
        ArrayList<memo> memos = new ArrayList<memo>();
        try {
            String query = "SELECT * FROM memo ORDER BY " + sortField + " " + sortOrder;
            Cursor cursor = database.rawQuery(query, null);
            memo newMemo;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                newMemo= new memo();
                newMemo.setMemoID(cursor.getInt(0));
                newMemo.setName(cursor.getString(1));
                newMemo.setText(cursor.getString(2));
                newMemo.setPriority(cursor.getString(3));
                newMemo.getDate().setTimeInMillis(Long.parseLong(cursor.getString(4)));
                memos.add(newMemo);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            memos = new ArrayList<memo>();
        }
        return memos;
    }

    public memo getSpecificMemo(int memoID){
        memo m = new memo();
        String query = "SELECT * FROM memo WHERE _id = " + memoID;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
                m.setMemoID(cursor.getInt(0));
                m.setName(cursor.getString(1));
                m.setText(cursor.getString(2));
                m.setPriority(cursor.getString(3));
                m.getDate().setTimeInMillis(Long.parseLong(cursor.getString(4)));

            cursor.close();
        }

        return m;
    }

    public boolean deleteMemo(int memoID){
        boolean didDelete = false;
        try{
            didDelete = database.delete("memo", "_id=" + memoID, null) > 0;
        }
        catch (Exception e){
            //Do nothing - will return false if there is an exception
        }
        return didDelete;
    }
}
