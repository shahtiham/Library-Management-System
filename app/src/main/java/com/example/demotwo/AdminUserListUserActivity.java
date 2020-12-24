package com.example.demotwo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListUserActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private AdminUserIssuedBookFragment adminUserIssuedBookFragment;
    private AdminUserReturnedBookFragment adminUserReturnedBookFragment;
    private AdminUserIssueRequestBookFragment adminUserIssueRequestBookFragment;
    private AdminUserReturnRequestBookFragment adminUserReturnRequestBookFragment;

    private String reciever_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list_user);

        toolbar = findViewById(R.id.adminuserlistuseractivitytabToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Users details");

        reciever_user_id = getIntent().getExtras().get("visit_user_id").toString();

        viewPager = findViewById(R.id.adminuserlistuseractivityviewpager);
        tabLayout = findViewById(R.id.adminuserlistuseractivitytablayout);

        Bundle bundle = new Bundle();
        bundle.putString("param1",reciever_user_id);
        bundle.putString("param2","");

        adminUserIssuedBookFragment = new AdminUserIssuedBookFragment();
        adminUserIssuedBookFragment.setArguments(bundle);
        adminUserReturnedBookFragment = new AdminUserReturnedBookFragment();
        adminUserReturnedBookFragment.setArguments(bundle);
        adminUserIssueRequestBookFragment = new AdminUserIssueRequestBookFragment();
        adminUserIssueRequestBookFragment.setArguments(bundle);
        adminUserReturnRequestBookFragment = new AdminUserReturnRequestBookFragment();
        adminUserReturnRequestBookFragment.setArguments(bundle);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        viewPagerAdapter.addfragment(adminUserIssuedBookFragment,"Issued Books");
        viewPagerAdapter.addfragment(adminUserReturnedBookFragment,"Returned Books");
        viewPagerAdapter.addfragment(adminUserIssueRequestBookFragment,"Issue Request");
        viewPagerAdapter.addfragment(adminUserReturnRequestBookFragment,"Return Request");
        viewPager.setAdapter(viewPagerAdapter);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmenttitles = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addfragment(Fragment fragment,String title){
            fragments.add(fragment);
            fragmenttitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmenttitles.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.empty_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }
}