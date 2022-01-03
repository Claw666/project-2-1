package edu.maastricht.ginrummy.melding;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;

import java.util.ArrayList;

public class MeldGrouping {

    private ArrayList<CardDeck> sets = new ArrayList<CardDeck>();
    private ArrayList<CardDeck> runs = new ArrayList<CardDeck>();
    private CardDeck deadwood = new CardDeck();

    private int deadwoodValue = -1;

    public ArrayList<CardDeck> getRuns() {
        return runs;
    }

    public ArrayList<CardDeck> getSets() {
        return sets;
    }

    public CardDeck getDeadwood() {
        return deadwood;
    }

    public int deadwoodValue()
    {
        if(this.deadwoodValue == -1)
        {
            this.deadwoodValue = 0;
            for (Card card : deadwood) {
                this.deadwoodValue += card.getDeadwoodValue();
            }
        }
        return this.deadwoodValue;
    }
}
