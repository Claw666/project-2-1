package edu.maastricht.ginrummy.game;

import edu.maastricht.ginrummy.UI.Event.EventHandler;
import edu.maastricht.ginrummy.agents.*;
import edu.maastricht.ginrummy.melding.advanced.Callback;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {

    public static void main(String[] args) {

        try {
            BufferedWriter raw = new BufferedWriter(new FileWriter("./data/montecarlo_greedy_extended_extended_raw.csv"));
            BufferedWriter data = new BufferedWriter(new FileWriter("./data/montecarlo_greedy_extended_extended_data.csv"));
            long time = System.currentTimeMillis();
            for(int searchDepth = 5; searchDepth <= 50; searchDepth++)
            {
                for(int evaluationsPerActions = 1; evaluationsPerActions <= 1001; evaluationsPerActions += 50)
                {
                    final List<Integer> rounds = new ArrayList<>();
                    final List<Player> players = new ArrayList<>();

                    for(int i = 0; i < 400; i++)
                    {
                        int finalEvaluationsPerActions = evaluationsPerActions;
                        int finalSearchDepth = searchDepth;
                        int finalI = i;

                        new Game((game) -> {
                            players.add(game.getWinner());
                            rounds.add(game.getRound().getRoundCounter());

                            try {
                                raw.write(String.format("%d,%d,%d,%d,%d,%d,%s\n",
                                        finalEvaluationsPerActions, finalSearchDepth, finalI, game.getRound().getRoundCounter(),
                                        game.getScore().getScoreP1(), game.getScore().getScoreP2(), game.getWinner().name()));
                                raw.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("done - " + System.currentTimeMillis());
                        }).start(new MonteCarloAgent2(
                                new RandomAgent(), new RandomAgent(), evaluationsPerActions, searchDepth
                        ), new GreedyAgent());
                        System.out.println(evaluationsPerActions + " " + - searchDepth);
                    }

                    data.write(String.format("%d,%d,%.4f\n",
                            evaluationsPerActions, searchDepth, ((double)players.stream().filter(e -> e == Player.P1).count() / players.size())));
                    data.flush();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //----
    private final EventHandler eventHandler = new EventHandler();
    private Round round;
    private Player winner;
    private Score score;
    private Callback<Game> callback;

    public Game(Callback<Game> callback)
    {
        this.callback = callback;
    }

    public Player getWinner() {
        return winner;
    }

    public Score getScore() {
        return score;
    }

    public void start(IAgent agentA, IAgent agentB)
    {
        Score gameScore = new Score(0, 0);
        this.round = new Round(gameScore, this.eventHandler, agentA, agentB);

        while (true)
        {
            gameScore.add(round.play());
            System.out.println(gameScore);
            if(gameScore.checkForWinner() != Player.NONE)
            {
                break;
            }
        }

        winner = gameScore.checkForWinner();
        score = gameScore;
        System.out.printf("%d - %d%n", gameScore.getScoreP1(), gameScore.getScoreP2());
        System.out.println(gameScore.checkForWinner());
        System.out.println("game");
        this.callback.apply(this);
    }

    public Round getRound() {
        return round;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }
}
