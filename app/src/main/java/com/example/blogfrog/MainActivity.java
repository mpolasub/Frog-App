package com.example.blogfrog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private Button logInButton;
    private Button createActButton;
    private EditText emailField;
    private EditText passwordField;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        logInButton = (Button) findViewById(R.id.logInButtonEt);
        createActButton = (Button) findViewById(R.id.signUpButtonEt);
        emailField = (EditText) findViewById(R.id.logInEmailEt);
        passwordField = (EditText) findViewById(R.id.logInPasswordEt);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null) {
                    Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Not Signed In", Toast.LENGTH_LONG).show();
                }
            }
        };

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(emailField.getText().toString())
                && !TextUtils.isEmpty(passwordField.getText().toString())) {
                    String email = emailField.getText().toString();
                    String pwd = emailField.getText().toString();
                    login(email, pwd);

                } else {

                }
            }
        });
    }

    private void login(String email, String pwd) {
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_LONG).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}