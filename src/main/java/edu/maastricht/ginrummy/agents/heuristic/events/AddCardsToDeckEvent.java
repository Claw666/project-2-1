package edu.maastricht.ginrummy.agents.heuristic.events;

import edu.maastricht.ginrummy.UI.Event.Event;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.cards.Card;

import java.util.List;

public class AddCardsToDeckEvent extends Event {

    private final IAgent agent;
    private final DeckType source;
    private final DeckType target;
    private final List<Card> cards;

    public AddCardsToDeckEvent(IAgent agent, DeckType source, List<Card> cards)
    {
        this(agent, source, DeckType.PLAYER, cards);
    }

    public AddCardsToDeckEvent(IAgent agent, DeckType source, DeckType target, List<Card> cards)
    {
        this.agent = agent;
        this.source = source;
        this.target = target;
        this.cards = cards;
    }

    public IAgent getAgent()
    {
        return agent;
    }

    public DeckType getSource()
    {
        return source;
    }

    public DeckType getTarget()
    {
        return target;
    }

    public List<Card> getCards()
    {
        return cards;
    }

    public static enum DeckType
    {
        PLAYER,
        STOCK,
        DISCARD
    }

}
