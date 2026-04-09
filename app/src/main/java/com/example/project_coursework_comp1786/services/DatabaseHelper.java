package com.example.project_coursework_comp1786.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ArchitectLedger.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PROJECTS = "projects";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROJECTS_TABLE = "CREATE TABLE " + TABLE_PROJECTS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "projectCode TEXT,"
                + "name TEXT,"
                + "description TEXT,"
                + "startDate TEXT,"
                + "endDate TEXT,"
                + "manager TEXT,"
                + "status TEXT,"
                + "budget REAL,"
                + "specialRequirements TEXT,"
                + "clientInfo TEXT,"
                + "jobDifficulty TEXT,"
                + "isSynced INTEGER DEFAULT 0)";
        db.execSQL(CREATE_PROJECTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        onCreate(db);
    }
}