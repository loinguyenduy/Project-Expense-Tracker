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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvProjects;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddProject;

    private ProjectAdapter projectAdapter;
    private ProjectService projectService;
    private List<Project> projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Service kết nối Database
        projectService = new ProjectService(this);
        projectList = new ArrayList<>();

        // Ánh xạ View
        rvProjects = findViewById(R.id.rvProjects);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddProject = findViewById(R.id.fabAddProject);

        // Cài đặt RecyclerView (Danh sách dạng cuộn dọc)
        rvProjects.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter và bắt sự kiện Click vào 1 thẻ dự án
        projectAdapter = new ProjectAdapter(projectList, project -> {
            // Tạm thời hiện Toast khi click. Ở bước sau ta sẽ mở trang Chi tiết / Expense.
            Toast.makeText(this, "Clicked: " + project.getName(), Toast.LENGTH_SHORT).show();
        });
        rvProjects.setAdapter(projectAdapter);

        // Bắt sự kiện bấm nút dấu +
        fabAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });
    }

    // Hàm này tự động chạy mỗi khi màn hình này xuất hiện lại trên cùng
    @Override
    protected void onResume() {
        super.onResume();
        loadProjectsFromDatabase();
    }

    private void loadProjectsFromDatabase() {
        // Lấy dữ liệu mới nhất từ SQLite
        projectList = projectService.getAllProjects();

        // Cập nhật lại cho Adapter
        projectAdapter.setProjects(projectList);

        // Kiểm tra xem danh sách có trống không để ẩn/hiện câu thông báo
        if (projectList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvProjects.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvProjects.setVisibility(View.VISIBLE);
        }
    }
}