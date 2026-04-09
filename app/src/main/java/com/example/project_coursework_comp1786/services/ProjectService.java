package com.example.project_coursework_comp1786.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import com.example.project_coursework_comp1786.models.Project;

public class ProjectService {
    private DatabaseHelper dbHelper;

    public ProjectService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertProject(Project project) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("projectCode", project.getProjectCode());
        values.put("name", project.getName());
        values.put("description", project.getDescription());
        values.put("startDate", project.getStartDate());
        values.put("endDate", project.getEndDate());
        values.put("manager", project.getManager());
        values.put("status", project.getStatus());
        values.put("budget", project.getBudget());
        values.put("specialRequirements", project.getSpecialRequirements());
        values.put("clientInfo", project.getClientInfo());
        values.put("jobDifficulty", project.getJobDifficulty());
        values.put("isSynced", project.getIsSynced());

        return db.insert(DatabaseHelper.TABLE_PROJECTS, null, values);
    }

    public boolean isProjectCodeExists(String projectCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS + " WHERE projectCode = ?", new String[]{projectCode});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public List<Project> getAllProjects() {
        List<Project> projectList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("projectCode"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
                String manager = cursor.getString(cursor.getColumnIndexOrThrow("manager"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                double budget = cursor.getDouble(cursor.getColumnIndexOrThrow("budget"));
                String specialReq = cursor.getString(cursor.getColumnIndexOrThrow("specialRequirements"));
                String client = cursor.getString(cursor.getColumnIndexOrThrow("clientInfo"));
                String diff = cursor.getString(cursor.getColumnIndexOrThrow("jobDifficulty"));
                int isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced"));

                Project project = new Project(id, code, name, desc, startDate, endDate, manager, status, budget, specialReq, client, diff, isSynced);
                projectList.add(project);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return projectList;
    }
}