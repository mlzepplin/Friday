package com.example.rishabh.friday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by rishabh on 06/02/17.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDB.db";
    public static final String TABLE_NAME = "label_table";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_DESCRIPTION = "description";

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TYPE + " text, " + COLUMN_DESCRIPTION + " text, " + COLUMN_LABEL + " text);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertLabel(String type, String description, String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_LABEL, label);
        contentValues.put(COLUMN_DESCRIPTION, description);

        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_ID + "=" + id + ";", null);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public boolean updateLabel(String type,String label, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LABEL, label);
        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_DESCRIPTION, description);
        db.update(TABLE_NAME, contentValues, COLUMN_LABEL + " = ? ", new String[]{label});
        return true;
    }

    public Integer deleteLabel(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }
    public Integer deleteLabel(String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_LABEL + " = ? ",
                new String[]{label});
    }

    public ArrayList<String> getAllLabelsofAType(String type) {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE " + COLUMN_TYPE + "=\"" + type + "\" ORDER BY " + COLUMN_LABEL + ";", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(COLUMN_LABEL)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }


    public ArrayList<String> getAllLabels() {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select distinct " + COLUMN_LABEL + " from " + TABLE_NAME + " order by " + COLUMN_LABEL + ";", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(COLUMN_LABEL)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }


    public boolean searchLabel(String label) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean find = false;
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + ";", null);

        res.moveToFirst();
        while(!res.isAfterLast()){

            if( label.equals(res.getString(res.getColumnIndex(COLUMN_LABEL)))){
               // find = res.getString(res.getColumnIndex(COLUMN_TYPE));
                find = true;
                break;
            }
            res.moveToNext();
        }
        res.close();

        return find;

    }
    public String getDescription(String label){

        SQLiteDatabase db = this.getReadableDatabase();
        String description = new String();
        Cursor res = db.rawQuery( "select * from " + TABLE_NAME + " WHERE " + COLUMN_LABEL + " = \"" + label + "\";", null );
        //meaning = res.getString(0);
        res.moveToFirst();
        while(!res.isAfterLast()){

            description = res.getString(res.getColumnIndex(COLUMN_DESCRIPTION));
            res.moveToNext();
        }
        res.close();

        return description;
        //return res.getString(res.getColumnIndex(COLUMN_MEANING));
    }




}
