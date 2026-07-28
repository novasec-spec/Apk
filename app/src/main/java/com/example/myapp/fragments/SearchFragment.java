package com.example.myapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapp.R;

public class SearchFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){


        return inflater.inflate(
                R.layout.fragment_search,
                container,
                false
        );

    }

}
