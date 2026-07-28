package com.example.myapp.fragments;


import android.os.Bundle;
import android.view.*;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.myapp.R;
import com.example.myapp.adapter.MusicAdapter;
import com.example.myapp.music.MusicScanner;
import com.example.myapp.model.Music;


import java.util.ArrayList;



public class MusicFragment extends Fragment {



@Nullable
@Override
public View onCreateView(
LayoutInflater inflater,
ViewGroup container,
Bundle savedInstanceState){


View view =
inflater.inflate(
R.layout.fragment_music,
container,
false);



RecyclerView recycler =
view.findViewById(
R.id.musicList);



recycler.setLayoutManager(
new LinearLayoutManager(
requireContext()
));



ArrayList<Music> songs =
MusicScanner.scan(
requireContext()
);



MusicAdapter adapter =
new MusicAdapter(
requireContext(),
songs
);



recycler.setAdapter(adapter);



return view;


}


}
