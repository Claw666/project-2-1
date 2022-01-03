package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.agents.functions.WhatToDiscard;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.Comparator;
import java.util.Optional;

public class GreedyAgent extends IAgent {


    public GreedyAgent()
    {
    }

    @Override
    public IAgent.CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        MeldGrouping handMeldGrouping = AdvancedMeldDetector.find(hand);

        CardDeck cloned = hand.clone();
        cloned.add(discard.getLast());

        MeldGrouping clonedMeldGrouping = AdvancedMeldDetector.find(cloned);

        if(clonedMeldGrouping.deadwoodValue() < handMeldGrouping.deadwoodValue())
        {
            return CardPickD.DISCARD;
        }

        return CardPickD.STACK;

    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        MeldGrouping handMeldGrouping = AdvancedMeldDetector.find(hand);
        Optional<Card> highestDeadwood = handMeldGrouping.getDeadwood().stream()
                .filter(e -> e.getByteIndex() != lastDiscardedCard.getByteIndex())
                .max(Comparator.comparingDouble(Card::getDeadwoodValue));
        return highestDeadwood.map(hand::indexOf).orElseGet(() -> WhatToDiscard.randomDeadwood(hand));
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isGin) {
        return true;
    }

    @Override
    public boolean doYouSkipPick() {
        return Math.random() < 0.5; //TODO
    }

}
