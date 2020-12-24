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

public class AdminHomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView txtheadadminname;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String cruid;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference isadminloggedin,adminref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        cruid = currentUser.getUid();
        adminref = FirebaseDatabase.getInstance().getReference().child("Registered");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Books");

        drawerLayout = findViewById(R.id.adminhomeDrawer);
        navigationView = findViewById(R.id.adminhomeNavview);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        View navhead = navigationView.getHeaderView(0);
        txtheadadminname = navhead.findViewById(R.id.txtheadadminname);
        setadminnameonhead();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment,new AdminBookListFragment());
        fragmentTransaction.commit();

        //MainMenu item click
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()){
                    case R.id.adminbtnBooklist:
                        Toast.makeText(AdminHomeActivity.this, "Book list", Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle("Books");
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment,new AdminBookListFragment());
                        fragmentTransaction.commit();
                        return true;
                    case R.id.adminbtnUserlist:
                        Toast.makeText(AdminHomeActivity.this, "User list", Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle("Users");
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment,new AdminUserListFragment());
                        fragmentTransaction.commit();
                        return true;
                    case R.id.adminbtnSignout:
                        Toast.makeText(AdminHomeActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        sendusertomainactivity();
                        return true;
                }
                return true;
            }
        });
    }

    private void setadminnameonhead() {
        adminref.child(cruid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    txtheadadminname.setText(snapshot.child("username").getValue().toString());
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
        Intent intent = new Intent(AdminHomeActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}