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
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // getWriteableDatabase() to insert data to DB
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
        values.put("assignedTo", project.getAssignedTo());
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
        values.put("assignedTo", project.getAssignedTo());
        values.put("isSynced", 0); // set to 0 to indicate that the project is not synced
        return db.update(DatabaseHelper.TABLE_PROJECTS, values, "id=?", new String[]{String.valueOf(project.getId())}) > 0;
    }

    public boolean deleteProject(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Set isSynced = -1 to projects table
        ContentValues values = new ContentValues();
        values.put("isSynced", -1);
        int rows = db.update(DatabaseHelper.TABLE_PROJECTS, values, "id=?", new String[]{String.valueOf(id)});

        // Set isSynced = -1 to expenses table
        ContentValues expValues = new ContentValues();
        expValues.put("isSynced", -1);
        db.update(DatabaseHelper.TABLE_EXPENSES, expValues, "projectId=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public List<Project> getAllProjects() {
        return searchAndFilterProjects("", "", "", "All", "");
    }

    public List<Project> searchAndFilterProjects(String query, String startDate, String endDate, String status, String manager) {
        List<Project> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder sql = new StringBuilder("SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS + " WHERE isSynced != -1");
        List<String> args = new ArrayList<>();

        //search by name, description, or projectCode
        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ? OR projectCode LIKE ?)");
            String wildCard = "%" + query.trim() + "%";
            args.add(wildCard); args.add(wildCard); args.add(wildCard);
        }

        //filter by start date and end date
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND startDate >= ?");
            args.add(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND endDate <= ?");
            args.add(endDate);
        }

        //filter by status
        if (status != null && !status.isEmpty() && !status.equals("All")) {
            sql.append(" AND status = ?");
            args.add(status);
        }
        //filter by manager
        if (manager != null && !manager.trim().isEmpty()) {
            sql.append(" AND manager LIKE ?");
            args.add("%" + manager.trim() + "%");
        }

        //sort by id in descending order
        sql.append(" ORDER BY id DESC");

        Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0])); //rawQuery is

        if (cursor.moveToFirst()) {
            do {
                Project p = new Project(
                        cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7), cursor.getDouble(8),
                        cursor.getString(9), cursor.getString(10), cursor.getString(11),
                        cursor.getString(12),
                        cursor.getInt(13)
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