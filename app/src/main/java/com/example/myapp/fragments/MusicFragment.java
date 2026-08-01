package com.example.myapp.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.adapter.MusicAdapter;
import com.example.myapp.music.MusicScanner;
import com.example.myapp.model.Music;
import com.example.myapp.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.Collections;

public class MusicFragment extends Fragment implements MusicAdapter.OnMusicClickListener {

    // UI Components
    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private ArrayList<Music> songs;
    
    // Mini Player
    private View miniPlayerContainer;
    private TextView miniPlayerTitle, miniPlayerArtist;
    private ImageButton miniPlayerPlayPause, miniPlayerNext;
    private ImageView miniPlayerArtwork;
    
    // Player Controls (for full player)
    private View fullPlayerContainer;
    private TextView fullPlayerTitle, fullPlayerArtist;
    private ImageButton fullPlayerPlayPause, fullPlayerPrev, fullPlayerNext, fullPlayerShuffle, fullPlayerRepeat;
    private ImageView fullPlayerArtwork;
    private SeekBar seekBar;
    private TextView currentTimeText, totalTimeText;
    
    // Service
    private MusicPlayerService musicService;
    private boolean isBound = false;
    private boolean isFullPlayerVisible = false;
    
    // Handler for updating seekbar
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (musicService != null && musicService.isPlaying()) {
                int currentPosition = musicService.getCurrentPosition();
                int duration = musicService.getDuration();
                
                if (duration > 0) {
                    seekBar.setProgress(currentPosition);
                    seekBar.setMax(duration);
                    currentTimeText.setText(formatTime(currentPosition));
                    totalTimeText.setText(formatTime(duration));
                }
            }
            handler.postDelayed(this, 1000);
        }
    };
    
    // Service Connection
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
            
            // Update UI with current song info
            updateUIWithCurrentSong();
            updatePlayPauseButtons();
            
            // Start seekbar updates
            handler.post(updateSeekBarRunnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadSongs();
        setupMiniPlayer();
        setupFullPlayer();
        
        // Bind to service
        Intent intent = new Intent(getContext(), MusicPlayerService.class);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.musicList);
        
        // Mini Player
        miniPlayerContainer = view.findViewById(R.id.miniPlayerContainer);
        miniPlayerTitle = view.findViewById(R.id.miniPlayerTitle);
        miniPlayerArtist = view.findViewById(R.id.miniPlayerArtist);
        miniPlayerPlayPause = view.findViewById(R.id.miniPlayerPlayPause);
        miniPlayerNext = view.findViewById(R.id.miniPlayerNext);
        miniPlayerArtwork = view.findViewById(R.id.miniPlayerArtwork);
        
        // Full Player
        fullPlayerContainer = view.findViewById(R.id.fullPlayerContainer);
        fullPlayerTitle = view.findViewById(R.id.fullPlayerTitle);
        fullPlayerArtist = view.findViewById(R.id.fullPlayerArtist);
        fullPlayerPlayPause = view.findViewById(R.id.fullPlayerPlayPause);
        fullPlayerPrev = view.findViewById(R.id.fullPlayerPrev);
        fullPlayerNext = view.findViewById(R.id.fullPlayerNext);
        fullPlayerShuffle = view.findViewById(R.id.fullPlayerShuffle);
        fullPlayerRepeat = view.findViewById(R.id.fullPlayerRepeat);
        fullPlayerArtwork = view.findViewById(R.id.fullPlayerArtwork);
        seekBar = view.findViewById(R.id.seekBar);
        currentTimeText = view.findViewById(R.id.currentTimeText);
        totalTimeText = view.findViewById(R.id.totalTimeText);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadSongs() {
        songs = MusicScanner.scan(requireContext());
        if (songs != null && !songs.isEmpty()) {
            adapter = new MusicAdapter(requireContext(), songs, this);
            recyclerView.setAdapter(adapter);
        }
    }

    private void setupMiniPlayer() {
        // Toggle full player when mini player is clicked
        miniPlayerContainer.setOnClickListener(v -> toggleFullPlayer());
        
        // Play/Pause button
        miniPlayerPlayPause.setOnClickListener(v -> togglePlayPause());
        
        // Next button
        miniPlayerNext.setOnClickListener(v -> playNext());
    }

    private void setupFullPlayer() {
        // Close full player
        fullPlayerContainer.setOnClickListener(v -> {
            // Only close if click is on the background
        });
        
        // Find the close button and set click listener
        ImageButton closeFullPlayer = fullPlayerContainer.findViewById(R.id.closeFullPlayer);
        if (closeFullPlayer != null) {
            closeFullPlayer.setOnClickListener(v -> toggleFullPlayer());
        }
        
        // Play/Pause
        fullPlayerPlayPause.setOnClickListener(v -> togglePlayPause());
        
        // Previous
        fullPlayerPrev.setOnClickListener(v -> playPrevious());
        
        // Next
        fullPlayerNext.setOnClickListener(v -> playNext());
        
        // Shuffle
        fullPlayerShuffle.setOnClickListener(v -> toggleShuffle());
        
        // Repeat
        fullPlayerRepeat.setOnClickListener(v -> toggleRepeat());
        
        // SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicService != null) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Remove updates while user is dragging
                handler.removeCallbacks(updateSeekBarRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume updates
                handler.post(updateSeekBarRunnable);
            }
        });
    }

    private void toggleFullPlayer() {
        if (isFullPlayerVisible) {
            fullPlayerContainer.setVisibility(View.GONE);
            miniPlayerContainer.setVisibility(View.VISIBLE);
            isFullPlayerVisible = false;
        } else {
            fullPlayerContainer.setVisibility(View.VISIBLE);
            miniPlayerContainer.setVisibility(View.GONE);
            isFullPlayerVisible = true;
            
            // Update full player UI with current song
            updateFullPlayerUI();
        }
    }

    private void togglePlayPause() {
        if (musicService != null) {
            if (musicService.isPlaying()) {
                musicService.pause();
            } else {
                musicService.play();
            }
            updatePlayPauseButtons();
        }
    }

    private void playNext() {
        if (musicService != null) {
            musicService.playNext();
            updateUIWithCurrentSong();
            updatePlayPauseButtons();
        }
    }

    private void playPrevious() {
        if (musicService != null) {
            musicService.playPrevious();
            updateUIWithCurrentSong();
            updatePlayPauseButtons();
        }
    }

    private void toggleShuffle() {
        if (musicService != null) {
            musicService.toggleShuffle();
            // Update shuffle button appearance
            boolean isShuffle = musicService.isShuffleEnabled();
            fullPlayerShuffle.setAlpha(isShuffle ? 1.0f : 0.5f);
        }
    }

    private void toggleRepeat() {
        if (musicService != null) {
            int mode = musicService.toggleRepeat();
            // Update repeat button appearance
            switch (mode) {
                case 0: // None
                    fullPlayerRepeat.setAlpha(0.5f);
                    break;
                case 1: // All
                    fullPlayerRepeat.setAlpha(1.0f);
                    break;
                case 2: // One
                    fullPlayerRepeat.setAlpha(1.0f);
                    break;
            }
        }
    }

    private void updateUIWithCurrentSong() {
        if (musicService != null) {
            Music currentSong = musicService.getCurrentSong();
            if (currentSong != null) {
                // Update mini player
                miniPlayerTitle.setText(currentSong.getTitle());
                miniPlayerArtist.setText(currentSong.getArtist());
                
                // Update full player
                fullPlayerTitle.setText(currentSong.getTitle());
                fullPlayerArtist.setText(currentSong.getArtist());
                
                // Update seekbar
                seekBar.setProgress(musicService.getCurrentPosition());
                seekBar.setMax(musicService.getDuration());
                currentTimeText.setText(formatTime(musicService.getCurrentPosition()));
                totalTimeText.setText(formatTime(musicService.getDuration()));
            }
        }
    }

    private void updateFullPlayerUI() {
        if (musicService != null) {
            Music currentSong = musicService.getCurrentSong();
            if (currentSong != null) {
                fullPlayerTitle.setText(currentSong.getTitle());
                fullPlayerArtist.setText(currentSong.getArtist());
                seekBar.setProgress(musicService.getCurrentPosition());
                seekBar.setMax(musicService.getDuration());
                currentTimeText.setText(formatTime(musicService.getCurrentPosition()));
                totalTimeText.setText(formatTime(musicService.getDuration()));
            }
        }
    }

    private void updatePlayPauseButtons() {
        if (musicService != null) {
            boolean isPlaying = musicService.isPlaying();
            miniPlayerPlayPause.setImageResource(isPlaying ? 
                android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
            fullPlayerPlayPause.setImageResource(isPlaying ? 
                android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
        }
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onMusicClick(Music music) {
        if (musicService != null) {
            // Get the position of the clicked song
            int position = songs.indexOf(music);
            if (position != -1) {
                musicService.playSong(position);
                updateUIWithCurrentSong();
                updatePlayPauseButtons();
                
                // Show mini player if not visible
                if (miniPlayerContainer.getVisibility() != View.VISIBLE) {
                    miniPlayerContainer.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isBound) {
            requireActivity().unbindService(serviceConnection);
            isBound = false;
        }
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make sure UI updates when fragment becomes visible
        if (isBound && musicService != null) {
            updateUIWithCurrentSong();
            updatePlayPauseButtons();
            handler.post(updateSeekBarRunnable);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(updateSeekBarRunnable);
    }
}
