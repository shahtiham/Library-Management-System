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
 * Use the {@link UserBookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBookListFragment extends Fragment {
    private View usermainbooklistView;

    private RecyclerView bookrecView;

    private DatabaseReference bookRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserBookListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserBookListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserBookListFragment newInstance(String param1, String param2) {
        UserBookListFragment fragment = new UserBookListFragment();
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
        usermainbooklistView = inflater.inflate(R.layout.fragment_user_book_list, container, false);

        bookrecView = usermainbooklistView.findViewById(R.id.usermainhomefragBooklist);
        bookrecView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookRef = FirebaseDatabase.getInstance().getReference().child("Books");

        searchbook("");

        return usermainbooklistView;
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
        FirebaseRecyclerAdapter<bookprofile, FragbookViewHolder> adapter =
                new FirebaseRecyclerAdapter<bookprofile, FragbookViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FragbookViewHolder holder, int position, @NonNull bookprofile model) {
                        holder.bookName.setText(model.getBookname());
                        holder.authorName.setText(model.getAuthorname());

                        // onClickListener on users list.
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_book_id = getRef(position).getKey();
                                Intent bookprofileintent = new Intent(getContext(),UserBookListBookActivity.class);
                                bookprofileintent.putExtra("visit_book_id",visit_book_id);
                                startActivity(bookprofileintent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FragbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_book_list_recview_display_books,parent,false);
                        FragbookViewHolder viewHolder = new FragbookViewHolder(view);
                        return viewHolder;
                    }
                };
        bookrecView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FragbookViewHolder extends RecyclerView.ViewHolder{
        TextView bookName, authorName;
        public FragbookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.usermainhomeRecbookname);
            authorName = itemView.findViewById(R.id.usermainhomeRecauthorname);

        }
    }
}