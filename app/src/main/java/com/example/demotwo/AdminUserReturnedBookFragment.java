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
 * Use the {@link AdminUserReturnedBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUserReturnedBookFragment extends Fragment {
    private View adusrtbk;

    private RecyclerView adminuserretdrecView;;
    private String userid, retbkid;

    private DatabaseReference usersRef, retbkref, bkref;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1 = "";
    private String mParam2 = "";

    public AdminUserReturnedBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUserReturnedBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUserReturnedBookFragment newInstance(String param1, String param2) {
        AdminUserReturnedBookFragment fragment = new AdminUserReturnedBookFragment();
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
        adusrtbk = inflater.inflate(R.layout.fragment_admin_user_returned_book, container, false);

        adminuserretdrecView = adusrtbk.findViewById(R.id.adminuserretBooklist);
        adminuserretdrecView.setLayoutManager(new LinearLayoutManager(getContext()));

        userid = mParam1;
        retbkref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Returnedbooks");
        bkref = retbkref;

        getbooks();

        return adusrtbk;
    }

    private void getbooks() {
        FirebaseRecyclerOptions<bookprofile> options =
                new FirebaseRecyclerOptions.Builder<bookprofile>()
                        .setQuery(retbkref.orderByChild("bookname"), bookprofile.class)
                        .build();
        FirebaseRecyclerAdapter<bookprofile, FragadminuserretbookViewHolder> adapter =
                new FirebaseRecyclerAdapter<bookprofile, FragadminuserretbookViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FragadminuserretbookViewHolder holder, int position, @NonNull bookprofile model) {
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
                    public FragadminuserretbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_book_list_recview_display_issued_books,parent,false);
                        FragadminuserretbookViewHolder viewHolder = new FragadminuserretbookViewHolder(view);
                        return viewHolder;
                    }
                };
        adminuserretdrecView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FragadminuserretbookViewHolder extends RecyclerView.ViewHolder{
        TextView bookName, authorName;
        public FragadminuserretbookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.usermainhomeRecissuedbookname);
            authorName = itemView.findViewById(R.id.usermainhomeRecissuedauthorname);

        }
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
}