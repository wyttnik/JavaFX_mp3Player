package com.example.mp3player;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlayerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}