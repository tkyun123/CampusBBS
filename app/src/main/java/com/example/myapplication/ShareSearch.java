package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareSearch#} factory method to
 * create an instance of this fragment.
 */
public class ShareSearch extends Fragment {

    public Spinner search_spinner;
    private int search_type = 0;

    public EditText search_edit_text;

    public Fragment search_result_fragment;
    Fragment empty_fragment = new Fragment();
    public ShareSearch() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_search, container, false);

        search_edit_text = view.findViewById(R.id.search_edit_text);
        search_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search_content = search_edit_text.getText().toString();
                    getChildFragmentManager().beginTransaction().replace(
                            R.id.search_result, new SearchShareBrowse(search_content)).commit();
                    return false;
                }
                return false;
            }
        });

        getChildFragmentManager().beginTransaction().replace(
                R.id.search_result, new EmptySearch()).commit();

        return view;
    }

}