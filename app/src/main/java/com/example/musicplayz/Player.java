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
        musicSeekbar = findViewById(R.id.seekbar_music);
        playPauseButton = findViewById(R.id.playButton_music);
        nextButton = findViewById(R.id.nextButton_music);
        prevButton = findViewById(R.id.prevButton_music);
        musicDisc = findViewById(R.id.music_disc);

        updateUIMusic();
        musicStartFlag();

        updatePlayPauseButtonBg();

        blurBackgroundImage();
        musicDiscRotateAnimation();
        seekBarFn();

        mediaPlayerListener();

        //initialization end

    }

    public void mediaPlayerListener(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextButton.performClick();
            }
        });
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
                musicSeekbar.setProgress(seekBar.getProgress());
                musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
            }
        });

        updateSeekbarUI();
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
                }while (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()-250);
                musicPlaying = false;
                Log.d("Debug","Stop Rotation");
            }
        });


    }

    public void musicStartFlag(){
        mediaPlayer.start();
        musicPlaying = true;
    }

    public void musicStopFlag(){
        mediaPlayer.stop();
        musicPlaying = false;
    }

    public void musicPauseFlag(){
        mediaPlayer.pause();
        musicPlaying = false;
    }

    public void updateSeekbarUI(){
        updateSeekbar = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;

                while(currentPosition < mediaPlayer.getDuration() - 250){
                    currentPosition = mediaPlayer.getCurrentPosition();
                    musicSeekbar.setProgress(currentPosition);
                    musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
                    SystemClock.sleep(500);
                    Log.d("Debug","Duration : " + mediaPlayer.getDuration() + ", Progress : "+ mediaPlayer.getCurrentPosition());
                }
                Log.d("Debug" , "Song End");

            }
        };
        updateSeekbar.start();
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

    public void updateUIMusic(){
        mediaPlayer = MediaPlayer.create(this, Uri.parse(Dashboard.musicList.get(Dashboard.position)));
        musicName.setText(Dashboard.musicName.get(Dashboard.position));
        musicName.setSelected(true);
        musicSeekbar.setMax(mediaPlayer.getDuration()-250);
        musicSeekbar.setProgress(0);
        musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
        musicEndTime.setText(toMin(mediaPlayer.getDuration()));
    }

    public void changeToPrevMusic(){
        musicStopFlag();
        updatePlayPauseButtonBg();
        Dashboard.position--;
        if(Dashboard.position == -1)
            Dashboard.position = Dashboard.musicList.size()-1;

        updateUIMusic();
        musicStartFlag();
        updatePlayPauseButtonBg();
        updateSeekbarUI();
        if(!threadMusicDisc.looping && musicPlaying)
            threadStart();
    }

    public void changeToPrevMusic(View v){
       changeToPrevMusic();

    }

    public void changeToNextMusic(){
        musicStopFlag();
        updatePlayPauseButtonBg();
        Dashboard.position++;
        if(Dashboard.position == Dashboard.musicList.size())
            Dashboard.position = 0;

        Log.d("Debug",Dashboard.musicName.get(Dashboard.position));
        updateUIMusic();
        musicStartFlag();
        updatePlayPauseButtonBg();
        updateSeekbarUI();
        if(!threadMusicDisc.looping && musicPlaying)
            threadStart();
    }

    public void changeToNextMusic(View v){
        changeToNextMusic();
    }

    public void playPauseMusic(){
        if(musicPlaying) {
            musicPauseFlag();
            updatePlayPauseButtonBg();
            threadQuit();
        }
        else {
            musicStartFlag();
            updatePlayPauseButtonBg();
            threadStart();
        }
    }

    public void playPauseMusic(View v){
       playPauseMusic();
    }

    void blurBackgroundImage(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            blurBackground.setRenderEffect(RenderEffect.createBlurEffect(50, 50, Shader.TileMode.MIRROR));



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

        musicStopFlag();
        updatePlayPauseButtonBg();
        threadQuit();

    }
}