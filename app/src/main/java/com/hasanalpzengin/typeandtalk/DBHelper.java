package com.hasanalpzengin.typeandtalk;

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

    public DBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create generated_activity_table
        String sql = "CREATE TABLE IF  NOT EXISTS " + GENERATED_ACTIVITY_TABLE + " (text TEXT, favorite INTEGER, category TEXT)";
        Log.d("SQL: ", sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GENERATED_ACTIVITY_TABLE);
        onCreate(sqLiteDatabase);
    }

}

