package com.example.project_coursework_comp1786.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project_coursework_comp1786.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private DatabaseHelper dbHelper;

    public UserService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Chỉ lấy những User có role là "staff"
    public List<User> getAllStaffLocally() {
        List<User> staffList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE role = 'staff'", null);

        if (cursor.moveToFirst()) {
            do {
                String uid = cursor.getString(cursor.getColumnIndexOrThrow("uid"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
                String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
                boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive")) == 1;

                staffList.add(new User(uid, email, fullName, role, isActive));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return staffList;
    }

    // Cập nhật trạng thái active/inactive dưới local SQLite
    public void updateStaffStatusLocally(String uid, boolean isActive) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isActive", isActive ? 1 : 0);
        db.update(DatabaseHelper.TABLE_USERS, values, "uid=?", new String[]{uid});
    }
}