package com.example.project_coursework_comp1786.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.project_coursework_comp1786.models.Expense;
import com.example.project_coursework_comp1786.models.Project;
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

        Toast.makeText(context, "Starting Cloud Sync...", Toast.LENGTH_SHORT).show();

        FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_URL);
        DatabaseReference projectsRef = database.getReference("projects");
        DatabaseReference expensesRef = database.getReference("expenses");
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int[] syncCounts = new int[]{0, 0};

        Cursor pCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROJECTS + " WHERE isSynced = 0", null);
        if (pCursor.moveToFirst()) {
            do {
                long id = pCursor.getLong(0);
                Project p = new Project(
                        id, pCursor.getString(1), pCursor.getString(2),
                        pCursor.getString(3), pCursor.getString(4), pCursor.getString(5),
                        pCursor.getString(6), pCursor.getString(7), pCursor.getDouble(8),
                        pCursor.getString(9), pCursor.getString(10), pCursor.getString(11),
                        1
                );

                projectsRef.child(String.valueOf(id)).setValue(p).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.execSQL("UPDATE " + DatabaseHelper.TABLE_PROJECTS + " SET isSynced = 1 WHERE id = " + id);
                    }
                });
                syncCounts[0]++;
            } while (pCursor.moveToNext());
        }
        pCursor.close();

        Cursor eCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_EXPENSES + " WHERE isSynced = 0", null);
        if (eCursor.moveToFirst()) {
            do {
                long id = eCursor.getLong(0);
                Expense e = new Expense(
                        id, eCursor.getLong(1), eCursor.getString(2), eCursor.getDouble(3),
                        eCursor.getString(4), eCursor.getString(5), eCursor.getString(6),
                        eCursor.getString(7), eCursor.getString(8), eCursor.getString(9),
                        eCursor.getString(10),
                        1
                );

                expensesRef.child(String.valueOf(id)).setValue(e).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.execSQL("UPDATE " + DatabaseHelper.TABLE_EXPENSES + " SET isSynced = 1 WHERE id = " + id);
                    }
                });
                syncCounts[1]++;
            } while (eCursor.moveToNext());
        }
        eCursor.close();

        if (syncCounts[0] == 0 && syncCounts[1] == 0) {
            Toast.makeText(context, "All data is already up to date on the Cloud!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Sync triggered for " + syncCounts[0] + " projects & " + syncCounts[1] + " expenses.", Toast.LENGTH_LONG).show();
        }
    }
}