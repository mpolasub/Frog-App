package com.example.blogfrog.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.blogfrog.Data.BlogRecylerAdapter;
import com.example.blogfrog.Model.Blog;
import com.example.blogfrog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private RecyclerView recyclerView;
    private BlogRecylerAdapter blogRecylerAdapter;
    private List<Blog> blogList;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MBlog");
        mDatabaseReference.keepSynced(true);

        blogList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                if (mUser != null && mAuth != null) {
                    startActivity(new Intent(PostListActivity.this, AddPostActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:
                if (mUser != null && mAuth != null) {
                    mAuth.signOut();
                    startActivity(new Intent(PostListActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                Blog blog = dataSnapshot.getValue(Blog.class);

                blogList.add(0, blog);

                blogRecylerAdapter = new BlogRecylerAdapter(PostListActivity.this, blogList);
                recyclerView.setAdapter(blogRecylerAdapter);
                blogRecylerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}