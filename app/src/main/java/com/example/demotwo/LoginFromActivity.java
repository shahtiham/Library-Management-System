package com.example.demotwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFromActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser cruser;
    private FirebaseUser currentUser;
    private DatabaseReference regref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_from);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        regref = FirebaseDatabase.getInstance().getReference().child("Registered");
        cruser = mAuth.getCurrentUser();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser != null){
            // Commented due to addition of admin activity
            //sendusertousermainhomeactivity();

            // ** Here begins verification.
            String crid = mAuth.getCurrentUser().getUid();
            regref.child(crid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child("isadmin").getValue().toString().equals("true")){
                            sendusertoadminhomeactivity();
                        }
                        else{
                            Toast.makeText(LoginFromActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginFromActivity.this,UserLoginActivity.class));
                            finish();
                        }
                    }
                    else {
                        sendusertousermainhomeactivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    String dtError = error.getMessage().toString();
                    Toast.makeText(LoginFromActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendusertoadminhomeactivity() {
        Intent intent = new Intent(LoginFromActivity.this,AdminHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendusertousermainhomeactivity() {
        Intent intent = new Intent(LoginFromActivity.this,UserMainHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}