package com.example.lotto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    //database 파일을 생성
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //실행할 때 DB 최초 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE numbers (_id INTEGER PRIMARY KEY AUTOINCREMENT, no1 TEXT, no2 TEXT, no3 INTEGER, no4 TEXT, no5 TEXT, no6 TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS numbers");
        onCreate(db);
    }



}
