package com.example.demotwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class UserBookListBookActivity extends AppCompatActivity {
    private String receiver_book_id, bookname, authorname, cat, avCopies, issuedcopies, issuedduration, bookid;
    private Long Lavcopies, Lreqcopies;
    private Toolbar toolbar;
    private Button btnissuebook;
    private EditText edtissuedcopies;
    private Spinner spinduration;
    private String cruid;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, bookRef, bkfieldRef, issuerequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_list_book);

        mAuth = FirebaseAuth.getInstance();
        cruid = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        bookRef = FirebaseDatabase.getInstance().getReference().child("Books");
        issuerequest = userRef.child(cruid).child("Issuerequest");

        toolbar = findViewById(R.id.userbooklistbookactivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        receiver_book_id = getIntent().getExtras().get("visit_book_id").toString();
        bookid = receiver_book_id;
        //Toast.makeText(this, "Id : " + receiver_book_id, Toast.LENGTH_SHORT).show();

        bkfieldRef = bookRef.child(receiver_book_id).getRef();

        edtissuedcopies = findViewById(R.id.edtuserbklistbkactissuebkCopies);
        spinduration = findViewById(R.id.userbklistbkactspinDuration);
        spinduration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String duration = parent.getItemAtPosition(position).toString();
                issuedduration = duration;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                issuedduration = "";
            }
        });

        btnissuebook = findViewById(R.id.btnuserbklistbkactIssuebk);
        btnissuebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cruser = mAuth.getCurrentUser().getUid();
                userRef.child(cruser).child("Issuedbooks").child(receiver_book_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild("bookname")){
                                    Toast.makeText(UserBookListBookActivity.this, "This book is already issued", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Check if the book is in requested issue book list..
                                    issuerequest.child(receiver_book_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        Toast.makeText(UserBookListBookActivity.this, "This book is already requested for issue", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        if(edtissuedcopies.getText().toString().equals("")){
                                                            Toast.makeText(UserBookListBookActivity.this, "Enter copies to issue the book", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if(issuedduration.equals("")){
                                                            Toast.makeText(UserBookListBookActivity.this, "Select duration to issue the book", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        issuedcopies = edtissuedcopies.getText().toString();

                                                        // check for valid input
                                                        boolean f = true;
                                                        char ch = '#';
                                                        for(int i = 0; i < issuedcopies.length(); i++){
                                                            if(!Character.isDigit(issuedcopies.charAt(i))){
                                                                f = false;
                                                                break;
                                                            }
                                                            if(issuedcopies.charAt(i) >= '0' && issuedcopies.charAt(i) <= '9' && ch == '#'){
                                                                ch = issuedcopies.charAt(i);
                                                            }
                                                        }
                                                        if(ch == '#'){
                                                            Toast.makeText(UserBookListBookActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if(ch == '0'){
                                                            Toast.makeText(UserBookListBookActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if(f == false){
                                                            Toast.makeText(UserBookListBookActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        Lreqcopies = Long.valueOf(issuedcopies);
                                                        if(Lreqcopies > 3){
                                                            Toast.makeText(UserBookListBookActivity.this, "You can not issue more than 3 copies", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        Lavcopies = Long.valueOf(avCopies);

                                                        if(Lreqcopies < 1){
                                                            Toast.makeText(UserBookListBookActivity.this, "Enter valid number of books", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if(Lreqcopies > Lavcopies){
                                                            if(Lavcopies.equals("0")){
                                                                Toast.makeText(UserBookListBookActivity.this, "Book is not available", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Toast.makeText(UserBookListBookActivity.this, "Only " + avCopies + " copies available", Toast.LENGTH_SHORT).show();
                                                            }
                                                            return;
                                                        }
                                                        //issuebook();
                                                        reqissuebook();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    String dtError = error.getMessage().toString();
                                                    Toast.makeText(UserBookListBookActivity.this, "Error1 : " + dtError, Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                String dtError = error.getMessage().toString();
                                Toast.makeText(UserBookListBookActivity.this, "Error2 : " + dtError, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void reqissuebook() {
        String cruser = mAuth.getCurrentUser().getUid();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");
        String formattedDate = simpleDateFormat.format(calendar.getTime());

        HashMap<String,String> mp = new HashMap<>();
        mp.put("authorname",authorname);
        mp.put("bookname",bookname);
        mp.put("catagory",cat);
        mp.put("issuerequestdate",formattedDate);
        mp.put("issueduration",issuedduration);
        mp.put("issuedcopies",issuedcopies);
        mp.put("bookid",bookid);

        DatabaseReference isrq = issuerequest;
        isrq.child(bookid).setValue(mp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UserBookListBookActivity.this, "Book requested for issue", Toast.LENGTH_SHORT).show();
                            bkfieldRef.child("copies").setValue(Long.toString(Lavcopies - Lreqcopies));
                        }
                        else{
                            Toast.makeText(UserBookListBookActivity.this, "Error3 : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bkfieldRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    bookname = snapshot.child("bookname").getValue().toString();
                    authorname = snapshot.child("authorname").getValue().toString();
                    avCopies = snapshot.child("copies").getValue().toString();
                    if(snapshot.hasChild("bookid")){
                        bookid = snapshot.child("bookid").getValue().toString();
                    }
                    if(snapshot.hasChild("catagory")){
                        cat = snapshot.child("catagory").getValue().toString();
                    } else {
                        cat = "N/A";
                    }
                    fillfield();
                } else {
                    Toast.makeText(UserBookListBookActivity.this, "Unknown Error Occured", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //String dtError = error.getMessage().toString();
                //Toast.makeText(UserBookListBookActivity.this, "Error4 : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillfield() {
        TextView edtbkname, edtautrname, edtcat, edtcpy;

        edtbkname = findViewById(R.id.edtuserbklistbkactbkname);
        edtautrname = findViewById(R.id.edtuserbklistbkactautrname);
        edtcat = findViewById(R.id.edtuserbklistbkactbkcat);
        edtcpy = findViewById(R.id.edtuserbklistbkactbkcopies);

        edtbkname.setText(bookname);
        edtautrname.setText(authorname);
        edtcat.setText(cat);
        edtcpy.setText(avCopies);
    }

    private void issuebook() {
        String cruser = mAuth.getCurrentUser().getUid();

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
        mp.put("bookid",bookid);

        userRef.child(cruser).child("Issuedbooks").child(receiver_book_id).setValue(mp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UserBookListBookActivity.this, "New Book Issued", Toast.LENGTH_SHORT).show();
                            bkfieldRef.child("copies").setValue(Long.toString(Lavcopies - Lreqcopies));
                        }else{
                            Toast.makeText(UserBookListBookActivity.this, "Error5 : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        onBackPressed();
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
        // This is for the back button of toolbar to work if there is menu..
        // If there is no menu, then this is not required.
        // If menu is not added then on pressing back arrow, application will run parent activity, but in this case, i want to run the
        // fragment.. so it can be done by adding menu. And the menu is empty.
        // *** If app goes back to parent activity ( like userlogin/signup toolbar back button ) ,
        // then onStart will run default fragment. but i want to run previous fragment.
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }
}