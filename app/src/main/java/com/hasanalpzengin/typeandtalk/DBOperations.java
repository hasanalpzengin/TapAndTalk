package com.hasanalpzengin.typeandtalk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by hasalp on 15.02.2018.
 */

public class DBOperations {
    SQLiteDatabase db;
    DBHelper dbh;
    private final int OLD_DB = 1;
    private final String[] COLUMNS = {"text", "favorite", "category"};
    private final String[] CATEGORY_COLUMNS = {"name", "lang"};

    public DBOperations(Context context) {
        this.dbh = new DBHelper(context, OLD_DB);
    }

    public void open_writable(){
        db = dbh.getWritableDatabase();
    }

    public void close_db(){
        db.close();
    }

    public void open_readable(){
        db = dbh.getReadableDatabase();
    }

    public void addText(String text, String category){
        ContentValues val = new ContentValues();
        val.put(COLUMNS[0],text);
        val.put(COLUMNS[1],0);
        val.put(COLUMNS[2],category);
        db.insert(DBHelper.GENERATED_ACTIVITY_TABLE, null, val);
        Log.i("DB","addText: Added");
    }

    public void addCategory(String name, String lang){
        ContentValues val = new ContentValues();
        val.put(CATEGORY_COLUMNS[0],name);
        val.put(CATEGORY_COLUMNS[1],lang);
        db.insert(DBHelper.CATEGORY_TABLE, null, val);
    }

    public void deleteCategory(String name){
        db.delete(DBHelper.GENERATED_ACTIVITY_TABLE, COLUMNS[2]+"=?", new String[]{name});
        db.delete(DBHelper.CATEGORY_TABLE, CATEGORY_COLUMNS[0]+"=?", new String[]{name});
    }

    public ArrayList<Category> getCategories(String lang){
        ArrayList<Category> categories = new ArrayList<>();
        Cursor c = db.query(DBHelper.CATEGORY_TABLE, CATEGORY_COLUMNS, "lang=?", new String[]{lang}, null, null, null);

        c.moveToFirst();
        while(!c.isAfterLast()){
            Category category = new Category();
            category.setTitle(c.getString(0));
            categories.add(category);
            c.moveToNext();
        }

        c.close();

        return categories;
    }

    public void changeFavorite(String text){
        Cursor c = db.query(DBHelper.GENERATED_ACTIVITY_TABLE,new String[]{COLUMNS[1]}, "text LIKE ?", new String[]{text},null, null, null);
        c.moveToFirst();
        int favorite = c.getInt(0);

        ContentValues cv = new ContentValues();
        if (favorite == 0){
            cv.put(COLUMNS[1],1);

        }else{
            cv.put(COLUMNS[1],0);
        }
        db.update(DBHelper.GENERATED_ACTIVITY_TABLE,cv,"text LIKE ?", new String[]{text});
    }

    public ArrayList<Favorite> getCategoryList(String category){
        ArrayList<Favorite> favorites = new ArrayList<>();

        Cursor c = db.query(DBHelper.GENERATED_ACTIVITY_TABLE, new String[]{COLUMNS[0],COLUMNS[1]}, "category LIKE ?", new String[]{category},null,null, COLUMNS[1]+" DESC, "+COLUMNS[0]+" ASC");
        c.moveToFirst();
        while(!c.isAfterLast()){
            favorites.add(new Favorite(c.getString(0), category, c.getInt(1)));
            Log.i("fromList",category);
            c.moveToNext();
        }

        c.close();

        return favorites;
    }

    public void deleteText(String text){
        db.delete(DBHelper.GENERATED_ACTIVITY_TABLE, COLUMNS[0]+"=?", new String[]{text});
    }
}
