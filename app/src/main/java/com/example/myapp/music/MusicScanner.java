package com.example.myapp.music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.myapp.model.Music;

import java.util.ArrayList;

public class MusicScanner {

    public static ArrayList<Music> scan(Context context) {
        ArrayList<Music> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        
        Cursor cursor = contentResolver.query(
                uri,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int pathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                String title = cursor.getString(titleIndex);
                String artist = cursor.getString(artistIndex);
                String path = cursor.getString(pathIndex);
                long duration = cursor.getLong(durationIndex);
                long albumId = cursor.getLong(albumIdIndex);

                Music song = new Music();
                song.setTitle(title);
                song.setArtist(artist);
                song.setPath(path);
                song.setDuration(duration);
                song.setAlbumId(albumId);

                songs.add(song);
            } while (cursor.moveToNext());
            
            cursor.close();
        }

        return songs;
    }
}
