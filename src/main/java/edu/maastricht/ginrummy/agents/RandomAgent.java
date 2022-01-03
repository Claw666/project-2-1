package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.agents.functions.DoYouKnock;
import edu.maastricht.ginrummy.agents.functions.WhatToDiscard;
import edu.maastricht.ginrummy.agents.functions.WhatToPick;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;

import java.util.Random;

public class RandomAgent extends IAgent{
    Random r = new Random();

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        return WhatToPick.random();
    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        return WhatToDiscard.random(hand);
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL) {
        return isBigGinBECAREFULLLLL || DoYouKnock.random();
    }

    @Override
    public boolean doYouSkipPick() {
        return Math.random() > 0.5;
    }
}
