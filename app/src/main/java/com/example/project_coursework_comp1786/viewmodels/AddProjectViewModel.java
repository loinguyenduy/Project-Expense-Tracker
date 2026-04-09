package com.example.project_coursework_comp1786.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_coursework_comp1786.services.ProjectService;

import java.util.HashMap;
import java.util.Map;

public class AddProjectViewModel extends AndroidViewModel {
    private ProjectService projectService;

    public AddProjectViewModel(@NonNull Application application) {
        super(application);
        projectService = new ProjectService(application);
    }

    public Map<String, String> validateProject(String code, String name, String desc, String manager,
                                               String budgetStr, String startDate, String endDate,
                                               String status, String difficulty, long currentProjectId) {

        Map<String, String> errors = new HashMap<>();

        if (code.isEmpty()) errors.put("code", "Project Code is required");
        else if (projectService.isProjectCodeExists(code, currentProjectId)) errors.put("code", "Code already exists");

        if (name.isEmpty()) errors.put("name", "Project Name is required");
        if (desc.isEmpty()) errors.put("desc", "Description is required");
        if (manager.isEmpty()) errors.put("manager", "Manager is required");
        if (startDate.isEmpty()) errors.put("startDate", "Start Date is required");
        if (endDate.isEmpty()) errors.put("endDate", "End Date is required");
        if (status.isEmpty()) errors.put("status", "Status is required");
        if (difficulty.isEmpty()) errors.put("difficulty", "Difficulty is required");

        if (budgetStr.isEmpty()) {
            errors.put("budget", "Budget is required");
        } else {
            try {
                double budget = Double.parseDouble(budgetStr);
                if (budget <= 0) errors.put("budget", "Must be > 0");
            } catch (NumberFormatException e) {
                errors.put("budget", "Invalid format");
            }
        }

        if (!startDate.isEmpty() && !endDate.isEmpty() && startDate.compareTo(endDate) > 0) {
            errors.put("endDate", "End date must be after start date");
        }

        return errors;
    }
}