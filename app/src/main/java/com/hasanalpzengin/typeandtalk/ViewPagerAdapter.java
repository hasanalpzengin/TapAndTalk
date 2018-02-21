package com.hasanalpzengin.typeandtalk;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by hasalp on 16.02.2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Category> categories;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        categories = new ArrayList<>();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categories.get(position).title;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    public void addCategory(Category category, String title) {
        category.setTitle(title);
        categories.add(category);
    }

    public void clear(){
        categories.clear();
    }

    public void updateAdapter(DBOperations dbOperations, String lang){
        clear();
        dbOperations.open_readable();
        categories = dbOperations.getCategories(lang);
        dbOperations.close_db();
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return categories.get(position);
    }


}
