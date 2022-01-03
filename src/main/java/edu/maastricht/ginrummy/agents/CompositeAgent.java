package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;

public class CompositeAgent extends IAgent {

    //TODO implement this so that the composite agent takes in 3 functions and uses them for decisions.

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        return null;
    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        return 0;
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL) {
        return false;
    }

    @Override
    public boolean doYouSkipPick() {
        return false;
    }
}
