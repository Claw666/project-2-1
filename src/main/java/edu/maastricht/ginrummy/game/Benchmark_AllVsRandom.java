package edu.maastricht.ginrummy.game;

import edu.maastricht.ginrummy.agents.*;
import edu.maastricht.ginrummy.agents.heuristic.HeuristicAgent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Benchmark_AllVsRandom {

    public static void main(String[] args) {
        final int numRounds = 80;
        try {
            BufferedWriter raw = new BufferedWriter(new FileWriter("./data/all_vs_random_raw.csv"));
            BufferedWriter data = new BufferedWriter(new FileWriter("./data/all_vs_random.csv"));
            for (int agentIdx = 3; agentIdx < 4; agentIdx++)
            {
                final List<Integer> rounds = new ArrayList<>();
                final List<Player> players = new ArrayList<>();
                long startTime = System.nanoTime();
                String agentName = null;
                for(int i = 0; i < numRounds; i++)
                {
                    int finalI = i;

                    Game g = new Game((game) -> {
                        players.add(game.getWinner());
                        rounds.add(game.getRound().getRoundCounter());

                        try {
                            raw.write(String.format("%d,%d,%d,%d,%s\n",
                                    finalI, game.getRound().getRoundCounter(),
                                    game.getScore().getScoreP1(), game.getScore().getScoreP2(), game.getWinner().name()));
                            raw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("done - " + System.currentTimeMillis());
                    });

                    IAgent agent = null;
                    switch (agentIdx) {
                        case 0:
                            agent = new DeadWoodDiscardAgent();
                            break;
                        case 1:
                            agent = new GreedyAgent();
                            break;
                        case 2:
                            agent = new MonteCarloAgent2(new RandomAgent(), new RandomAgent(), 100, 6); //TODO SHOULD THIS BE CHANGED TO THE OPTIMAL PARAMETERS?
                            break;
                        case 3:
                            agent = new HeuristicAgent(g, false);
                            break;

                    }
                    assert agent != null;
                    agentName = agent.getClass().getSimpleName();
                    g.start(agent, new RandomAgent());
                }
                float roundAverage = 0;
                for(var e: rounds)
                {
                    roundAverage += e;
                }
                roundAverage /= rounds.size();
                var averageRoundTimeMilSec = ((float)(System.nanoTime() - startTime) / 1000000) / numRounds;

                data.write(String.format("%s %.1f %.2f\n",
                        agentName, roundAverage, averageRoundTimeMilSec));
                data.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
