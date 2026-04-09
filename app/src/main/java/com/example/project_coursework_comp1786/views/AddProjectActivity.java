package com.example.project_coursework_comp1786.views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.models.Project;
import com.example.project_coursework_comp1786.viewmodels.AddProjectViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Map;

public class AddProjectActivity extends AppCompatActivity {

    private TextInputLayout layoutCode, layoutName, layoutDesc, layoutManager, layoutBudget, layoutStartDate, layoutEndDate, layoutStatus, layoutDifficulty;
    private TextInputEditText edtCode, edtName, edtDesc, edtManager, edtBudget, edtStartDate, edtEndDate, edtSpecialReq, edtClient;
    private AutoCompleteTextView actvStatus, actvDifficulty;
    private MaterialButton btnReview;

    private AddProjectViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAdd);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(AddProjectViewModel.class);

        initViews();
        setupDropdowns();
        setupDatePickers();

        btnReview.setOnClickListener(v -> reviewDataBeforeSave());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        layoutCode = findViewById(R.id.layoutCode);
        layoutName = findViewById(R.id.layoutName);
        layoutDesc = findViewById(R.id.layoutDesc);
        layoutManager = findViewById(R.id.layoutManager);
        layoutBudget = findViewById(R.id.layoutBudget);
        layoutStartDate = findViewById(R.id.layoutStartDate);
        layoutEndDate = findViewById(R.id.layoutEndDate);
        layoutStatus = findViewById(R.id.layoutStatus);
        layoutDifficulty = findViewById(R.id.layoutDifficulty);

        edtCode = findViewById(R.id.edtCode);
        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDesc);
        edtManager = findViewById(R.id.edtManager);
        edtBudget = findViewById(R.id.edtBudget);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        actvStatus = findViewById(R.id.actvStatus);
        actvDifficulty = findViewById(R.id.actvDifficulty);
        edtSpecialReq = findViewById(R.id.edtSpecialReq);
        edtClient = findViewById(R.id.edtClient);
        btnReview = findViewById(R.id.btnReview);
    }

    private void setupDropdowns() {
        String[] statuses = {"Active", "On Hold", "Completed"};
        actvStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses));

        String[] difficulties = {"Easy", "Medium", "Hard", "Critical"};
        actvDifficulty.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, difficulties));
    }

    private void setupDatePickers() {
        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            target.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void reviewDataBeforeSave() {
        String code = edtCode.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();
        String manager = edtManager.getText().toString().trim();
        String budgetStr = edtBudget.getText().toString().trim();
        String startDate = edtStartDate.getText().toString().trim();
        String endDate = edtEndDate.getText().toString().trim();
        String status = actvStatus.getText().toString().trim();
        String difficulty = actvDifficulty.getText().toString().trim();

        Map<String, String> errors = viewModel.validateProject(code, name, desc, manager, budgetStr, startDate, endDate, status, difficulty);

        layoutCode.setError(errors.get("code"));
        layoutName.setError(errors.get("name"));
        layoutDesc.setError(errors.get("desc"));
        layoutManager.setError(errors.get("manager"));
        layoutBudget.setError(errors.get("budget"));
        layoutStartDate.setError(errors.get("startDate"));
        layoutEndDate.setError(errors.get("endDate"));
        layoutStatus.setError(errors.get("status"));
        layoutDifficulty.setError(errors.get("difficulty"));

        if (errors.isEmpty()) {
            double budget = Double.parseDouble(budgetStr);
            String specialReq = edtSpecialReq.getText().toString().trim();
            String client = edtClient.getText().toString().trim();

            Project projectToReview = new Project(code, name, desc, startDate, endDate, manager, status, budget, specialReq, client, difficulty, 0);

            Intent intent = new Intent(this, ConfirmProjectActivity.class);
            intent.putExtra("PROJECT_DATA", projectToReview);
            startActivity(intent);
        }
    }
}