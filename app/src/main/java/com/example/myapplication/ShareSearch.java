package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareSearch#} factory method to
 * create an instance of this fragment.
 */
public class ShareSearch extends Fragment {

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

        TabLayout tab_layout = view.findViewById(R.id.search_select_tab);
        tab_layout.addTab(tab_layout.newTab().setText(R.string.search_select_type1));
        tab_layout.addTab(tab_layout.newTab().setText(R.string.search_select_type2));

//        search_result_fragment = new ShareBrowseFollow();
//        getChildFragmentManager().beginTransaction().replace(
//                R.id.search_result, search_result_fragment).commit();

        getChildFragmentManager().beginTransaction().replace(
                R.id.search_result, new EmptySearch()).commit();

        return view;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d("log", "onPause: ");
//        getChildFragmentManager().beginTransaction().remove(
//                search_result_fragment).commit();
//    }

}