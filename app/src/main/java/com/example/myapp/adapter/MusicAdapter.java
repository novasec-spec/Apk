package com.example.myapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapp.R;
import com.example.myapp.model.Music;
import com.example.myapp.service.MusicService;


import java.util.ArrayList;



public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{


Context context;

ArrayList<Music> songs;



public MusicAdapter(Context context, ArrayList<Music> songs){

this.context=context;
this.songs=songs;

}



@NonNull
@Override
public ViewHolder onCreateViewHolder(
ViewGroup parent,
int viewType){


View view =
LayoutInflater.from(context)
.inflate(
R.layout.music_item,
parent,
false);


return new ViewHolder(view);

}



@Override
public void onBindViewHolder(
ViewHolder holder,
int position){


Music song =
songs.get(position);


holder.title.setText(song.title);

holder.itemView.setOnClickListener(v -> {


Intent intent =
new Intent(
context,
MusicService.class
);


intent.putExtra(
"path",
song.path
);


context.startForegroundService(intent);


});


}



@Override
public int getItemCount(){

return songs.size();

}



class ViewHolder extends RecyclerView.ViewHolder{


TextView title;


ViewHolder(View itemView){

super(itemView);

title =
itemView.findViewById(R.id.songTitle);

}


}


}
