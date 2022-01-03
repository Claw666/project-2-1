package edu.maastricht.ginrummy.UI;

import edu.maastricht.ginrummy.game.GameGUI;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.concurrent.locks.ReentrantLock;

public class StartScreen {
    private GameUI gameUI;
    private ReentrantLock readyLock = new ReentrantLock();

    public StartScreen(GameGUI gameGUI, Stage stage) {
        readyLock.lock();

        VBox mainPage = new VBox();
        mainPage.setSpacing(100);
        Text title = new Text("Gim Rummy Card Game");
        title.getStyleClass().add("title");
        //Play Button Setup
        Button playBttn = new Button("PLAY");
        playBttn.getStyleClass().add("play_bttn");
        playBttn.setOnMouseClicked(event -> {
            this.gameUI = new GameUI(gameGUI, stage);
            readyLock.unlock();
        });

        //Add main container pref plus elements to it
        mainPage.setAlignment(Pos.CENTER);
        mainPage.setPrefHeight(300);
        mainPage.getChildren().addAll(title,playBttn);

        //Add scene
        Scene scene = new Scene(mainPage);

        //Add external stylesheet
        scene.getStylesheets().add("Main.css");

        //set Stage boundaries to visible bounds of the main screen
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());

        //Stage setup
        stage.setTitle("Gim Rummy Game");
        stage.setScene(scene);
        stage.show();
    }

    public GameUI getGameUI()
    {
        return gameUI;
    }

    public ReentrantLock getReadyLock()
    {
        return readyLock;
    }
}

