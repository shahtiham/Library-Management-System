package com.example.demotwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserSignupActivity extends AppCompatActivity {
    private TextView txtGotoLogin;
    private Button btnSignup;
    private Toolbar toolbar;
    private TextInputEditText edttxtEmail,edttxtPassword,edttxtUsername;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        InitFields();

        toolbar = findViewById(R.id.signupToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign up");

        // If already have account. Go to Login activity.
        txtGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignupActivity.this,UserLoginActivity.class));
                finish();
            }
        });

        // If Sign up button is clicked.
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    // Initializing Fields.
    private void InitFields() {
        edttxtEmail = findViewById(R.id.edttxtSignupemail);
        edttxtPassword = findViewById(R.id.edttxtSignuppassword);
        edttxtUsername = findViewById(R.id.edttxtSignupusername);
        btnSignup = findViewById(R.id.btnSignup);
        txtGotoLogin = findViewById(R.id.txtGotologin);
        loadingbar = new ProgressDialog(this);
    }

    // User Registration.
    private void registerUser() {
        String email,password,username;
        email = edttxtEmail.getText().toString();
        password = edttxtPassword.getText().toString();
        username = edttxtUsername.getText().toString();

        if(email.equals("")){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please enter an valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals("")){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < 6){
            Toast.makeText(this, "Password length must be at least 6", Toast.LENGTH_SHORT).show();
            return;
        }
        if(username.equals("")){
            Toast.makeText(this, "Please enter a user name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Checking if username already exists or not.
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Usernamearch");
        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(UserSignupActivity.this, "User name exists !", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(UserSignupActivity.this, "User name doesn't exist !", Toast.LENGTH_SHORT).show();
                    // Creating new account.
                    loadingbar.setTitle("Creating New Account");
                    loadingbar.setMessage("Please wait");
                    loadingbar.setCanceledOnTouchOutside(true);
                    loadingbar.show();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        String userUid = mAuth.getCurrentUser().getUid();
                                        //rootRef.child("Users").child(userUid).setValue("");
                                        //putuserinfo(email,username);

                                        //sendusertousermainhomeactivity();
                                        //Toast.makeText(UserSignupActivity.this, "Welcome to RUET library !", Toast.LENGTH_SHORT).show();

                                        // ** Here begins verification.
                                        mAuth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            rootRef.child("Registered").child(userUid).setValue("");
                                                            putuserinfotoregister(email, username, userUid);
                                                        } else{
                                                            Toast.makeText(UserSignupActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        loadingbar.dismiss();
                                    } else {
                                        Toast.makeText(UserSignupActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String dtError = databaseError.getMessage().toString();
                Toast.makeText(UserSignupActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }

    private void putuserinfotoregister(String email, String username, String userUid) {
        HashMap<String, String> UprofileMap = new HashMap<>();
        UprofileMap.put("uid", userUid);
        UprofileMap.put("username", username);
        UprofileMap.put("email", email);
        UprofileMap.put("isadmin","false");

        DatabaseReference Adt = FirebaseDatabase.getInstance().getReference().child("Usernamearch");
        HashMap<String,String> nhs = new HashMap<>();
        nhs.put("Username",username);
        Adt.child(userUid).setValue(nhs);
        
        rootRef.child("Registered").child(userUid).setValue(UprofileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(UserSignupActivity.this, "Error :" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(UserSignupActivity.this, "Registered successfully. Please verify email to login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserSignupActivity.this,UserLoginActivity.class));
                            finish();
                        }
                    }
                });
    }

    private void putuserinfo(String email, String username) {
        String userUid = mAuth.getCurrentUser().getUid();
        HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", userUid);
            profileMap.put("username", username);
            profileMap.put("email", email);
        rootRef.child("Users").child(userUid).setValue(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(UserSignupActivity.this, "Error :" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendusertousermainhomeactivity() {
        Intent intent = new Intent(UserSignupActivity.this,UserMainHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}