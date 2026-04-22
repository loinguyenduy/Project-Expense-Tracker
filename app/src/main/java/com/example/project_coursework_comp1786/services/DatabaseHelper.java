package com.example.project_coursework_comp1786.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ArchitectLedger.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_PROJECTS = "projects";
    public static final String TABLE_EXPENSES = "expenses";
    public static final String TABLE_USERS = "users";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
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
                + "assignedTo TEXT,"
                + "isSynced INTEGER DEFAULT 0)";
        db.execSQL(CREATE_PROJECTS_TABLE);

        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "projectId INTEGER,"
                + "date TEXT,"
                + "amount REAL,"
                + "currency TEXT,"
                + "type TEXT,"
                + "paymentMethod TEXT,"
                + "claimant TEXT,"
                + "status TEXT,"
                + "description TEXT,"
                + "location TEXT,"
                + "isSynced INTEGER DEFAULT 0,"
                + "FOREIGN KEY(projectId) REFERENCES " + TABLE_PROJECTS + "(id) ON DELETE CASCADE)";
        db.execSQL(CREATE_EXPENSES_TABLE);

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + "uid TEXT PRIMARY KEY,"
                + "email TEXT,"
                + "fullName TEXT,"
                + "role TEXT,"
                + "isActive INTEGER DEFAULT 1)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

}