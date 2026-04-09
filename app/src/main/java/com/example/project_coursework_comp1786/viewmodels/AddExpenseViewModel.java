package com.example.project_coursework_comp1786.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.HashMap;
import java.util.Map;

public class AddExpenseViewModel extends AndroidViewModel {

    public AddExpenseViewModel(@NonNull Application application) {
        super(application);
    }

    public Map<String, String> validateExpense(String date, String amountStr, String currency,
                                               String type, String method, String claimant, String status) {
        Map<String, String> errors = new HashMap<>();

        if (date.isEmpty()) errors.put("date", "Date is required");
        if (currency.isEmpty()) errors.put("currency", "Currency is required");
        if (type.isEmpty()) errors.put("type", "Expense Type is required");
        if (method.isEmpty()) errors.put("method", "Payment Method is required");
        if (claimant.isEmpty()) errors.put("claimant", "Claimant is required");
        if (status.isEmpty()) errors.put("status", "Status is required");

        if (amountStr.isEmpty()) {
            errors.put("amount", "Amount is required");
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) errors.put("amount", "Must be > 0");
            } catch (NumberFormatException e) {
                errors.put("amount", "Invalid format");
            }
        }
        return errors;
    }
}