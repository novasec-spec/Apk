package com.example.myapp;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class NotificationActivity extends Activity{


@Override
protected void onCreate(Bundle b){

super.onCreate(b);


TextView text =
new TextView(this);


text.setText(
"Opened from notification"
);


text.setTextSize(24);


setContentView(text);


}


}
