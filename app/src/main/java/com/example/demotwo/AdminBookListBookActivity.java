package com.example.demotwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AdminBookListBookActivity extends AppCompatActivity {
    private String receiver_book_id , bookname, authorname, cat, avCopies;
    private Button btnaddcopies;
    private Toolbar toolbar;
    private Long avCpy, adCpy;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, bookRef, bkfieldRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_book_list_book);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        bookRef = FirebaseDatabase.getInstance().getReference().child("Books");

        toolbar = findViewById(R.id.adminbooklistbookactivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        receiver_book_id = getIntent().getExtras().get("visit_book_id").toString();

        btnaddcopies = findViewById(R.id.btnadminbklistbkactaddcopies);
        btnaddcopies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addmore();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        bkfieldRef = bookRef.child(receiver_book_id).getRef();
        bkfieldRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    bookname = snapshot.child("bookname").getValue().toString();
                    authorname = snapshot.child("authorname").getValue().toString();
                    avCopies = snapshot.child("copies").getValue().toString();
                    if(snapshot.hasChild("catagory")){
                        cat = snapshot.child("catagory").getValue().toString();
                    } else {
                        cat = "N/A";
                    }
                    fillfield();
                } else {
                    Toast.makeText(AdminBookListBookActivity.this, "Unknown Error Occured", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //String dtError = error.getMessage().toString();
                //Toast.makeText(AdminBookListBookActivity.this, "Error : " + dtError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addmore() {
        EditText edtaddcpy ;
        edtaddcpy = findViewById(R.id.edtadminbklistbkactissuebkCopies);
        String cpy = edtaddcpy.getText().toString();
        if(cpy.equals("")){
            Toast.makeText(this, "Please enter number of copies", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean f = true;
        char ch = '#';
        for(int i = 0; i < cpy.length(); i++){
            if(!Character.isDigit(cpy.charAt(i))){
                f = false;
                break;
            }
            if(cpy.charAt(i) >= '0' && cpy.charAt(i) <= '9' && ch == '#'){
                ch = cpy.charAt(i);
            }
        }
        if(ch == '#'){
            Toast.makeText(this, "Please enter valid number", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ch == '0'){
            Toast.makeText(this, "Please enter valid number", Toast.LENGTH_SHORT).show();
            return;
        }
        if(f == false){
            Toast.makeText(this, "Please enter valid number", Toast.LENGTH_SHORT).show();
            return;
        }
        avCpy = Long.valueOf(avCopies);
        adCpy = Long.valueOf(cpy);
        if(adCpy + adCpy > 10000){
            Toast.makeText(this, "Space not available", Toast.LENGTH_SHORT).show();
            return;
        }
        bookRef.child(receiver_book_id).child("copies").setValue(Long.toString(adCpy+avCpy)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    edtaddcpy.setText("");
                    Toast.makeText(AdminBookListBookActivity.this, "new "+cpy+" copies added", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AdminBookListBookActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillfield() {
        TextView edtbkname, edtautrname, edtcat, edtcpy;

        edtbkname = findViewById(R.id.edtadminbklistbkactbkname);
        edtautrname = findViewById(R.id.edtadminbklistbkactautrname);
        edtcat = findViewById(R.id.edtadminbklistbkactbkcat);
        edtcpy = findViewById(R.id.edtadminbklistbkactbkcopies);

        edtbkname.setText(bookname);
        edtautrname.setText(authorname);
        edtcat.setText(cat);
        edtcpy.setText(avCopies);
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