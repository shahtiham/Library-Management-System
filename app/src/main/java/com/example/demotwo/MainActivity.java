package com.example.demotwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference isadminloggedin, regref, userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        regref = FirebaseDatabase.getInstance().getReference().child("Registered");
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = mAuth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Library Management System");

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        //MainFragment Loading
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment,new MainFragment());
        fragmentTransaction.commit();


        //MainMenu item click
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()){
                    case R.id.home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment,new MainFragment());
                        fragmentTransaction.commit();
                        return true;
                    case R.id.userLogin:
                        Toast.makeText(MainActivity.this, "User Login", Toast.LENGTH_SHORT).show();
                        Intent intentLogin = new Intent(MainActivity.this,UserLoginActivity.class);
                        startActivity(intentLogin);
                        return true;
                    case R.id.userSignup:
                        Toast.makeText(MainActivity.this, "Sign up", Toast.LENGTH_SHORT).show();
                        Intent intentSignup = new Intent(MainActivity.this,UserSignupActivity.class);
                        startActivity(intentSignup);
                        return true;
                    case R.id.contactUs:
                        Toast.makeText(MainActivity.this, "Contact us !", Toast.LENGTH_SHORT).show();
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment,new ContactUsFragment());
                        fragmentTransaction.commit();
                        return true;
                    case R.id.aboutUs:
                        Toast.makeText(MainActivity.this, "About us", Toast.LENGTH_SHORT).show();
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment,new AboutUsFragment());
                        fragmentTransaction.commit();
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        check();

        //fragmentManager = getSupportFragmentManager();
        //fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.container_fragment,new MainFragment());
        //fragmentTransaction.commit();
    }

    private void check() {
        if(currentUser != null){
            // Commented due to addition of admin activity
            //sendusertousermainhomeactivity();

            // ** Here begins verification.
            String crid = mAuth.getCurrentUser().getUid();
            if(currentUser.isEmailVerified()){
                regref.child(crid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.child("isadmin").getValue().toString().equals("true")){
                                sendusertoadminhomeactivity();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,UserLoginActivity.class));
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
                        Toast.makeText(MainActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                regref.child(crid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.child("isadmin").getValue().toString().equals("true")){
                                sendusertoadminhomeactivity();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,UserLoginActivity.class));
                                finish();
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please verify your email to login", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this,UserLoginActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        String dtError = error.getMessage().toString();
                        Toast.makeText(MainActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }


    private void sendusertoadminhomeactivity() {
        Intent intent = new Intent(MainActivity.this,AdminHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendusertousermainhomeactivity() {
        Intent intent = new Intent(MainActivity.this,UserMainHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}