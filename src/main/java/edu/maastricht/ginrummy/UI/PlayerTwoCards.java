package edu.maastricht.ginrummy.UI;

import edu.maastricht.ginrummy.agents.AskHumanGUIAgent;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardImages;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.Collections;
import java.util.List;

public class PlayerTwoCards extends HBox {

    private final MouseGestures mg = new MouseGestures();

    private final ImageView cardBack = new ImageView(CardImages.getBack());

    private GameUI gameUI;

    public  PlayerTwoCards(GameUI gameUI) {
        this.gameUI = gameUI;
        setPadding(new Insets(20,20,20,20));
        setPrefHeight(200.0);
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
        showBackOfCards();

    }

    public void showBackOfCards()
    {
        getChildren().clear();
        for (int i = 0; i < 10 ; i ++) {
            getChildren().add(createBack());
        }
    }

    public void showFaceOfCards()
    {

        Platform.runLater(() -> {
            IAgent opponentPlayer = gameUI.getGameGUI().getRound().getOpponentPlayer();
            final List<Card> cards = Collections.synchronizedList(opponentPlayer.getCardDeck());

            getChildren().clear();
            cards.forEach(card -> this.getChildren().add(createImageView(cards, card)));
        });

    }

    private ImageView createImageView(List<Card> cards, Card card)
    {

        ImageView cardImg = new ImageView(CardImages.getCard(card));
        cardImg.setFitHeight(180);
        cardImg.setScaleX(0);
        cardImg.setPreserveRatio(true);

        cardImg.setUserData(card);

        {
            ScaleTransition stHideBack = new ScaleTransition(Duration.millis(300));
            stHideBack.setNode(cardBack);
            stHideBack.setFromX(1);
            stHideBack.setToX(0);
            stHideBack.play();

            ScaleTransition stShowFront = new ScaleTransition(Duration.millis(300));
            stShowFront.setNode(cardImg);
            stShowFront.setFromX(0);
            stShowFront.setToX(1);
            stHideBack.setOnFinished(event -> {
                stShowFront.play();
            });
        }

        cardImg.setOnMousePressed(event -> {
            if (gameUI.getGameState() == GameUI.GameState.WHAT_TO_DISCARD) {
                ((AskHumanGUIAgent) gameUI.getGameGUI().getRound().getCurrentPlayer()).wakeup(ignore -> {
                    this.gameUI.setDiscardedCard(cards.indexOf(card));
                });
            }
        });

        return cardImg;
    }



    private ImageView createBack()
    {

        ImageView cardImg = new ImageView(CardImages.getBack());
        cardImg.setFitHeight(180);
        cardImg.setScaleX(0);
        cardImg.setPreserveRatio(true);
        mg.makeDraggable(cardImg);
        //cardImg.setUserData(card);

        {
            ScaleTransition stShowBack = new ScaleTransition(Duration.millis(300));
            stShowBack.setNode(cardImg);
            stShowBack.setFromX(0);
            stShowBack.setToX(1);
            stShowBack.play();
        }

        return cardImg;
    }
}
