package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;

public class HybridAgent extends IAgent {

    private final IAgent pickAgent, discardAgent;

    public HybridAgent(IAgent agentA, IAgent agentB)
    {
        // so that they share the same reference
        agentA.setCardDeck(this.getCardDeck());
        agentB.setCardDeck(this.getCardDeck());

        this.pickAgent = agentA;
        this.discardAgent = agentB;
    }

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        return pickAgent.whatToPick(hand, discard);
    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        return discardAgent.whatToDiscard(hand, discard, lastDiscardedCard);
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL) {
        return true;
    }

    @Override
    public boolean doYouSkipPick() {
        return Math.random() > 0.5;
    }
}
