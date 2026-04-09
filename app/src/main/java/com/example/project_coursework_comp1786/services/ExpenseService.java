package com.example.project_coursework_comp1786.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project_coursework_comp1786.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseService {
    private DatabaseHelper dbHelper;

    public ExpenseService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<Expense> getExpensesByProjectId(long projectId) {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_EXPENSES + " WHERE projectId = ? ORDER BY date DESC", new String[]{String.valueOf(projectId)});

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String method = cursor.getString(cursor.getColumnIndexOrThrow("paymentMethod"));
                String claimant = cursor.getString(cursor.getColumnIndexOrThrow("claimant"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                int isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced"));

                Expense expense = new Expense(id, projectId, date, amount, currency, type, method, claimant, status, desc, location, isSynced);
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expenseList;
    }

    public long insertExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("projectId", expense.getProjectId());
        values.put("date", expense.getDate());
        values.put("amount", expense.getAmount());
        values.put("currency", expense.getCurrency());
        values.put("type", expense.getType());
        values.put("paymentMethod", expense.getPaymentMethod());
        values.put("claimant", expense.getClaimant());
        values.put("status", expense.getStatus());
        values.put("description", expense.getDescription());
        values.put("location", expense.getLocation());
        values.put("isSynced", expense.getIsSynced());

        return db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
    }

    public boolean updateExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("projectId", expense.getProjectId());
        values.put("date", expense.getDate());
        values.put("amount", expense.getAmount());
        values.put("currency", expense.getCurrency());
        values.put("type", expense.getType());
        values.put("paymentMethod", expense.getPaymentMethod());
        values.put("claimant", expense.getClaimant());
        values.put("status", expense.getStatus());
        values.put("description", expense.getDescription());
        values.put("location", expense.getLocation());
        values.put("isSynced", 0);

        int rows = db.update(DatabaseHelper.TABLE_EXPENSES, values, "id=?", new String[]{String.valueOf(expense.getId())});
        return rows > 0;
    }

    public boolean deleteExpense(long expenseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_EXPENSES, "id=?", new String[]{String.valueOf(expenseId)});
        return rows > 0;
    }
}