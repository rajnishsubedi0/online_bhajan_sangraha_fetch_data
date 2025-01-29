package com.rkant.bhajanapp.FirstActivities;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.rkant.bhajanapp.Favourites.FavouriteBookmarked;
import com.rkant.bhajanapp.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DB_Handler extends SQLiteOpenHelper {
    ArrayList<DataHolderForDB> arrayList_DataHolderForDB =new ArrayList<>();
    Context context;
    ArrayList<DataHolder> arrayListDataHolder =new ArrayList<>();
    private static final String DB_NAME="bhajan_list";
    private static final int DB_VERSION=1;
    //Favourite bhajan bookmark db name
    private static final String DB_TABLE_FAVOURITE="bhajan_favourite";
    //Main activity bhajan list db name
    private static final String DB_TABLE_BHAJAN_MENU_LIST="bhajan_list";
    private static final String DB_TABLE_BHAJANS="bhajans_table";
    private static final String SERIAL_NO="serial_no";
    private static final String BHAJAN_ID="bhajan_id";
    private static final String BHAJAN_NAME_NEPALI="bhajan_name_nepali";
    private static final String BHAJAN_NAME_ENGLISH="bhajan_name_english";

        public DB_Handler(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context=context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String query="CREATE TABLE "+DB_TABLE_FAVOURITE+" ("+
                    SERIAL_NO+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    BHAJAN_ID+" TEXT)";
            String query2="CREATE TABLE "+DB_TABLE_BHAJAN_MENU_LIST+" ("+
                    SERIAL_NO+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    BHAJAN_ID+" TEXT, "+
                    BHAJAN_NAME_NEPALI+" TEXT, "+
                    BHAJAN_NAME_ENGLISH+" TEXT)";
            String query3="CREATE TABLE "+DB_TABLE_BHAJANS+" ("+
                    SERIAL_NO+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    BHAJAN_ID+" VARCHAR, "+
                    BHAJAN_NAME_NEPALI+" VARCHAR)";
            sqLiteDatabase.execSQL(query);
            sqLiteDatabase.execSQL(query2);
            sqLiteDatabase.execSQL(query3);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DB_TABLE_BHAJAN_MENU_LIST);
            onCreate(sqLiteDatabase);
        }
         public boolean checkIfDB_Exists(String DB_TABLE_PASSED_VALUE_FROM_ANOTHER_FUNCTION){
            SQLiteDatabase db=this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE_PASSED_VALUE_FROM_ANOTHER_FUNCTION, null);
            cursor.moveToFirst();
            try{
                if ("1".equals(cursor.getString(0))) {
                    Toast.makeText(context, "Already Loded", Toast.LENGTH_SHORT).show();
                    db.close();
                    cursor.close();
                    return true;
                }} catch (Exception ignored){

            }

            db.close();
            return false;
        }
    public boolean fetchBhajansFromCloud(JSONArray jsonArray) throws JSONException {

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE_BHAJANS, null);
        cursor.moveToFirst();
        try{
            if ("1".equals(cursor.getString(0))) {
                Toast.makeText(context, "Already Loded", Toast.LENGTH_SHORT).show();
                db.close();
                cursor.close();
                return true;
            }} catch (Exception ignored){

        }



        db.close();
        db=this.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject= null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String nepali_bhajan= null;
            try {
                nepali_bhajan = jsonObject.getString("bhajan");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String id= null;
            try {
                id = jsonObject.getString("id");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }



            contentValues.put(BHAJAN_ID,id);
            contentValues.put(BHAJAN_NAME_NEPALI,nepali_bhajan);
            db.insert(DB_TABLE_BHAJANS,null,contentValues);
        };

        db.close();
        Toast.makeText(context, "Data Added For second activity", Toast.LENGTH_SHORT).show();
        return false;
    }
        public boolean addDataFromCloud(JSONArray jsonArray) throws JSONException {

            SQLiteDatabase db=this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE_BHAJAN_MENU_LIST, null);
            cursor.moveToFirst();
            try{
            if ("1".equals(cursor.getString(0))) {
                Toast.makeText(context, "Already Loded", Toast.LENGTH_SHORT).show();
                db.close();
                cursor.close();
                return true;
            }} catch (Exception ignored){

            }



            db.close();
            db=this.getReadableDatabase();
            ContentValues contentValues=new ContentValues();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject= null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                String nepali_bhajan= null;
                try {
                    nepali_bhajan = jsonObject.getString("bhajan_nepali");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                String bhajan_english_for_search= null;
                try {
                    bhajan_english_for_search = jsonObject.getString("bhajan_english");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                String id= null;
                try {
                    id = jsonObject.getString("id");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }



            contentValues.put(BHAJAN_ID,id);
            contentValues.put(BHAJAN_NAME_ENGLISH,bhajan_english_for_search);
            contentValues.put(BHAJAN_NAME_NEPALI,nepali_bhajan);
            db.insert(DB_TABLE_BHAJAN_MENU_LIST,null,contentValues);
        };

            db.close();
            Toast.makeText(context, "Data Added", Toast.LENGTH_SHORT).show();
            return false;
        }
        public void addData(String str){
            if(checkIfExists(str)){
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues contentValues=new ContentValues();
            contentValues.put(BHAJAN_ID,str);
            db.insert(DB_TABLE_FAVOURITE,null,contentValues);
            db.close();
            Toast.makeText(context, "Bhajan Added", Toast.LENGTH_SHORT).show();
            }


        }
        public boolean checkIfExists(String str) {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE_FAVOURITE, null);
            while (cursor.moveToNext()) {
                if (str.equals(cursor.getString(1))) {
                    Toast.makeText(context, "Bhajan already been added", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
            db.close();
            cursor.close();
            return true;

        }
        public void fetchDbData(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DB_TABLE_FAVOURITE,null);
        // ArrayList<DataHolder> arrayList=new ArrayList<>();
        while (cursor.moveToNext()){
            FavouriteBookmarked.publicArrayList.add(new DataHolder(cursor.getString(1)));
           // FavouriteBookmarked.publicRecyclerAdapter.notifyDataSetChanged();
        }
        cursor.close();
    }

    public ArrayList<DataHolder> fetchActualBhajanData(String position) throws JSONException {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DB_TABLE_BHAJANS+" WHERE "+BHAJAN_ID+"='"+position+"'",new String[]{});
        cursor.moveToFirst();
        JSONArray jsonArray=new JSONArray(cursor.getString(2));
        for (int i=0;i< jsonArray.length();i++){
            arrayListDataHolder.add(new DataHolder(jsonArray.getString(i)));
        }

        cursor.close();
        return arrayListDataHolder;
    }
    public ArrayList<DataHolderForDB> fetchDbDataFromDBForBhajanList(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DB_TABLE_BHAJAN_MENU_LIST,null);

        while (cursor.moveToNext()){
            arrayList_DataHolderForDB.add(new DataHolderForDB(cursor.getString(2),cursor.getString(3),cursor.getString(1)));

        }
        cursor.close();
        return arrayList_DataHolderForDB;
    }
    public void deleteCourse(String bhajan_id_name) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(DB_TABLE_BHAJAN_MENU_LIST, "bhajan_id=?", new String[]{bhajan_id_name});
        FavouriteBookmarked.publicArrayList.clear();
        FavouriteBookmarked.nepaliNumberArrayList.clear();
        FavouriteBookmarked.notPublicArrayList.clear();
        fetchDbData();
        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
        db.close();
    }

    }

