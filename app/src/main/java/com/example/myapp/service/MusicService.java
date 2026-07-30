package com.example.myapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.myapp.R; // Make sure this import matches your package

public class MusicService extends Service {

    private MediaPlayer player;

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            "music_channel",
                            "Music Playback",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        player = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification =
                new NotificationCompat.Builder(this, "music_channel")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Music Player")
                        .setContentText("Playing music")
                        .setOngoing(true)
                        .build();

        startForeground(1, notification);

        String path = intent.getStringExtra("path");

        try {
            player.reset();
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
