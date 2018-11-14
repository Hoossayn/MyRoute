package com.example.android.myroute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.text.StringSubstitutor;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ROUTES";
    public static final String TABLE_NAME = "routes_table";
    public static final String _id = "_id";
    public static final String col_1 = "DESCRIPTION";
    public static final String col_2 = "ROUTES";
    public static final String col_3 = "DATE";
    public static final String col_4 = "TIME";
    public static final String col_5 = "INITIAL";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DESCRIPTION TEXT, " +
                "ROUTES TEXT, DATE TEXT, TIME TEXT, INITIAL TEXT)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String description, ArrayList<String> routes, String date, String time, String initial) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1, description);
        contentValues.put(col_2, String.valueOf(routes));
        contentValues.put(col_3, String.valueOf(date));
        contentValues.put(col_4, String.valueOf(time));
        contentValues.put(col_5, String.valueOf(initial));


        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) return false;
        else return true;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME , null);
        return res;
    }

    public Cursor getSomeData(String softvalue) {
        String findquery = StringSubstitutor.replace("select * from ${tableName} WHERE DESCRIPTION LIKE '%${query}%' order by DATE DESC",
                ImmutableMap.of("tableName", TABLE_NAME, "query", softvalue));
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(findquery, null);
        return res;
    }

    public Cursor getArrayData(ArrayList<LatLng> id) {
        String findquery = StringSubstitutor.replace("select ROUTES from ${tableName} WHERE _id = '%${query}%' ",
                ImmutableMap.of("tableName", TABLE_NAME, "query", id));
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(findquery, null);
        return res;
    }

    public ArrayList<String> getAllAddress() {
        ArrayList<String> allOrganizations = new
                ArrayList<String>();
        String value = "select ADDRESS" + " from " + TABLE_NAME;
        Cursor cursor = getReadableDatabase().rawQuery(value, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                allOrganizations.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return allOrganizations;
    }
}