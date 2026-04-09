package com.example.project_coursework_comp1786.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_coursework_comp1786.services.ProjectService;

public class AddProjectViewModel extends AndroidViewModel {
    private ProjectService projectService;

    public AddProjectViewModel(@NonNull Application application) {
        super(application);
        projectService = new ProjectService(application);
    }

    public String validateProject(String code, String name, String desc, String manager,
                                  String budgetStr, String startDate, String endDate,
                                  String status, String difficulty) {

        if (code.isEmpty()) return "Project Code is required.";
        if (name.isEmpty()) return "Project Name is required.";
        if (desc.isEmpty()) return "Project Description is required.";
        if (manager.isEmpty()) return "Manager is required.";
        if (budgetStr.isEmpty()) return "Budget is required.";
        if (startDate.isEmpty()) return "Start Date is required.";
        if (endDate.isEmpty()) return "End Date is required.";
        if (status.isEmpty()) return "Status is required.";
        if (difficulty.isEmpty()) return "Difficulty is required.";

        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
            if (budget <= 0) return "Budget must be greater than 0.";
        } catch (NumberFormatException e) {
            return "Invalid budget format.";
        }

        if (startDate.compareTo(endDate) > 0) {
            return "End date cannot be before Start date.";
        }

        if (projectService.isProjectCodeExists(code)) {
            return "This Project Code already exists. Please use a unique code.";
        }

        return "VALID";
    }
}