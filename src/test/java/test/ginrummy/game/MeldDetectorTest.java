package test.ginrummy.game;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MeldDetectorTest
{

    @Test
    public void test_full_hand()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.FRENCH, false);
        AdvancedMeldDetector.find(hand);
    }

    @Test
    public void testBug_1()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN)); //:SET
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:SET
        hand.add(new Card(Card.Suit.HEARTS, Card.Rank.SEVEN)); //:SET

        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.SIX)); //:DW
        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR)); //:DW

        hand.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.NINE)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.TEN)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.JACK)); //:RUN

        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);
        Assertions.assertEquals(2, meldGrouping.getDeadwood().size());

        Assertions.assertEquals(1, meldGrouping.getSets().size());
        {

            CardDeck set = meldGrouping.getSets().get(0);

            Assertions.assertEquals(3, set.size());
            Assertions.assertTrue(set.contains(Card.Suit.HEARTS, Card.Rank.SEVEN));
            Assertions.assertTrue(set.contains(Card.Suit.DIAMONDS, Card.Rank.SEVEN));
            Assertions.assertTrue(set.contains(Card.Suit.CLUBS, Card.Rank.SEVEN));
        }

        Assertions.assertEquals(1, meldGrouping.getRuns().size());

        {
            CardDeck run = meldGrouping.getRuns().get(0);

            Assertions.assertEquals(5, run.size());
            Assertions.assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.SEVEN));
            Assertions.assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.EIGHT));
            Assertions.assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.NINE));
            Assertions.assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.TEN));
            Assertions.assertTrue(run.contains(Card.Suit.SPADES, Card.Rank.JACK));
        }

    }

    @Test
    public void testBug_2()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.NINE)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TEN)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.JACK)); //:RUN

        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.JACK)); //:SET
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.JACK)); //:SET
        hand.add(new Card(Card.Suit.HEARTS, Card.Rank.JACK)); //:SET

        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT)); //:DW
        hand.add(new Card(Card.Suit.HEARTS, Card.Rank.ACE)); //:DW

        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);
        Assertions.assertEquals(2, meldGrouping.getDeadwood().size());

        Assertions.assertEquals(1, meldGrouping.getSets().size());
        {

            CardDeck set = meldGrouping.getSets().get(0);

            Assertions.assertEquals(3, set.size());
            Assertions.assertTrue(set.contains(Card.Suit.HEARTS, Card.Rank.JACK));
            Assertions.assertTrue(set.contains(Card.Suit.DIAMONDS, Card.Rank.JACK));
            Assertions.assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.JACK));
        }

        Assertions.assertEquals(1, meldGrouping.getRuns().size());

        {
            CardDeck run = meldGrouping.getRuns().get(0);

            Assertions.assertEquals(5, run.size());
            Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.SEVEN));
            Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.EIGHT));
            Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.NINE));
            Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.TEN));
            Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.JACK));
        }

    }

    @Test
    public void testRunDetectionInMostBasicCase()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.TWO));
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.THREE));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.FOUR));
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.SIX));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.ACE));

        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TEN));
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.JACK));
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.QUEEN));
        hand.shuffle();

        /**
         * - A set is three or four cards of the same rank (5-5-5)
         * - A run is three or more cards of consecutive rank in the same suit
         */
        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);

        Assertions.assertEquals(7, meldGrouping.getDeadwood().size());

        Assertions.assertTrue(meldGrouping.getSets().isEmpty());


        Assertions.assertEquals(1, meldGrouping.getRuns().size());
        CardDeck run = meldGrouping.getRuns().get(0);

        Assertions.assertEquals(3, run.size());
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.TEN));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.JACK));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.QUEEN));
    }

    @Test
    public void testRunDetectionInFullHand()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.TWO));
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.THREE));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.FOUR));
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.SIX));

        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.NINE)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TEN)); //:RUN

        hand.shuffle();

        /**
         * - A set is three or four cards of the same rank (5-5-5)
         * - A run is three or more cards of consecutive rank in the same suit
         */
        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);

        Assertions.assertEquals(6, meldGrouping.getDeadwood().size());

        Assertions.assertTrue(meldGrouping.getSets().isEmpty());


        Assertions.assertEquals(1, meldGrouping.getRuns().size());
        CardDeck run = meldGrouping.getRuns().get(0);

        Assertions.assertEquals(4, run.size());
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.SEVEN));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.EIGHT));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.NINE));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.TEN));
    }

    @Test
    public void testDetectionWithRunAndSet()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.HEARTS, Card.Rank.SIX)); //:SET
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.SIX)); //:SET
        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.SIX)); //:SET

        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.FOUR));

        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.NINE)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TEN)); //:RUN

        hand.shuffle();

        /**
         * - A set is three or four cards of the same rank (5-5-5)
         * - A run is three or more cards of consecutive rank in the same suit
         */
        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);

        Assertions.assertEquals(2, meldGrouping.getDeadwood().size());

        Assertions.assertEquals(1, meldGrouping.getSets().size());
        CardDeck set = meldGrouping.getSets().get(0);

        Assertions.assertEquals(3, set.size());
        Assertions.assertTrue(set.contains(Card.Suit.HEARTS, Card.Rank.SIX));
        Assertions.assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.SIX));
        Assertions.assertTrue(set.contains(Card.Suit.DIAMONDS, Card.Rank.SIX));


        Assertions.assertEquals(1, meldGrouping.getRuns().size());
        CardDeck run = meldGrouping.getRuns().get(0);

        Assertions.assertEquals(4, run.size());
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.SEVEN));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.EIGHT));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.NINE));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.TEN));
    }

    @Test
    public void testDetectionWithRunAndTwoSets()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.HEARTS, Card.Rank.SIX)); //:SET
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.SIX)); //:SET
        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.SIX)); //:SET


        hand.add(new Card(Card.Suit.HEARTS, Card.Rank.FIVE)); //:SET
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.FIVE)); //:SET
        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE)); //:SET

        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.NINE)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TEN)); //:RUN

        hand.shuffle();

        /**
         * - A set is three or four cards of the same rank (5-5-5)
         * - A run is three or more cards of consecutive rank in the same suit
         */
        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);

        Assertions.assertEquals(0, meldGrouping.getDeadwood().size());

        Assertions.assertEquals(2, meldGrouping.getSets().size());
        Assertions.assertEquals(1, meldGrouping.getRuns().size());
        CardDeck run = meldGrouping.getRuns().get(0);

        Assertions.assertEquals(4, run.size());
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.SEVEN));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.EIGHT));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.NINE));
        Assertions.assertTrue(run.contains(Card.Suit.CLUBS, Card.Rank.TEN));
    }

    @Test
    public void testTwoRunsOfFour()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.TWO));

        hand.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.NINE)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.TEN)); //:RUN

        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.NINE)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TEN)); //:RUN

        hand.shuffle();

        /**
         * - A set is three or four cards of the same rank (5-5-5)
         * - A run is three or more cards of consecutive rank in the same suit
         */
        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);

        Assertions.assertEquals(2, meldGrouping.getDeadwood().size());

        Assertions.assertTrue(meldGrouping.getSets().isEmpty());


        Assertions.assertEquals(2, meldGrouping.getRuns().size());

    }

    @Test
    public void testThreeRunsOfThree()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));

        hand.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.NINE)); //:RUN

        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.ACE)); //:RUN
        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.TWO)); //:RUN
        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.THREE)); //:RUN

        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.NINE)); //:RUN
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TEN)); //:RUN

        hand.shuffle();

        /**
         * - A set is three or four cards of the same rank (5-5-5)
         * - A run is three or more cards of consecutive rank in the same suit
         */
        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);

        Assertions.assertEquals(1, meldGrouping.getDeadwood().size());

        Assertions.assertTrue(meldGrouping.getSets().isEmpty());


        Assertions.assertEquals(3, meldGrouping.getRuns().size());

    }

    @Test
    public void testRunOfLength3to13()
    {
        Card.Rank[] ranks = Card.Rank.values();
        for(Card.Suit suit : Card.Suit.values())
        {
            for(int length = 3; length <= ranks.length; length++) {
                for (int i = 0; i < ranks.length - (length - 1); i++) {
                    CardDeck hand = new CardDeck();
                    for (int j = 0; j < length; j++) {
                        hand.add(new Card(suit, ranks[j + i]));
                    }

                    hand.shuffle();


                    MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);

                    Assertions.assertTrue(meldGrouping.getDeadwood().isEmpty());
                    Assertions.assertTrue(meldGrouping.getSets().isEmpty());
                    Assertions.assertEquals(1, meldGrouping.getRuns().size());

                }
            }
        }
    }

    @Test
    public void testEmptyCardDeck()
    {
        CardDeck cards = new CardDeck(CardDeck.DeckType.EMPTY, false);

        MeldGrouping meldGrouping = AdvancedMeldDetector.find(cards);

        Assertions.assertTrue(meldGrouping.getDeadwood().isEmpty());
        Assertions.assertTrue(meldGrouping.getSets().isEmpty());
        Assertions.assertTrue(meldGrouping.getRuns().isEmpty());
    }

    @Test
    public void testNoRunsAndNoSetsButNotEmpty()
    {
        CardDeck cards = new CardDeck(CardDeck.DeckType.EMPTY, false);
        cards.add(new Card(Card.Suit.CLUBS, Card.Rank.JACK));
        cards.add(new Card(Card.Suit.SPADES, Card.Rank.ACE));
        cards.add(new Card(Card.Suit.DIAMONDS, Card.Rank.TWO));
        cards.add(new Card(Card.Suit.HEARTS, Card.Rank.THREE));

        cards.shuffle();

        MeldGrouping meldGrouping = AdvancedMeldDetector.find(cards);

        Assertions.assertEquals(4, meldGrouping.getDeadwood().size());
        Assertions.assertTrue(meldGrouping.getSets().isEmpty());
        Assertions.assertTrue(meldGrouping.getRuns().isEmpty());

    }

    @Test
    public void testTrailingZeroSet()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.HEARTS, Card.Rank.ACE)); //:SET
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.ACE)); //:SET
        hand.add(new Card(Card.Suit.DIAMONDS, Card.Rank.ACE)); //:SET
        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE)); //:SET

        hand.shuffle();

        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);
        Assertions.assertTrue(meldGrouping.getDeadwood().isEmpty());
        Assertions.assertTrue(meldGrouping.getRuns().isEmpty());
        Assertions.assertEquals(1, meldGrouping.getSets().size());

        {
            CardDeck set = meldGrouping.getSets().get(0);
            Assertions.assertTrue(set.contains(Card.Suit.HEARTS, Card.Rank.ACE));
            Assertions.assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.ACE));
            Assertions.assertTrue(set.contains(Card.Suit.DIAMONDS, Card.Rank.ACE));
            Assertions.assertTrue(set.contains(Card.Suit.CLUBS, Card.Rank.ACE));

        }

    }

    @Test
    public void testTrailingZeroRun()
    {
        CardDeck hand = new CardDeck(CardDeck.DeckType.EMPTY, false);
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.ACE)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.TWO)); //:RUN
        hand.add(new Card(Card.Suit.SPADES, Card.Rank.THREE)); //:RUN

        hand.shuffle();

        MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);
        Assertions.assertTrue(meldGrouping.getDeadwood().isEmpty());
        Assertions.assertTrue(meldGrouping.getSets().isEmpty());
        Assertions.assertEquals(1, meldGrouping.getRuns().size());

        {
            CardDeck set = meldGrouping.getRuns().get(0);
            Assertions.assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.ACE));
            Assertions.assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.TWO));
            Assertions.assertTrue(set.contains(Card.Suit.SPADES, Card.Rank.THREE));

        }

    }

    // The method that these tests find are now private
    //
//    @Test
//    public void testExtractAllButLast()
//    {
//        CardDeck hand = new CardDeck();
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
//        hand.add(new Card(Card.Suit.SPADES, Card.Rank.TWO));
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.THREE));
//        hand.add(new Card(Card.Suit.SPADES, Card.Rank.FOUR));
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));
//        hand.add(new Card(Card.Suit.SPADES, Card.Rank.SIX));
//
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)); //:RUN
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)); //:RUN
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.NINE)); //:RUN
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TEN)); //:RUN
//
//        var ncd = MeldDetector.extractAllButLast(hand);
//    }
//
//    @Test
//    public void testIsSet()
//    {
//        CardDeck hand1 = new CardDeck();
//        hand1.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
//        hand1.add(new Card(Card.Suit.SPADES, Card.Rank.ACE));
//        hand1.add(new Card(Card.Suit.DIAMONDS, Card.Rank.ACE));
//        hand1.add(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
//
//        CardDeck hand2 = new CardDeck();
//        hand2.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT));
//        hand2.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT));
//        hand2.add(new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT));
//
//        CardDeck hand3 = new CardDeck();
//        hand3.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
//        hand3.add(new Card(Card.Suit.SPADES, Card.Rank.KING));
//        hand3.add(new Card(Card.Suit.DIAMONDS, Card.Rank.ACE));
//        hand3.add(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
//
//        Assertions.assertTrue(MeldDetector.isSet(hand1));
//        Assertions.assertTrue(MeldDetector.isSet(hand2));
//        Assertions.assertFalse(MeldDetector.isSet(hand3));
//    }
//
//    @Test
//    public void testIsRun()
//    {
//        CardDeck hand = new CardDeck();
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.TWO));
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.THREE));
//        hand.add(new Card(Card.Suit.CLUBS, Card.Rank.FOUR));
//
//        var ncd = MeldDetector.isOrderedRun(hand);
//    }
}
