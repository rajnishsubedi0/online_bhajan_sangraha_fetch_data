package com.rkant.bhajanapp.secondActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.rkant.bhajanapp.FirstActivities.DB_Handler;
import com.rkant.bhajanapp.FirstActivities.DataHolder;
import com.rkant.bhajanapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    MenuItem youtube_url_menu;
    ArrayList<com.rkant.bhajanapp.FirstActivities.DataHolder> arrayList;
    RecyclerView recyclerView;
    RecyclerAdapter bhajanData_recyclerView;
    String youtube_url;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic);
       // url="https://mocki.io/v1/92e83900-5c0f-4dbc-bf18-3394bd40a19c";
        recyclerView = findViewById(R.id.recyclerSecondView);
        arrayList = new ArrayList<>();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff6200ed));


        try {
            setData();
            getBhajanData();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }


    }

    public void getBhajanData() throws JSONException {
        Bundle bundle=getIntent().getExtras();
        String intentPosition=bundle.getString("position");
        arrayList=new DB_Handler(getApplicationContext()).fetchActualBhajanData(intentPosition);
        setAdapter();
    }
    public void setData() throws IOException, JSONException {
        Bundle bundle=getIntent().getExtras();
        String intentPosition=bundle.getString("position");

        String string_url_link=readDataFromFile(R.raw.youtube_link);
        JSONArray url_array=new JSONArray(string_url_link);
        youtube_url="";
        for (int j=0;j<url_array.length();j++){
            JSONObject object=url_array.getJSONObject(j);
            if(intentPosition.equals(object.getString("id"))){
                youtube_url=object.getString("link");
                break;
            }
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
    //Keeping Screen On
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }, 120000);

    }

    public void setAdapter() {
        bhajanData_recyclerView=new RecyclerAdapter(arrayList, SecondActivity.this   );
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(bhajanData_recyclerView);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.youtube_link,menu);
        youtube_url_menu=menu.findItem(R.id.youtube_url);
        youtube_url_menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
               try {
                   if(youtube_url!=""){
                       Uri uri=Uri.parse(youtube_url);
                       Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                       startActivity(intent);
                   }
                else {
                       Toast.makeText(SecondActivity.this, "No link found", Toast.LENGTH_SHORT).show();
                   }
               }catch (Exception e) {
                   Toast.makeText(SecondActivity.this, "No link found", Toast.LENGTH_SHORT).show();


               }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }}

