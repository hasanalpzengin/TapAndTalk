package com.hasanalpzengin.typeandtalk;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by hasalp on 16.02.2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> categories;
    private ArrayList<String> titles;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        categories = new ArrayList<>();
        titles = new ArrayList<>();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public void setPageTitle(int position, String title){
        titles.set(position,title);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    public void addCategory(Fragment fragment, String title) {
        ((Category)fragment).setTitle(title);
        categories.add(fragment);
        titles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return categories.get(position);
    }
}
