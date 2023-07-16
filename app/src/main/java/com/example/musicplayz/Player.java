package com.example.musicplayz;

import static android.graphics.Color.argb;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatDrawableManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import io.alterac.blurkit.BlurLayout;

public class Player extends AppCompatActivity {

    TextView musicNameShow, musicProgressTime, musicEndTime;
    ImageView blurBackground, musicDisc;
    BlurLayout blurLayout;
    MediaPlayer mediaPlayer;
    SeekBar musicSeekbar;
    ImageButton playPauseButton, nextButton, prevButton, shuffleButton, repeatButton;
    Thread updateSeekbar;
    LooperThreadMusicDisc threadMusicDisc;
    boolean musicPlaying;
    int repeatMode = 0;
    int shuffleMode = 0;
    ArrayList<String> musicList, musicName;
    ArrayList<String> tempMusicList, tempMusicName;
    int position;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //initialization
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Intent intent = getIntent();
        Bundle contents = intent.getExtras();
        musicList = contents.getStringArrayList("musicList");
        musicName = contents.getStringArrayList("musicName");
        position = contents.getInt("position");
        tempMusicList = new ArrayList<>();
        tempMusicName = new ArrayList<>();

        blurBackground = findViewById(R.id.blurBackground);
        blurLayout = findViewById(R.id.alteracBlurLayout);

        Log.d("Debug", musicList.get(position));

        musicNameShow = findViewById(R.id.playing_music_name);
        musicProgressTime = findViewById(R.id.music_progressTime);
        musicEndTime = findViewById(R.id.music_endTime);
        musicSeekbar = findViewById(R.id.seekbar_music);
        playPauseButton = findViewById(R.id.playButton_music);
        nextButton = findViewById(R.id.nextButton_music);
        prevButton = findViewById(R.id.prevButton_music);
        musicDisc = findViewById(R.id.music_disc);
        shuffleButton = findViewById(R.id.shuffleMode_music);
        repeatButton = findViewById(R.id.repeatMode_music);

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

    public void musicStopFlag(boolean flag){
        mediaPlayer.stop();
        if(flag)
            mediaPlayer.release();
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

                try {
                    while (currentPosition < mediaPlayer.getDuration() - 250) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        musicSeekbar.setProgress(currentPosition);
                        //TODO: set text inside another thread not working properly
                        Player.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
                                }catch (Exception e){}
                            }
                        });

                        SystemClock.sleep(500);
                        // Log.d("Debug","Duration : " + mediaPlayer.getDuration() + ", Progress : "+ mediaPlayer.getCurrentPosition());
                    }
                }
                catch (Exception e){}
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
        mediaPlayer = MediaPlayer.create(this, Uri.parse(musicList.get(position)));
        musicNameShow.setText(musicName.get(position));
        musicNameShow.setSelected(true);
        musicSeekbar.setMax(mediaPlayer.getDuration()-250);
        musicSeekbar.setProgress(0);
        musicProgressTime.setText(toMin(mediaPlayer.getCurrentPosition()));
        musicEndTime.setText(toMin(mediaPlayer.getDuration()));
    }

    public void changeToPrevMusic(){
        musicStopFlag(false);
        updatePlayPauseButtonBg();
        position--;
        if(position == -1)
            position = musicList.size()-1;

        updateUIMusic();
        musicStartFlag();
        updatePlayPauseButtonBg();
        updateSeekbarUI();
        mediaPlayerListener();
        if(!threadMusicDisc.looping && musicPlaying)
            threadStart();
    }

    public void changeToPrevMusic(View v){
       changeToPrevMusic();

    }

    public void changeToNextMusic(){
        if(repeatMode == 0){
            musicStopFlag(false);
            updatePlayPauseButtonBg();
            position++;
            if (position == musicList.size()){
                position--;
                musicStopFlag(false);
                threadQuit();
                updateUIMusic();
                updatePlayPauseButtonBg();
                mediaPlayerListener();
            }
            else{
                updateUIMusic();
                musicStartFlag();
                updatePlayPauseButtonBg();
                updateSeekbarUI();
                mediaPlayerListener();
                if(!threadMusicDisc.looping && musicPlaying)
                    threadStart();
            }

        }
        else if(repeatMode == 1) {
            musicStopFlag(false);
            updatePlayPauseButtonBg();
            position++;
            if (position == musicList.size())
                position = 0;
            Log.d("Debug",musicName.get(position));

            updateUIMusic();
            musicStartFlag();
            updatePlayPauseButtonBg();
            updateSeekbarUI();
            mediaPlayerListener();
            if(!threadMusicDisc.looping && musicPlaying)
                threadStart();
        }
        else if(repeatMode == 2){
            musicStopFlag(false);
            updatePlayPauseButtonBg();
            Log.d("Debug",musicName.get(position));

            updateUIMusic();
            musicStartFlag();
            updatePlayPauseButtonBg();
            updateSeekbarUI();
            mediaPlayerListener();
            if(!threadMusicDisc.looping && musicPlaying)
                threadStart();
        }


    }

    public void changeToNextMusic(View v){
        changeToNextMusic();
        Log.d("Debug", "Next button Clicked");
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
            if(!threadMusicDisc.looping && musicPlaying)
                threadStart();
        }
    }

    public void playPauseMusic(View v){
       playPauseMusic();
    }

    public void shuffleArrayList(){
        for(int i = 0;i<musicList.size();i++){
            int rand = (int) Math.random() * musicList.size();
            String temp = musicList.get(rand);
            musicList.set(rand, musicList.get(i));
            musicList.set(i,temp);

            temp = musicName.get(rand);
            musicName.set(rand, musicName.get(i));
            musicName.set(i,temp);
        }
    }

    @SuppressLint("RestrictedApi")
    public void changeShuffleMode(View v){
        if(shuffleMode == 0){
            shuffleButton.setBackground(AppCompatDrawableManager.get().getDrawable(this, R.drawable.shuffle_on));
            shuffleMode = 1;
            musicStopFlag(true);
            threadQuit();


            tempMusicList = copyArrayList(musicList);
            tempMusicName = copyArrayList(musicName);
            shuffleArrayList();

            display(tempMusicName);

            position = 0;
            updatePlayPauseButtonBg();
            updateUIMusic();
            mediaPlayerListener();
        }
        else{
            shuffleButton.setBackground(AppCompatDrawableManager.get().getDrawable(this, R.drawable.shuffle_off));
            shuffleMode = 0;
            musicStopFlag(true);
            threadQuit();


            musicList = copyArrayList(tempMusicList);
            musicName = copyArrayList(tempMusicName);

            display(musicName);

            position = 0;
            updatePlayPauseButtonBg();
            updateUIMusic();
            mediaPlayerListener();
        }

    }

    private ArrayList<String> copyArrayList(ArrayList<String> musicName) {
        ArrayList<String> temp = new ArrayList<String>();
        for(String name: musicName)
            temp.add(name);

        return temp;
    }

    @SuppressLint("RestrictedApi")
    public void changeRepeatMode(View v){
        if(repeatMode == 0){
            repeatButton.setBackground(AppCompatDrawableManager.get().getDrawable(this, R.drawable.repeat_playlist_on));
            repeatMode = 1;
        }
        else if(repeatMode == 1){
            repeatButton.setBackground(AppCompatDrawableManager.get().getDrawable(this, R.drawable.repeat_one_on));
            repeatMode = 2;
        }
        else if(repeatMode == 2){
            repeatButton.setBackground(AppCompatDrawableManager.get().getDrawable(this, R.drawable.repeat_off));
            repeatMode = 0;
        }

    }

    void blurBackgroundImage(){

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S){
            Toast.makeText(this, "Above android S", Toast.LENGTH_SHORT).show();
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
    protected void onStart() {
        super.onStart();
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S)
            blurLayout.startBlur();
    }

    @Override
    protected void onStop() {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S)
            blurLayout.pauseBlur();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        musicStopFlag(true);
        updatePlayPauseButtonBg();
        threadQuit();

    }

    private void display(ArrayList<String> musicName) {
        for(String name : musicName)
            Log.d("Debug", name);
    }
}