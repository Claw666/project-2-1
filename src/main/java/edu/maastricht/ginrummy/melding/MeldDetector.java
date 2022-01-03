package edu.maastricht.ginrummy.melding;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;

import java.util.*;

public class MeldDetector {

    private MeldDetector() {}

    public static int getDeadwoodValue(CardDeck cardDeck)
    {
        return getBestMeldGrouping(cardDeck).deadwoodValue();
    }

    public static MeldGrouping getBestMeldGrouping(CardDeck cardDeck)
    {
        int lowestDeadwoodValue = Integer.MAX_VALUE;
        MeldGrouping bestGrouping = new MeldGrouping();
        for (List<Integer> p : Permutations.createPermutations(cardDeck.size()))
        {
            MeldGrouping currentGrouping = groupMelds(cardDeck.drawByIdxList(p));
            if (currentGrouping.deadwoodValue() < lowestDeadwoodValue)
            {
                lowestDeadwoodValue = currentGrouping.deadwoodValue();
                bestGrouping = currentGrouping;
            }
        }
        return bestGrouping;
    }

    static List<List<Integer>> allPermutations(int n)
    {
        throw new UnsupportedOperationException();
    }

    static MeldGrouping groupMelds(CardDeck deck)
    {
        CardDeck noDetection = new CardDeck(CardDeck.DeckType.EMPTY, false);
        MeldGrouping mg = new MeldGrouping();

        boolean wasSet = false;
        boolean wasRun = false;
        for (Card card : deck) {
            noDetection.add(card);
            if (isSet(noDetection))
            {
                wasSet = true;
            }
            else if (wasSet)
            {
                wasSet = false;
                mg.getSets().add(extractAllButLast(noDetection));
                continue;
            }
            if (isOrderedRun(noDetection))
            {
                wasRun = true;
            }
            else if (wasRun)
            {
                wasRun = false;
                mg.getRuns().add(extractAllButLast(noDetection));
            }
        }
        if (isSet(noDetection))
        {
            mg.getSets().add(noDetection);
        }
        else if (isOrderedRun(noDetection))
        {
            mg.getRuns().add(noDetection);
        }
        else
        {
            mg.getDeadwood().addAll(noDetection);
        }
        return mg;
    }

    static CardDeck extractAllButLast(CardDeck deck)
    {
        CardDeck nDeck = new CardDeck(CardDeck.DeckType.EMPTY, false);
        for (int i = deck.size() - 2; i >= 0; i--) {
            nDeck.add(deck.get(i));
            deck.remove(i);
        }
        Collections.reverse(nDeck);
        return nDeck;
    }

    static boolean isSet(CardDeck deck)
    {
        return ((deck.size() == 3 || deck.size() == 4) && deck.allSameRank());
    }

    static boolean isOrderedRun(CardDeck deck)
    {
        if(deck.size() < 3)
        {
            return false;
        }

        if (!deck.allSameSuit())
        {
            return false;
        }

        for (int i = 0; i < deck.size() - 1; i++) {
            if (deck.get(i).getRank().getNumRank() != deck.get(i+1).getRank().getNumRank() - 1)
            {
                return false;
            }
        }
        return true;
    }


}
