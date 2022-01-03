package edu.maastricht.ginrummy.melding.advanced;

import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.MeldGrouping;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector.Combination;

public class EvaluateCombination implements Function<List<Combination>, EvaluateCombination.Result> {

    @Override
    public Result apply(List<Combination> possibleCombinations) {
        //--- This method checks whether or not this is a valid combination of sets and runs, or if anythings overlaps
        // and therefore cannot be used.
        MeldGrouping meldGrouping = new MeldGrouping();

        long encodedCards = 0; // Encodes which cards have been used by the combinations

        for(Combination combination : possibleCombinations)
        {
            // If there is a card in encodedCards and in the combination => they are overlapping, and this becomes
            // an invalid combination
            if((encodedCards & combination.getEncoding()) == 0)
            {
                encodedCards |= combination.getEncoding();

                CardDeck cards = new CardDeck(CardDeck.DeckType.EMPTY, false);
                cards.addAll(combination.getCombination());
                if(combination.getType() == Combination.Type.RUN)
                {
                    meldGrouping.getRuns().add(cards);
                }
                else
                {
                    meldGrouping.getSets().add(cards);
                }
            }
            else
            {
                return null;
            }
        }

        return new Result(meldGrouping, encodedCards);
    }

    /**
     * Stores the result from the evaluation.
     */
    static class Result {

        private final MeldGrouping meldGrouping;
        private final long encodedCards;

        /**
         * @param meldGrouping The meld grouping.
         * @param encodedCards A long in which all cards that have been used in a set or run are encoded.
         */
        Result(MeldGrouping meldGrouping, long encodedCards)
        {
            this.meldGrouping = meldGrouping;
            this.encodedCards = encodedCards;
        }

        MeldGrouping getMeldGrouping()
        {
            return this.meldGrouping;
        }

        /**
         * Returns the cards used in sets and runs in the melding group (i.e. everything but the deadwood cards).
         * @return
         */
        long getEncodedCards()
        {
            return this.encodedCards;
        }

    }

}
