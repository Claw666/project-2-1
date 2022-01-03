package edu.maastricht.ginrummy.UI.Event.Events;

import edu.maastricht.ginrummy.UI.Event.Event;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;

import java.util.List;

public abstract class PlayerPickedEvent extends Event {

    private CardDeck pile;
    private List<Card> added, removed;

    public PlayerPickedEvent(CardDeck pile, List<Card> added, List<Card> removed)
    {
        this.pile = pile;
        this.added = added;
        this.removed = removed;
    }

    public CardDeck getPile()
    {
        return pile;
    }

    public List<Card> getAdded()
    {
        return added;
    }

    public List<Card> getRemoved()
    {
        return removed;
    }

    public static class Stock extends PlayerPickedEvent
    {
        public Stock(CardDeck pile, List<Card> added, List<Card> removed) {
            super(pile, added, removed);
        }
    }

    public static class Discard extends PlayerPickedEvent
    {
        public Discard(CardDeck pile, List<Card> added, List<Card> removed) {
            super(pile, added, removed);
        }
    }

}
