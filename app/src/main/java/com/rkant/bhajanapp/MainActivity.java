package com.rkant.bhajanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rkant.bhajanapp.Favourites.FavouriteBookmarked;
import com.rkant.bhajanapp.FirstActivities.DB_Handler;
import com.rkant.bhajanapp.FirstActivities.DataHolderForDB;
import com.rkant.bhajanapp.FirstActivities.RecyclerAdapter;
import com.rkant.bhajanapp.secondActivities.DataHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MenuItem menuItem,favourite_bhajan_menuItem;
    SearchView searchView;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerCustomAdapter;
    Boolean backPressed=false;
   ArrayList<DataHolder> arrayList;

ArrayList<com.rkant.bhajanapp.FirstActivities.DataHolder> nepaliNumbers;
AdapterView.OnItemSelectedListener listener;
JsonArrayRequest request,request2;
String url,url_bhajans;
RequestQueue requestQueue, requestQueue2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url="https://mocki.io/v1/b22dac58-2110-41ca-a7c9-a38ba80ae0ce";
        url_bhajans="https://mocki.io/v1/8583b6d5-cb54-40f1-84a6-fc0802f260a6 ";

        recyclerView=findViewById(R.id.recyclerView);
        arrayList=new ArrayList<>();
        nepaliNumbers=new ArrayList<>();


        getOnlineData();
        settingAdapter();
        try {
            addData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        getBhajansOnlineData();


        // Changing Action Bar colour
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff6200ed));
    }




    private void settingAdapter() {
        recyclerCustomAdapter=new RecyclerAdapter(arrayList,listener,MainActivity.this,nepaliNumbers);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerCustomAdapter);


    }
    public void getBhajansOnlineData(){
        requestQueue2=Volley.newRequestQueue(getApplicationContext());
        request2=new JsonArrayRequest(Request.Method.GET, url_bhajans, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    new DB_Handler(MainActivity.this).fetchBhajansFromCloud(jsonArray);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue2.add(request2);
    }
       public void getOnlineData(){
              if (new DB_Handler(MainActivity.this).checkIfDB_Exists("bhajan_list")){
                   ArrayList<DataHolderForDB> dArrayList=new DB_Handler(MainActivity.this).fetchDbDataFromDBForBhajanList();
                   for (int i=0;i<dArrayList.size();i++){
                       arrayList.add(new DataHolder(dArrayList.get(i).getBHAJAN_NAME_NEPALI(),
                               dArrayList.get(i).getBHAJAN_NAME_ENGLISH(),
                               dArrayList.get(i).getBHAJAN_ID()
                       ));
                   }
                   settingAdapter();
                   return;
               }

        requestQueue=Volley.newRequestQueue(getApplicationContext());
        request=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                   if (new DB_Handler(MainActivity.this).addDataFromCloud(jsonArray)){
                       ArrayList<DataHolderForDB> dArrayList=new DB_Handler(MainActivity.this).fetchDbDataFromDBForBhajanList();
                        for (int i=0;i<dArrayList.size();i++){
                            arrayList.add(new DataHolder(dArrayList.get(i).getBHAJAN_NAME_NEPALI(),
                                    dArrayList.get(i).getBHAJAN_NAME_ENGLISH(),
                                    dArrayList.get(i).getBHAJAN_ID()
                                    ));
                        }
                       settingAdapter();
                       return;
                   }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

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
                    arrayList.add( new DataHolder(nepali_bhajan,bhajan_english_for_search,id));
                }


                settingAdapter();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Response not got, Server error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);

    }
    public void addData() throws IOException, JSONException {
               String jsonData=readDataFromFile(R.raw.nepali_numbers);
        JSONArray array=new JSONArray(jsonData);
        for (int j=0;j<array.length();j++){
            String strr=array.getString(j);
            nepaliNumbers.add(new com.rkant.bhajanapp.FirstActivities.DataHolder(strr));


        }


    }

    public String readDataFromFile(int i) throws IOException {

        InputStream inputStream=null;
        StringBuilder builder=new StringBuilder();
        try{
            String jsonString=null;
            inputStream=getResources().openRawResource(i);
            BufferedReader bufferedReader=new BufferedReader(
                    new InputStreamReader(inputStream,"UTF-8"));
            while ((jsonString=bufferedReader.readLine()) !=null){
                builder.append(jsonString);
            }
        }
        finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
        return new String(builder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_search,menu);
        menuItem =menu.findItem(R.id.search_bar);
        favourite_bhajan_menuItem=menu.findItem(R.id.favourite_bhajan);
        searchView= (SearchView) MenuItemCompat.getActionView(menuItem);
        favourite_bhajan_menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent=new Intent(MainActivity.this, FavouriteBookmarked.class);
                startActivity(intent);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                menuItem.collapseActionView();
                searchView.onActionViewCollapsed();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<DataHolder> filteredList=new ArrayList<>();
                for(int a=0; a< arrayList.size(); a++){
                    DataHolder item=arrayList.get(a);
                    if (item.getBhajan_name_nepali().toLowerCase().contains(s.toString().toLowerCase()) || item.getBhajan_name_english().toLowerCase().contains(s.toString().toLowerCase()) ){
                        filteredList.add(item);
                    }
                }
                recyclerCustomAdapter.filterList(filteredList);
                return false;
            }

        });





        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        menuItem.collapseActionView();
        searchView.onActionViewCollapsed();
        if (backPressed) {
            super.onBackPressed();
        } else {
            backPressed = true;

        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                backPressed = false;
            }
        }, 2500);

    }



    }




