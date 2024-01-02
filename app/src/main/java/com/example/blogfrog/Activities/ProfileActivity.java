package com.example.blogfrog.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.blogfrog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameLabel;
    private TextView emailLabel;
    private Button saveButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgressDialog;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        mDatabaseReference.keepSynced(true);

        mProgressDialog = new ProgressDialog(this);

        userNameLabel = (TextView) findViewById(R.id.userNameDisplay);
        emailLabel = (TextView) findViewById(R.id.emailDisplay);
        saveButton = (Button) findViewById(R.id.saveProfileBt);

        displayEmail();
        displayUser();
    }

    private void displayUser() {
        String userid = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = mDatabaseReference.child(userid);
        currentUserDb.child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    userNameLabel.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }


    private void displayEmail() {
        String userid = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = mDatabaseReference.child(userid);
        currentUserDb.child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    emailLabel.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

    }
}