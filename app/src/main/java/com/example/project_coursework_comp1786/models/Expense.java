package com.example.project_coursework_comp1786.models;

import java.io.Serializable;

public class Expense implements Serializable {
    private long id;
    private long projectId;
    private String date;
    private double amount;
    private String currency;
    private String type;
    private String paymentMethod;
    private String claimant;
    private String status;
    private String description;
    private String location;
    private int isSynced;

    public Expense(long id, long projectId, String date, double amount, String currency, String type,
                   String paymentMethod, String claimant, String status, String description, String location, int isSynced) {
        this.id = id;
        this.projectId = projectId;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.claimant = claimant;
        this.status = status;
        this.description = description;
        this.location = location;
        this.isSynced = isSynced;
    }

    public Expense(long projectId, String date, double amount, String currency, String type,
                   String paymentMethod, String claimant, String status, String description, String location, int isSynced) {
        this.projectId = projectId;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.claimant = claimant;
        this.status = status;
        this.description = description;
        this.location = location;
        this.isSynced = isSynced;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProjectId() { return projectId; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getClaimant() { return claimant; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getIsSynced() { return isSynced; }
    public void setIsSynced(int isSynced) { this.isSynced = isSynced; }
}