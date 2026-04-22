package com.example.project_coursework_comp1786.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.models.Project;
import com.example.project_coursework_comp1786.models.User;
import com.example.project_coursework_comp1786.services.ProjectService;
import com.example.project_coursework_comp1786.services.UserService;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ConfirmProjectActivity extends AppCompatActivity {

    private Project projectData;
    private ProjectService projectService;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_project);

        projectService = new ProjectService(this);
        userService = new UserService(this);

        projectData = (Project) getIntent().getSerializableExtra("PROJECT_DATA");

        if (projectData != null) {
            displayData();
        }

        MaterialButton btnEdit = findViewById(R.id.btnEdit);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);

        btnEdit.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            boolean success = false;

            if (projectData.getId() > 0) {
                success = projectService.updateProject(projectData);
                if (success) Toast.makeText(this, "Project Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
            else {
                long newId = projectService.insertProject(projectData);
                success = (newId != -1);
                if (success) Toast.makeText(this, "Project Saved Successfully!", Toast.LENGTH_SHORT).show();
            }

            if (success) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData() {
        TextView tvCode = findViewById(R.id.tvCode);
        TextView tvAssignedStaff = findViewById(R.id.tvAssignedStaff);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvDesc = findViewById(R.id.tvDesc);
        TextView tvManager = findViewById(R.id.tvManager);
        TextView tvBudget = findViewById(R.id.tvBudget);
        TextView tvTimeline = findViewById(R.id.tvTimeline);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvDifficulty = findViewById(R.id.tvDifficulty);
        TextView tvSpecialReq = findViewById(R.id.tvSpecialReq);
        TextView tvClient = findViewById(R.id.tvClient);

        tvCode.setText("Code: " + projectData.getProjectCode());
        tvName.setText("Name: " + projectData.getName());
        tvDesc.setText("Desc: " + projectData.getDescription());
        tvManager.setText("Manager: " + projectData.getManager());
        tvBudget.setText(String.format("Budget: $%.2f", projectData.getBudget()));
        tvTimeline.setText("Timeline: " + projectData.getStartDate() + " to " + projectData.getEndDate());
        tvStatus.setText("Status: " + projectData.getStatus());
        tvDifficulty.setText("Difficulty: " + projectData.getJobDifficulty());

        tvSpecialReq.setText("Requirements: " + (projectData.getSpecialRequirements().isEmpty() ? "N/A" : projectData.getSpecialRequirements()));
        tvClient.setText("Client/Dept: " + (projectData.getClientInfo().isEmpty() ? "N/A" : projectData.getClientInfo()));

        String staffUid = projectData.getAssignedTo();
        String staffDisplayName = "No staff assigned";

        if (staffUid != null && !staffUid.equals("unassigned")) {
            List<User> allStaff = userService.getAllStaffLocally();
            for (User u : allStaff) {
                if (u.getUid().equals(staffUid)) {
                    staffDisplayName = u.getFullName();
                    break;
                }
            }
        }
        tvAssignedStaff.setText("Assigned Staff: " + staffDisplayName);
    }
}