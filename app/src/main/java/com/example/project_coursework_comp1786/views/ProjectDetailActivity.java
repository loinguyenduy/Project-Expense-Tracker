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
import com.example.project_coursework_comp1786.models.User;
import com.example.project_coursework_comp1786.services.ExpenseService;
import com.example.project_coursework_comp1786.services.ProjectService;
import com.example.project_coursework_comp1786.services.UserService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailActivity extends AppCompatActivity {

    private Project currentProject;
    private ProjectService projectService;
    private ExpenseService expenseService;
    private UserService userService;
    private ExpenseAdapter expenseAdapter;

    private RecyclerView rvExpenses;
    private TextView tvEmptyExpense;

    private TextView tvRemainingBudget, tvUtilizedPercent, tvTotalSpent, tvDashboardBudget;
    private LinearProgressIndicator progressBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        initViews();

        projectService = new ProjectService(this);
        expenseService = new ExpenseService(this);
        userService = new UserService(this);

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

        if (currentProject.getStatus().equalsIgnoreCase("Completed")) {
            btnAddExpense.setVisibility(View.GONE);
        } else {
            btnAddExpense.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddExpenseActivity.class);
                intent.putExtra("PROJECT_ID", currentProject.getId());
                intent.putExtra("PROJECT_STATUS", currentProject.getStatus());
                startActivity(intent);
            });
        }
    }

    private void initViews() {
        rvExpenses = findViewById(R.id.rvExpenses);
        tvEmptyExpense = findViewById(R.id.tvEmptyExpense);
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
            intent.putExtra("PROJECT_STATUS", currentProject.getStatus());
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

        double totalSpent = 0;
        for (Expense e : expenses) {
            String status = e.getStatus();
            if (status != null && (status.equalsIgnoreCase("Paid") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Reimbursed"))) {
                totalSpent += e.getAmount();
            }
        }
        updateDashboard(totalSpent);

        if (expenses == null || expenses.isEmpty()) {
            tvEmptyExpense.setVisibility(View.VISIBLE);
            rvExpenses.setVisibility(View.GONE);
        } else {
            tvEmptyExpense.setVisibility(View.GONE);
            rvExpenses.setVisibility(View.VISIBLE);
        }
    }

    private void updateDashboard(double totalSpent) {
        double budget = currentProject.getBudget();
        double remaining = budget - totalSpent;
        int utilization = budget > 0 ? (int) ((totalSpent / budget) * 100) : 0;

        tvRemainingBudget.setText(String.format("$%,.2f", remaining));
        tvTotalSpent.setText(String.format("$%,.2f", totalSpent));
        tvDashboardBudget.setText(String.format("$%,.2f", budget));
        tvUtilizedPercent.setText(utilization + "%");

        progressBudget.setProgressCompat(Math.min(utilization, 100), true);

        int colorRed = android.graphics.Color.parseColor("#EF4444");
        int colorPrimary = android.graphics.Color.parseColor("#0284C7");

        if (utilization >= 100) {
            progressBudget.setIndicatorColor(colorRed);
            tvUtilizedPercent.setTextColor(colorRed);
            if (remaining < 0) tvRemainingBudget.setTextColor(colorRed);
        } else {
            progressBudget.setIndicatorColor(android.graphics.Color.parseColor("#10B981"));
            tvUtilizedPercent.setTextColor(android.graphics.Color.parseColor("#10B981"));
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
        TextView tvAssignedStaff = findViewById(R.id.tvDetAssignedStaff);

        tvName.setText(currentProject.getName());
        tvStatus.setText(currentProject.getStatus());
        tvCode.setText("Code: " + currentProject.getProjectCode());
        tvManager.setText("Owner: " + currentProject.getManager());
        tvDesc.setText(currentProject.getDescription());
        tvBudget.setText("Budget: " + String.format("$%,.2f", currentProject.getBudget()));
        tvDates.setText("Period: " + currentProject.getStartDate() + " to " + currentProject.getEndDate());
        tvDifficulty.setText("Difficulty: " + currentProject.getJobDifficulty());
        tvClient.setText("Client: " + (currentProject.getClientInfo().isEmpty() ? "N/A" : currentProject.getClientInfo()));
        tvSpecial.setText("Notes: " + (currentProject.getSpecialRequirements().isEmpty() ? "N/A" : currentProject.getSpecialRequirements()));

        String staffUid = currentProject.getAssignedTo();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_detail, menu);
        return true;
    }

    // UPDATE: Bổ sung lại hàm onPrepareOptionsMenu để ẩn menu khi dự án đã Completed
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentProject != null && currentProject.getStatus().equalsIgnoreCase("Completed")) {
            MenuItem itemEdit = menu.findItem(R.id.action_edit);
            MenuItem itemDelete = menu.findItem(R.id.action_delete);

            if (itemEdit != null) itemEdit.setVisible(false);
            if (itemDelete != null) itemDelete.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish(); return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmDialog(); return true;
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddProjectActivity.class);
            intent.putExtra("PROJECT_DATA_TO_EDIT", currentProject);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmDialog() {
        if (currentProject.getStatus().equalsIgnoreCase("Completed")) {
            Toast.makeText(this, "Cannot delete completed projects!", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Delete '" + currentProject.getName() + "'?")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    if (projectService.deleteProject(currentProject.getId())) {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", null).show();
    }
}