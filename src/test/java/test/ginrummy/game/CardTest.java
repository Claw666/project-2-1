package test.ginrummy.game;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class CardTest {

    @Test
    public void testUniquenessOfSETByteIndex()
    {

        Set<Long> byteIndices = new HashSet<>();
        Set<Integer> powersUsed = new HashSet<>();

        CardDeck cards = new CardDeck(CardDeck.DeckType.FRENCH, false);
        for (Card card : cards) {

            final long index = Card.toByteIndex(card.getRank(), card.getSuit(), AdvancedMeldDetector.Combination.Type.SET);

            Assertions.assertFalse(byteIndices.contains(index));
            byteIndices.add(index); // make sure that index is actually unique

            double power = (Math.log(index) / Math.log(2));
            Assertions.assertFalse(powersUsed.contains((int) power));

            powersUsed.add((int) power);
        }
    }

    @Test
    public void testUniquenessOfRUNByteIndex()
    {
        Set<Long> byteIndices = new HashSet<>();
        Set<Integer> powersUsed = new HashSet<>();

        CardDeck cards = new CardDeck(CardDeck.DeckType.FRENCH, false);
        for (Card card : cards) {

            final long index = Card.toByteIndex(card.getRank(), card.getSuit(), AdvancedMeldDetector.Combination.Type.RUN);

            Assertions.assertFalse(byteIndices.contains(index));
            byteIndices.add(index); // make sure that index is actually unique

            double power = (Math.log(index) / Math.log(2));
            Assertions.assertFalse(powersUsed.contains((int) power));

            powersUsed.add((int) power);
        }
    }


}
