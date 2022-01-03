package edu.maastricht.ginrummy.agents.heuristic;

import edu.maastricht.ginrummy.UI.Event.EventListener;
import edu.maastricht.ginrummy.UI.Event.Subscribe;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.agents.heuristic.CardTracker.Status;
import edu.maastricht.ginrummy.agents.heuristic.events.AddCardsToDeckEvent;
import edu.maastricht.ginrummy.agents.heuristic.events.CardDeckClearEvent;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.game.Game;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeuristicAgent extends IAgent implements EventListener {

    private Game game;
    private final CardTracker cardTracker = new CardTracker();

    private final boolean legacy;

    public HeuristicAgent(Game game, boolean legacy)
    {
         game.getEventHandler().registerListener(this);
         this.game = game;
         this.legacy = legacy;
    }

    public CardTracker getCardTracker() {
        return cardTracker;
    }

    @Subscribe
    public void onDeckClearEvent(CardDeckClearEvent event)
    {
        cardTracker.reset();
    }

    @Subscribe
    public void onAddCardsToDeck(AddCardsToDeckEvent event)
    {
        // DISCARD -> PLAYER (Player picks a card from discard)
        if(event.getSource() == AddCardsToDeckEvent.DeckType.DISCARD && event.getTarget() == AddCardsToDeckEvent.DeckType.PLAYER)
        {
            this.cardTracker.put(event.getCards(), (event.getAgent() == this) ? Status.IN_OWN_HAND : Status.IN_ENEMY_HAND);
        }
        // STOCK -> PLAYER (Player picks a card from stock)
        else if(event.getSource() == AddCardsToDeckEvent.DeckType.STOCK && event.getTarget() == AddCardsToDeckEvent.DeckType.PLAYER)
        {
            if(event.getAgent() == this)
            {
                this.cardTracker.put(event.getCards(), Status.IN_OWN_HAND);
            }
        }
        // PLAYER -> DISCARD (Player discards a card)
        else if(event.getSource() == AddCardsToDeckEvent.DeckType.PLAYER && event.getTarget() == AddCardsToDeckEvent.DeckType.DISCARD)
        {
            this.cardTracker.put(event.getCards(), Status.IN_DISCARD);
        }
        // DISCARD -> STOCK (refshuffle deck)
        else if(event.getSource() == AddCardsToDeckEvent.DeckType.DISCARD && event.getTarget() == AddCardsToDeckEvent.DeckType.STOCK)
        {
            this.cardTracker.put(event.getCards(), Status.IN_STOCK);
        }
        // STOCK -> DISCARD (round start)
        else if(event.getSource() == AddCardsToDeckEvent.DeckType.STOCK && event.getTarget() == AddCardsToDeckEvent.DeckType.DISCARD)
        {
            this.cardTracker.put(event.getCards(), Status.IN_DISCARD);
        }
        else
        {
            throw new IllegalArgumentException(String.format("Unhandled event [IAgent: %s, Source: %s, Target: %s, Cards: %s]",
                    event.getAgent(), event.getSource(), event.getTarget(), event.getCards()));
        }
    }

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard) {

        if(this.legacy)
        {
            return CardPickD.STACK;
        }

        //--- Deduce knowledge
        //  - If 2 or more cards of a set are in a position where they are not reachable, then don't pick any of the cards related to those

        final int currentDeadwood = AdvancedMeldDetector.find(hand).deadwoodValue();
        double discardDeadwoodImprovement = 0;
        double stackPileImprovement = 0;

        {
            CardDeck clonedHand = hand.clone();
            clonedHand.add(discard.getLast().clone());
            discardDeadwoodImprovement = (currentDeadwood - AdvancedMeldDetector.find(clonedHand).deadwoodValue());
        }

        List<Card> stockPile = this.cardTracker.get(Status.IN_STOCK);
        if(!stockPile.isEmpty()) {
            for(Card card : stockPile)
            {
                CardDeck clonedHand = hand.clone();
                clonedHand.add(card);
                stackPileImprovement += (currentDeadwood - AdvancedMeldDetector.find(clonedHand).deadwoodValue());
            }
            stackPileImprovement /= stockPile.size();
        }

        if(stackPileImprovement == discardDeadwoodImprovement)
        {
            return Math.random() > 0.5 ? CardPickD.STACK : CardPickD.DISCARD;
        }
        else if(stackPileImprovement < discardDeadwoodImprovement)
        {
            return CardPickD.DISCARD;
        }
        else
        {
            return CardPickD.STACK;
        }
    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastPickedCard) {

        //--- What is the chance for each card to be used in a run or set?
        MeldGrouping meldGrouping = AdvancedMeldDetector.find(getCardDeck());
        final long usedCards = encodeMeldGrouping(meldGrouping);
        long setCards = encodeCards(getCardDeck(), AdvancedMeldDetector.Combination.Type.SET);
        long runCards = encodeCards(getCardDeck(), AdvancedMeldDetector.Combination.Type.RUN);

        Map<Card, List<Card>> requiredCards = new HashMap<>();
        for(Card card : getCardDeck())
        {
            if((card.getByteIndex() & usedCards) == 0 && card.getByteIndex() != lastPickedCard.getByteIndex())
            {
                requiredCards.put(card, new ArrayList<>());
                {
                    //--- Note: Figuring out which cards we would need for a set. To do this, we can the index in a set encoding
                    // of the card which we have, and then we get the bytes in front of it, and the bytes after it depending
                    // at which position it is in the set.
                    final short index = (short) (Math.log(Card.toByteIndex(card, AdvancedMeldDetector.Combination.Type.SET)) / Math.log(2));
                    final short norm = (short) (index % 4);
                    for(short i = 1; i <= norm; i++)
                    {
                        if((setCards & (1L << (index - i))) == 0)
                        {
                            requiredCards.get(card).add(Card.fromSingleByteIndex(1L << (index - i), AdvancedMeldDetector.Combination.Type.SET));
                        }
                    }
                    for(short i = 1; i <= (3 - norm); i++)
                    {
                        if((setCards & (1L << (index + i))) == 0)
                        {
                            requiredCards.get(card).add(Card.fromSingleByteIndex(1L << (index + i), AdvancedMeldDetector.Combination.Type.SET));
                        }
                    }
                }
                {
                    final short index = (short) (Math.log(Card.toByteIndex(card, AdvancedMeldDetector.Combination.Type.RUN)) / Math.log(2));
                    final short norm = (short) (index % 13);
                    for(short i = 1; i <= norm; i++)
                    {
                        if((runCards & (1L << (index - i))) == 0)
                        {
                            requiredCards.get(card).add(Card.fromSingleByteIndex(1L << (index - i), AdvancedMeldDetector.Combination.Type.RUN));
                        }
                    }
                    for(short i = 1; i <= (3 - norm); i++)
                    {
                        if((runCards & (1L << (index + i))) == 0)
                        {
                            requiredCards.get(card).add(Card.fromSingleByteIndex(1L << (index + i), AdvancedMeldDetector.Combination.Type.RUN));
                        }
                    }
                }
            }
        }

        // NOTE: Does this make sense? - I think when 'requiredCards' is empty that means that none of the cards in our
        // current hand are in any way useful?...
        /*if(requiredCards.isEmpty())
        {
            System.out.println("stupid");
            return (int) (hand.size() * Math.random());
        }*/

        //--- Calculate chance that I can still get
        Card lowestValue = null;
        double lowestP = Integer.MAX_VALUE;
        for(Map.Entry<Card, List<Card>> card : requiredCards.entrySet())
        {
            final double tmp = calculateProbability(card.getValue());
            if(lowestValue == null || tmp < lowestP)
            {
                lowestValue = card.getKey();
                lowestP = tmp;
            }
        }

        return hand.indexOf(lowestValue);
    }

    private double calculateProbability(List<Card> cards)
    {
        double p = 0;
        final double w = 1D / cards.size();
        for(Card card : cards)
        {
            Status status = cardTracker.get(card);
            switch (status)
            {
                case IN_OWN_HAND:
                    p += w * 1;
                    break;
                case IN_DISCARD:
                    //TODO keep track of how often opponent picks discard vs. stock
                    p += w * Math.pow(0.5, game.getRound().getDiscardDeck().indexOf(card));
                    break;

                case UNKNOWN:
                case IN_STOCK:
                    p += (1D / game.getRound().getStock().size()) * w;
                    break;

                case IN_ENEMY_HAND:
                    return 0;

                default:
                    throw new IllegalStateException("Unexpected value: " + status);
            }
        }
        return p;
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isGin) {
        return true;
    }

    @Override
    public boolean doYouSkipPick() {
        return false;
    }

    private static long encodeMeldGrouping(MeldGrouping meldGrouping)
    {
        long encoding = 0L;
        for(CardDeck set : meldGrouping.getSets())
        {
            for(Card card : set)
            {
                encoding |= card.getByteIndex();
            }
        }
        for(CardDeck run : meldGrouping.getRuns())
        {
            for(Card card : run)
            {
                encoding |= card.getByteIndex();
            }
        }
        return encoding;
    }

    private static long encodeCardsByDefault(List<Card> cards)
    {
        return encodeCards(cards, AdvancedMeldDetector.Combination.Type.RUN);
    }

    private static long encodeCards(List<Card> cards, AdvancedMeldDetector.Combination.Type type)
    {
        long encoding = 0L;
        for(Card card : cards)
        {
            encoding |= Card.toByteIndex(card, type);
        }
        return encoding;
    }

}
