package com.example.myapp.music;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.myapp.model.Music;

import java.util.ArrayList;


public class MusicScanner {


public static ArrayList<Music> scan(Context context){


ArrayList<Music> songs =
new ArrayList<>();


Cursor cursor =
context.getContentResolver()
.query(

MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,

null,

MediaStore.Audio.Media.IS_MUSIC+"!=0",

null,

MediaStore.Audio.Media.TITLE+" ASC"

);



if(cursor!=null){


while(cursor.moveToNext()){


String title =
cursor.getString(
cursor.getColumnIndexOrThrow(
MediaStore.Audio.Media.TITLE));


String artist =
cursor.getString(
cursor.getColumnIndexOrThrow(
MediaStore.Audio.Media.ARTIST));


String path =
cursor.getString(
cursor.getColumnIndexOrThrow(
MediaStore.Audio.Media.DATA));



songs.add(
new Music(
title,
artist,
path
));


}


cursor.close();

}


return songs;


}


}
