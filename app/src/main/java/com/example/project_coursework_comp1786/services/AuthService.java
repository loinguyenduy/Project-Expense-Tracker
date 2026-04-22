package com.example.project_coursework_comp1786.services;

import com.example.project_coursework_comp1786.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthService {
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public AuthService() {
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    // Task đăng nhập
    public Task<AuthResult> loginUser(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    // Task tạo tài khoản trên Auth
    public Task<AuthResult> registerUserAuth(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    // Task lưu thông tin vào Realtime Database
    public Task<Void> saveUserToDatabase(String uid, User user) {
        return usersRef.child(uid).setValue(user);
    }

    // Task lấy thông tin Role từ Database
    public Task<DataSnapshot> getUserProfile(String uid) {
        return usersRef.child(uid).get();
    }

    public void logout() {
        mAuth.signOut();
    }
}