package edu.maastricht.ginrummy.melding.advanced;

import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.MeldGrouping;

public class MergeResult {

    private final MeldGrouping meldGrouping;
    private final CardDeck mergingCards;

    public MergeResult(MeldGrouping meldGrouping, CardDeck mergingCards)
    {
        this.meldGrouping = meldGrouping;
        this.mergingCards = mergingCards;
    }

    public MeldGrouping getMeldGrouping()
    {
        return this.meldGrouping;
    }

    public CardDeck getMergingCards()
    {
        return this.mergingCards;
    }
}