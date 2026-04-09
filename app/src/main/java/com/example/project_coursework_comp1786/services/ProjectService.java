package com.example.project_coursework_comp1786.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.project_coursework_comp1786.models.Project;
import java.util.ArrayList;
import java.util.List;

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

    public boolean updateProject(Project project) {
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
        values.put("isSynced", 0);
        return db.update(DatabaseHelper.TABLE_PROJECTS, values, "id=?", new String[]{String.valueOf(project.getId())}) > 0;
    }

    public boolean deleteProject(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_PROJECTS, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<Project> getAllProjects() {
        return searchProjects("");
    }

    public List<Project> searchProjects(String query) {
        List<Project> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS +
                " WHERE name LIKE ? OR description LIKE ? OR manager LIKE ? ORDER BY id DESC";
        String wildCardQuery = "%" + query + "%";

        Cursor cursor = db.rawQuery(sql, new String[]{wildCardQuery, wildCardQuery, wildCardQuery});

        if (cursor.moveToFirst()) {
            do {
                Project p = new Project(
                        cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7), cursor.getDouble(8),
                        cursor.getString(9), cursor.getString(10), cursor.getString(11),
                        cursor.getInt(12)
                );
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean isProjectCodeExists(String projectCode, long excludeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS + " WHERE projectCode = ? AND id != ?", new String[]{projectCode, String.valueOf(excludeId)});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}