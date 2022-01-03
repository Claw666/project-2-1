package edu.maastricht.ginrummy.UI;

import edu.maastricht.ginrummy.UI.Event.EventListener;
import edu.maastricht.ginrummy.UI.Event.Events.*;
import edu.maastricht.ginrummy.UI.Event.Subscribe;
import edu.maastricht.ginrummy.agents.AskHumanGUIAgent;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.game.GameGUI;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;
import edu.maastricht.ginrummy.melding.advanced.Callback;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import static javafx.application.Platform.*;

public class GameUI implements EventListener {

    private final GameGUI gameGUI;

    private GameState gameState;
    private IAgent.CardPickD cardPickD;
    private int discardedCard;
    private boolean doYouKnock;
    private boolean isSkipping;

    private Button knockBttn;
    private Button knockNegBttn;

    private Button skipBttn;
    private Button skipNegBttn;

    private final DeckView aDeckView;
    private final DiscardPileView aDiscardPile;
    private final PlayerOneCards player1Cards;
    private final PlayerTwoCards player2Cards;

    //----
    private final Text deadwoodValue = new Text("0");
    private final Text player1Score = new Text("0");
    private final Text player2Score = new Text("0");

    private Stage stage;

    public GameUI(GameGUI gameGUI, Stage stage) {

        this.gameGUI = gameGUI;
        this.stage = stage;

        this.aDeckView = new DeckView();
        this.aDiscardPile = new DiscardPileView(this);
        this.player1Cards = new PlayerOneCards(this);
        this.player2Cards = new PlayerTwoCards(this);;

        gameGUI.getEventHandler().registerListener(this);

        HBox outsideContainer = new HBox();
        VBox sideBar = new VBox();
        VBox boardContainer = new VBox();
        HBox upperBoardCont = new HBox();
        BorderPane lowerBoardCont = new BorderPane();
        HBox lowerMenuBar = new HBox();


        Button exitBttn = new Button("EXIT");
        exitBttn.getStyleClass().add("play_bttn");
        Button fullScreenBttn = new Button("FULL SCREEN");
        fullScreenBttn.getStyleClass().add("play_bttn");

        Text deadwoodLabel = new Text("Deadwood Value: ");
        Text player1ScoreLabel = new Text("Player 1: ");
        Text player2ScoreLabel = new Text("Player 2: ");
        Text scoreLabel = new Text("Score: ");
        scoreLabel.setStyle("-fx-font-size: 1.5em;");
        player1ScoreLabel.setStyle("-fx-font-size: 1.3em;");
        player2ScoreLabel.setStyle("-fx-font-size: 1.3em;");
        player1Score.setStyle("-fx-font-size: 1.3em; -fx-font-weight: bold");
        player2Score.setStyle("-fx-font-size: 1.3em; -fx-font-weight: bold");
        deadwoodLabel.setStyle("-fx-font-size: 2em;");
        deadwoodValue.setStyle("-fx-font-size: 2em;");

        {
            Button[] buttons = createOptionButtons(GameState.DO_YOU_KNOCK, "KNOCK", "DON'T KNOCK", value -> {
                this.doYouKnock = value;
            });
            knockBttn = buttons[0];
            knockNegBttn = buttons[1];
        }

        {
            Button[] buttons = createOptionButtons(GameState.DO_YOU_SKIP, "SKIP", "DON'T SKIP", value -> this.isSkipping = value);
            skipBttn = buttons[0];
            skipNegBttn = buttons[1];
        }

        HBox deadwoodCont = new HBox();
        deadwoodCont.getChildren().addAll(deadwoodLabel,deadwoodValue);
        deadwoodCont.setPadding(new Insets(0,0,20,20));

        outsideContainer.getChildren().addAll(boardContainer,sideBar);
        HBox.setHgrow(sideBar,Priority.ALWAYS);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        boardContainer.setPrefWidth(primaryScreenBounds.getWidth()-250);
        boardContainer.getStyleClass().add("board");

        boardContainer.getChildren().addAll(upperBoardCont,lowerBoardCont,lowerMenuBar);


        upperBoardCont.setPrefHeight(primaryScreenBounds.getHeight()/2.0 - 50);
        upperBoardCont.getStyleClass().add("upperBoard");
        upperBoardCont.setAlignment(Pos.CENTER_LEFT);
        upperBoardCont.getChildren().addAll(player2Cards);

        lowerBoardCont.setPrefHeight(primaryScreenBounds.getHeight()/2.0 - 50);
        lowerBoardCont.getStyleClass().add("lowerBoard");
        lowerBoardCont.setCenter(player1Cards);
        lowerBoardCont.setBottom(deadwoodCont);



        lowerMenuBar.getChildren().addAll(knockBttn,knockNegBttn,skipBttn,skipNegBttn,fullScreenBttn,exitBttn, scoreLabel, player1ScoreLabel,player1Score,player2ScoreLabel,player2Score);
        lowerMenuBar.setAlignment(Pos.CENTER_LEFT);
        lowerMenuBar.setSpacing(50);
        lowerMenuBar.setPadding(new Insets(0, 0 , 0 ,50));
        lowerMenuBar.setPrefHeight(100);

        sideBar.getChildren().addAll(aDiscardPile,aDeckView);
        sideBar.setSpacing(100);

        sideBar.getStyleClass().add("sideBar");
        sideBar.setAlignment(Pos.CENTER);
        sideBar.setPadding(new Insets(0,0,0,0));

        exitBttn.setOnAction(event -> {
                GameGUI startScreen = new GameGUI();
                startScreen.start(stage);
        });

        fullScreenBttn.setOnAction(event -> {
                stage.setFullScreen(true);
        });

        aDeckView.button.setOnMouseClicked(event -> {
            if(gameState == GameState.PICK_FROM_DISCARD_OR_STOCK)
            {
                ((AskHumanGUIAgent) gameGUI.getRound().getCurrentPlayer()).wakeup(ignore -> {
                    this.cardPickD = IAgent.CardPickD.STACK;
                });
            }
        });

        aDiscardPile.setOnMouseClicked(event -> {
            if(gameState == GameState.PICK_FROM_DISCARD_OR_STOCK)
            {
                ((AskHumanGUIAgent) gameGUI.getRound().getCurrentPlayer()).wakeup(ignore -> {
                    this.cardPickD = IAgent.CardPickD.DISCARD;
                });
            }
        });


        Scene scene = new Scene(outsideContainer);
        scene.getStylesheets().add("Main.css");
        stage.setScene(scene);
        stage.show();
    }

    private Button[] createOptionButtons(GameState gameState, String accept, String deny, Callback<Boolean> click)
    {
        Button denyButton = new Button(deny);
        denyButton.setDisable(true);
        denyButton.getStyleClass().add("play_bttn");

        Button acceptButton = new Button(accept);
        acceptButton.setDisable(true);
        acceptButton.getStyleClass().add("play_bttn");

        acceptButton.setOnAction(event -> {
            if(this.gameState == gameState)
            {
                ((AskHumanGUIAgent) gameGUI.getRound().getCurrentPlayer()).wakeup(ignore -> {
                    click.apply(true);
                    runLater(() -> {
                        acceptButton.setDisable(true);
                        denyButton.setDisable(true);
                    });
                });
            }
        });

        denyButton.setOnAction(event -> {
            if(this.gameState == gameState)
            {
                ((AskHumanGUIAgent) gameGUI.getRound().getCurrentPlayer()).wakeup(ignore -> {
                    click.apply(false);
                    runLater(() -> {
                        acceptButton.setDisable(true);
                        denyButton.setDisable(true);
                    });
                });
            }
        });
        return new Button[] {acceptButton, denyButton};
    }

    @Subscribe
    public void onRoundStart(RoundStartEvent event)
    {
        player1Cards.updateCards(null, null);
        aDiscardPile.updateCards(event.getRound().getDiscardDeck());
        runLater(() -> {
            deadwoodValue.setText(String.valueOf(AdvancedMeldDetector.find(event.getRound().getCurrentPlayer().getCardDeck()).deadwoodValue()));
        });
    }

    @Subscribe
    public void onUpdateDiscardPile(PlayerPickedEvent.Discard playerPickedDiscard, PlayerPickedEvent.Stock playerPickedStock,
                                    PlayerSwitchEvent playerSwitchEvent)
    {
        if(playerSwitchEvent != null)
        {
            player1Cards.updateCards(null, null);
            player1Score.setText(String.valueOf(gameGUI.getGameScore().getScoreP1()));
            player2Score.setText(String.valueOf(gameGUI.getGameScore().getScoreP2()));
        }
        else if(playerPickedDiscard != null || playerPickedStock != null)
        {
            PlayerPickedEvent event = playerPickedDiscard;
            if(event == null)
            {
                event = playerPickedStock;
            }
            player1Cards.updateCards(event.getAdded(), event.getRemoved());

        }

        runLater(() -> {
            //TODO synchronise ginRummy.getRound().getCurrentPlayer().getCardDeck()
            deadwoodValue.setText(String.valueOf(AdvancedMeldDetector.find(gameGUI.getRound().getCurrentPlayer().getCardDeck()).deadwoodValue()));
        });
    }

    @Subscribe
    public void onGameEnd(GameEndEvent event)
    {
        runLater(() -> {
            new WinningScene(this.gameGUI, this.stage);
        });
    }

    @Subscribe
    public void onRoundEnd(RoundEndEvent event)
    {
        runLater(() -> {
            player1Score.setText(String.valueOf(gameGUI.getGameScore().getScoreP1()));
            player2Score.setText(String.valueOf(gameGUI.getGameScore().getScoreP2()));
            new newRoundPopUp(this.stage);
        });
    }

    public GameGUI getGameGUI() {
        return this.gameGUI;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState)
    {
        this.gameState = gameState;

        if(this.gameState == GameState.DO_YOU_KNOCK)
        {
            runLater(() -> {
                knockBttn.setDisable(false);
                knockNegBttn.setDisable(false);
            });
        }
        else if(this.gameState == GameState.DO_YOU_SKIP)
        {
            runLater(() -> {
                skipBttn.setDisable(false);
                skipNegBttn.setDisable(false);
            });
        }
        else if(this.gameState == GameState.WAIT_FOR_PLAYER_SWITCH)
        {
            //TODO :DisplaySwitchScreen
            runLater(() -> {
                ((AskHumanGUIAgent) gameGUI.getRound().getCurrentPlayer()).wakeup(ignore -> {});
            });
        }
    }

    //----
    public IAgent.CardPickD getCardPickD() {
        return cardPickD;
    }
    public int getDiscardedCard()
    {
        return this.discardedCard;
    }

    public void setDiscardedCard(int  discardedCard)
    {
        this.discardedCard = discardedCard;
    }

    public boolean isKnocking() {
        return doYouKnock;
    }

    public boolean isSkipping() {
        return isSkipping;
    }

    public Text getDeadwoodScore() {
        return this.deadwoodValue;
    }

    public enum GameState
    {

        PICK_FROM_DISCARD_OR_STOCK,
        WHAT_TO_DISCARD,
        DO_LAYOFF,
        REORDER_DECK,

        DO_YOU_KNOCK,
        DO_YOU_SKIP,
        WAIT_FOR_PLAYER_SWITCH,

        IDLE,
    }

}
