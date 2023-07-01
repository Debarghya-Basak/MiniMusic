package com.example.musicplayz;
import static android.graphics.Color.argb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.res.ColorStateList;
import android.graphics.RenderEffect;
import android.graphics.Shader;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Player extends AppCompatActivity {

    TextView musicName, musicProgressTime, musicEndTime;
    ImageView blurBackground, musicDisc;
    MediaPlayer mediaPlayer;
    SeekBar musicSeekbar;
    ImageButton playPauseButton, nextButton, prevButton;
    Thread updateSeekbar;
    LooperThreadMusicDisc threadMusicDisc;

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
        musicProgressTime = findViewById(R.id.music_progressTime);
        musicEndTime = findViewById(R.id.music_endTime);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(Dashboard.musicList.get(Dashboard.position)));
        musicSeekbar = findViewById(R.id.seekbar_music);
        musicSeekbar.setMax(mediaPlayer.getDuration());
        playPauseButton = findViewById(R.id.playButton_music);
        nextButton = findViewById(R.id.nextButton_music);
        prevButton = findViewById(R.id.prevButton_music);
        musicDisc = findViewById(R.id.music_disc);
        musicName.setText(Dashboard.musicName.get(Dashboard.position));
        musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
        musicEndTime.setText(toMin(mediaPlayer.getDuration()));
        musicName.setSelected(true);
        mediaPlayer.start();
        musicPlaying = true;

        updatePlayPauseButtonBg();

        blurBackgroundImage();
        musicDiscRotateAnimation();
        seekBarFn();

        //initialization end

    }

    public String toMin(long millis){
        long sec = (millis / 1000) % 60;
        long min = (millis / 1000) / 60;
        long hour = (millis / 1000) / (60*60);

        String seconds=sec+"",minutes=min+"",hours=hour+"";
        if(sec < 10)
            seconds = "0" + sec;
        if(min < 10)
            minutes = "0" + minutes;
        if(hour < 10)
            hours = "0" + hour;

        return hours + ":" + minutes + ":" + seconds;
    }

    public void seekBarFn(){
        musicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeekbar = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;

                while(currentPosition < mediaPlayer.getDuration()){
                    currentPosition = mediaPlayer.getCurrentPosition();
                    musicSeekbar.setProgress(currentPosition);
                    musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
                    SystemClock.sleep(500);
                }
            }
        };
        updateSeekbar.start();
    }

    public void musicDiscRotateAnimation(){
        threadMusicDisc = new LooperThreadMusicDisc();

        threadMusicDisc.start();
        SystemClock.sleep(100);
        threadMusicDisc.looping = true;

        Handler handler = new Handler(threadMusicDisc.looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                do{

                    //if animate() is looped, it cannot run directly on a simple Thread. It needs a Looper Thread
                    musicDisc.animate().rotationBy(100).setDuration(5000);
                    musicDisc.setHasTransientState(true);
                    Log.d("Debug","Player : Rotation" + musicDisc.hasTransientState());
                    SystemClock.sleep(5000);
                    if(!musicPlaying)
                        break;
                }while (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration());
                Log.d("Debug","Stop Rotation");
            }
        });


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
        musicSeekbar.setMax(mediaPlayer.getDuration());
        musicSeekbar.setProgress(0);
        musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
        musicEndTime.setText(toMin(mediaPlayer.getDuration()));
        mediaPlayer.start();
        musicPlaying = true;
        updatePlayPauseButtonBg();
        if(!threadMusicDisc.looping && musicPlaying)
            threadStart();

    }

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
        musicSeekbar.setMax(mediaPlayer.getDuration());
        musicSeekbar.setProgress(0);
        musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
        musicEndTime.setText(toMin(mediaPlayer.getDuration()));
        mediaPlayer.start();
        musicPlaying = true;
        updatePlayPauseButtonBg();
        if(!threadMusicDisc.looping && musicPlaying)
            threadStart();
    }

    public void playPauseMusic(View v){
        if(musicPlaying) {
            mediaPlayer.pause();
            musicPlaying = false;
            updatePlayPauseButtonBg();
            threadQuit();
        }
        else {
            mediaPlayer.start();
            musicPlaying = true;
            updatePlayPauseButtonBg();
            threadStart();
        }

    }

    void blurBackgroundImage(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            blurBackground.setRenderEffect(RenderEffect.createBlurEffect(50, 50, Shader.TileMode.MIRROR));

        }

    }

    public void threadQuit(){

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Log.d("Debug","Thread stopped");
                threadMusicDisc.looper.quit();
            }
        },5000);

    }

    public void threadStart(){
        if(threadMusicDisc.looping){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Log.d("Debug","Next Song");
                    musicDiscRotateAnimation();
                }
            },5100);
        }
        else{
            musicDiscRotateAnimation();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        musicPlaying = false;
        updatePlayPauseButtonBg();
        threadQuit();

    }
}