package com.example.demotwo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUserListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUserListFragment extends Fragment {
    private View adminuserlistView;

    private RecyclerView userrecView;

    private DatabaseReference usersRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminUserListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUserListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUserListFragment newInstance(String param1, String param2) {
        AdminUserListFragment fragment = new AdminUserListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adminuserlistView = inflater.inflate(R.layout.fragment_admin_user_list, container, false);

        userrecView = adminuserlistView.findViewById(R.id.adminhomefragUserlistrecview);
        userrecView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchuser("");

        return adminuserlistView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.usermainhomeSearch);
        item.setVisible(true);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchuser(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchuser(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchuser(String newText) {
        FirebaseRecyclerOptions<userprofile> options =
                new FirebaseRecyclerOptions.Builder<userprofile>()
                        .setQuery(usersRef.orderByChild("username").startAt(newText).endAt(newText+"\uf8ff"), userprofile.class)
                        .build();
        FirebaseRecyclerAdapter<userprofile, FragadminuserViewHolder> adapter =
                new FirebaseRecyclerAdapter<userprofile, FragadminuserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FragadminuserViewHolder holder, int position, @NonNull userprofile model) {
                        holder.userName.setText(model.getUsername());
                        holder.userEmail.setText(model.getEmail());

                        // onClickListener on users list.
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();
                                Intent aduserprofileintent = new Intent(getContext(),AdminUserListUserActivity.class);
                                aduserprofileintent.putExtra("visit_user_id",visit_user_id);
                                startActivity(aduserprofileintent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FragadminuserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_user_list_recview_display_users,parent,false);
                        FragadminuserViewHolder viewHolder = new FragadminuserViewHolder(view);
                        return viewHolder;
                    }
                };
        userrecView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FragadminuserViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userEmail;
        public FragadminuserViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.adminhomeRecusername);
            userEmail = itemView.findViewById(R.id.adminhomeRecemail);

        }
    }
}