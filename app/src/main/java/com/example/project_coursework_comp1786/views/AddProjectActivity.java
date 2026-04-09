package com.example.project_coursework_comp1786.views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.models.Project;
import com.example.project_coursework_comp1786.viewmodels.AddProjectViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddProjectActivity extends AppCompatActivity {

    private TextInputEditText edtCode, edtName, edtDesc, edtManager, edtBudget, edtStartDate, edtEndDate, edtSpecialReq, edtClient;
    private AutoCompleteTextView actvStatus, actvDifficulty;
    private MaterialButton btnReview;

    private AddProjectViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        viewModel = new ViewModelProvider(this).get(AddProjectViewModel.class);

        initViews();
        setupDropdowns();
        setupDatePickers();

        btnReview.setOnClickListener(v -> reviewDataBeforeSave());
    }

    private void initViews() {
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
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        actvStatus.setAdapter(statusAdapter);

        String[] difficulties = {"Easy", "Medium", "Hard", "Critical"};
        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, difficulties);
        actvDifficulty.setAdapter(diffAdapter);
    }

    private void setupDatePickers() {
        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));
    }

    private void showDatePicker(TextInputEditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            targetEditText.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

        String specialReq = edtSpecialReq.getText().toString().trim();
        String client = edtClient.getText().toString().trim();

        String result = viewModel.validateProject(code, name, desc, manager, budgetStr, startDate, endDate, status, difficulty);

        if (result.equals("VALID")) {
            double budget = Double.parseDouble(budgetStr);
            Project projectToReview = new Project(code, name, desc, startDate, endDate, manager, status, budget, specialReq, client, difficulty, 0);

            Intent intent = new Intent(this, ConfirmProjectActivity.class);
            intent.putExtra("PROJECT_DATA", projectToReview);
            startActivity(intent);
        } else {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }
}