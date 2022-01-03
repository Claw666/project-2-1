package edu.maastricht.ginrummy.UI;

import edu.maastricht.ginrummy.game.GameGUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class WinningScene {
    public WinningScene(GameGUI gameGUI, Stage stage) {
        VBox winningPage = new VBox();
        HBox buttonCont = new HBox(50);
        buttonCont.setAlignment(Pos.CENTER);

        winningPage.setSpacing(100);
        Text title = new Text("You just WON! Congratulations!");
        title.getStyleClass().add("title");
        //Play Button Setup
        Button playBttn = new Button("PLAY NEW GAME");
        Button exitButtn = new Button("EXIT GAME");
        buttonCont.getChildren().addAll(playBttn,exitButtn);
        playBttn.getStyleClass().add("play_bttn");
        exitButtn.getStyleClass().add("play_bttn");


        playBttn.setOnMouseClicked(event -> {
            new GameUI(gameGUI, stage);
        });

        exitButtn.setOnMouseClicked(event -> {
            GameGUI startScreen = new GameGUI();
            startScreen.start(stage);
        });


        winningPage.setAlignment(Pos.CENTER);
        winningPage.setPrefHeight(300);
        winningPage.getChildren().addAll(title,buttonCont);

        //Stage setup
        Scene scene = new Scene(winningPage);
        scene.getStylesheets().add("Main.css");
        stage.setScene(scene);
        stage.show();
    }
}
