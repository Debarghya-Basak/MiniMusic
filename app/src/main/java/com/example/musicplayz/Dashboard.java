package com.example.musicplayz;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {


    SearchView musicSearch;
    RecyclerView musicItemRecyclerView;
    static ArrayList<String> musicList = new ArrayList<>();
    static ArrayList<String> musicName = new ArrayList<>();
    static int position = -1;

    final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //initialization
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        musicItemRecyclerView = findViewById(R.id.music_item_recyclerView);
        musicSearch = findViewById(R.id.music_searchView);
        //initialization end


        checkPerm();

    }

    public void checkPerm(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            try {
                loadMusicListFromDirectory();
            } catch (IOException e) {
            }
        }
        else {
            ActivityCompat.requestPermissions(Dashboard.this, new String[] { Manifest.permission.READ_MEDIA_AUDIO }, REQUEST_CODE);

            Log.d("Debug","Dashboard : Permission not granted");
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    try {
                        loadMusicListFromDirectory();
                    } catch (IOException e) {
                    }
                }
        }

    }

    public void loadMusicListFromDirectory() throws IOException {

            String path = Environment.getExternalStorageDirectory().toString()+"/Music";

            File directory = new File(path);
            File files[] = directory.listFiles();

            if (files != null) {
                for (int i = 0; i < files.length; ++i){
                    String fileList = files[i].getAbsolutePath();
                    String fileName = files[i].getName();
                    if(fileList.endsWith(".mp3")){
                        musicList.add(fileList);
                        musicName.add(fileName);
                        Log.d("Debug", "Dashboard: files = " + fileList);
                    }
                }
                updateRecyclerView();
            }
            else
                Log.d("Debug","Dashboard: No music found");

            //startSearchView();

    }

    @SuppressLint("RestrictedApi")
    public void updateRecyclerView(){
        MusicItemRecyclerViewAdapter adapter = new MusicItemRecyclerViewAdapter(this);
        musicItemRecyclerView.setAdapter(adapter);
        musicItemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(this)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        musicList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicList = new ArrayList<>();
        musicName = new ArrayList<>();
        position = -1;
    }
}