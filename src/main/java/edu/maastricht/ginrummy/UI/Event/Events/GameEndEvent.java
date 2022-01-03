package edu.maastricht.ginrummy.UI.Event.Events;

import edu.maastricht.ginrummy.UI.Event.Event;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.game.Score;

public class GameEndEvent extends Event {

    private final Score score;
    private final IAgent winner;
    private final IAgent loser;

    public GameEndEvent(Score score, IAgent winner, IAgent loser)
    {
        this.score = score;
        this.winner = winner;
        this.loser = loser;
    }

    public Score getScore()
    {
        return score;
    }

    public IAgent getWinner()
    {
        return winner;
    }

    public IAgent getLoser()
    {
        return loser;
    }
}
