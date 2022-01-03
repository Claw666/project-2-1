package edu.maastricht.ginrummy.UI.Event.Events;

import edu.maastricht.ginrummy.UI.Event.Event;
import edu.maastricht.ginrummy.game.Round;

public class RoundStartEvent extends Event {

    private final Round round;

    public RoundStartEvent(Round round)
    {
        this.round = round;
    }

    public Round getRound()
    {
        return round;
    }
}
