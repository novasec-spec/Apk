package com.example.myapp.model;

public class Music {
    private String title;
    private String artist;
    private String path;
    private long duration;
    private long albumId;

    // Constructors
    public Music() {
    }

    public Music(String title, String artist, String path) {
        this.title = title;
        this.artist = artist;
        this.path = path;
    }

    public Music(String title, String artist, String path, long duration) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
    }

    // Getter methods
    public String getTitle() {
        return title != null ? title : "Unknown Title";
    }

    public String getArtist() {
        return artist != null ? artist : "Unknown Artist";
    }

    public String getPath() {
        return path != null ? path : "";
    }

    public long getDuration() {
        return duration;
    }

    public long getAlbumId() {
        return albumId;
    }

    // Setter methods
    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", path='" + path + '\'' +
                ", duration=" + duration +
                '}';
    }
}
