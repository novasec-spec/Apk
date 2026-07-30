package com.example.myapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapp.NotificationHelper;
import com.example.myapp.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        Button test = view.findViewById(R.id.testNotification);

        test.setOnClickListener(v -> {
            NotificationHelper.sendNotification(
                    requireContext(),
                    "Welcome",
                    "Your first notification works 🔥"
            );
        });

        return view;
    }
}
