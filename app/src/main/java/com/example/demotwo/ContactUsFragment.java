package com.example.demotwo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactUsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactUsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View x;
    //private View y;
    private TextView Mail1;
    //private TextView Mail2;


    public ContactUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactUsFragment newInstance(String param1, String param2) {
        ContactUsFragment fragment = new ContactUsFragment();
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
        x= inflater.inflate(R.layout.fragment_contact_us, container, false);

        Mail1 = x.findViewById(R.id.textViewMail);

        TextView feedback = (TextView) x.findViewById(R.id.textViewMail);
        feedback.setText(Html.fromHtml("<a href=\"mailto:sultanularefinpavel@gmail.com\">sultanularefinpavel@gmail.com</a>"));
        feedback.setMovementMethod(LinkMovementMethod.getInstance());

        TextView feedback2= (TextView) x.findViewById(R.id.textViewMail2);
        feedback2.setText(Html.fromHtml("<a href=\"mailto:tihamshah25599@gmail.com\">tihamshah25599@gmail.com</a>"));
        feedback2.setMovementMethod(LinkMovementMethod.getInstance());

        return x;
    }
}