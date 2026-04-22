package com.example.project_coursework_comp1786.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_coursework_comp1786.R;
import com.example.project_coursework_comp1786.models.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<Project> projectList;
    private OnItemClickListener listener;     //handle click from activity

    //
    public interface OnItemClickListener {
        void onItemClick(Project project);
    }

    //this constructor is used to pass data to adapter
    public ProjectAdapter(List<Project> projectList, OnItemClickListener listener) {
        this.projectList = projectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    //create view holder for each item in list
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    //bind data to view holder
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project currentProject = projectList.get(position);
        holder.bind(currentProject, listener);
    }

    @Override
    public int getItemCount() {
        return projectList == null ? 0 : projectList.size();
    }

    public void setProjects(List<Project> newProjects) {
        this.projectList = newProjects;
        notifyDataSetChanged();
    }

    //view holder class for each item in list
    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCodeManager, tvStatus, tvBudget;
        ImageView imgSyncStatus;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvCodeManager = itemView.findViewById(R.id.tvItemCodeManager);
            tvStatus = itemView.findViewById(R.id.tvItemStatus);
            tvBudget = itemView.findViewById(R.id.tvItemBudget);
            imgSyncStatus = itemView.findViewById(R.id.imgSyncStatus);
        }

        //display data to UI
        public void bind(final Project project, final OnItemClickListener listener) {
            tvName.setText(project.getName());

            String codeAndManager = project.getProjectCode() + " • Manager: " + project.getManager();
            tvCodeManager.setText(codeAndManager);

            tvStatus.setText(project.getStatus());
            tvBudget.setText(String.format("$%,.2f", project.getBudget()));

            if (project.getIsSynced() == 1) {
                imgSyncStatus.setColorFilter(itemView.getContext().getColor(android.R.color.holo_green_dark));
            } else {
                imgSyncStatus.setColorFilter(itemView.getContext().getColor(android.R.color.darker_gray));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(project);
            });
        }
    }
}