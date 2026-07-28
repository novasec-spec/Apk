package com.example.myapp.service;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;


public class MusicService extends Service {


MediaPlayer player;


@Override
public void onCreate(){

super.onCreate();

player =
new MediaPlayer();

}



@Override
public int onStartCommand(
Intent intent,
int flags,
int startId){


String path =
intent.getStringExtra("path");


try{

player.reset();

player.setDataSource(path);

player.prepare();

player.start();


}catch(Exception e){

e.printStackTrace();

}


return START_STICKY;

}



@Override
public IBinder onBind(Intent intent){

return null;

}


}
