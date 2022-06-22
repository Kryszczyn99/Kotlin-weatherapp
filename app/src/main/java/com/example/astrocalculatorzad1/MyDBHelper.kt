package com.example.astrocalculatorzad1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context:Context) : SQLiteOpenHelper(context,"CITYDB",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE CITY(CITYID INTEGER PRIMARY KEY AUTOINCREMENT, CITYNAME TEXT)")
        db?.execSQL("INSERT INTO CITY(CITYNAME) VALUES ('lodz')")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}