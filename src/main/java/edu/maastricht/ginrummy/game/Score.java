package edu.maastricht.ginrummy.game;

public class Score {
    private int scoreP1 = 0;
    private int scoreP2 = 0;

    public Score (int scoreP1, int scoreP2) {
        this.scoreP1 = scoreP1;
        this.scoreP2 = scoreP2;
    }

    public void add(Score other)
    {
        scoreP1 += other.scoreP1;
        scoreP2 += other.scoreP2;
    }
    public int getScoreP1() {
        return scoreP1;
    }

    public int getScoreP2() {
        return scoreP2;
    }

    @Override
    public String toString()
    {
        return String.format("P1: %s, P2: %s", scoreP1, scoreP2);
    }

    public Player checkForWinner()
    {
        int winningScore = 100;
        if (scoreP1 >= winningScore || scoreP2 >= winningScore)
        {
            if (scoreP1 > scoreP2)
            {
                return Player.P1;
            }
            else if (scoreP2 > scoreP1)
            {
                return Player.P2;
            }
        }
        return Player.NONE;
    }
}
