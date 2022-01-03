package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;
import edu.maastricht.ginrummy.melding.advanced.MergeResult;

public abstract class IAgent {

    private CardDeck cardDeck = new CardDeck();

    public IAgent() { }
    public IAgent(CardDeck cardDeck) {
        this.cardDeck = cardDeck;
    }

    public CardDeck getCardDeck() {
        return cardDeck;
    }

    public void setCardDeck(CardDeck cardDeck) {
        this.cardDeck = cardDeck;
    }

    public CardDeck getCardDeckClone() {
        return cardDeck.clone();
    }

    public abstract CardPickD whatToPick(CardDeck hand, CardDeck discard);

    public abstract int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard);

    public abstract boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL);

    public abstract boolean doYouSkipPick();

    public MergeResult doLayoff(CardDeck knocker) {
        return AdvancedMeldDetector.mergeDeadwood(knocker, this.getCardDeck());
    }

    public static enum CardPickD
    {
        STACK,
        DISCARD
    }
}