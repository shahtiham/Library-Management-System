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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserMainHomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private TextView txtheadusername;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String cruid;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_home);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        cruid = currentUser.getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Books");

        drawerLayout = findViewById(R.id.userhomeDrawer);
        navigationView = findViewById(R.id.usermainhomeNavview);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        View navhead = navigationView.getHeaderView(0);
        txtheadusername = navhead.findViewById(R.id.txtheadusername);
        setusernameonhead();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment,new UserBookListFragment());
        fragmentTransaction.commit();


        //MainMenu item click
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()){
                    case R.id.btnBooklist:
                        Toast.makeText(UserMainHomeActivity.this, "Book list", Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle("Books");
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment,new UserBookListFragment());
                        fragmentTransaction.commit();
                        return true;
                    case R.id.btnissuedBooklist:
                        Toast.makeText(UserMainHomeActivity.this, "Issued book list", Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle("Issued Books");
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment,new UserIssuedBookListFragment());
                        fragmentTransaction.commit();
                        return true;
                    case R.id.btnreturnedBooklist:
                        Toast.makeText(UserMainHomeActivity.this, "Returned book list", Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle("Returned Books");
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment,new UserReturnedBookListFragment());
                        fragmentTransaction.commit();
                        return true;
                    case R.id.btnSignout:
                        Toast.makeText(UserMainHomeActivity.this, "Sign out", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        sendusertomainactivity();
                        return true;
                }
                return true;
            }
        });

    }

    private void setusernameonhead() {
        String name = "";
        userref.child(cruid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    txtheadusername.setText(snapshot.child("username").getValue().toString());
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.usermainhomeSearch);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    private void sendusertomainactivity() {
        Intent intent = new Intent(UserMainHomeActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}