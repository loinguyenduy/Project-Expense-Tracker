package com.example.project_coursework_comp1786.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.adapters.ProjectAdapter;
import com.example.project_coursework_comp1786.models.Project;
import com.example.project_coursework_comp1786.services.ProjectService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvProjects;
    private TextView tvEmptyState, tvTotalBudget;
    private EditText edtSearch;
    private ProjectAdapter projectAdapter;
    private ProjectService projectService;
    private List<Project> projectList;

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
                loadProjects(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects(edtSearch.getText().toString());
    }

    private void loadProjects(String query) {
        projectList = projectService.searchProjects(query);
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
}