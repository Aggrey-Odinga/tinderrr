package com.example.tinderreloaded;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Button mLogin;
    private EditText mEmail, mPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.email);
        mLogin = (Button) findViewById(R.id.login);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if (isStringNull(email) || isStringNull(password)){
                    Toast.makeText(LoginActivity.this, "fill out all fields", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }else{
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, "Verify email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()){
                    Intent i =  new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }
            }
        };
    }
    private boolean isStringNull(String email) {
        return email.equals("");
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(firebaseAuthStateListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mAuth.removeAuthStateListener(firebaseAuthStateListener);
//    }
}
