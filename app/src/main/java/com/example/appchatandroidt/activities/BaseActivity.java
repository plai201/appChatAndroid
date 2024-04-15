package com.example.appchatandroidt.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class BaseActivity extends AppCompatActivity {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userRef != null) {
            updateOnlineStatus("online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userRef != null) {
            updateOnlineStatus("offline");
        }
    }
    private void updateOnlineStatus(String status) {
        userRef.child("status").setValue(status);
        if (status.equals("offline")) {
            userRef.child("lastActive").setValue(ServerValue.TIMESTAMP);
        } else {
            userRef.child("lastActive").setValue("online");
        }
    }

    protected String calculateTimeSinceLastActive(long lastActive) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastActive;

        long seconds = timeDifference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String timeAgo;

        if (hours > 0) {
            timeAgo = hours + " hours ago";
        } else if (minutes > 0) {
            timeAgo = minutes + " minutes ago";
        } else {
            timeAgo = seconds + " seconds ago";
        }
        return timeAgo;
     }
}
