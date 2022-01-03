package edu.maastricht.ginrummy.game;

import edu.maastricht.ginrummy.UI.Event.EventHandler;
import edu.maastricht.ginrummy.UI.Event.Events.GameEndEvent;
import edu.maastricht.ginrummy.UI.Event.Events.RoundEndEvent;
import edu.maastricht.ginrummy.UI.StartScreen;
import edu.maastricht.ginrummy.agents.AskHumanGUIAgent;
import edu.maastricht.ginrummy.util.Log;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GameGUI extends Application {

    public final static Random _random = new Random();
    private StartScreen startScreen;
    private Round round;
    private final EventHandler eventHandler = new EventHandler();
    private Score gameScore = new Score(0, 0);

    public GameGUI()
    {
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public StartScreen getStartScreen() {
        return startScreen;
    }

    public Round getRound() {
        return round;
    }

    public Score getGameScore() {
        return gameScore;
    }

    public void start(Stage stage){
        this.startScreen =  new StartScreen(this, stage);

        Thread thread = new Thread(() -> {

            //--- This is actually better than busy-waiting
            while (true) {
                try {
                    if (startScreen.getReadyLock().tryLock(500, TimeUnit.MILLISECONDS)) break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            gameScore = new Score(0, 0);
            while (true)
            {
                gameScore.add(round.play());
                if (gameScore.checkForWinner() != Player.NONE) {
                    break;
                }
                Log.logger.info("The current score is {}", gameScore);
                eventHandler.trigger(new RoundEndEvent(round));
            }
            Log.logger.info("{} won with {}", gameScore.checkForWinner(), gameScore);
            eventHandler.trigger(new GameEndEvent(
                    gameScore,
                    round.getCurrentPlayer(),
                    round.getOpponentPlayer()
            ));

        });

        this.round = new Round(gameScore, this.eventHandler, new AskHumanGUIAgent(this, thread), new AskHumanGUIAgent(this, thread));
        thread.setName("GameLogic-Thread");
        thread.start();

    }

    public static void main(String[] args)
    {
        Application.launch(args);
    }
}
