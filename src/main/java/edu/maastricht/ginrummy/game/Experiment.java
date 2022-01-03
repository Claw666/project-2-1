package edu.maastricht.ginrummy.game;

import edu.maastricht.ginrummy.UI.Event.EventListener;
import edu.maastricht.ginrummy.UI.Event.Events.GameEndEvent;
import edu.maastricht.ginrummy.UI.Event.Events.PlayerSwitchEvent;
import edu.maastricht.ginrummy.UI.Event.Events.RoundEndEvent;
import edu.maastricht.ginrummy.UI.Event.Events.RoundStartEvent;
import edu.maastricht.ginrummy.UI.Event.Subscribe;
import edu.maastricht.ginrummy.agents.*;
import edu.maastricht.ginrummy.agents.heuristic.HeuristicAgent;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.ArrayList;
import java.util.List;

public class Experiment {

    public static void main(String[] args) {
        List<Integer> rounds = new ArrayList<>();
        List<Player> winner = new ArrayList<>();
        List<Score> points = new ArrayList<>();
        List<Double[]> dwReduction = new ArrayList<>();
        for(int i = 0; i < 200; i++)
        {
            Tracker tracker = new Tracker();

            Game g = new Game((game) -> {
                rounds.add(game.getRound().getRoundCounter());
                winner.add(game.getWinner());
                points.add(game.getScore());
                dwReduction.add(new Double[] {tracker.getResultP1(), tracker.getResultP2()});
                System.out.println("done - " + System.currentTimeMillis());
            });

            g.getEventHandler().registerListener(tracker);
            g.start(new GreedyAgent(), new MonteCarloAgent2());
        }

        // Turns
        double avgRounds = rounds.stream().mapToInt(e -> e).average().getAsDouble();
        // Points
        double avgPointsA = points.stream().mapToInt(Score::getScoreP1).average().getAsDouble();
        double avgPointsB = points.stream().mapToInt(Score::getScoreP2).average().getAsDouble();
        // Domination
        double dominationA = (double) points.stream().mapToInt(Score::getScoreP1).sum() / points.stream().mapToInt(e -> e.getScoreP1() + e.getScoreP2()).sum();
        double dominationB = 1 - dominationA;
        // Win-Rate
        double winRateA = (double) winner.stream().filter(e -> e == Player.P1).count() / winner.size();
        double winRateB = 1 - winRateA;
        // DW Reduction
        double avgReductionA = dwReduction.stream().mapToDouble(e -> e[0]).average().getAsDouble();
        double avgReductionB = dwReduction.stream().mapToDouble(e -> e[1]).average().getAsDouble();

        System.out.printf("Avg. rounds: %.2f\n", avgRounds);
        System.out.printf("Avg. points: %.4f|%.4f\n", avgPointsA, avgPointsB);
        System.out.printf("Domination: %.4f|%.4f\n", dominationA, dominationB);
        System.out.printf("Win Rate: %.4f|%.4f\n", winRateA, winRateB);
        System.out.printf("Reduction: %.4f|%.4f\n", avgReductionA, avgReductionB);
    }

    public static class Tracker implements EventListener
    {

        private Round round;

        private List<Integer> player1 = new ArrayList<>();
        private int player1DW = -1;

        private List<Integer> player2 = new ArrayList<>();
        private int player2DW = -1;

        public Tracker()
        {
        }

        public double getResultP1()
        {
            return this.player1.stream().mapToInt(e -> e).average().getAsDouble();
        }

        public double getResultP2()
        {
            return this.player2.stream().mapToInt(e -> e).average().getAsDouble();
        }

        @Subscribe
        public void onSwitch(PlayerSwitchEvent event)
        {
            final int dw = AdvancedMeldDetector.find(event.getOld().getCardDeck()).deadwoodValue();
            //System.out.println("switch");
            if(event.getOld() == round.getPlayer1())
            {
                player1.add(dw - player1DW);
                player1DW = dw;
            }
            else
            {
                player2.add(dw - player2DW);
                player2DW = dw;
            }
        }

        @Subscribe
        public void onStart(RoundStartEvent event)
        {
            this.round = event.getRound();
            player1DW = AdvancedMeldDetector.find(event.getRound().getPlayer1().getCardDeck()).deadwoodValue();
            player2DW = AdvancedMeldDetector.find(event.getRound().getPlayer2().getCardDeck()).deadwoodValue();
        }

    }


}
