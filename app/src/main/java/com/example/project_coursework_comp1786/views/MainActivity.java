package com.example.project_coursework_comp1786.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private FloatingActionButton fabAddProject;
    private BottomNavigationView bottomNavigation;

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
        fabAddProject = findViewById(R.id.fabAddProject);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        rvProjects.setLayoutManager(new LinearLayoutManager(this));

        projectAdapter = new ProjectAdapter(projectList, project -> {
            Intent intent = new Intent(MainActivity.this, ProjectDetailActivity.class);
            intent.putExtra("PROJECT_DATA", project);
            startActivity(intent);
        });
        rvProjects.setAdapter(projectAdapter);

        fabAddProject.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddProjectActivity.class));
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_projects) {
                return true;
            } else if (id == R.id.nav_projects) {
                Toast.makeText(this, "Expenses Feature Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_settings) {
                Toast.makeText(this, "Sync Cloud Feature Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjectsFromDatabase();
    }

    private void loadProjectsFromDatabase() {
        projectList = projectService.getAllProjects();
        projectAdapter.setProjects(projectList);

        double totalBudget = 0;
        for (Project p : projectList) {
            totalBudget += p.getBudget();
        }
        tvTotalBudget.setText(String.format("$%,.2f", totalBudget));

        if (projectList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvProjects.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvProjects.setVisibility(View.VISIBLE);
        }
    }
}