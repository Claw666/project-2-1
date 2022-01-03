package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.agents.functions.DoYouKnock;
import edu.maastricht.ginrummy.agents.functions.WhatToDiscard;
import edu.maastricht.ginrummy.agents.functions.WhatToPick;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;

public class DeadWoodDiscardAgent extends IAgent {
    @Override
    public IAgent.CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        return WhatToPick.random();
    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        return WhatToDiscard.randomDeadwood(hand);
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
