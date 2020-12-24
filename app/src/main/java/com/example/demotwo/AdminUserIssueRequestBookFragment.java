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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUserIssueRequestBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUserIssueRequestBookFragment extends Fragment {
    private View adusisrqrt;
    private RecyclerView adminuserissuerqrecView;
    private String userid, rqbkid;
    private String rqcpy, rqdate, rqduration, rqaut, rqbkname, rqcat, curavbk;
    private Long Lcuravbk, Lrq;

    private DatabaseReference userRef, issuerqbkref, bkref, isbkref,bkfieldRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1 = "";
    private String mParam2 = "";

    public AdminUserIssueRequestBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUserIssueRequestBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUserIssueRequestBookFragment newInstance(String param1, String param2) {
        AdminUserIssueRequestBookFragment fragment = new AdminUserIssueRequestBookFragment();
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
        adusisrqrt = inflater.inflate(R.layout.fragment_admin_user_issue_request_book, container, false);

        userid = mParam1;
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        issuerqbkref = userRef.child(userid).child("Issuerequest");
        isbkref = userRef.child(userid).child("Issuedbooks");
        bkfieldRef = FirebaseDatabase.getInstance().getReference().child("Books");

        bkref = issuerqbkref;

        adminuserissuerqrecView = adusisrqrt.findViewById(R.id.adminuserissuerqBooklist);
        adminuserissuerqrecView.setLayoutManager(new LinearLayoutManager(getContext()));

        return adusisrqrt;
    }

    @Override
    public void onStart() {
        super.onStart();
        getbooks();
    }

    private void getbooks() {
        FirebaseRecyclerOptions<bookprofile> options =
                new FirebaseRecyclerOptions.Builder<bookprofile>()
                        .setQuery(issuerqbkref.orderByChild("bookname"), bookprofile.class)
                        .build();
        FirebaseRecyclerAdapter<bookprofile, FragadminuserissuerqbookViewHolder> adapter =
                new FirebaseRecyclerAdapter<bookprofile, FragadminuserissuerqbookViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FragadminuserissuerqbookViewHolder holder, int position, @NonNull bookprofile model) {
                        holder.bookName.setText(model.getBookname());
                        holder.authorName.setText(model.getAuthorname());

                        // onClickListener on users list.
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rqbkid = getRef(position).getKey();
                                showdialog();
                                //Toast.makeText(getContext(), "Clicked " + rqbkid, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FragadminuserissuerqbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_us_is_rq_bk_display, parent, false);
                        FragadminuserissuerqbookViewHolder viewHolder = new FragadminuserissuerqbookViewHolder(view);
                        return viewHolder;
                    }
                };
        adminuserissuerqrecView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FragadminuserissuerqbookViewHolder extends RecyclerView.ViewHolder{
        TextView bookName, authorName;
        public FragadminuserissuerqbookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.usermainhomeRecissuerqbookname);
            authorName = itemView.findViewById(R.id.usermainhomeRecissuerqauthorname);

        }
    }

    private void showdialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.activity_user_is_rq_bk_list_dialog,null);

        final TextView txtcpy, txtduration, txtissuedate ;
        final Button btnapp, btncan;
        btnapp = (Button) mView.findViewById(R.id.btnadusisrqapprove);
        btncan = (Button) mView.findViewById(R.id.btnadusisrqcancel);
        txtcpy = (TextView) mView.findViewById(R.id.txtadusisrqbkcopies);
        txtduration = (TextView) mView.findViewById(R.id.txtadusisrqbkissueduration);
        txtissuedate = (TextView) mView.findViewById(R.id.txtadusrqbkrqdate);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setTitle("Book Details");
        String cruser = userid, authorname, bookname, cat,issuedcopies,issuedduration;

        bkref.child(rqbkid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    rqcpy = snapshot.child("issuedcopies").getValue().toString();
                    rqduration = snapshot.child("issueduration").getValue().toString();
                    rqdate = snapshot.child("issuerequestdate").getValue().toString();
                    rqbkname = snapshot.child("bookname").getValue().toString();
                    rqaut = snapshot.child("authorname").getValue().toString();
                    rqcat = snapshot.child("catagory").getValue().toString();

                    txtcpy.setText(snapshot.child("issuedcopies").getValue().toString()+" copies requested");
                    txtduration.setText("Issue requested for "+snapshot.child("issueduration").getValue().toString());
                    txtissuedate.setText("Issue requested on "+snapshot.child("issuerequestdate").getValue().toString());
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
                issuebook(rqaut,rqbkname,rqcat,rqcpy,rqduration);
                alertDialog.dismiss();
            }
        });

        btncan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelreq(rqcpy);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void cancelreq(String rqcpy) {
        bkfieldRef.child(rqbkid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    curavbk = snapshot.child("copies").getValue().toString();
                    Lcuravbk = Long.valueOf(curavbk);
                    Lrq = Long.valueOf(rqcpy);
                    incbk(Long.toString(Lcuravbk+Lrq));
                    rmvdata(rqbkid);
                    Toast.makeText(getContext(), "Issue request cancelled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String dtError = error.getMessage().toString();
                Toast.makeText(getContext(), "Error : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void incbk(String toString) {
        DatabaseReference dt = bkfieldRef;
        dt.child(rqbkid).child("copies").setValue(toString);
    }

    private void issuebook(String authorname,String bookname,String cat,String issuedcopies,String issuedduration) {
        String cruser = userid;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");
        String formattedDate = simpleDateFormat.format(calendar.getTime());

        HashMap<String,String> mp = new HashMap<>();
        mp.put("authorname",authorname);
        mp.put("bookname",bookname);
        mp.put("catagory",cat);
        mp.put("issuedate",formattedDate);
        mp.put("issueduration",issuedduration);
        mp.put("issuedcopies",issuedcopies);
        mp.put("bookid",rqbkid);

        userRef.child(cruser).child("Issuedbooks").child(rqbkid).setValue(mp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Request approved", Toast.LENGTH_SHORT).show();
                            rmvdata(rqbkid);
                        }else{
                            Toast.makeText(getContext(), "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void rmvdata(String r) {
        DatabaseReference dt = issuerqbkref.child(rqbkid);
        dt.removeValue();
        getbooks();
    }
}