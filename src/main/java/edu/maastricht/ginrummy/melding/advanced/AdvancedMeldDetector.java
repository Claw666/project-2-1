package edu.maastricht.ginrummy.melding.advanced;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.MeldGrouping;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdvancedMeldDetector {

    /**
     * This method takes in a CardDeck, and returns the best combinationOfCombinations of sets and runs it can find within
     * this deck.
     * @param cardDeck The card deck in question.
     * @return Never null, MeldGrouping with results.
     */
    public static MeldGrouping find(final CardDeck cardDeck)
    {

        //--- empty deck
        if(cardDeck.isEmpty())
        {
            return new MeldGrouping();
        }

        //--- Generates all possible SETs and RUNs
        List<Combination> combinations = generatePossibleCombinations(cardDeck);

        //--- If no combinations (=> no runs & no sets) have been found, all of the cards are deadwood
        if(combinations.isEmpty())
        {
            MeldGrouping meldGrouping = new MeldGrouping();
            meldGrouping.getDeadwood().addAll(cardDeck);
            return meldGrouping;
        }

        //--- Try all possible combinations of the combinations (weirdly worded ^^) and pick the one with least deadwood
        EvaluateCombination.Result bestResult = findLeastDeadwoodCombination(cardDeck, combinations);

        //--- Figure out which cards are deadwood
        long encodedUsedCards = bestResult.getEncodedCards();
        for(Card card : cardDeck)
        {
            if((encodedUsedCards & card.getByteIndex()) == 0)
            {
                bestResult.getMeldGrouping().getDeadwood().add(card);
            }
        }

        return bestResult.getMeldGrouping();
    }

    /**
     * This method tries to merge the Deadwood cars of the merger into the sets and runs of the knocker.
     * @param knocker
     * @param merger
     * @return Null, if it couldn't merge, otherwise the results of the merge operation.
     */
    public static MergeResult mergeDeadwood(CardDeck knocker, CardDeck merger)
    {
        final CardDeck deadwood = find(merger).getDeadwood();
        //--- No deadwood -> merging impossible
        if(deadwood.isEmpty())
        {
            return null;
        }

        //--- Convert knocker's hand to combinations, and only keep the combinations that can actually be extended.
        final MeldGrouping meldGrouping = find(knocker);
        List<Combination> combinations = new ArrayList<>();
        for (CardDeck cards : meldGrouping.getSets()) {
            combinations.add(new Combination(Combination.Type.SET, encodeCards(cards), cards));
        }
        for (CardDeck s : meldGrouping.getRuns()) {
            combinations.add(new Combination(Combination.Type.RUN, encodeCards(s), s));
        }

        //--- No combinations -> merging impossible
        if(combinations.isEmpty())
        {
            return null;
        }

        // - A set can only be extended if it has only 3 cards in it
        // - A run can only be extended if it has less than 13 cards in it
        combinations = combinations.stream().filter(c -> {
                    if(c.getType() == Combination.Type.SET && c.getCombination().size() == 3)
                    {
                        return true;
                    }
                    else if(c.getType() == Combination.Type.RUN && c.getCombination().size() < 13)
                    {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());

        LinkedList<Combination> validMerges = new LinkedList<>();

        for (final Combination entry : combinations) {
            List<Card> tmp = entry.getCombination();
            tmp.addAll(deadwood);
            generatePossibleCombinations(tmp, validMerges);
        }

        final long deadwoodEncoding = encodeCards(deadwood);
        validMerges = validMerges.stream()
                .filter(merge -> ((merge.getEncoding() & deadwoodEncoding) != 0 && (merge.getEncoding() ^ deadwoodEncoding) != 0))
                .collect(Collectors.toCollection(LinkedList::new));

        if(validMerges.isEmpty())
        {
            return null;
        }

        CardDeck cardDeck = knocker.clone();
        cardDeck.addAll(deadwood);

        EvaluateCombination.Result bestResult = findLeastDeadwoodCombination(cardDeck, validMerges);

        //---
        CardDeck usedCardsFromDeadwood = new CardDeck();
        for(Card card : deadwood)
        {
            if((card.getByteIndex() & bestResult.getEncodedCards()) != 0)
            {
                usedCardsFromDeadwood.add(card);
            }
        }

        return new MergeResult(bestResult.getMeldGrouping(), usedCardsFromDeadwood);
    }

    private static EvaluateCombination.Result findLeastDeadwoodCombination(CardDeck cardDeck, List<Combination> combinations)
    {
        final long cardDeckEncoded = encodeCards(cardDeck);

        boolean anyChange = false;
        long bestDeadwood = Integer.MAX_VALUE;

        EvaluateCombination.Result bestResult = new EvaluateCombination.Result(null, 0);

        for(int i = 1; i <= Math.floorDiv(cardDeck.size(), 3); i++)
        {
            if(i <= combinations.size()) {
                EvaluateCombination.Result tmp = combinationOfCombinations(cardDeck, combinations, i);
                final int tmpDeadwood = countDeadwoodValue(cardDeckEncoded ^ tmp.getEncodedCards());
                if(tmpDeadwood < bestDeadwood)
                {
                    anyChange = true;
                    bestResult = tmp;
                    bestDeadwood = tmpDeadwood;
                    // If the amount for this valid combination is 0 then this is a (!) perfect combination.
                    if(tmpDeadwood == 0)
                    {
                        break;
                    }
                }
            }
            // If there is no possible set of combinations at this point then it will be impossible to find any
            // other combinations for larger sets of combinations. For this reason, this for-loop's direction cannot (!)
            // be reversed.
            if(!anyChange)
            {
                break;
            }
            anyChange = false;
        }

        return bestResult;

    }

    private static List<Combination> generatePossibleCombinations(List<Card> cardDeck)
    {
        List<Combination> combinations = new ArrayList<>();
        generatePossibleCombinations(cardDeck,combinations);
        return combinations;
    }

    private static void generatePossibleCombinations(List<Card> cardDeck, List<Combination> combinations)
    {
        generatePossibleCombinations(cardDeck, combinations, Combination.Type.RUN);
        generatePossibleCombinations(cardDeck, combinations, Combination.Type.SET);
    }


    private static void generatePossibleCombinations(List<Card> cardDeck, List<Combination> combinations,
                                                     final Combination.Type type)
    {
        assert type != null && combinations != null && cardDeck != null;
        final int cutOffIndex = (type == Combination.Type.RUN ? Card.Rank.values().length : Card.Suit.values().length);
        {
            long encodedCards = groupCardsByType(cardDeck, type);

            List<Card> cards = new ArrayList<>();
            int lastIndex = 0;
            while (encodedCards > 0)
            {
                final long bitMask = encodedCards & -encodedCards;
                final int index = (int) (Math.log(bitMask) / Math.log(2));
                Card card = Card.fromSingleByteIndex(bitMask, type);
                encodedCards ^= bitMask;


                if(index - lastIndex > 1 || (index % cutOffIndex == 0 && index != 0))
                {
                    if(cards.size() >= 3)
                    {
                        //TODO Having this here, and below even though it is the same code is fucking stupid!!! [:JanIsSometimesStupidAF]
                        for(int startIndex = 0; startIndex <= (cards.size() - 3); startIndex++)
                        {
                            for(int end = cards.size() - 1; end >= 2 && ((end + 1) - startIndex) >= 3; end--)
                            {
                                List<Card> c = cards.subList(startIndex, end+1);
                                combinations.add(new Combination(type, encodeCards(c), c));
                            }
                        }
                        cards = new ArrayList<>();
                    }
                    else
                    {
                        cards.clear();
                    }
                }

                cards.add(card);
                lastIndex = index;
            }
            if(cards.size() >= 3)
            {
                //TODO Having this here, and below even though it is the same code is fucking stupid!!! [:JanIsSometimesStupidAF]
                for(int startIndex = 0; startIndex <= (cards.size() - 3); startIndex++)
                {
                    for(int end = cards.size() - 1; end >= 2 && ((end + 1) - startIndex) >= 3; end--)
                    {
                        List<Card> c = cards.subList(startIndex, end+1);
                        combinations.add(new Combination(type, encodeCards(c), c));
                    }
                }
            }
        }
    }

    /**
     * Iterates through all iterations of the combinations, calls the callback to evaluate them, and then returns
     * the best combination out of the combinations it tested.
     *
     * @param cardDeck
     * @param combinations All combinations.
     * @param k k <= combinations, set size
     * @return
     */
    private static EvaluateCombination.Result combinationOfCombinations(CardDeck cardDeck, List<Combination> combinations, int k){

        final AtomicReference<EvaluateCombination.Result> bestResult = new AtomicReference<>(new EvaluateCombination.Result(null, 0));
        final AtomicInteger bestDeadwood = new AtomicInteger(Integer.MAX_VALUE);
        final long cardDeckEncoded = encodeCards(cardDeck);

        iterateCombinations(combinations, k, new PrepareFunction<>() {
            @Override
            public <A extends Function<List<Combination>, EvaluateCombination.Result>> void apply(A callback, List<Combination> input) {
                EvaluateCombination.Result result;
                if((result = callback.apply(input)) != null)
                {
                    int tmpDeadwood = countDeadwoodValue(cardDeckEncoded ^ result.getEncodedCards());
                    if(tmpDeadwood < bestDeadwood.get())
                    {
                        bestResult.set(result);
                        bestDeadwood.set(tmpDeadwood);
                    }
                }
            }

        }, new EvaluateCombination());

        return bestResult.get();

    }

    /**
     * This encodes a set of unique cards in a long. Each card is assigned a unique index determined by {@link Card#getByteIndex()}.
     * @param cards The cards that need to be encoded.
     * @return
     */
    private static long encodeCards(List<Card> cards)
    {
        return groupCardsByType(cards, Combination.Type.RUN);
    }

    private static long groupCardsByType(List<Card> cards, Combination.Type type)
    {
        long encoded = 0L;
        for(Card card : cards)
        {
            long encoding = Card.toByteIndex(card.getRank(), card.getSuit(), type);
            if ((encoded & encoding) != 0) {
                throw new IllegalArgumentException(String.format("The card %s appears more than once in this set. This is" +
                        " not valid.", card.long_string()));
            }
            encoded |= encoding;
        }
        return encoded;
    }


    /**
     * Decodes an encoded set of cards in O(n) where <code>n=amount of set bits</code> is which is pretty cool.
     * @param encoded
     * @return
     */
    private static CardDeck decodeCards(long encoded, Callback<Card> callback)
    {
        CardDeck cards = new CardDeck();
        while (encoded > 0)
        {
            final long bitMask = encoded & -encoded;
            Card card = Card.fromSingleByteIndex(bitMask, Combination.Type.RUN);
            encoded ^= bitMask;
            cards.add(card);
            callback.apply(card);
        }
        return cards;
    }

    private static int countDeadwoodValue(long encodedCards)
    {
        final AtomicInteger tmpDeadwood = new AtomicInteger(0);
        decodeCards(encodedCards, card -> {
            tmpDeadwood.addAndGet(card.getRank().getDeadwoodValue());
        });
        return tmpDeadwood.get();
    }

    /**
     * @SOURCE: http://hmkcode.com/calculate-find-all-possible-combinations-of-an-array-using-java/ - The code is heavily
     * based on the "Forward-Backward" algorithm shown on this page, but it has been modified so that it can be used in
     * this application.
     * Last Accessed: 2019/10/01
     * -------------------------
     * Iterates over combinations of size <code>k</code> defined below.
     * @param deck The original data whose elements have to be combined.
     * @param k The size of each combination.
     * @param prepare Called before the <code>combinationCallback</code> is called to prepare the input.
     * @param combinationCallback Actually process the combination at hand.
     * @param <T> The type of data that is going to be processed.
     * @param <R> The return type of the callback function.
     */
    public static <T,R> void iterateCombinations(List<T> deck, int k, PrepareFunction<T, R> prepare,
                                                  Function<List<T>, R> combinationCallback){

        // init combinationOfCombinations index array
        int[] indices = new int[k];


        int r = 0; // index for combinationOfCombinations array
        int i = 0; // index for elements array

        while(r >= 0)
        {

            // forward step if i < (N + (r-K))
            if(i <= (deck.size() + (r - k)))
            {
                indices[r] = i;

                // if combinationOfCombinations array is full print and increment i;
                if(r == k-1)
                {
                    LinkedList<T> combination = new LinkedList<>();
                    for(int pointer : indices)
                    {
                        combination.add(deck.get(pointer));
                    }
                    prepare.apply(combinationCallback, combination);
                    i++;
                }
                else
                {
                    // if combinationOfCombinations is not full yet, select next element
                    i = indices[r]+1;
                    r++;
                }
            }
            // backward step
            else
            {
                r--;
                if(r >= 0)
                {
                    i = indices[r]+1;
                }
            }
        }
    }

    /**
     * This function is called before the actual combination callback is called. - This can be used to modify what is passed
     * to the actual callback, and other shenanigans.
     * @param <B> The type of data the combinations consists of.
     */
    public static class PrepareFunction<B,R> {

        public <A extends Function<List<B>, R>> void apply(A callback, List<B> input) {
            callback.apply(input);
        }

    }

    public static class Combination {

        private final Type type;
        private final List<Card> combination;
        private final long encoding;

        private Combination(Type type, long encoding, List<Card> combination)
        {
            this.type = type;
            this.combination = combination;
            this.encoding = encoding;
        }

        public Type getType() {
            return type;
        }

        public List<Card> getCombination() {
            return combination;
        }

        public long getEncoding() {
            return encoding;
        }

        public enum Type {
            RUN,
            SET
        }

    }

}
