package com.example.project_coursework_comp1786.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.adapters.ExpenseAdapter;
import com.example.project_coursework_comp1786.models.Expense;
import com.example.project_coursework_comp1786.models.Project;
import com.example.project_coursework_comp1786.services.ExpenseService;
import com.example.project_coursework_comp1786.services.ProjectService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailActivity extends AppCompatActivity {

    private Project currentProject;
    private ProjectService projectService;
    private ExpenseService expenseService;
    private ExpenseAdapter expenseAdapter;

    // Giao diện cũ
    private RecyclerView rvExpenses;
    private TextView tvEmptyExpense;

    // Giao diện Dashboard mới
    private TextView tvRemainingBudget, tvUtilizedPercent, tvTotalSpent, tvDashboardBudget;
    private LinearProgressIndicator progressBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        initViews();

        projectService = new ProjectService(this);
        expenseService = new ExpenseService(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        currentProject = (Project) getIntent().getSerializableExtra("PROJECT_DATA");

        if (currentProject != null) {
            displayProjectData();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(currentProject.getProjectCode());
            }
        } else {
            Toast.makeText(this, "Error loading project", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();

        MaterialButton btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            intent.putExtra("PROJECT_ID", currentProject.getId());
            startActivity(intent);
        });
    }

    private void initViews() {
        rvExpenses = findViewById(R.id.rvExpenses);
        tvEmptyExpense = findViewById(R.id.tvEmptyExpense);

        // Ánh xạ Dashboard
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);
        tvUtilizedPercent = findViewById(R.id.tvUtilizedPercent);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        tvDashboardBudget = findViewById(R.id.tvDashboardBudget);
        progressBudget = findViewById(R.id.progressBudget);
    }

    private void setupRecyclerView() {
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));

        expenseAdapter = new ExpenseAdapter(new ArrayList<>(), expense -> {
            Intent intent = new Intent(ProjectDetailActivity.this, AddExpenseActivity.class);
            intent.putExtra("PROJECT_ID", currentProject.getId());
            intent.putExtra("EXPENSE_DATA_TO_EDIT", expense);
            startActivity(intent);
        });

        rvExpenses.setAdapter(expenseAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentProject != null) {
            loadExpenses();
        }
    }

    private void loadExpenses() {
        List<Expense> expenses = expenseService.getExpensesByProjectId(currentProject.getId());

        expenseAdapter.setExpenses(expenses);
        expenseAdapter.notifyDataSetChanged();

        double totalSpent = 0;
        for (Expense e : expenses) {
            String status = e.getStatus();
            if (status != null && (status.equalsIgnoreCase("Paid") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Reimbursed"))) {
                totalSpent += e.getAmount();
            }
        }

        // Gọi hàm cập nhật UI Dashboard
        updateDashboard(totalSpent);

        // BƯỚC 3: HIỂN THỊ DANH SÁCH HOẶC TEXT TRỐNG (Logic cũ của bạn)
        if (expenses == null || expenses.isEmpty()) {
            tvEmptyExpense.setVisibility(View.VISIBLE);
            rvExpenses.setVisibility(View.GONE);
        } else {
            tvEmptyExpense.setVisibility(View.GONE);
            rvExpenses.setVisibility(View.VISIBLE);
        }
    }

    // --- HÀM XỬ LÝ LOGIC HIỂN THỊ DASHBOARD ---
    private void updateDashboard(double totalSpent) {
        double budget = currentProject.getBudget();
        double remaining = budget - totalSpent;

        int utilization = 0;
        if (budget > 0) {
            utilization = (int) ((totalSpent / budget) * 100);
        }

        // Cập nhật text
        tvRemainingBudget.setText(String.format("$%,.2f", remaining));
        tvTotalSpent.setText(String.format("$%,.2f", totalSpent));
        tvDashboardBudget.setText(String.format("$%,.2f", budget));
        tvUtilizedPercent.setText(utilization + "%");

        // Cập nhật thanh ProgressBar (Tối đa 100 để không bị vỡ giao diện)
        progressBudget.setProgressCompat(Math.min(utilization, 100), true);

        // Logic đổi màu cảnh báo
        int colorGreen = android.graphics.Color.parseColor("#10B981");
        int colorOrange = android.graphics.Color.parseColor("#F59E0B"); // Cảnh báo 80%
        int colorRed = android.graphics.Color.parseColor("#EF4444");   // Báo động 100%
        int colorPrimary = android.graphics.Color.parseColor("#0284C7");

        if (utilization >= 100) {
            progressBudget.setIndicatorColor(colorRed);
            tvUtilizedPercent.setTextColor(colorRed);
            if (remaining < 0) {
                tvRemainingBudget.setTextColor(colorRed);
            } else {
                tvRemainingBudget.setTextColor(colorPrimary);
            }
        } else if (utilization >= 80) {
            progressBudget.setIndicatorColor(colorOrange);
            tvUtilizedPercent.setTextColor(colorOrange);
            tvRemainingBudget.setTextColor(colorPrimary);
        } else {
            progressBudget.setIndicatorColor(colorGreen);
            tvUtilizedPercent.setTextColor(colorGreen);
            tvRemainingBudget.setTextColor(colorPrimary);
        }
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
            Intent intent = new Intent(this, AddProjectActivity.class);
            intent.putExtra("PROJECT_DATA_TO_EDIT", currentProject);
            startActivity(intent);
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