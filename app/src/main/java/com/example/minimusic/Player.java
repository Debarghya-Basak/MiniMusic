package com.example.minimusic;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.RenderEffect;
import android.graphics.Shader;

import android.os.Build;
import android.os.Bundle;

import android.view.WindowManager;
import android.widget.ImageView;

public class Player extends AppCompatActivity {

    ImageView blurBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //initialization
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        blurBackground = findViewById(R.id.blurBackground);
        //initialization end

        blurBackgroundImage();

    }

    void blurBackgroundImage(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            blurBackground.setRenderEffect(RenderEffect.createBlurEffect(50, 50, Shader.TileMode.MIRROR));

        }

    }


}