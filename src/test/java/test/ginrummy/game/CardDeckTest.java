package test.ginrummy.game;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CardDeckTest {

    @Test
    public void testCreatingAnEmptyDeck()
    {
        CardDeck cardDeck = new CardDeck(CardDeck.DeckType.EMPTY, false);
        Assertions.assertTrue(cardDeck.isEmpty());
    }

    @Test
    public void testCreatingAStandardDeckOfCards()
    {
        CardDeck cardDeck = new CardDeck(CardDeck.DeckType.FRENCH, false);
        Assertions.assertEquals(52, cardDeck.size());
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                Assertions.assertTrue(cardDeck.contains(suit, rank));
            }
        }
    }
}
