package com.example.project_coursework_comp1786.views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.adapters.ProjectAdapter;
import com.example.project_coursework_comp1786.models.Project;
import com.example.project_coursework_comp1786.services.FirebaseSyncService;
import com.example.project_coursework_comp1786.services.ProjectService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvProjects;
    private TextView tvEmptyState, tvTotalBudget;
    private EditText edtSearch;
    private TextInputLayout searchInputLayout;
    private BottomNavigationView bottomNavigation;

    private ProjectAdapter projectAdapter;
    private ProjectService projectService;
    private List<Project> projectList;

    private String filterStart = "";
    private String filterEnd = "";
    private String filterStatus = "All";
    private String filterManager = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projectService = new ProjectService(this);
        projectList = new ArrayList<>();

        rvProjects = findViewById(R.id.rvProjects);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        edtSearch = findViewById(R.id.edtSearch);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        searchInputLayout = (TextInputLayout) edtSearch.getParent().getParent();

        rvProjects.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(projectList, project -> {
            Intent intent = new Intent(MainActivity.this, ProjectDetailActivity.class);
            intent.putExtra("PROJECT_DATA", project);
            startActivity(intent);
        });
        rvProjects.setAdapter(projectAdapter);

        findViewById(R.id.fabAddProject).setOnClickListener(v -> startActivity(new Intent(this, AddProjectActivity.class)));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applySearchAndFilter();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchInputLayout.setEndIconOnClickListener(v -> showAdvancedFilterDialog());

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_projects) {
                return true;
            } else if (id == R.id.nav_settings) {
                FirebaseSyncService syncService = new FirebaseSyncService(MainActivity.this);
                syncService.syncUnsyncedData();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        applySearchAndFilter();
    }

    private void applySearchAndFilter() {
        String query = edtSearch.getText().toString();
        projectList = projectService.searchAndFilterProjects(query, filterStart, filterEnd, filterStatus, filterManager);

        projectAdapter.setProjects(projectList);

        double total = 0;
        for (Project p : projectList) total += p.getBudget();
        tvTotalBudget.setText(String.format("$%,.2f", total));

        if (projectList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvProjects.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvProjects.setVisibility(View.VISIBLE);
        }
    }

    private void showAdvancedFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextInputEditText edtStart = dialogView.findViewById(R.id.edtFilterStart);
        TextInputEditText edtEnd = dialogView.findViewById(R.id.edtFilterEnd);
        AutoCompleteTextView actvStatus = dialogView.findViewById(R.id.actvFilterStatus);
        TextInputEditText edtManager = dialogView.findViewById(R.id.edtFilterManager);
        MaterialButton btnClear = dialogView.findViewById(R.id.btnClearFilter);
        MaterialButton btnApply = dialogView.findViewById(R.id.btnApplyFilter);

        edtStart.setText(filterStart);
        edtEnd.setText(filterEnd);
        actvStatus.setText(filterStatus, false);
        edtManager.setText(filterManager);

        String[] statuses = {"All", "Active", "On Hold", "Completed"};
        actvStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses));

        edtStart.setOnClickListener(v -> showDatePicker(edtStart));
        edtEnd.setOnClickListener(v -> showDatePicker(edtEnd));

        btnClear.setOnClickListener(v -> {
            filterStart = ""; filterEnd = ""; filterStatus = "All"; filterManager = "";
            applySearchAndFilter();
            dialog.dismiss();
        });

        btnApply.setOnClickListener(v -> {
            filterStart = edtStart.getText().toString().trim();
            filterEnd = edtEnd.getText().toString().trim();
            filterStatus = actvStatus.getText().toString().trim();
            filterManager = edtManager.getText().toString().trim();

            applySearchAndFilter();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            target.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}