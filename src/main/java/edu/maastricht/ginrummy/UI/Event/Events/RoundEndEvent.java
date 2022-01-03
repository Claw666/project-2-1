package edu.maastricht.ginrummy.UI.Event.Events;

import edu.maastricht.ginrummy.UI.Event.Event;
import edu.maastricht.ginrummy.game.Round;

public class RoundEndEvent extends Event {

    private final Round round;

    public RoundEndEvent(Round round)
    {
        this.round = round;
    }

    public Round getRound()
    {
        return round;
    }
}
