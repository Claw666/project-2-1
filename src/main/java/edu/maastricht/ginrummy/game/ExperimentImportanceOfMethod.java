package edu.maastricht.ginrummy.game;

import edu.maastricht.ginrummy.agents.*;
import edu.maastricht.ginrummy.agents.heuristic.HeuristicAgent;

import java.util.ArrayList;
import java.util.List;

public class ExperimentImportanceOfMethod {

    public static void main(String[] args) {

        List<int[]> rounds = new ArrayList<>();
        List<Player[]> winner = new ArrayList<>();
        List<Score[]> points = new ArrayList<>();

        int n = 100;

        for(int i = 0; i < n; i++)
        {
            int finalI = i;
            Game g = new Game((game) -> {
                rounds.add(finalI, new int[] {game.getRound().getRoundCounter(), -1});
                winner.add(finalI, new Player[] {game.getWinner(), null});
                points.add(finalI, new Score[] {game.getScore(), null});
                System.out.println("done - " + System.currentTimeMillis());
            });
            //--- CHANGE ME
            IAgent aAgent = new MonteCarloAgent2();
            IAgent bAgent = new RandomAgent();
            //---
            g.start(new HybridAgent(aAgent, new RandomAgent()), bAgent);
        }

        for(int i = 0; i < n; i++)
        {
            int finalI = i;
            Game g = new Game((game) -> {
                rounds.get(finalI)[1] = game.getRound().getRoundCounter();
                winner.get(finalI)[1] = game.getWinner();
                points.get(finalI)[1] = game.getScore();
                System.out.println("done - " + System.currentTimeMillis());
            });
            //--- CHANGE ME
            IAgent aAgent = new MonteCarloAgent2();
            IAgent bAgent = new RandomAgent();
            //---
            g.start(new HybridAgent(new RandomAgent(), aAgent), bAgent);
        }

        {
            // Turns
            double pickAvgRounds = rounds.stream().mapToInt(e -> e[0]).average().getAsDouble();
            // Points
            double pickAvgPointsA = points.stream().mapToInt(e -> e[0].getScoreP1()).average().getAsDouble();
            double pickAvgPointsB = points.stream().mapToInt(e -> e[0].getScoreP2()).average().getAsDouble();
            // Domination
            double pickDominationA = (double) points.stream().mapToInt(e -> e[0].getScoreP1()).sum() / points.stream().mapToInt(e -> e[0].getScoreP1() + e[0].getScoreP2()).sum();
            double pickDominationB = 1 - pickDominationA;
            // Win-Rate
            double pickWinRateA = (double) winner.stream().filter(e -> e[0] == Player.P1).count() / winner.size();
            double pickWinRateB = 1 - pickWinRateA;

            System.out.println("-------- PICK ----------");
            System.out.printf("Avg. rounds: %.2f\n", pickAvgRounds);
            System.out.printf("Avg. points: %.4f|%.4f\n", pickAvgPointsA, pickAvgPointsB);
            System.out.printf("Domination: %.4f|%.4f\n", pickDominationA, pickDominationB);
            System.out.printf("Win Rate: %.4f|%.4f\n", pickWinRateA, pickWinRateB);
            System.out.println("-------- PICK ----------");
        }

        System.out.println("\n\n");

        {
            // Turns
            double discardAvgRounds = rounds.stream().mapToInt(e -> e[1]).average().getAsDouble();
            // Points
            double discardAvgPointsA = points.stream().mapToInt(e -> e[1].getScoreP1()).average().getAsDouble();
            double discardAvgPointsB = points.stream().mapToInt(e -> e[1].getScoreP2()).average().getAsDouble();
            // Domination
            double discardDominationA = (double) points.stream().mapToInt(e -> e[1].getScoreP1()).sum() / points.stream().mapToInt(e -> e[1].getScoreP1() + e[1].getScoreP2()).sum();
            double discardDominationB = 1 - discardDominationA;
            // Win-Rate
            double discardWinRateA = (double) winner.stream().filter(e -> e[1] == Player.P1).count() / winner.size();
            double discardWinRateB = 1 - discardWinRateA;

            System.out.println("-------- DISCARD ----------");
            System.out.printf("Avg. rounds: %.2f\n", discardAvgRounds);
            System.out.printf("Avg. points: %.4f|%.4f\n", discardAvgPointsA, discardAvgPointsB);
            System.out.printf("Domination: %.4f|%.4f\n", discardDominationA, discardDominationB);
            System.out.printf("Win Rate: %.4f|%.4f\n", discardWinRateA, discardWinRateB);
            System.out.println("-------- DISCARD ----------");
        }
    }

}
