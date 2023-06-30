package com.example.musicplayz;
import static android.graphics.Color.argb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatDrawableManager;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.RenderEffect;
import android.graphics.Shader;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Player extends AppCompatActivity {

    TextView musicName;
    ImageView blurBackground, musicDisc;
    MediaPlayer mediaPlayer;
    SeekBar musicSeekbar;
    ImageButton playPauseButton, nextButton, prevButton;

    boolean musicPlaying;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //initialization
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        blurBackground = findViewById(R.id.blurBackground);

        Log.d("Debug", Dashboard.musicList.get(Dashboard.position));
        musicName = findViewById(R.id.playing_music_name);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(Dashboard.musicList.get(Dashboard.position)));
        playPauseButton = findViewById(R.id.playButton_music);
        nextButton = findViewById(R.id.nextButton_music);
        prevButton = findViewById(R.id.prevButton_music);
        musicSeekbar = findViewById(R.id.seekbar_music);
        musicDisc = findViewById(R.id.music_disc);
        musicName.setText(Dashboard.musicName.get(Dashboard.position));
        musicName.setSelected(true);
        mediaPlayer.start();
        musicPlaying = true;
        updatePlayPauseButtonBg();
        musicDisc.animate().rotationBy(360f).setDuration(10000);

        //initialization end

        blurBackgroundImage();

    }


    @SuppressLint("RestrictedApi")
    private void updatePlayPauseButtonBg() {
        if(musicPlaying){
            playPauseButton.setBackground(AppCompatDrawableManager.get().getDrawable(this, R.drawable.pause_button));
            playPauseButton.setBackgroundTintList(ColorStateList.valueOf(argb(127,255,255,255)));
        }
        else{
            playPauseButton.setBackground(AppCompatDrawableManager.get().getDrawable(this, R.drawable.play_button));
            playPauseButton.setBackgroundTintList(ColorStateList.valueOf(argb(127,255,255,255)));
        }


    }

    @SuppressLint("RestrictedApi")
    public void changeToPrevMusic(View v){
        mediaPlayer.stop();
        musicPlaying = false;
        updatePlayPauseButtonBg();
        Dashboard.position--;
        if(Dashboard.position == -1)
            Dashboard.position = Dashboard.musicList.size()-1;
        musicName.setText(Dashboard.musicName.get(Dashboard.position));
        musicName.setSelected(true);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(Dashboard.musicList.get(Dashboard.position)));
        mediaPlayer.start();
        musicPlaying = true;
        updatePlayPauseButtonBg();


    }

    @SuppressLint("RestrictedApi")
    public void changeToNextMusic(View v){
        mediaPlayer.stop();
        musicPlaying = false;
        updatePlayPauseButtonBg();
        Dashboard.position++;
        if(Dashboard.position == Dashboard.musicList.size())
            Dashboard.position = 0;
        musicName.setText(Dashboard.musicName.get(Dashboard.position));
        musicName.setSelected(true);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(Dashboard.musicList.get(Dashboard.position)));
        mediaPlayer.start();
        musicPlaying = true;
        updatePlayPauseButtonBg();
    }

    @SuppressLint("RestrictedApi")
    public void playPauseMusic(View v){
        if(musicPlaying) {
            mediaPlayer.pause();
            musicPlaying = false;
            updatePlayPauseButtonBg();
        }
        else {
            mediaPlayer.start();
            musicPlaying = true;
            updatePlayPauseButtonBg();
        }

    }

    void blurBackgroundImage(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            blurBackground.setRenderEffect(RenderEffect.createBlurEffect(50, 50, Shader.TileMode.MIRROR));

        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        musicPlaying = false;
        updatePlayPauseButtonBg();
    }
}