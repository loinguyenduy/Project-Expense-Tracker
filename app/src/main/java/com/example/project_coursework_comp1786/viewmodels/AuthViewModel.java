package com.example.project_coursework_comp1786.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_coursework_comp1786.models.User;
import com.example.project_coursework_comp1786.services.AuthService;
import com.example.project_coursework_comp1786.utils.SessionManager;

public class AuthViewModel extends AndroidViewModel {
    private AuthService authService;
    private SessionManager sessionManager;

    public interface AuthListener {
        void onLoading();
        void onSuccess(String message);
        void onError(String message);
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authService = new AuthService();
        sessionManager = new SessionManager(application);
    }

    public void login(String email, String password, AuthListener listener) {
        if (email.isEmpty() || password.isEmpty()) {
            listener.onError("Please enter email and password");
            return;
        }

        listener.onLoading();
        authService.loginUser(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                checkAndSaveSession(uid, email, listener);
            } else {
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown authentication error.";
                listener.onError("Email or password is incorrect!:");
            }
        });
    }

    public void register(String fullName, String email, String password, AuthListener listener) {
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            listener.onError("Please fill all fields");
            return;
        }
        if (password.length() < 6) {
            listener.onError("Password must be at least 6 characters");
            return;
        }

        listener.onLoading();
        authService.registerUserAuth(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                User newAdmin = new User(uid, email, fullName, "admin");

                authService.saveUserToDatabase(uid, newAdmin).addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        sessionManager.createLoginSession(uid, email, fullName, "admin");
                        listener.onSuccess("Registration Successful!");
                    } else {
                        // UPDATE: Xử lý Exception an toàn cho Database
                        String dbError = dbTask.getException() != null ? dbTask.getException().getMessage() : "Could not reach database.";
                        listener.onError("Database Error: " + dbError);
                    }
                });
            } else {
                // UPDATE: Xử lý Exception an toàn cho Auth (Khắc phục lỗi treo Creating account)
                String authError = task.getException() != null ? task.getException().getMessage() : "Unknown registry error.";
                listener.onError("Registration Failed: " + authError);
            }
        });
    }

    private void checkAndSaveSession(String uid, String email, AuthListener listener) {
        authService.getUserProfile(uid).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String role = task.getResult().child("role").getValue(String.class);
                String fullName = task.getResult().child("fullName").getValue(String.class);

                if ("admin".equals(role)) {
                    sessionManager.createLoginSession(uid, email, fullName, role);
                    listener.onSuccess("Welcome " + fullName);
                } else {
                    authService.logout();
                    listener.onError("Access Denied. You are not an Admin.");
                }
            } else {
                authService.logout();
                String profileErr = task.getException() != null ? task.getException().getMessage() : "User profile not found.";
                listener.onError("Role Check Failed: " + profileErr);
            }
        });
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
}