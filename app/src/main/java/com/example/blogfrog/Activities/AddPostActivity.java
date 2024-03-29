package com.example.blogfrog.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blogfrog.Model.Blog;
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

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private Button mBackButton;
    private StorageReference mStorage;
    private DatabaseReference mPostDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Uri mImageUri;
    private String retrievedUserName;
    private static final int GALLERY_CODE = 1;
    private String pfpUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog");
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");

        mDatabaseReference.keepSynced(true);

        mPostImage = (ImageButton) findViewById(R.id.imageButton);
        mPostTitle = (EditText) findViewById(R.id.postTitleEt);
        mPostDesc = (EditText) findViewById(R.id.descriptionEt);
        mSubmitButton = (Button) findViewById(R.id.submitPost);
        mBackButton = (Button) findViewById(R.id.backButton);

        fetchUsername();

//        PFP
        displayPfp();


        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // post to db
                startPosting();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                finish();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

//   PFP
    public void displayPfp() {
        String userid = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = mDatabaseReference.child(userid);
        currentUserDb.child("image").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@android.support.annotation.NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    pfpUrl = "content://com.android.providers.media.documents/document/image%3A31";
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    pfpUrl = task.getResult().getValue().toString();
                }
            }
        });
    }

    private void fetchUsername() {
        String userid = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = mDatabaseReference.child(userid);
        currentUserDb.child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    retrievedUserName = String.valueOf(task.getResult().getValue());
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }


    private void startPosting() {

        mProgress.setMessage("Posting to app...");
        mProgress.show();

        String titleVal = mPostTitle.getText().toString().trim();
        String descVal = mPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal)
        && mImageUri != null) {
            // upload

            StorageReference filepath = mStorage.child("MBlog_images").
                    child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    DatabaseReference newPost = mPostDatabase.push();
                                    Map<String, String> dataToSave = new HashMap<>();
                                    dataToSave.put("title", titleVal);
                                    dataToSave.put("desc", descVal);
                                    dataToSave.put("image", imageUrl);
                                    dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));
                                    dataToSave.put("userid", mUser.getUid());
                                    dataToSave.put("userName", retrievedUserName);
//                                    PFP
                                    dataToSave.put("pfp", pfpUrl);

                                    newPost.setValue(dataToSave);

                                    mProgress.dismiss();

                                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                                    finish();
                                }
                            });
                        }
                    }
                }
            });

        } else {
            Toast.makeText(AddPostActivity.this, "Please fill in the blanks", Toast.LENGTH_LONG).show();
        }
    }
}