package com.example.mp3player;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.media.Media;
import javafx.stage.WindowEvent;
import javafx.util.Duration;


public class SongsPlayer extends Application {
    private Label songName;
    private Slider volume;
    private ProgressBar songProgress;
    private Media media;
    private MediaPlayer mp;
    private ArrayList<File> songs;
    private int songNumber;
    private Timer timer;
    private boolean running;
    private boolean pauseFlag = true;
    private Image icon;
    private ImageView imgView;

    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        root.setMaxHeight(165);
        root.setMaxWidth(654.0);
        root.setMinHeight(165);
        root.setStyle("-fx-background-color: #000000;");

        songName = new Label("Welcome back, mate");
        songName.setPrefHeight(100.0);
        songName.setPrefWidth(654.0);
        songName.setAlignment(Pos.CENTER);
        songName.setFont(new Font("System",30));
        songName.setTextFill(Color.web("#00ff1a"));

        songProgress = new ProgressBar();
        songProgress.setStyle("-fx-accent: #00ff1a;");
        songProgress.setPrefWidth(654);
        songProgress.setProgress(0);

        HBox buttonPanel = new HBox();
        buttonPanel.setPrefWidth(654.0);
        buttonPanel.setAlignment(Pos.CENTER);

        Button play = new Button();
        icon = new Image(new File("images/icon-play.png").toURI().toString());
        imgView = new ImageView(icon);
        imgView.setFitHeight(23);
        imgView.setPreserveRatio(true);
        play.setPrefHeight(46);
        play.setPrefWidth(85);
        play.setGraphic(imgView);
        play.setFont(new Font("System",18));

        Button reset = new Button();
        reset.setPrefHeight(46);
        reset.setPrefWidth(85);
        icon = new Image(new File("images/icon-replay.png").toURI().toString());
        imgView = new ImageView(icon);
        imgView.setFitHeight(23);
        imgView.setPreserveRatio(true);
        reset.setGraphic(imgView);
        reset.setFont(new Font("System",18));

        Button rewind = new Button();
        rewind.setPrefHeight(46);
        rewind.setPrefWidth(85);
        icon = new Image(new File("images/icon-rewind.png").toURI().toString());
        imgView = new ImageView(icon);
        imgView.setFitHeight(23);
        imgView.setPreserveRatio(true);
        rewind.setGraphic(imgView);
        rewind.setFont(new Font("System",18));

        Button fastForward = new Button();
        fastForward.setPrefHeight(46);
        fastForward.setPrefWidth(85);
        icon = new Image(new File("images/icon-fast-forward.png").toURI().toString());
        imgView = new ImageView(icon);
        imgView.setFitHeight(23);
        imgView.setPreserveRatio(true);
        fastForward.setGraphic(imgView);
        fastForward.setFont(new Font("System",18));

        Button next = new Button();
        next.setPrefHeight(46);
        next.setPrefWidth(85);
        icon = new Image(new File("images/icon-next.png").toURI().toString());
        imgView = new ImageView(icon);
        imgView.setFitHeight(23);
        imgView.setPreserveRatio(true);
        next.setGraphic(imgView);
        next.setFont(new Font("System",18));

        Button prev = new Button();
        prev.setPrefHeight(46);
        prev.setPrefWidth(85);
        icon = new Image(new File("images/icon-back.png").toURI().toString());
        imgView = new ImageView(icon);
        imgView.setFitHeight(23);
        imgView.setPreserveRatio(true);
        prev.setGraphic(imgView);
        prev.setFont(new Font("System",18));

        volume = new Slider();
        volume.setPrefHeight(14);
        volume.setPrefWidth(149);
        volume.setValue(50);

        buttonPanel.getChildren().addAll(prev, rewind, play, fastForward, next, reset, volume);
        root.getChildren().addAll(songName, songProgress, buttonPanel);

        play.addEventFilter(MouseEvent.MOUSE_CLICKED,event -> {
            if(pauseFlag) {
                pauseFlag = false;
                icon = new Image(new File("images/icon-pause.png").toURI().toString());
                playMedia();
            }
            else {
                pauseFlag = true;
                icon = new Image(new File("images/icon-play.png").toURI().toString());
                pauseMedia();
            }
            imgView = new ImageView(icon);
            imgView.setFitHeight(23);
            imgView.setPreserveRatio(true);
            play.setGraphic(imgView);

        });

        reset.addEventFilter(MouseEvent.MOUSE_CLICKED,event -> resetMedia());

        volume.valueProperty().addListener((observableValue, number, t1) -> mp.setVolume(t1.doubleValue() * 0.01));

        fastForward.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> mp.seek(Duration.seconds(mp.getCurrentTime().toSeconds()+10)));

        rewind.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> mp.seek(Duration.seconds(mp.getCurrentTime().toSeconds()-10)));

        next.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> nextTrack());

        prev.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> previousTrack());

        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setTitle("MP3Player");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });

        initialize();
    }

    void initialize() {
        songs = new ArrayList<>();
        File[] files = (new File("music").listFiles());

        if(files != null) Collections.addAll(songs, files);

        media = new Media(songs.get(songNumber).toURI().toString());
        mp = new MediaPlayer(media);
        String name = songs.get(songNumber).getName();
        songName.setText(name.substring(0,name.length()-4));
    }

    void playMedia() {
        beginTimer();
        mp.setVolume(volume.getValue() * 0.01);
        mp.play();
    }

    void resetMedia() {
        songProgress.setProgress(0);
        mp.seek(Duration.seconds(0));
    }

    void previousTrack() {
        if(songNumber > 0) songNumber--;
        else songNumber = songs.size() - 1;
        mp.stop();
        if(running) cancelTimer();
        media = new Media(songs.get(songNumber).toURI().toString());
        mp = new MediaPlayer(media);
        String name = songs.get(songNumber).getName();
        songName.setText(name.substring(0,name.length()-4));
        playMedia();
    }

    void nextTrack() {
        if(songNumber == songs.size() - 1) songNumber = 0;
        else songNumber++;
        mp.stop();
        if(running) cancelTimer();
        media = new Media(songs.get(songNumber).toURI().toString());
        mp = new MediaPlayer(media);
        String name = songs.get(songNumber).getName();
        songName.setText(name.substring(0,name.length()-4));
        playMedia();
    }

    void beginTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                running = true;
                double current = mp.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgress.setProgress(current / end);
                if (current / end == 1) cancelTimer();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 500);
    }

    void cancelTimer() {
        running = false;
        timer.cancel();
    }

    void pauseMedia() {
        cancelTimer();
        mp.pause();
    }

    public static void main(String[] args) {
        launch();
    }
}