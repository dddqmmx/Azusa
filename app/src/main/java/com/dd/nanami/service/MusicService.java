package com.dd.nanami.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

public class MusicService extends Service {
    public MusicService() {
    }

    public MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        try {
            mediaPlayer.setDataSource("http://39.107.229.253/data/music/audio.mp3");
            //异步准备
            mediaPlayer.prepareAsync();
            //添加准备好的监听
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //如果准备好了，就会进行这个方法
                    mp.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}