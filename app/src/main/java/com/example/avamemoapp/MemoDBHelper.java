package com.example.avamemoapp;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MemoDBHelper extends SQLiteOpenHelper {
    public SQLiteDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DB_STATUS", "Is database open? ")+ db.isOpen();
        return db;
    }

    private static final String DATABASE_NAME = "memo.db";
    private static final int DATABASE_VERSION = 1;


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
