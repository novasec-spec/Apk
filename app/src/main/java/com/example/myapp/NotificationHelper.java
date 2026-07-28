package com.example.myapp;

import android.app.*;
import android.content.*;
import androidx.core.app.NotificationCompat;


public class NotificationHelper {


    public static final String CHANNEL_ID="app_notifications";


    public static void createChannel(Context context){


        if(android.os.Build.VERSION.SDK_INT >= 26){


            NotificationChannel channel =
                    new NotificationChannel(
                    CHANNEL_ID,
                    "App Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );


            NotificationManager manager =
                    context.getSystemService(
                    NotificationManager.class);


            manager.createNotificationChannel(channel);

        }

    }




    public static void sendNotification(
            Context context,
            String title,
            String message){


        createChannel(context);



        Intent intent =
                new Intent(context,
                NotificationActivity.class);



        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE);



        Notification notification =
                new NotificationCompat.Builder(
                context,
                CHANNEL_ID)

                .setSmallIcon(
                R.drawable.ic_notification)

                .setContentTitle(title)

                .setContentText(message)

                .setAutoCancel(true)

                .setContentIntent(pendingIntent)

                .build();



        NotificationManager manager =
                (NotificationManager)
                context.getSystemService(
                Context.NOTIFICATION_SERVICE);



        manager.notify(
                1,
                notification);

    }

}
