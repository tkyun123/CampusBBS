package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class SearchShareBrowse extends Fragment {

    private int sort_type = 0;
    public SearchShareBrowse(int type) {
        sort_type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_share_work_type, container, false);

        TabLayout tab_layout = view.findViewById(R.id.search_select_tab);
        tab_layout.addTab(tab_layout.newTab().setText("文字"));
        tab_layout.addTab(tab_layout.newTab().setText("视频"));
        tab_layout.addTab(tab_layout.newTab().setText("音频"));

        tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = view.findViewById(R.id.search_work_type_pager);
        final SearchPagerAdapter adapter = new SearchPagerAdapter
                (getChildFragmentManager(), tab_layout.getTabCount(), sort_type);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tab_layout));
        tab_layout.addOnTabSelectedListener(
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        return view;
    }
}
