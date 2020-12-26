package com.example.demotwo;

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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserReturnedBookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserReturnedBookListFragment extends Fragment {
    private View usermainreturnedbooklistView;

    private RecyclerView bookrecView;

    private DatabaseReference bookRef, userRef, bkref;
    private FirebaseAuth mAuth;
    private String cruserid, retbkcpy, retbkduration, retbkissuedate, retbkretdate;
    private String retbkid;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserReturnedBookListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserReturnedBookListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserReturnedBookListFragment newInstance(String param1, String param2) {
        UserReturnedBookListFragment fragment = new UserReturnedBookListFragment();
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
        usermainreturnedbooklistView = inflater.inflate(R.layout.fragment_user_returned_book_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        cruserid = mAuth.getCurrentUser().getUid();
        bookRef = userRef.child(cruserid).child("Returnedbooks");
        bkref = bookRef;

        bookrecView = usermainreturnedbooklistView.findViewById(R.id.usermainhomefragreturnedBooklist);
        bookrecView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchbook("");

        return  usermainreturnedbooklistView;
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
        FirebaseRecyclerAdapter<bookprofile, FragreturnedbookViewHolder> adapter =
                new FirebaseRecyclerAdapter<bookprofile, FragreturnedbookViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FragreturnedbookViewHolder holder, int position, @NonNull bookprofile model) {
                        holder.bookName.setText(model.getBookname());
                        holder.authorName.setText(model.getAuthorname());

                        // onClickListener on users list.
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                retbkid = getRef(position).getKey();
                                showdialog();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FragreturnedbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_book_list_recview_display_issued_books,parent,false);
                        FragreturnedbookViewHolder viewHolder = new FragreturnedbookViewHolder(view);
                        return viewHolder;
                    }
                };
        bookrecView.setAdapter(adapter);
        adapter.startListening();
    }

    private void showdialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.activity_user_returned_book_list_dialog,null);

        final TextView txtcpy, txtduration, txtissuedate, txtreturndate, adfine;
        txtcpy = (TextView) mView.findViewById(R.id.txtuserretbklistissuedcopies);
        txtduration = (TextView) mView.findViewById(R.id.txtuserretbklistissuedduration);
        txtissuedate = (TextView) mView.findViewById(R.id.txtuserretbklistissuedate);
        txtreturndate = (TextView) mView.findViewById(R.id.txtuserretbklistreturndate);
        adfine = (TextView) mView.findViewById(R.id.txtuserretbklistfinepaid);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setTitle("Returned Book Details");

        bkref.child(retbkid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    txtcpy.setText(snapshot.child("issuedcopies").getValue().toString()+" copies issued");
                    txtduration.setText("Issued for "+snapshot.child("issueduration").getValue().toString());
                    txtissuedate.setText("Issued on "+snapshot.child("issuedate").getValue().toString());
                    txtreturndate.setText("Returned on "+snapshot.child("returndate").getValue().toString());
                    if(snapshot.hasChild("fine")){
                        adfine.setText("Fine paid    " + snapshot.child("fine").getValue().toString() + " Tk");
                    } else {
                        adfine.setText("Fine paid    " + "0" + " Tk");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        alertDialog.show();
    }


    public static class FragreturnedbookViewHolder extends RecyclerView.ViewHolder{
        TextView bookName, authorName;
        public FragreturnedbookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.usermainhomeRecissuedbookname);
            authorName = itemView.findViewById(R.id.usermainhomeRecissuedauthorname);

        }
    }
}