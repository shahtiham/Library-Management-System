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

public class UserLoginActivity extends AppCompatActivity {
    private TextView txtGotosignup, txtforgot;
    private Button btnLogin,btnadminlogin;
    private ProgressDialog loadingbar;
    private Toolbar toolbar;
    private TextInputEditText edttxtEmail,edttxtPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference usersref, regref, rgrf, rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersref = FirebaseDatabase.getInstance().getReference().child("Users");
        regref = FirebaseDatabase.getInstance().getReference().child("Registered");
        rgrf = regref;

        InitFields();

        toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign in");

        // If new account needed.
        txtGotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this,UserSignupActivity.class));
                finish();
            }
        });

        // Forgot password.
        txtforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendpassrecovery();
            }
        });

        // User Login.
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowLogin();
            }
        });

    }

    private void sendpassrecovery() {
        String email;
        email = edttxtEmail.getText().toString();

        if(email.equals("")){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please enter an valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UserLoginActivity.this, "Please check your email to reset password", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(UserLoginActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendusertoadminhomeactivity() {
        Intent intent = new Intent(UserLoginActivity.this,AdminHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // User login.
    private void AllowLogin() {
        String email,password;
        email = edttxtEmail.getText().toString();
        password = edttxtPassword.getText().toString();

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

        // User Log in.
        loadingbar.setTitle("Signing In");
        loadingbar.setMessage("Please wait");
        loadingbar.setCanceledOnTouchOutside(true);
        loadingbar.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String crid = mAuth.getCurrentUser().getUid().toString();
                            // Check if this user is an admin, admin doesn't require email verification.
                            regref.child(crid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String isad;
                                        isad = snapshot.child("isadmin").getValue().toString();
                                        if(isad.equals("true")){
                                            sendusertoadminhomeactivity();
                                            Toast.makeText(UserLoginActivity.this, "Signed in successfully !", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            //// Check Email verification here (later).
                                            //sendusertousermainhomeactivity();
                                            //Toast.makeText(UserLoginActivity.this, "Signed in successfully !", Toast.LENGTH_SHORT).show();

                                            // ** Here begins verification.
                                            if(mAuth.getCurrentUser().isEmailVerified()){
                                                rgrf.child(crid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(!snapshot.exists()){
                                                            sendusertousermainhomeactivity();
                                                            Toast.makeText(UserLoginActivity.this, "Signed in successfully !", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            String email = snapshot.child("email").getValue().toString();
                                                            String username = snapshot.child("username").getValue().toString();
                                                            rootRef.child("Users").child(crid).setValue("");
                                                            putuserinfo(email,username,crid);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        String dtError = error.getMessage().toString();
                                                        Toast.makeText(UserLoginActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(UserLoginActivity.this, "Please verify your email to login", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    else{
                                        if(mAuth.getCurrentUser().isEmailVerified()){
                                            sendusertousermainhomeactivity();
                                            Toast.makeText(UserLoginActivity.this, "Signed in successfully !", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(UserLoginActivity.this, "Please verify your email to login", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    String dtError = error.getMessage().toString();
                                    Toast.makeText(UserLoginActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
                                }
                            });

                            loadingbar.dismiss();
                        }
                        else{
                            Toast.makeText(UserLoginActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });
    }

    private void putuserinfo(String email, String username, String crid) {
        DatabaseReference dt = FirebaseDatabase.getInstance().getReference().child("Registered").child(crid);
        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put("uid", crid);
        profileMap.put("username", username);
        profileMap.put("email", email);
        profileMap.put("isadmin","false");

        rootRef.child("Users").child(crid).setValue(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(UserLoginActivity.this, "Error :" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dt.removeValue();
                            sendusertousermainhomeactivity();
                            Toast.makeText(UserLoginActivity.this, "Signed in successfully !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Initializing Fields.
    private void InitFields() {
        edttxtEmail = findViewById(R.id.edttxtLoginemail);
        edttxtPassword = findViewById(R.id.edttxtLoginpassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtGotosignup = findViewById(R.id.txtGotosignup);
        txtforgot = findViewById(R.id.txtforgotpass);
        loadingbar = new ProgressDialog(this);
    }

    private void sendusertousermainhomeactivity() {
        Intent intent = new Intent(UserLoginActivity.this,UserMainHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}