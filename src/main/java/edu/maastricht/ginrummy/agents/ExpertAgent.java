package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.UI.Event.EventListener;
import edu.maastricht.ginrummy.UI.Event.Subscribe;
import edu.maastricht.ginrummy.agents.functions.WhatToDiscard;
import edu.maastricht.ginrummy.agents.heuristic.events.AddCardsToDeckEvent;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.game.Game;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.ArrayList;

/*
Strategy for this agent.

If-statements DISCARD:
	IF card is part of meld THEN don't discard
	IF opponent discard y THEN discard y-1 OR y+1
		ELSE discard card with value of an element of Y
			ELSE discard edge cards (cards with the lowest possibility of making melds (aces, 2's, kings))
	IF deckSize < 26 THEN discard highest value deadwood
	IF deckSize >= 26 THEN discard lowest value deadwood
	IF opponent picked card z THEN do not discard z-1 OR z+1

If-statements PICK:
	IF discarded card can't make a meld THEN pick stock
	IF deckSize < 20 AND discard card value < highest deadwood value hand THEN pick discard card

If-statements KNOCK:
	IF can knock THEN knock

If-statement SKIP:
	IF can skip THEN don't
 */

public class ExpertAgent extends IAgent implements EventListener  {

    private Game game;
    private ArrayList<Card> opponentDiscard = new ArrayList<Card>();
    private int turn;

    public ExpertAgent(Game game)
    {
        game.getEventHandler().registerListener(this);
        this.game = game;
        this.turn = 0;
    }

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        this.turn += 1;

        MeldGrouping handMeldGrouping = AdvancedMeldDetector.find(hand); //same as Greedy Agent

        CardDeck cloned = hand.clone();
        cloned.add(discard.getLast());

        MeldGrouping clonedMeldGrouping = AdvancedMeldDetector.find(cloned);

        if(clonedMeldGrouping.deadwoodValue() < handMeldGrouping.deadwoodValue())
        {
            return CardPickD.DISCARD;
        }

        if(this.turn <= 20){
            Card buh = highestDeadwood(hand, discard);
            if (buh != discard.getLast()){
                return CardPickD.DISCARD;
            }
        }

        return CardPickD.STACK;
    }

    @Override
    public int whatToDiscard(CardDeck hand2, CardDeck discard, Card lastDiscardedCard) {
        int highestVal = 0;
        Card highVal = null;

        int lowestVal = 0;
        Card lowVal = null;

        CardDeck hand = hand2.clone();
        hand.removeLast();

        MeldGrouping originalGrouping = AdvancedMeldDetector.find(hand);
        CardDeck dwCards = originalGrouping.getDeadwood(); //-- Make sure to only discard cards that contribute to deadwood

        for (Card i : dwCards){
            if (!discard.isEmpty())
            {
                if (discard.getLast().getRank().getNumRank() == i.getRank().getNumRank()-1 || discard.getLast().getRank().getNumRank() == i.getRank().getNumRank()+1)
                {
                    return hand.indexOf(i); //IF opponent discard card with rank y THEN discard y-1 OR y+1
                }
                for (Card j : opponentDiscard)
                {
                    if (j.getRank().getNumRank() == i.getRank().getNumRank())
                    {
                        return hand.indexOf(i); //ELSE discard card with value of an element of the opponents discarded cards
                    }
                }
            }

            if (this.turn > 26)
            {
                if (highestVal < i.getDeadwoodValue())
                {
                    highestVal = i.getDeadwoodValue();
                    highVal = i;
                }

            } else {
                if (lowestVal > i.getDeadwoodValue())
                {
                    lowestVal = i.getDeadwoodValue();
                    lowVal = i;
                }
            }
        }
        if (highVal != null)
        {
            return hand.indexOf(highVal);
        }
        if (lowVal != null)
        {
            return hand.indexOf(lowVal);
        }
        return WhatToDiscard.randomDeadwood(hand);
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL) {
        return true;
    }

    @Override
    public boolean doYouSkipPick() {
        return false;
    }

    private Card highestDeadwood(CardDeck hand, CardDeck discard){
        int highestVal = 0;
        Card highVal = null;

        MeldGrouping originalGrouping = AdvancedMeldDetector.find(hand);
        CardDeck dwCards = originalGrouping.getDeadwood();

        for(Card i : dwCards){
            if (highestVal < i.getDeadwoodValue())
            {
                highestVal = i.getDeadwoodValue();
                highVal = i;
            }
        }

        if(highestVal < discard.getLast().getDeadwoodValue()){
            return discard.getLast();
        } else {
            assert (highVal != null);
            return highVal;
        }
    }

    @Subscribe
    public void onDiscard(AddCardsToDeckEvent event)
    {
        if(event.getAgent() != this && event.getSource() == AddCardsToDeckEvent.DeckType.PLAYER && event.getTarget() == AddCardsToDeckEvent.DeckType.DISCARD)
        {
            opponentDiscard.addAll(event.getCards());
        }
    }
}
