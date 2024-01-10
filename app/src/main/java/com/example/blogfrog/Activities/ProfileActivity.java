package com.example.blogfrog.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.blogfrog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameLabel;
    private TextView emailLabel;
    private TextView idLabel;
    private Button saveButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private StorageReference mFirebaseStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgressDialog;
    private Button mBackButton1;
    private ImageButton profilePic;
    private Uri resultUri = null;
    private final static int GALLERY_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("MBlog_Profile_Pics");
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        mDatabaseReference.keepSynced(true);

        mProgressDialog = new ProgressDialog(this);

        userNameLabel = (TextView) findViewById(R.id.userNameDisplay);
        emailLabel = (TextView) findViewById(R.id.emailDisplay);
        idLabel = (TextView) findViewById(R.id.idDisplay);
        saveButton = (Button) findViewById(R.id.saveProfileBt);
        mBackButton1 = (Button) findViewById(R.id.backButton1);
        profilePic = (ImageButton) findViewById(R.id.profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });


        displayEmail();
        displayUser();
        idLabel.setText(mAuth.getCurrentUser().getUid());

        mBackButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, PostListActivity.class));
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("firebase outer save", String.valueOf(resultUri.toString()));

                if (resultUri != null) {
                    StorageReference imagePath = mFirebaseStorage.child("MBlog_Profile_Pics")
                            .child(resultUri.getLastPathSegment());
                    imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("firebase inner save", String.valueOf(resultUri.toString()));

                            String userid = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = mDatabaseReference.child(userid);

                            currentUserDb.child("image").setValue(resultUri.toString());

                            Intent intent = new Intent(ProfileActivity.this, PostListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            resultUri = data.getData();

            profilePic.setImageURI(resultUri);
            Log.d("image resultURI", String.valueOf(resultUri.toString()));
        }
    }
}