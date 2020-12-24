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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUserReturnRequestBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUserReturnRequestBookFragment extends Fragment {
    private View adusrtrq;
    private RecyclerView adminuserrtrqrecView;
    private String userid, rtbkid;
    private String rqcpy, rqdate, rqduration, rqaut, rqbkname, rqcat, curavbk, rqisdate;
    private Long Lcuravbk, Lrq;

    private DatabaseReference userRef, rtrqbkref, bkref, rtbkref,bkfieldRef,issuedbkref;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1 = "";
    private String mParam2 = "";

    public AdminUserReturnRequestBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUserReturnRequestBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUserReturnRequestBookFragment newInstance(String param1, String param2) {
        AdminUserReturnRequestBookFragment fragment = new AdminUserReturnRequestBookFragment();
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
        adusrtrq = inflater.inflate(R.layout.fragment_admin_user_return_request_book, container, false);

        userid = mParam1;
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rtrqbkref = userRef.child(userid).child("Returnrequest");
        rtbkref = userRef.child(userid).child("Returnedbooks");
        issuedbkref = userRef.child(userid).child("Issuedbooks");
        bkfieldRef = FirebaseDatabase.getInstance().getReference().child("Books");
        bkref = rtrqbkref;

        adminuserrtrqrecView = adusrtrq.findViewById(R.id.adminuserrtrqBooklist);
        adminuserrtrqrecView.setLayoutManager(new LinearLayoutManager(getContext()));

        return adusrtrq;
    }

    @Override
    public void onStart() {
        super.onStart();
        getbooks();
    }

    private void getbooks() {
        FirebaseRecyclerOptions<bookprofile> options =
                new FirebaseRecyclerOptions.Builder<bookprofile>()
                .setQuery(rtrqbkref.orderByChild("bookname"), bookprofile.class)
                .build();
        FirebaseRecyclerAdapter<bookprofile, FragadminuserrtrqbookViewHolder> adapter =
                new FirebaseRecyclerAdapter<bookprofile, FragadminuserrtrqbookViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FragadminuserrtrqbookViewHolder holder, int position, @NonNull bookprofile model) {
                        holder.bookName.setText(model.getBookname());
                        holder.authorName.setText(model.getAuthorname());

                        // onClickListener on users list.
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rtbkid = getRef(position).getKey();
                                showdialog();
                                //Toast.makeText(getContext(), "rt clicked " + rtbkid, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FragadminuserrtrqbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_us_is_rq_bk_display, parent, false);
                        FragadminuserrtrqbookViewHolder viewHolder = new FragadminuserrtrqbookViewHolder(view);
                        return viewHolder;
                    }
                };
        adminuserrtrqrecView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FragadminuserrtrqbookViewHolder extends RecyclerView.ViewHolder{
        TextView bookName, authorName;
        public FragadminuserrtrqbookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.usermainhomeRecissuerqbookname);
            authorName = itemView.findViewById(R.id.usermainhomeRecissuerqauthorname);

        }
    }

    private void showdialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.activity_user_rt_rq_bk_list_dialog,null);

        final TextView txtcpy, txtduration, txtissuedate, txtrtrqdate;
        final Button btnapp, btncan;

        btnapp = (Button) mView.findViewById(R.id.btnadusrtrqapprove);
        btncan = (Button) mView.findViewById(R.id.btnadusrtrqcancel);
        txtcpy = (TextView) mView.findViewById(R.id.txtadusrtrqbkcopies);
        txtduration = (TextView) mView.findViewById(R.id.txtadusrtrqbkissueduration);
        txtissuedate = (TextView) mView.findViewById(R.id.txtadusrtrqbkissueddate);
        txtrtrqdate = (TextView) mView.findViewById(R.id.txtadusrtrqbkrtrqdate);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setTitle("Book Details");

        bkref.child(rtbkid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    rqaut = snapshot.child("authorname").getValue().toString();
                    rqbkname = snapshot.child("bookname").getValue().toString();
                    rqcat = snapshot.child("catagory").getValue().toString();
                    rqisdate = snapshot.child("issuedate").getValue().toString();
                    rqcpy = snapshot.child("issuedcopies").getValue().toString();
                    rqduration = snapshot.child("issueduration").getValue().toString();
                    rqdate = snapshot.child("returnrequestdate").getValue().toString();

                    txtcpy.setText(rqcpy + " copies issued");
                    txtduration.setText("Issued for " + rqduration);
                    txtissuedate.setText("Issued on " + rqisdate);
                    txtrtrqdate.setText("Requested for return on " + rqdate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String dtError = error.getMessage().toString();
                Toast.makeText(getContext(), "Error : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });

        btnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rtbk(rqaut,rqbkname,rqcat,rqisdate,rqcpy,rqduration,rqdate);
                alertDialog.dismiss();
            }
        });

        btncan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
    private void rtbk(String authorname,String bookname,String cat,String issuedate, String issuedcopies,String issuedduration, String rtdate) {
        String nwkey = rtbkref.push().getKey();

        HashMap<String,String> mp = new HashMap<>();
        mp.put("authorname",authorname);
        mp.put("bookname",bookname);
        mp.put("bookid",rtbkid);
        mp.put("catagory",cat);
        mp.put("issuedate",issuedate);
        mp.put("issuedcopies",issuedcopies);
        mp.put("issueduration",issuedduration);
        mp.put("returndate",rtdate);

        rtbkref.child(nwkey).setValue(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Request approved", Toast.LENGTH_SHORT).show();
                    DatabaseReference dt = rtrqbkref.child(rtbkid);
                    dt.removeValue();
                    DatabaseReference isdt = issuedbkref.child(rtbkid);
                    isdt.removeValue();
                    getbooks();
                }
            }
        });
    }
}