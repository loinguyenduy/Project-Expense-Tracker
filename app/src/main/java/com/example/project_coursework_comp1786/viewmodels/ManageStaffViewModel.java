package com.example.project_coursework_comp1786.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_coursework_comp1786.models.User;
import com.example.project_coursework_comp1786.services.UserService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ManageStaffViewModel extends AndroidViewModel {
    private UserService userService;
    private DatabaseReference usersRef;

    public interface StaffLoadListener {
        void onLoaded(List<User> staffList);
    }

    public interface StatusToggleListener {
        void onSuccess();
        void onError(String error);
    }

    public ManageStaffViewModel(@NonNull Application application) {
        super(application);
        userService = new UserService(application);
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void loadLocalStaff(StaffLoadListener listener) {
        List<User> list = userService.getAllStaffLocally();
        listener.onLoaded(list);
    }

    public void toggleStaffStatus(User user, boolean isChecked, StatusToggleListener listener) {
        usersRef.child(user.getUid()).child("isActive").setValue(isChecked)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userService.updateStaffStatusLocally(user.getUid(), isChecked);
                        listener.onSuccess();
                    } else {
                        String err = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                        listener.onError("Failed to update on Cloud: " + err);
                    }
                });
    }
}