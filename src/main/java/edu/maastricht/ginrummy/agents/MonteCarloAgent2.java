package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.UI.Event.EventHandler;
import edu.maastricht.ginrummy.agents.functions.WhatToPick;
import edu.maastricht.ginrummy.bees.BeeHive;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.cards.DeckGeneration;
import edu.maastricht.ginrummy.game.Player;
import edu.maastricht.ginrummy.game.Round;
import edu.maastricht.ginrummy.game.Score;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// DO NOT CHANGE THE NAME OF THIS CLASS OR IT MIGHT BREAK SOMETHING INTELLIJ RELATED. JOHANNES
public class MonteCarloAgent2 extends IAgent {

    private final IAgent evaluationAgent1;
    private final IAgent evaluationAgent2;
    private final int evaluationsPerActions;
    private final int searchDepth;

    public MonteCarloAgent2()
    {
        this(new RandomAgent(), new RandomAgent(), 400, 4);
    }

    public MonteCarloAgent2(IAgent evaluationAgent1, IAgent evaluationAgent2, int evaluationsPerActions, int searchDepth) {
        this.evaluationAgent1 = evaluationAgent1;
        this.evaluationAgent2 = evaluationAgent2;
        this.evaluationsPerActions = evaluationsPerActions;
        this.searchDepth = searchDepth;
    }

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        MeldGrouping originalGrouping = AdvancedMeldDetector.find(hand);
        AtomicInteger atomicInteger = new AtomicInteger(); //TODO replace with CountDownLatch

        AtomicInteger discardScore = new AtomicInteger();

        AtomicReference<Double> stockScore = new AtomicReference<>();
        stockScore.set(0D);

        atomicInteger.addAndGet(1);
        BeeHive.add(() -> {
            for (int j = 0; j < evaluationsPerActions; j++) {
                var h = hand.clone();
                var d = discard.clone();
                h.add(d.draw());
                IAgent aAgent = new RandomAgent();
                Round round = new Round(new Score(1, 0), new EventHandler(), aAgent, new RandomAgent());
                round.playFromGameDeckState(DeckGeneration.completeStateRandomly(h, d), Player.P2, this.searchDepth, true);
                MeldGrouping aMelt = AdvancedMeldDetector.find(aAgent.getCardDeck());
                discardScore.addAndGet(originalGrouping.deadwoodValue() - aMelt.deadwoodValue());
            }
            atomicInteger.decrementAndGet();
        });

        CardDeck cards = new CardDeck(CardDeck.DeckType.FRENCH, false);
        cards.removeAll(hand);
        cards.removeAll(discard);

        for(Card card : cards)
        {
            BeeHive.add(() -> {
                atomicInteger.addAndGet(1);

                for (int j = 0; j < evaluationsPerActions; j++) {
                    var h = hand.clone();
                    var d = discard.clone();
                    h.add(card);

                    IAgent aAgent = new RandomAgent();
                    Round round = new Round(new Score(1, 0), new EventHandler(), aAgent, new RandomAgent());
                    round.playFromGameDeckState(DeckGeneration.completeStateRandomly(h, d), Player.P2, searchDepth, true);

                    MeldGrouping aMelt = AdvancedMeldDetector.find(aAgent.getCardDeck());
                    stockScore.set(stockScore.get() + ((originalGrouping.deadwoodValue() - aMelt.deadwoodValue()) * (1D / cards.size())));
                }
                atomicInteger.decrementAndGet();
            });
        }


        while(atomicInteger.get() > 0);//TODO replace busy waiting
        if(discardScore.get() > stockScore.get())
        {
            return CardPickD.DISCARD;
        }
        else if(discardScore.get() < stockScore.get())
        {
            return CardPickD.STACK;
        }

        return WhatToPick.random();
    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        MeldGrouping originalGrouping = AdvancedMeldDetector.find(hand);
        var actionScores = new HashMap<Integer, Integer>();
        AtomicInteger atomicInteger = new AtomicInteger();
        for (int i = 0; i < hand.size(); i++) {

            if(hand.get(i).getByteIndex() == lastDiscardedCard.getByteIndex())
            {
                continue;
            }

            int finalI = i;
            atomicInteger.incrementAndGet();
            BeeHive.add(() -> {
                int score = 0;

                for (int j = 0; j < evaluationsPerActions; j++) {
                    var h = hand.clone();
                    var d = discard.clone();
                    d.add(h.remove(finalI));
                    IAgent aAgent = new RandomAgent();
                    Round round = new Round(new Score(1, 0), new EventHandler(), aAgent, new RandomAgent());
                    round.playFromGameDeckState(DeckGeneration.completeStateRandomly(h, d), Player.P2, searchDepth, false);
                    MeldGrouping aMelt = AdvancedMeldDetector.find(aAgent.getCardDeck());
                    score += originalGrouping.deadwoodValue() - aMelt.deadwoodValue();
                }
                synchronized (actionScores)
                {
                    actionScores.put(finalI, score);
                    atomicInteger.decrementAndGet();
                }
            });
        }
        while(atomicInteger.get() > 0); //TODO replace busy waiting
        return actionScores.entrySet().stream().max(Comparator.comparingDouble(Map.Entry::getValue)).get().getKey();
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL) {
        return true;
    }

    @Override
    public boolean doYouSkipPick() {
        return false;
    }
}