package com.example.demotwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UserIssuedBookListBookActivity extends AppCompatActivity {
    private String receiver_book_id, bookname, authorname, cat, cruserid, issuedcopies, issuedduration, issuedon;
    private Toolbar toolbar;
    private Button btnreturnbook;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, bookRef, bkfieldRef, retbkrf, rqrtbk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_issued_book_list_book);

        mAuth = FirebaseAuth.getInstance();
        cruserid = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        bookRef = FirebaseDatabase.getInstance().getReference().child("Books");
        retbkrf = userRef.child(cruserid).child("Returnedbooks");
        rqrtbk = userRef.child(cruserid).child("Returnrequest");

        toolbar = findViewById(R.id.userissuedbooklistbookactivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Issued Book Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        receiver_book_id = getIntent().getExtras().get("visit_book_id").toString();
        //Toast.makeText(this, "" + receiver_book_id, Toast.LENGTH_SHORT).show();

        btnreturnbook = findViewById(R.id.btnissuedbookReturn);
        btnreturnbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //returnbook();
                reqreturnbook();
                //Toast.makeText(UserIssuedBookListBookActivity.this, "ret clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reqreturnbook() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");
        String formattedDate = simpleDateFormat.format(calendar.getTime());

        HashMap<String, String> mpa = new HashMap<>();
        mpa.put("authorname",authorname);
        mpa.put("bookname",bookname);
        mpa.put("catagory",cat);
        mpa.put("issuedate",issuedon);
        mpa.put("returnrequestdate",formattedDate);
        mpa.put("issueduration",issuedduration);
        mpa.put("issuedcopies",issuedcopies);
        mpa.put("bookid",receiver_book_id);

        DatabaseReference rqrt = rqrtbk;

        rqrtbk.child(receiver_book_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(UserIssuedBookListBookActivity.this, "The book is already requested for return", Toast.LENGTH_SHORT).show();
                }
                else{
                    rqrt.child(receiver_book_id).setValue(mpa)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(UserIssuedBookListBookActivity.this, "The book is requested for return", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                    else{
                                        Toast.makeText(UserIssuedBookListBookActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String dtError = error.getMessage().toString();
                Toast.makeText(UserIssuedBookListBookActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userRef.child(cruserid).child("Issuedbooks").child(receiver_book_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    bookname = snapshot.child("bookname").getValue().toString();
                    authorname = snapshot.child("authorname").getValue().toString();
                    issuedcopies = snapshot.child("issuedcopies").getValue().toString();
                    issuedduration = snapshot.child("issueduration").getValue().toString();
                    issuedon = snapshot.child("issuedate").getValue().toString();
                    if(snapshot.hasChild("catagory")){
                        cat = snapshot.child("catagory").getValue().toString();
                    } else {
                        cat = "N/A";
                    }
                    fillfield();
                } else {
                    Toast.makeText(UserIssuedBookListBookActivity.this, "Unknown Error Occured", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String dtError = error.getMessage().toString();
                Toast.makeText(UserIssuedBookListBookActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillfield() {
        TextView edtbkname, edtautrname, edtcat, edton, edtfor, edtcopies;
        edtbkname = findViewById(R.id.edtuserissuedbklistbkactbkname);
        edtautrname = findViewById(R.id.edtuserissuedbklistbkactautrname);
        edtcat = findViewById(R.id.edtuserissuedbklistbkactbkcat);
        edton = findViewById(R.id.edtuserissuedbklistbkactbkissuedon);
        edtfor = findViewById(R.id.edtuserissuedbklistbkactbkissuedfor);
        edtcopies = findViewById(R.id.edtuserissuedbklistbkactbkcopiesissued);
        edtbkname.setText(bookname);
        edtautrname.setText(authorname);
        edtcat.setText(cat);
        edton.setText(issuedon);
        String cpy = "";
        if(issuedcopies.equals("1")) cpy = " copy";
        else cpy = " copies";
        edtfor.setText(issuedduration);
        edtcopies.setText(issuedcopies + cpy);
    }

    private void returnbook() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");
        String formattedDate = simpleDateFormat.format(calendar.getTime());

        HashMap<String, String> mpa = new HashMap<>();
        mpa.put("authorname",authorname);
        mpa.put("bookname",bookname);
        mpa.put("catagory",cat);
        mpa.put("issuedate",issuedon);
        mpa.put("returndate",formattedDate);
        mpa.put("issueduration",issuedduration);
        mpa.put("issuedcopies",issuedcopies);
        mpa.put("bookid",receiver_book_id);

        String nwkey;
        nwkey = retbkrf.push().getKey();
        retbkrf.child(nwkey).setValue(mpa).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DatabaseReference rmv = userRef.child(cruserid).child("Issuedbooks").child(receiver_book_id);
                    rmv.removeValue();
                    Toast.makeText(UserIssuedBookListBookActivity.this, "Book Returned Successfully", Toast.LENGTH_SHORT).show();
                    addcopies();
                    onBackPressed();
                }
                else{
                    Toast.makeText(UserIssuedBookListBookActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addcopies() {
        DatabaseReference dt = FirebaseDatabase.getInstance().getReference().child("Books");
        dt.child(receiver_book_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String cpy = snapshot.child("copies").getValue().toString();
                    increasecopies(cpy);
                }
                else{
                    addnewbook();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String dtError = error.getMessage().toString();
                Toast.makeText(UserIssuedBookListBookActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void increasecopies(String cpy) {
        Long cr = Long.valueOf(cpy);
        Long add = Long.valueOf(issuedcopies);
        Long ans;
        if(cr + add > 10000) {
            ans = Long.valueOf("10000");
        }else{
            ans = cr + add;
        }
        bookRef.child(receiver_book_id).child("copies").setValue(Long.toString(ans));
    }

    private void addnewbook() {
        HashMap<String, String> mp = new HashMap<>();
        mp.put("authorname",authorname);
        mp.put("bookname",bookname);
        mp.put("catagory",cat);
        mp.put("copies",issuedcopies);
        mp.put("bookid",receiver_book_id);
        bookRef.child(receiver_book_id).setValue(mp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.empty_menu_issuedbook,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        // This is for the back button of toolbar to work if there is menu..
        // If there is no menu, then this is not required.
        // If menu is not added then on pressing back arrow, application will run parent activity, but in this case, i want to run the
        // fragment.. so it can be done by adding menu. And the menu is empty.
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }
}