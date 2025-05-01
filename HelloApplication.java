package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;


public class HelloApplication extends Application {
    private MusicPlayer musicPlayer;
    @Override
    public void start(Stage stage) throws IOException {
        // 初始化音乐播放器
        musicPlayer = new MusicPlayer("E:\\JAVA FX\\demo\\Music\\main.mp3");
        musicPlayer.setVolume(0.5); // 设置音量
        musicPlayer.play(); // 播放音乐
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 580);
        stage.setTitle("我独自修仙");
        stage.setScene(scene);
        stage.show();//
    }
    @Override
    public void stop() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}
