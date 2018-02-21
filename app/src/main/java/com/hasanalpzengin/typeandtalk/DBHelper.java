package com.hasanalpzengin.typeandtalk;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hasal on 28-Jan-18.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "typeAndTalk";
    public static final String GENERATED_ACTIVITY_TABLE = "favorites";
    public static final String CATEGORY_TABLE = "categories";
    private String[] COLUMNS = {"text", "favorite", "category"};
    private final String[] CATEGORY_COLUMNS = {"name", "lang"};
    Context context;
    public DBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create generated_activity_table
        String sql = "CREATE TABLE IF  NOT EXISTS " + GENERATED_ACTIVITY_TABLE + " (text TEXT, favorite INTEGER, category TEXT)";
        Log.d("SQL: ", sql);
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE TABLE IF  NOT EXISTS " + CATEGORY_TABLE + " (name TEXT, lang TEXT)";
        Log.d("SQL: ", sql);
        sqLiteDatabase.execSQL(sql);
        setDefaultCategories(sqLiteDatabase);
        setDefaults(sqLiteDatabase);
    }

    public void setDefaultCategories(SQLiteDatabase sqLiteDatabase){
        //english resources
        String[] categories = context.getResources().getStringArray(R.array.categories);
        for (String category: categories){
            ContentValues val = new ContentValues();
            val.put(CATEGORY_COLUMNS[0], category);
            val.put(CATEGORY_COLUMNS[1],"en");
            sqLiteDatabase.insert(DBHelper.CATEGORY_TABLE, null, val);
        }
        //turkish resources
        String[] katagoriler = context.getResources().getStringArray(R.array.katagoriler);
        for (String category: katagoriler){
            ContentValues val = new ContentValues();
            val.put(CATEGORY_COLUMNS[0], category);
            val.put(CATEGORY_COLUMNS[1],"tr");
            sqLiteDatabase.insert(DBHelper.CATEGORY_TABLE, null, val);
        }
    }

    private void setDefaults(SQLiteDatabase database) {
        String[] defaultArray = context.getResources().getStringArray(R.array.defaultTexts);
        for (String text: defaultArray){
            String[] textSplited = text.split("@");
            ContentValues val = new ContentValues();
            val.put(COLUMNS[0],textSplited[0]);
            val.put(COLUMNS[1],0);
            val.put(COLUMNS[2],textSplited[1]);
            database.insert(DBHelper.GENERATED_ACTIVITY_TABLE, null, val);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GENERATED_ACTIVITY_TABLE);
        onCreate(sqLiteDatabase);
    }

}

