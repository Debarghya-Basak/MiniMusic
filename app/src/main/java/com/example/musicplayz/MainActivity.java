package com.example.musicplayz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView splashScreenIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        splashScreenIcon = (ImageView) findViewById(R.id.splash_screen_icon);

        splashScreenIcon.animate().scaleX(2).scaleY(2).rotationBy(90f).setDuration(1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startDashboardPage();
            }
        },1500);

    }

    public void startDashboardPage(){
        Intent intent = new Intent(MainActivity.this, Dashboard.class);
        startActivity(intent);
        finish();
    }
}