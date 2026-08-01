package com.example.myapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.myapp.R;
import com.example.myapp.model.Music;

import java.util.ArrayList;
import java.util.Collections;

public class MusicPlayerService extends Service {

    private MediaPlayer mediaPlayer;
    private ArrayList<Music> playlist;
    private int currentPosition = -1;
    private boolean isShuffle = false;
    private int repeatMode = 0; // 0: none, 1: all, 2: one
    
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setPlaylist(ArrayList<Music> songs) {
        this.playlist = songs;
    }

    public void playSong(int position) {
        if (playlist == null || playlist.isEmpty()) return;
        
        if (isShuffle) {
            // Randomize playlist order
            Collections.shuffle(playlist);
            currentPosition = 0;
        } else {
            currentPosition = position;
        }
        
        Music song = playlist.get(currentPosition);
        
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            
            // Update notification
            startForeground(1, createNotification(song));
            
            // Set completion listener
            mediaPlayer.setOnCompletionListener(mp -> {
                if (repeatMode == 2) {
                    // Repeat one
                    playSong(currentPosition);
                } else if (repeatMode == 1 || playlist.size() > 1) {
                    // Repeat all or play next
                    playNext();
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playNext() {
        if (playlist == null || playlist.isEmpty()) return;
        
        if (currentPosition < playlist.size() - 1) {
            playSong(currentPosition + 1);
        } else if (repeatMode == 1) {
            playSong(0);
        }
    }

    public void playPrevious() {
        if (playlist == null || playlist.isEmpty()) return;
        
        if (currentPosition > 0) {
            playSong(currentPosition - 1);
        } else if (repeatMode == 1) {
            playSong(playlist.size() - 1);
        }
    }

    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateNotification();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateNotification();
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public Music getCurrentSong() {
        if (playlist != null && currentPosition >= 0 && currentPosition < playlist.size()) {
            return playlist.get(currentPosition);
        }
        return null;
    }

    public void toggleShuffle() {
        isShuffle = !isShuffle;
        if (isShuffle) {
            Collections.shuffle(playlist);
        }
    }

    public boolean isShuffleEnabled() {
        return isShuffle;
    }

    public int toggleRepeat() {
        repeatMode = (repeatMode + 1) % 3;
        return repeatMode;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "music_channel",
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(Music song) {
        Intent playIntent = new Intent(this, MusicPlayerService.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, MusicPlayerService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, MusicPlayerService.class);
        nextIntent.setAction("NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 2, nextIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "music_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())
                .setOngoing(true)
                .setShowWhen(false)
                .addAction(android.R.drawable.ic_media_previous, "Previous", null)
                .addAction(isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play, 
                    isPlaying() ? "Pause" : "Play", 
                    isPlaying() ? pausePendingIntent : playPendingIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent);

        return builder.build();
    }

    private void updateNotification() {
        if (playlist != null && currentPosition >= 0) {
            Music song = playlist.get(currentPosition);
            startForeground(1, createNotification(song));
        }
    }
}
