package com.example.project_coursework_comp1786.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.project_coursework_comp1786.models.Expense;
import com.example.project_coursework_comp1786.models.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseSyncService {

    private static final String FIREBASE_URL = "https://manage-expense-comp1786-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private Context context;
    private DatabaseHelper dbHelper;

    public FirebaseSyncService(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public void syncUnsyncedData() {
        if (!isNetworkAvailable()) {
            Toast.makeText(context, "No Internet Connection! Please check your Wi-Fi/4G.", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(context, "Synchronizing with Cloud...", Toast.LENGTH_SHORT).show();

        FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_URL);
        DatabaseReference projectsRef = database.getReference("projects");
        DatabaseReference expensesRef = database.getReference("expenses");
        DatabaseReference usersRef = database.getReference("users");
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Push data of unsynced data from projects to cloud
        Cursor pCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS + " WHERE isSynced = 0", null);
        if (pCursor.moveToFirst()) {
            do {
                long id = pCursor.getLong(0);
                Project p = new Project(
                        id, pCursor.getString(1), pCursor.getString(2),
                        pCursor.getString(3), pCursor.getString(4), pCursor.getString(5),
                        pCursor.getString(6), pCursor.getString(7), pCursor.getDouble(8),
                        pCursor.getString(9), pCursor.getString(10), pCursor.getString(11),
                        pCursor.getString(12), 1
                );

                projectsRef.child(String.valueOf(id)).setValue(p).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.execSQL("UPDATE " + DatabaseHelper.TABLE_PROJECTS + " SET isSynced = 1 WHERE id = " + id);
                    }
                });
            } while (pCursor.moveToNext());
        }
        pCursor.close();

        // Push data of unsynced data from expenses to cloud
        Cursor eCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_EXPENSES + " WHERE isSynced = 0", null);
        if (eCursor.moveToFirst()) {
            do {
                long id = eCursor.getLong(0);
                Expense e = new Expense(
                        id, eCursor.getLong(1), eCursor.getString(2), eCursor.getDouble(3),
                        eCursor.getString(4), eCursor.getString(5), eCursor.getString(6),
                        eCursor.getString(7), eCursor.getString(8), eCursor.getString(9),
                        eCursor.getString(10), 1
                );
                expensesRef.child(String.valueOf(id)).setValue(e).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.execSQL("UPDATE " + DatabaseHelper.TABLE_EXPENSES + " SET isSynced = 1 WHERE id = " + id);
                    }
                });
            } while (eCursor.moveToNext());
        }
        eCursor.close();

        // Delete data of synced data from projects in cloud
        Cursor pDelCursor = db.rawQuery("SELECT id FROM " + DatabaseHelper.TABLE_PROJECTS + " WHERE isSynced = -1", null);
        if (pDelCursor.moveToFirst()) {
            do {
                long id = pDelCursor.getLong(0);
                projectsRef.child(String.valueOf(id)).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.delete(DatabaseHelper.TABLE_PROJECTS, "id=?", new String[]{String.valueOf(id)});
                    }
                });
            } while (pDelCursor.moveToNext());
        }
        pDelCursor.close();

        // Delete data of synced data from expenses in cloud
        Cursor eDelCursor = db.rawQuery("SELECT id FROM " + DatabaseHelper.TABLE_EXPENSES + " WHERE isSynced = -1", null);
        if (eDelCursor.moveToFirst()) {
            do {
                long id = eDelCursor.getLong(0);
                expensesRef.child(String.valueOf(id)).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.delete(DatabaseHelper.TABLE_EXPENSES, "id=?", new String[]{String.valueOf(id)});
                    }
                });
            } while (eDelCursor.moveToNext());
        }
        eDelCursor.close();


        // Pull data from cloud and update local database of projects
        projectsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snap : task.getResult().getChildren()) {

                    // SỬA LỖI Ở ĐÂY: Dùng getLongSafe thay vì ép kiểu trực tiếp
                    long id = getLongSafe(snap, "id");
                    if (id == 0) continue;

                    Cursor checkCursor = db.rawQuery("SELECT isSynced FROM " + DatabaseHelper.TABLE_PROJECTS + " WHERE id = " + id, null);
                    boolean shouldSkip = false;
                    if (checkCursor.moveToFirst()) {
                        int localStatus = checkCursor.getInt(0);
                        if (localStatus == 0 || localStatus == -1) {
                            shouldSkip = true;
                        }
                    }
                    checkCursor.close();

                    if (shouldSkip) continue;

                    ContentValues values = new ContentValues();
                    values.put("id", id);
                    values.put("projectCode", getStringSafe(snap, "projectCode"));
                    values.put("name", getStringSafe(snap, "name"));
                    values.put("description", getStringSafe(snap, "description"));
                    values.put("startDate", getStringSafe(snap, "startDate"));
                    values.put("endDate", getStringSafe(snap, "endDate"));
                    values.put("manager", getStringSafe(snap, "manager"));
                    values.put("status", getStringSafe(snap, "status"));
                    values.put("budget", getDoubleSafe(snap, "budget"));
                    values.put("specialRequirements", getStringSafe(snap, "specialRequirements"));
                    values.put("clientInfo", getStringSafe(snap, "clientInfo"));
                    values.put("jobDifficulty", getStringSafe(snap, "jobDifficulty"));
                    values.put("assignedTo", getStringSafe(snap, "assignedTo"));
                    values.put("isSynced", 1);

                    db.insertWithOnConflict(DatabaseHelper.TABLE_PROJECTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
        });

        // Pull data from cloud and update local database of expenses
        expensesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snap : task.getResult().getChildren()) {

                    // SỬA LỖI Ở ĐÂY: Dùng getLongSafe cho cả id và projectId
                    long id = getLongSafe(snap, "id");
                    if (id == 0) continue;

                    Cursor checkCursor = db.rawQuery("SELECT isSynced FROM " + DatabaseHelper.TABLE_EXPENSES + " WHERE id = " + id, null);
                    boolean shouldSkip = false;
                    if (checkCursor.moveToFirst()) {
                        int localStatus = checkCursor.getInt(0);
                        if (localStatus == 0 || localStatus == -1) {
                            shouldSkip = true;
                        }
                    }
                    checkCursor.close();
                    if (shouldSkip) continue;

                    ContentValues values = new ContentValues();
                    values.put("id", id);
                    values.put("projectId", getLongSafe(snap, "projectId")); // SỬA LỖI MẢNG NÀY NỮA
                    values.put("date", getStringSafe(snap, "date"));
                    values.put("amount", getDoubleSafe(snap, "amount"));
                    values.put("currency", getStringSafe(snap, "currency"));
                    values.put("type", getStringSafe(snap, "type"));
                    values.put("paymentMethod", getStringSafe(snap, "paymentMethod"));
                    values.put("claimant", getStringSafe(snap, "claimant"));

                    String status = snap.hasChild("paymentStatus") ? getStringSafe(snap, "paymentStatus") : getStringSafe(snap, "status");
                    values.put("status", status);

                    values.put("description", getStringSafe(snap, "description"));
                    values.put("location", getStringSafe(snap, "location"));
                    values.put("isSynced", 1);

                    db.insertWithOnConflict(DatabaseHelper.TABLE_EXPENSES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
        });

        // Kéo danh sách Users từ Firebase về SQLite
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    String uid = snap.getKey();
                    if (uid == null) continue;

                    ContentValues values = new ContentValues();
                    values.put("uid", uid);
                    values.put("email", getStringSafe(snap, "email"));
                    values.put("fullName", getStringSafe(snap, "fullName"));
                    values.put("role", getStringSafe(snap, "role"));

                    boolean isActive = true;
                    if (snap.hasChild("isActive")) {
                        isActive = Boolean.TRUE.equals(snap.child("isActive").getValue(Boolean.class));
                    }
                    values.put("isActive", isActive ? 1 : 0);

                    db.insertWithOnConflict(DatabaseHelper.TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                Toast.makeText(context, "Sync Completed! Cloud & Local are up to date.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Sync Completed for Projects & Expenses.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- CÁC HÀM TIỆN ÍCH AN TOÀN ---

    private String getStringSafe(DataSnapshot snap, String key) {
        if (snap.hasChild(key) && snap.child(key).getValue() != null) {
            return String.valueOf(snap.child(key).getValue());
        }
        return "";
    }

    private double getDoubleSafe(DataSnapshot snap, String key) {
        if (snap.hasChild(key) && snap.child(key).getValue() != null) {
            try {
                return Double.parseDouble(String.valueOf(snap.child(key).getValue()));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    // THÊM MỚI: Hàm ép kiểu an toàn cho ID (Xử lý cả số Long của Android và chuỗi String của React Native)
    private long getLongSafe(DataSnapshot snap, String key) {
        if (snap.hasChild(key) && snap.child(key).getValue() != null) {
            Object value = snap.child(key).getValue();
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else if (value instanceof String) {
                try {
                    return Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    return 0L;
                }
            }
        }
        return 0L;
    }
}