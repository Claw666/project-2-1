package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.cards.CardDeck;

public abstract class IHumanAgent extends IAgent {
    public abstract CardDeck howToReorderDeck(CardDeck hand);
    public abstract void roundOverHook(CardDeck hand);
}
