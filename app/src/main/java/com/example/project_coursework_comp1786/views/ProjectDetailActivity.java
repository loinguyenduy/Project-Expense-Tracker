package com.example.project_coursework_comp1786.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.models.Project;
import com.example.project_coursework_comp1786.services.ProjectService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ProjectDetailActivity extends AppCompatActivity {

    private Project currentProject;
    private ProjectService projectService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        projectService = new ProjectService(this);

        currentProject = (Project) getIntent().getSerializableExtra("PROJECT_DATA");

        if (currentProject != null) {
            displayProjectData();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(currentProject.getProjectCode());
            }
        } else {
            Toast.makeText(this, "Error loading project", Toast.LENGTH_SHORT).show();
            finish();
        }

        MaterialButton btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            Toast.makeText(this, "Add Expense feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayProjectData() {
        TextView tvName = findViewById(R.id.tvDetName);
        TextView tvStatus = findViewById(R.id.tvDetStatus);
        TextView tvCode = findViewById(R.id.tvDetCode);
        TextView tvManager = findViewById(R.id.tvDetManager);
        TextView tvDesc = findViewById(R.id.tvDetDesc);
        TextView tvBudget = findViewById(R.id.tvDetBudget);
        TextView tvDates = findViewById(R.id.tvDetDates);
        TextView tvDifficulty = findViewById(R.id.tvDetDifficulty);
        TextView tvClient = findViewById(R.id.tvDetClient);
        TextView tvSpecial = findViewById(R.id.tvDetSpecial);

        tvName.setText(currentProject.getName());
        tvStatus.setText(currentProject.getStatus());
        tvCode.setText("Code: " + currentProject.getProjectCode());
        tvManager.setText("Manager: " + currentProject.getManager());
        tvDesc.setText("Description: " + currentProject.getDescription());
        tvBudget.setText(String.format("Budget: $%,.2f", currentProject.getBudget()));
        tvDates.setText("Timeline: " + currentProject.getStartDate() + " to " + currentProject.getEndDate());
        tvDifficulty.setText("Difficulty: " + currentProject.getJobDifficulty());
        tvClient.setText("Client: " + (currentProject.getClientInfo().isEmpty() ? "N/A" : currentProject.getClientInfo()));
        tvSpecial.setText("Special Req: " + (currentProject.getSpecialRequirements().isEmpty() ? "N/A" : currentProject.getSpecialRequirements()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        else if (id == R.id.action_delete) {
            showDeleteConfirmDialog();
            return true;
        }
        else if (id == R.id.action_edit) {
            Toast.makeText(this, "Edit function coming next!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to permanently delete '" + currentProject.getName() + "'?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDeleted = projectService.deleteProject(currentProject.getId());
                        if (isDeleted) {
                            Toast.makeText(ProjectDetailActivity.this, "Project Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ProjectDetailActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }
}