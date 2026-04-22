package com.example.project_coursework_comp1786.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.adapters.UserAdapter;
import com.example.project_coursework_comp1786.viewmodels.ManageStaffViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ManageStaffActivity extends AppCompatActivity {

    private RecyclerView rvStaff;
    private UserAdapter adapter;
    private ManageStaffViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_staff);

        MaterialToolbar toolbar = findViewById(R.id.toolbarStaff);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvStaff = findViewById(R.id.rvStaff);
        rvStaff.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(ManageStaffViewModel.class);

        adapter = new UserAdapter(new ArrayList<>(), (user, isChecked) -> {
            viewModel.toggleStaffStatus(user, isChecked, new ManageStaffViewModel.StatusToggleListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ManageStaffActivity.this, "Status updated for " + user.getFullName(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ManageStaffActivity.this, error, Toast.LENGTH_SHORT).show();
                    // Load lại danh sách để trả Switch về trạng thái cũ nếu lỗi mạng
                    loadData();
                }
            });
        });

        rvStaff.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        viewModel.loadLocalStaff(staffList -> {
            adapter.setUsers(staffList);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}