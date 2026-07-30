package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;
import android.view.animation.TranslateAnimation;
import io.sentry.Sentry;

import com.example.myapp.auth.SessionManager;
 
public class SplashActivity extends Activity {

    private static final int SPLASH_TIME = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    // waiting for view to draw to better represent a captured error with a screenshot
    findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(() -> {
      try {
        throw new Exception("This app uses Sentry! :)");
      } catch (Exception e) {
        Sentry.captureException(e);
      }
    });


        ImageView image = new ImageView(this);

        image.setImageResource(com.example.myapp.R.mipmap.ic_launcher);

        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        setContentView(image);


        new Handler().postDelayed(() -> {


            TranslateAnimation slideUp =
                    new TranslateAnimation(
                            0,
                            0,
                            0,
                            -getResources().getDisplayMetrics().heightPixels
                    );


            slideUp.setDuration(700);


            slideUp.setFillAfter(true);


            image.startAnimation(slideUp);



            new Handler().postDelayed(() -> {

SessionManager session = new SessionManager(this);

Intent intent;

if (session.isLoggedIn()) {

    intent = new Intent(this, MainActivity.class);

} else {

    intent = new Intent(this, LoginActivity.class);

}

startActivity(intent);
finish();


            },700);



        }, SPLASH_TIME);

    }
}
