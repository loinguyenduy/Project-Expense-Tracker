package com.example.project_coursework_comp1786.views;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.models.Expense;
import com.example.project_coursework_comp1786.services.ExpenseService;
import com.example.project_coursework_comp1786.viewmodels.AddExpenseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    private long currentProjectId = -1;
    private Expense expenseToEdit = null;
    private ExpenseService expenseService;
    private AddExpenseViewModel viewModel;

    private TextInputLayout layoutDate, layoutAmount, layoutCurrency, layoutType, layoutMethod, layoutClaimant, layoutStatus;
    private TextInputEditText edtDate, edtAmount, edtClaimant, edtDesc, edtLocation;
    private AutoCompleteTextView actvCurrency, actvType, actvMethod, actvStatus;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddExpense);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currentProjectId = getIntent().getLongExtra("PROJECT_ID", -1);
        expenseToEdit = (Expense) getIntent().getSerializableExtra("EXPENSE_DATA_TO_EDIT");

        if (currentProjectId == -1) {
            Toast.makeText(this, "Error: Unknown Project", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        expenseService = new ExpenseService(this);
        viewModel = new ViewModelProvider(this).get(AddExpenseViewModel.class);

        initViews();
        setupDropdowns();

        if (expenseToEdit != null) {
            populateDataForEdit();
        }
        edtDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveExpenseData());
    }

    private void initViews() {
        layoutDate = findViewById(R.id.layoutDate);
        layoutAmount = findViewById(R.id.layoutAmount);
        layoutCurrency = findViewById(R.id.layoutCurrency);
        layoutType = findViewById(R.id.layoutType);
        layoutMethod = findViewById(R.id.layoutMethod);
        layoutClaimant = findViewById(R.id.layoutClaimant);
        layoutStatus = findViewById(R.id.layoutStatus);

        edtDate = findViewById(R.id.edtDate);
        edtAmount = findViewById(R.id.edtAmount);
        actvCurrency = findViewById(R.id.actvCurrency);
        actvType = findViewById(R.id.actvType);
        actvMethod = findViewById(R.id.actvMethod);
        edtClaimant = findViewById(R.id.edtClaimant);
        actvStatus = findViewById(R.id.actvStatus);
        edtDesc = findViewById(R.id.edtDesc);
        edtLocation = findViewById(R.id.edtLocation);
        btnSave = findViewById(R.id.btnSaveExpense);
    }

    private void setupDropdowns() {
        String[] currencies = {"USD", "EUR", "GBP", "VND", "CAD", "AUD", "JPY"};
        actvCurrency.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, currencies));

        String[] types = {"Travel", "Equipment", "Materials", "Services", "Software/Licenses", "Labour costs", "Utilities", "Miscellaneous"};
        actvType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types));

        String[] methods = {"Cash", "Credit Card", "Bank Transfer", "Cheque"};
        actvMethod.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, methods));

        String[] statuses = {"Paid", "Pending", "Reimbursed"};
        actvStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses));
    }

    private void populateDataForEdit() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Expense");
        btnSave.setText("Update Expense");

        edtDate.setText(expenseToEdit.getDate());
        edtAmount.setText(String.valueOf(expenseToEdit.getAmount()));

        actvCurrency.setText(expenseToEdit.getCurrency(), false);
        actvType.setText(expenseToEdit.getType(), false);
        actvMethod.setText(expenseToEdit.getPaymentMethod(), false);
        actvStatus.setText(expenseToEdit.getStatus(), false);

        edtClaimant.setText(expenseToEdit.getClaimant());
        edtDesc.setText(expenseToEdit.getDescription());
        edtLocation.setText(expenseToEdit.getLocation());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            edtDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveExpenseData() {
        String date = edtDate.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String currency = actvCurrency.getText().toString().trim();
        String type = actvType.getText().toString().trim();
        String method = actvMethod.getText().toString().trim();
        String claimant = edtClaimant.getText().toString().trim();
        String status = actvStatus.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();
        String loc = edtLocation.getText().toString().trim();

        Map<String, String> errors = viewModel.validateExpense(date, amountStr, currency, type, method, claimant, status);

        layoutDate.setError(errors.get("date"));
        layoutAmount.setError(errors.get("amount"));
        layoutCurrency.setError(errors.get("currency"));
        layoutType.setError(errors.get("type"));
        layoutMethod.setError(errors.get("method"));
        layoutClaimant.setError(errors.get("claimant"));
        layoutStatus.setError(errors.get("status"));

        if (errors.isEmpty()) {
            double amount = Double.parseDouble(amountStr);
            boolean isSuccess;

            if (expenseToEdit != null) {
                Expense updatedExpense = new Expense(expenseToEdit.getId(), currentProjectId, date, amount, currency, type, method, claimant, status, desc, loc, 0);
                isSuccess = expenseService.updateExpense(updatedExpense);
            } else {
                Expense newExpense = new Expense(currentProjectId, date, amount, currency, type, method, claimant, status, desc, loc, 0);
                isSuccess = (expenseService.insertExpense(newExpense) != -1);
            }

            if (isSuccess) {
                Toast.makeText(this, "Expense Saved Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Database Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (expenseToEdit != null) {
            getMenuInflater().inflate(R.menu.menu_expense_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_delete_expense) {
            showDeleteConfirmDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to permanently delete this expense record?")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    if (expenseService.deleteExpense(expenseToEdit.getId())) {
                        Toast.makeText(this, "Expense Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }
}