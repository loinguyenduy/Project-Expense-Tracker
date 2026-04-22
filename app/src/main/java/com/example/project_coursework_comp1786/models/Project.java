package com.example.project_coursework_comp1786.models;

import java.io.Serializable;

public class Project implements Serializable {
    private long id;
    private int isSynced;

    private String projectCode;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String manager;
    private String status;
    private double budget;
    private String specialRequirements;
    private String clientInfo;
    private String jobDifficulty;
    private String assignedTo;


    public Project(long id, String projectCode, String name, String description, String startDate,
                   String endDate, String manager, String status, double budget, String specialRequirements,
                   String clientInfo, String jobDifficulty, String assignedTo, int isSynced){
        this.id = id;
        this.projectCode = projectCode;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.manager = manager;
        this.status = status;
        this.budget = budget;
        this.specialRequirements = specialRequirements;
        this.clientInfo = clientInfo;
        this.jobDifficulty = jobDifficulty;
        this.assignedTo = assignedTo;
        this.isSynced = isSynced;
    }


    public Project(String projectCode, String name, String description, String startDate,
                   String endDate, String manager, String status, double budget,
                   String specialRequirements, String clientInfo, String jobDifficulty, String assignedTo, int isSynced) {
        this.projectCode = projectCode;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.manager = manager;
        this.status = status;
        this.budget = budget;
        this.specialRequirements = specialRequirements;
        this.clientInfo = clientInfo;
        this.jobDifficulty = jobDifficulty;
        this.assignedTo = assignedTo;
        this.isSynced = isSynced;
    }

    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    public int getIsSynced() { return isSynced; }
    public void setIsSynced(int isSynced) { this.isSynced = isSynced; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getSpecialRequirements() { return specialRequirements; }
    public void setSpecialRequirements(String specialRequirements) { this.specialRequirements = specialRequirements; }

    public String getClientInfo() { return clientInfo; }
    public void setClientInfo(String clientInfo) { this.clientInfo = clientInfo; }

    public String getJobDifficulty() { return jobDifficulty; }
    public void setJobDifficulty(String jobDifficulty) { this.jobDifficulty = jobDifficulty; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
}