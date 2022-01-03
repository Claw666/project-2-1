package edu.maastricht.ginrummy.agents.functions;

import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.MeldDetector;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.Random;

public class WhatToDiscard {

    private static Random r = new Random();

    public static int random(CardDeck hand) {
        return r.nextInt(hand.size());
    }

    public static int randomDeadwood(CardDeck hand)
    {
        CardDeck dw = AdvancedMeldDetector.find(hand).getDeadwood();
        if (dw.size() == 0 || dw.size() == 1)
            return random(hand);
        else
            return hand.indexOf(dw.get(r.nextInt(dw.size())));
    }

}
