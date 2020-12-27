package com.example.demotwo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminBookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminBookListFragment extends Fragment {
    private View adminbooklistView;

    private RecyclerView bookrecView;
    private FloatingActionButton fab;

    private DatabaseReference bookRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminBookListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminBookListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminBookListFragment newInstance(String param1, String param2) {
        AdminBookListFragment fragment = new AdminBookListFragment();
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
        adminbooklistView = inflater.inflate(R.layout.fragment_admin_book_list, container, false);

        bookRef = FirebaseDatabase.getInstance().getReference().child("Books");

        bookrecView = adminbooklistView.findViewById(R.id.adminhomefragBooklistrecview);
        bookrecView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchbook("");

        fab = adminbooklistView.findViewById(R.id.adminbooklistfragfabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showaddbookdialog();
            }
        });

        return adminbooklistView;
    }

    private void showaddbookdialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.activity_admin_book_list_add_books,null);

        final EditText bk, authr, cat, copies;
        final Button btnadd;
        bk = (EditText) mView.findViewById(R.id.edttxtadmindiabookname);
        authr = (EditText) mView.findViewById(R.id.edttxtadmindiaauthorname);
        cat = (EditText) mView.findViewById(R.id.edttxtadmindiacat);
        copies = (EditText) mView.findViewById(R.id.edttxtadmindiacopies);
        btnadd = (Button) mView.findViewById(R.id.btnadmindiaaddbook);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setTitle("New Book");

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Sbk, Sauthr, Scat = "N/A", Scpy;
                if(bk.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Enter book name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(authr.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Enter author name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!cat.getText().toString().equals("")){
                    Scat = cat.getText().toString();
                }
                if(copies.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Enter number of copies", Toast.LENGTH_SHORT).show();
                    return;
                }
                Sbk = bk.getText().toString();
                Sauthr = authr.getText().toString();
                Scpy = copies.getText().toString();

                boolean f = true;
                char ch = '#';
                for(int i = 0; i < Scpy.length(); i++){
                    if(!Character.isDigit(Scpy.charAt(i))){
                        f = false;
                        break;
                    }
                    if(Scpy.charAt(i) >= '0' && Scpy.charAt(i) <= '9' && ch == '#'){
                        ch = Scpy.charAt(i);
                    }
                }
                if(ch == '#'){
                    Toast.makeText(getContext(), "Please enter valid number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ch == '0'){
                    Toast.makeText(getContext(), "Please enter valid number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(f == false){
                    Toast.makeText(getContext(), "Please enter valid number", Toast.LENGTH_SHORT).show();
                    return;
                }

                addbooktodatabase(Sbk, Sauthr, Scat, Scpy);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void addbooktodatabase(String sbk, String sauthr, String scat, String scpy) {
        DatabaseReference dt = FirebaseDatabase.getInstance().getReference().child("Books");
        DatabaseReference adddata = FirebaseDatabase.getInstance().getReference().child("Books");
        dt.orderByChild("bookname").equalTo(sbk).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(getContext(), "The book already exists", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(getContext(), "New book added", Toast.LENGTH_SHORT).show();
                    HashMap<String,String> mp = new HashMap<>();
                    mp.put("bookname",sbk);
                    mp.put("authorname",sauthr);
                    mp.put("catagory",scat);
                    mp.put("copies",scpy);
                    String bkid =  adddata.push().getKey();
                    mp.put("bookid",bkid);
                    adddata.child(bkid).setValue(mp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String dtError = error.getMessage().toString();
                Toast.makeText(getContext(), "Error : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });
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
                searchbook(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchbook(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchbook(String newText) {
        FirebaseRecyclerOptions<bookprofile> options =
                new FirebaseRecyclerOptions.Builder<bookprofile>()
                        .setQuery(bookRef.orderByChild("bookname").startAt(newText).endAt(newText+"\uf8ff"), bookprofile.class)
                        .build();
        FirebaseRecyclerAdapter<bookprofile, FragadminbookViewHolder> adapter =
                new FirebaseRecyclerAdapter<bookprofile, FragadminbookViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FragadminbookViewHolder holder, int position, @NonNull bookprofile model) {
                        holder.bookName.setText(model.getBookname());
                        holder.authorName.setText(model.getAuthorname());

                        // onClickListener on users list.
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_book_id = getRef(position).getKey();
                                Intent adbookprofileintent = new Intent(getContext(),AdminBookListBookActivity.class);
                                adbookprofileintent.putExtra("visit_book_id",visit_book_id);
                                startActivity(adbookprofileintent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FragadminbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_book_list_recview_display_books,parent,false);
                        FragadminbookViewHolder viewHolder = new FragadminbookViewHolder(view);
                        return viewHolder;
                    }
                };
        bookrecView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FragadminbookViewHolder extends RecyclerView.ViewHolder{
        TextView bookName, authorName;
        public FragadminbookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.usermainhomeRecbookname);
            authorName = itemView.findViewById(R.id.usermainhomeRecauthorname);

        }
    }
}