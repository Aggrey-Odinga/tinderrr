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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()){
                    Intent i = new Intent(RegisterActivity.this, SplashScreenActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mName = (EditText) findViewById(R.id.name);
        mPassword = (EditText) findViewById(R.id.password);


        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String name = mName.getText().toString();
                final String password = mPassword.getText().toString();


                if (checkInputs(email, name, password)){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        task.getException();
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }else{
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "Successfully Registered" + "Check Email for Verification", Toast.LENGTH_SHORT).show();
                                                    String userId = mAuth.getCurrentUser().getUid();
                                                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                                                    Map userInfo = new HashMap<>();

                                                    userInfo.put("name", name);

                                                    userInfo.put("profileImageUrl", "default");
                                                    currentUserDb.updateChildren(userInfo);

                                                    mEmail.setText("");
                                                    mName.setText("");
                                                    mPassword.setText("");
                                                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                                else {
                                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            }

            private boolean checkInputs(String email, String username, String password) {
                if (email.equals("") || username.equals("") || password.equals("")){
                    Toast.makeText(RegisterActivity.this, "Fill Out All Fields", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        });
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
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(RegisterActivity.this, LoginOrRegistrationActivity.class);
//        startActivity(i);
//        finish();
//        return;
//    }

}