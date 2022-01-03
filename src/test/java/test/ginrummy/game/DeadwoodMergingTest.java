package test.ginrummy.game;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;
import edu.maastricht.ginrummy.melding.advanced.MergeResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeadwoodMergingTest {

    @Test
    public void testMergerHasNoRunsAndNoSets()
    {
        CardDeck knocker = new CardDeck();
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN)); //:DW
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.NINE)); //:DW

        CardDeck merger = new CardDeck();
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.SIX)); //:DW
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT)); //:DW

        MergeResult mergeResult = AdvancedMeldDetector.mergeDeadwood(knocker, merger);

        assertNull(mergeResult);
    }

    @Test
    public void testMergerHasNoDeadwood()
    {
        CardDeck knocker = new CardDeck();
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN)); //:RUN[1]
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT)); //:RUN[1]
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.NINE)); //:RUN[1]

        CardDeck merger = new CardDeck();
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.SIX)); //:RUN[2]
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.FIVE)); //:RUN[2]
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.FOUR)); //:RUN[2]

        MergeResult mergeResult = AdvancedMeldDetector.mergeDeadwood(knocker, merger);

        assertNull(mergeResult);
    }

    @Test
    public void testSimpleRun()
    {

        CardDeck knocker = new CardDeck();
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN)); //:RUN[1]
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT)); //:RUN[1]
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.NINE)); //:RUN[1]

        CardDeck merger = new CardDeck();
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.SIX)); //:[1]
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.FIVE)); //:[1]
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.THREE)); //:DW

        MergeResult mergeResult = AdvancedMeldDetector.mergeDeadwood(knocker, merger);

        assertNotNull(mergeResult);
        assertNotNull(mergeResult.getMergingCards());
        assertNotNull(mergeResult.getMeldGrouping());

        assertEquals(2, mergeResult.getMergingCards().size());
        assertTrue(mergeResult.getMergingCards().contains(Card.Suit.SPADES, Card.Rank.FIVE));
        assertTrue(mergeResult.getMergingCards().contains(Card.Suit.SPADES, Card.Rank.SIX));

        assertEquals(1, mergeResult.getMeldGrouping().getRuns().size());
        {
            CardDeck run = mergeResult.getMeldGrouping().getRuns().get(0);
            assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.FIVE));
            assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.SIX));
            assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.SEVEN));
            assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.EIGHT));
            assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.NINE));
        }


        assertTrue(mergeResult.getMeldGrouping().getSets().isEmpty());

    }

    @Test
    public void testSimpleSet()
    {

        CardDeck knocker = new CardDeck();
        knocker.add(new Card(Card.Suit.HEARTS, Card.Rank.SEVEN)); //:SET[1]
        knocker.add(new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN)); //:SET[1]
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN)); //:SET[1]

        CardDeck merger = new CardDeck();
        merger.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:[1]
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.FIVE)); //:DW
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.THREE)); //:DW

        MergeResult mergeResult = AdvancedMeldDetector.mergeDeadwood(knocker, merger);

        assertNotNull(mergeResult);
        assertNotNull(mergeResult.getMergingCards());
        assertNotNull(mergeResult.getMeldGrouping());

        assertEquals(1, mergeResult.getMergingCards().size());
        assertTrue(mergeResult.getMergingCards().contains(Card.Suit.CLUBS, Card.Rank.SEVEN));

        assertEquals(1, mergeResult.getMeldGrouping().getSets().size());
        {
            CardDeck set = mergeResult.getMeldGrouping().getSets().get(0);
            assertTrue(set.contains(Card.Suit.HEARTS, Card.Rank.SEVEN));
            assertTrue(set.contains(Card.Suit.DIAMONDS, Card.Rank.SEVEN));
            assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.SEVEN));
            assertTrue(set.contains(Card.Suit.CLUBS, Card.Rank.SEVEN));
        }

        assertTrue(mergeResult.getMeldGrouping().getRuns().isEmpty());

    }

    @Test
    public void testSimpleSetAndRun()
    {

        CardDeck knocker = new CardDeck();
        knocker.add(new Card(Card.Suit.HEARTS, Card.Rank.SEVEN)); //:SET[1]
        knocker.add(new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN)); //:SET[1]
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN)); //:SET[1]

        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.ACE)); //:RUN[2]
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.TWO)); //:RUN[2]
        knocker.add(new Card(Card.Suit.SPADES, Card.Rank.THREE)); //:RUN[2]

        CardDeck merger = new CardDeck();
        merger.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:[1]
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.FOUR)); //:[2]
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.FIVE)); //:[2]
        merger.add(new Card(Card.Suit.SPADES, Card.Rank.KING)); //:DW

        MergeResult mergeResult = AdvancedMeldDetector.mergeDeadwood(knocker, merger);

        assertNotNull(mergeResult);
        assertNotNull(mergeResult.getMergingCards());
        assertNotNull(mergeResult.getMeldGrouping());

        assertEquals(3, mergeResult.getMergingCards().size());

        {
            CardDeck mergingCards = mergeResult.getMergingCards();
            assertTrue(mergingCards.contains(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:[1]
            assertTrue(mergingCards.contains(Card.Suit.SPADES, Card.Rank.FOUR)); //:[2]
        }

        assertEquals(1, mergeResult.getMeldGrouping().getSets().size());
        {
            CardDeck set = mergeResult.getMeldGrouping().getSets().get(0);
            assertTrue(set.contains(Card.Suit.HEARTS, Card.Rank.SEVEN));
            assertTrue(set.contains(Card.Suit.DIAMONDS, Card.Rank.SEVEN));
            assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.SEVEN));
            assertTrue(set.contains(Card.Suit.CLUBS, Card.Rank.SEVEN));
        }

        assertEquals(1, mergeResult.getMeldGrouping().getRuns().size());
        {
            CardDeck set = mergeResult.getMeldGrouping().getRuns().get(0);
            assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.ACE));
            assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.TWO));
            assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.THREE));
            assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.FOUR));
            assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.FIVE));
        }
    }


}
