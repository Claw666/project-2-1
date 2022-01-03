package edu.maastricht.ginrummy.UI;

import edu.maastricht.ginrummy.game.GameGUI;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class newRoundPopUp {

    public newRoundPopUp(Stage stage) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        Text winnerLabel = new Text("Winner: ");
        Text winner = new Text("Player 1");
        HBox winnerCont = new HBox();
        winnerCont.setAlignment(Pos.CENTER);
        winnerCont.getChildren().addAll(winnerLabel,winner);
        winnerCont.setStyle("-fx-font-size: 3em; -fx-font-weight: bold");
        Text question = new Text("What do you want to do?");
        Button newRoundBttn = new Button("NEW ROUND");
        newRoundBttn.getStyleClass().add("play_bttn");
        Button exitRound = new Button("EXIT");
        exitRound.getStyleClass().add("play_bttn");
        HBox bttnCont = new HBox(100);
        bttnCont.getChildren().addAll(newRoundBttn,exitRound);
        bttnCont.setAlignment(Pos.CENTER);
        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().addAll(winnerCont,question,bttnCont);

        exitRound.setOnAction(event -> {
            GameGUI startScreen = new GameGUI();
            startScreen.start(stage);
            dialog.close();
        });

        newRoundBttn.setOnAction(event -> {
            dialog.close();
        });

        Scene dialogScene = new Scene(dialogVbox, 500, 300);
        dialogScene.getStylesheets().add("Main.css");
        dialog.setScene(dialogScene);
        dialog.setTitle("New round setup");
        dialog.show();
    }
}
