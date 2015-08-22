package com.example.brian.vehiclerecallinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 07/08/15.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "recallDB.db";
    private static final String TABLE_RECALLINFO = "recallinfo";
    private static final String TABLE_TOLLFREE = "tollfree";
    private static final String COL_ID = "id";
    private static final String COL_MAKE = "make";
    private static final String COL_YEAR = "year";
    private static final String COL_NAME = "name";
    private static final String COL_PART = "part";
    private static final String COL_DESC = "desc";
    private static final String COL_TOLLFREE = "tollfree";

    public MyDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "create table " + TABLE_RECALLINFO + "("
                        + COL_MAKE + " text, "
                        + COL_YEAR + " text, "
                        + COL_NAME + " text, "
                        + COL_PART + " text, "
                        + COL_DESC + " text)";

        String firstRow = "insert into " + TABLE_RECALLINFO + "("
                        + COL_MAKE + ", "  + COL_YEAR + ", " + COL_NAME + ", " + COL_PART + ") "
                        + "values ('Make', 'Year', 'Name', 'Part')";
        db.execSQL(create);
        db.execSQL(firstRow);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Wipe older tables if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECALLINFO);
            // Create tables again
            onCreate(db);
        }
    }

    public void addInfo(RecallInfo ri){
        ContentValues values = new ContentValues();
        values.put(COL_MAKE, ri.getMake());
        values.put(COL_YEAR, ri.getYear());
        values.put(COL_NAME, ri.getName());
        values.put(COL_PART, ri.getPart());
        values.put(COL_DESC, ri.getDesc());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_RECALLINFO, null, values);
        db.close();
    }

    public List<String> findMakesFromRecallInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> makes = new ArrayList<String>();

        String query = "select distinct " + COL_MAKE + " from " + TABLE_RECALLINFO;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                String make = cursor.getString(cursor.getColumnIndex(COL_MAKE));
                makes.add(make);
            }while(cursor.moveToNext());
        }

        return makes;
    }

    public List<String> findMakesFromTollfree(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> makes = new ArrayList<String>();

        String query = "select distinct " + COL_MAKE + " from " + TABLE_TOLLFREE;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                String make = cursor.getString(cursor.getColumnIndex(COL_MAKE));
                makes.add(make);
            }while(cursor.moveToNext());
        }

        return makes;
    }

    public List<String> findYears(String make){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> years = new ArrayList<String>();

        String query = "select distinct " + COL_YEAR + " from " + TABLE_RECALLINFO
                        + " where lower(" + COL_MAKE + ") = lower('" + make + "')"
                        + " order by " + COL_YEAR + " desc";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                String year = cursor.getString(cursor.getColumnIndex(COL_YEAR));
                years.add(year);
            }while(cursor.moveToNext());
        }

        return years;
    }

    public List<String> findNames(String make, String year){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> names = new ArrayList<String>();

        String query = "select distinct " + COL_NAME + " from " + TABLE_RECALLINFO
                        + " where lower(" + COL_MAKE + ") = lower('" + make + "') and "
                        + COL_YEAR + " = '" + year + "'";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                names.add(name);
            }while(cursor.moveToNext());
        }

        return names;
    }

    public List<String> findParts(String make, String year, String name){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> parts = new ArrayList<String>();

        String query = "select distinct " + COL_PART + " from " + TABLE_RECALLINFO
                        + " where lower(" + COL_MAKE + ") = lower('" + make + "') and "
                        + COL_YEAR + " = '" + year + "' and "
                        + COL_NAME + " = '" + name + "'";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                String part = cursor.getString(cursor.getColumnIndex(COL_PART));
                parts.add(part);
            }while(cursor.moveToNext());
        }

        return parts;
    }

    public String findInfo(String make, String year, String name, String part){
        SQLiteDatabase db = this.getReadableDatabase();
        String desc = null;

        Log.d("args", make+year+name+part);

        String query = "select * from " + TABLE_RECALLINFO
                + " where lower(" + COL_MAKE + ") = lower('" + make + "') and "
                + COL_YEAR + " = '" + year + "' and "
                + COL_NAME + " = '" + name + "' and "
                + COL_PART + " = '" + part + "'";

        Log.d("query?", query);

        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null)
            cursor.moveToFirst();

        desc = cursor.getString(cursor.getColumnIndex(COL_DESC));

        return desc;
    }

    public int countRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select count(*) from " + TABLE_RECALLINFO;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        db.close();

        return result;
    }

    public void deleteAllRowsInRecallinfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_RECALLINFO, null, null);

        String firstRow = "insert into " + TABLE_RECALLINFO + "("
                + COL_MAKE + ", "  + COL_YEAR + ", " + COL_NAME + ", " + COL_PART + ") "
                + "values ('Make', 'Year', 'Name', 'Part')";

        db.execSQL(firstRow);
    }

    public String findTollFree(String make){
        SQLiteDatabase db = this.getReadableDatabase();
        String tollfree = null;

        String query = "select " + COL_TOLLFREE + " from " + TABLE_TOLLFREE
                + " where " + COL_MAKE + " = '" + make + "'";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null)
            cursor.moveToFirst();

        tollfree = cursor.getString(cursor.getColumnIndex(COL_TOLLFREE));

        return tollfree;
    }
}
