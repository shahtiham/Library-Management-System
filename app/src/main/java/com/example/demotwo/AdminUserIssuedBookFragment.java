package com.example.demotwo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUserIssuedBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUserIssuedBookFragment extends Fragment {
    private View adusisbk;

    private RecyclerView adminuserissuedrecView;
    private String userid, retbkid;

    private DatabaseReference usersRef, issuedbkref, bkref;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1 = "";
    private String mParam2 = "";

    public AdminUserIssuedBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUserIssuedBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUserIssuedBookFragment newInstance(String param1, String param2) {
        AdminUserIssuedBookFragment fragment = new AdminUserIssuedBookFragment();
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
        adusisbk = inflater.inflate(R.layout.fragment_admin_user_issued_book, container, false);

        userid = mParam1;
        issuedbkref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Issuedbooks");
        bkref = issuedbkref;

        adminuserissuedrecView = adusisbk.findViewById(R.id.adminuserissuedBooklist);
        adminuserissuedrecView.setLayoutManager(new LinearLayoutManager(getContext()));

        getbooks();

        return adusisbk;
    }

    private void getbooks() {
        FirebaseRecyclerOptions<bookprofile> options =
                new FirebaseRecyclerOptions.Builder<bookprofile>()
                        .setQuery(issuedbkref.orderByChild("bookname"), bookprofile.class)
                        .build();
        FirebaseRecyclerAdapter<bookprofile, FragadminuserissuedbookViewHolder> adapter =
                new FirebaseRecyclerAdapter<bookprofile, FragadminuserissuedbookViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FragadminuserissuedbookViewHolder holder, int position, @NonNull bookprofile model) {
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
                    public FragadminuserissuedbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_book_list_recview_display_issued_books,parent,false);
                        FragadminuserissuedbookViewHolder viewHolder = new FragadminuserissuedbookViewHolder(view);
                        return viewHolder;
                    }
                };
        adminuserissuedrecView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FragadminuserissuedbookViewHolder extends RecyclerView.ViewHolder{
        TextView bookName, authorName;
        public FragadminuserissuedbookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.usermainhomeRecissuedbookname);
            authorName = itemView.findViewById(R.id.usermainhomeRecissuedauthorname);

        }
    }

    private void showdialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.activity_user_issued_book_list_dialog,null);

        final TextView txtcpy, txtduration, txtissuedate ;
        txtcpy = (TextView) mView.findViewById(R.id.txtadusisbkcopies);
        txtduration = (TextView) mView.findViewById(R.id.txtadusisbkissueduration);
        txtissuedate = (TextView) mView.findViewById(R.id.txtuserissuedbklistissuedate);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setTitle("Issued Book Details");

        bkref.child(retbkid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    txtcpy.setText(snapshot.child("issuedcopies").getValue().toString()+" copies issued");
                    txtduration.setText("Issued for "+snapshot.child("issueduration").getValue().toString());
                    txtissuedate.setText("Issued on "+snapshot.child("issuedate").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        alertDialog.show();
    }
}