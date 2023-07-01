package com.example.musicplayz;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class LooperThreadMusicDisc extends Thread{


    public boolean looping = false;
    public Looper looper;
    public Handler handler;

    @Override
    public void run(){
        Looper.prepare();
        looper = Looper.myLooper();
        handler = new Handler();
        Looper.loop();

        looping = false;
        Log.d("Debug","Looper Thread: End of run() : " + looping);
    }

}
