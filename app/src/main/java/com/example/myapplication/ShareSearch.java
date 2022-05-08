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

        search_spinner = view.findViewById(R.id.search_spinner);
        search_spinner.setSelection(0);
        search_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1) {
                    search_type = 1;
                }
                else if(i == 2) {
                    search_type = 2;
                }
                else {
                    search_type = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        search_edit_text = view.findViewById(R.id.search_edit_text);
        search_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    switch (search_type) {
                        case 0:
                            getChildFragmentManager().beginTransaction().replace(
                                    R.id.search_result, new SearchShareBrowse(0)).commit();
                            break;
                        case 1:
                            getChildFragmentManager().beginTransaction().replace(
                                    R.id.search_result, new SearchShareBrowse(1)).commit();
                            break;
                        case 2:
                            getChildFragmentManager().beginTransaction().replace(
                                    R.id.search_result, new SearchShareBrowse(2)).commit();
                            break;
                        default:
                            break;
                    }
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