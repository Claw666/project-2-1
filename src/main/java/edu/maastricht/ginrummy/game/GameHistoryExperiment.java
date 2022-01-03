package edu.maastricht.ginrummy.game;

import edu.maastricht.ginrummy.UI.Event.EventListener;
import edu.maastricht.ginrummy.UI.Event.Events.PlayerSwitchEvent;
import edu.maastricht.ginrummy.UI.Event.Subscribe;
import edu.maastricht.ginrummy.agents.*;
import edu.maastricht.ginrummy.agents.heuristic.HeuristicAgent;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class GameHistoryExperiment implements EventListener {

    public static void main(String[] args)
    {
        new GameHistoryExperiment();
    }

    private List<Integer> p1HistoryDW = new LinkedList<>();
    private List<Integer> p1HistoryPoints = new LinkedList<>();
    private List<Integer> p2HistoryDW = new LinkedList<>();
    private List<Integer> p2HistoryPoints = new LinkedList<>();

    public GameHistoryExperiment()
    {

        Game game = new Game(value -> {
            try {
                BufferedWriter file = new BufferedWriter(new FileWriter("./data/games/h-greedy-montecarlo_greedy.csv"));
                file.write("Round, H(G,MC) Points, H(G,MC) Deadwood, Greedy Points, Greedy Deadwood\n");

                for(int i = 0; i < p1HistoryPoints.size(); i++)
                {
                    file.write(String.format("%d,%d,%d,%d,%d\n", i,
                            p1HistoryPoints.get(i), p1HistoryDW.get(i),
                            p2HistoryPoints.get(i), p2HistoryDW.get(i)
                        ));
                }
                file.flush();
                file.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        final IAgent aAgent = new HybridAgent(new GreedyAgent(), new MonteCarloAgent2());
        final IAgent bAgent = new GreedyAgent();
        game.getEventHandler().registerListener(this);
        game.start(aAgent, bAgent);

    }

    @Subscribe
    public void onSwitch(PlayerSwitchEvent event)
    {
        p1HistoryDW.add(AdvancedMeldDetector.find(event.getRound().getPlayer1().getCardDeck()).deadwoodValue());
        p1HistoryPoints.add(event.getCurrentScore().getScoreP1());
        p2HistoryDW.add(AdvancedMeldDetector.find(event.getRound().getPlayer2().getCardDeck()).deadwoodValue());
        p2HistoryPoints.add(event.getCurrentScore().getScoreP2());
    }

}
