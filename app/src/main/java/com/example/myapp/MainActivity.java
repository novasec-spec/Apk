package com.example.myapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.example.myapp.fragments.HomeFragment;
import com.example.myapp.fragments.SearchFragment;
import com.example.myapp.fragments.ProfileFragment;
import com.example.myapp.fragments.MusicFragment;



public class MainActivity extends AppCompatActivity {


    private static final int NOTIFICATION_PERMISSION_CODE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        requestNotificationPermission();



        // Default fragment

        if(savedInstanceState == null){

            loadFragment(new HomeFragment());

        }




        // Test notification button

        Button button =
                findViewById(R.id.testNotification);



        button.setOnClickListener(v -> {


            NotificationHelper.sendNotification(
                    this,
                    "Welcome",
                    "Your first notification works 🔥"
            );


        });





        // Settings button

        ImageButton settingsButton =
                findViewById(R.id.settingsButton);



        settingsButton.setOnClickListener(v -> {


            Toast.makeText(
                    this,
                    "Settings clicked",
                    Toast.LENGTH_SHORT
            ).show();


        });





        // Bottom navigation

        ImageButton homeTab =
                findViewById(R.id.homeTab);


        ImageButton searchTab =
                findViewById(R.id.searchTab);


        ImageButton profileTab =
                findViewById(R.id.profileTab);


        ImageButton musicTab =
                findViewById(R.id.musicTab);





        homeTab.setOnClickListener(v -> {

            loadFragment(
                    new HomeFragment()
            );

        });





        searchTab.setOnClickListener(v -> {

            loadFragment(
                    new SearchFragment()
            );

        });





        profileTab.setOnClickListener(v -> {

            loadFragment(
                    new ProfileFragment()
            );

        });





        musicTab.setOnClickListener(v -> {

            loadFragment(
                    new MusicFragment()
            );

        });



    }





    private void loadFragment(Fragment fragment){


        getSupportFragmentManager()

                .beginTransaction()

                .replace(
                        R.id.fragment_container,
                        fragment
                )

                .commit();


    }






    private void requestNotificationPermission(){



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){



            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            )
            != PackageManager.PERMISSION_GRANTED){



                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_CODE
                );


            }


        }


    }





    @Override
    protected void onNewIntent(Intent intent){


        super.onNewIntent(intent);

        handleShortcut(intent);


    }






    private void handleShortcut(Intent intent){


        String data = "";



        if(intent.getData()!=null){

            data = intent.getData().toString();

        }




        if(data.equals("myapp://new_note")){


            Toast.makeText(
                    this,
                    "Opening new note",
                    Toast.LENGTH_SHORT
            ).show();



        }



        else if(data.equals("myapp://profile")){


            loadFragment(
                    new ProfileFragment()
            );


        }



    }



}
