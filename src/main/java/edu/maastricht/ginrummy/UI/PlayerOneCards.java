package edu.maastricht.ginrummy.UI;

import edu.maastricht.ginrummy.agents.AskHumanGUIAgent;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.agents.IHumanAgent;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardImages;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PlayerOneCards extends HBox {

    private final MouseGestures mg = new MouseGestures();

    private final ImageView cardBack = new ImageView(CardImages.getBack());


    private GameUI gameUI;

    public PlayerOneCards(GameUI gameUI) {
        this.gameUI = gameUI;
        setPadding(new Insets(20,20,20,20));
        setPrefHeight(200.0);
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        //Test cards
        updateCards(null, null);
    }

    public void updateCards(List<Card> added, List<Card> removed)
    {
        Platform.runLater(() -> {
            IAgent currentPlayer = gameUI.getGameGUI().getRound().getCurrentPlayer();

            if (currentPlayer instanceof IHumanAgent)
            {
                final List<Card> cards = Collections.synchronizedList(currentPlayer.getCardDeck());

                gameUI.getDeadwoodScore().setText(String.valueOf(AdvancedMeldDetector.find(currentPlayer.getCardDeck())));
                if(added == null && removed == null)
                {
                    getChildren().clear();
                    cards.forEach(card -> this.getChildren().add(createImageView(cards, card)));
                }
                else if(removed != null)
                {
                    for(int i = 0; i < removed.size(); i++)
                    {
                        ImageView view = new ImageView(CardImages.getCard(removed.get(i)));
                        view.setFitHeight(180);
                        view.setPreserveRatio(true);
                        view.setVisible(true);
                        getChildren().add(getChildren().size()+i, createImageView(cards, removed.get(i)));
                    }
                }
                else if(added != null)
                {
                    Iterator<Node> iterator = getChildren().iterator();

                    while(iterator.hasNext())
                    {
                        Node n = iterator.next();
                        if(added.contains(n.getUserData()))
                        {
                            n.setVisible(false);
                        }
                    }
                }

            }

        });

    }

    private ImageView createImageView(List<Card> cards, Card card)
    {

        ImageView cardImg = new ImageView(CardImages.getCard(card));
        cardImg.setFitHeight(180);
        cardImg.setScaleX(0);
        cardImg.setPreserveRatio(true);

        cardImg.setUserData(card);
        //TODO: make human player cards draggable and reorderable
        mg.makeDraggable(cardImg);

        {
            ScaleTransition stHideBack = new ScaleTransition(Duration.millis(300));
            stHideBack.setNode(cardBack);
            stHideBack.setFromX(1);
            stHideBack.setToX(0);
            stHideBack.play();


            ScaleTransition stShowFront = new ScaleTransition(Duration.millis(300));
            stShowFront.setNode(cardImg);
            stShowFront.setFromX(0);
            stShowFront.setInterpolator(Interpolator.LINEAR);
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


}
