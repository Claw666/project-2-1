package edu.maastricht.ginrummy.game;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AverageExpectedDeadwood {

    public static void main(String[] args)
    {

        final long[] counter = {0};
        final long[] sum = {0};

        Map<Integer, Integer> counts = new HashMap<>();

        AdvancedMeldDetector.iterateCombinations(new CardDeck(CardDeck.DeckType.FRENCH, false), 10, new AdvancedMeldDetector.PrepareFunction<>(),
                new Function<List<Card>, Object>() {
                    @Override
                    public Object apply(List<Card> cards) {
                        CardDeck cd = new CardDeck();
                        cd.addAll(cards);
                        int dw = AdvancedMeldDetector.find(cd).deadwoodValue();
                        sum[0] += dw;
                        counter[0]++;
                        counts.put(dw, 1 + counts.getOrDefault(dw, 0));

                        if(dw == 100)
                        {
                            System.out.println(cd);
                        }

                        if(counter[0] % 10000 == 0)
                        {
                        }
                        return null;
                    }
                });

        for(Map.Entry<Integer,Integer> e : counts.entrySet())
        {
            System.out.println(e.getKey() + "," + ((double) e.getValue() / counter[0]));
        }
        System.out.println("avg.: " + ((double) sum[0] / counter[0]));
    }


}
