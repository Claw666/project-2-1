package edu.maastricht.ginrummy.UI.Event.Events;

import edu.maastricht.ginrummy.UI.Event.Event;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.game.Round;
import edu.maastricht.ginrummy.game.Score;

public class PlayerSwitchEvent extends Event {

    private final Round round;
    private final IAgent current;
    private final IAgent old;

    private Score currentScore;

    public PlayerSwitchEvent(Round round, Score currentScore, IAgent current, IAgent old) {
        this.round = round;
        this.current = current;
        this.currentScore = currentScore;
        this.old = old;
    }

    public Round getRound()
    {
        return round;
    }

    public Score getCurrentScore() {
        return currentScore;
    }

    public IAgent getCurrent() {
        return current;
    }

    public IAgent getOld() {
        return old;
    }

}
