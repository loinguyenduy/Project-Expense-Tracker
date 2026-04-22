package com.example.project_coursework_comp1786.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.project_coursework_comp1786.views.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "ArchitectLedgerSession";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_UID = "uid";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "fullName";
    public static final String KEY_ROLE = "role";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String uid, String email, String fullName, String role) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, fullName);
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_UID, pref.getString(KEY_UID, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}