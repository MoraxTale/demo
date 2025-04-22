package com.example.demo1;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class MusicPlayer {
    private MediaPlayer mediaPlayer;

    public MusicPlayer(String musicFilePath) {
        File musicFile = new File(musicFilePath);
        if (musicFile.exists()) {
            Media media = new Media(musicFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }
}