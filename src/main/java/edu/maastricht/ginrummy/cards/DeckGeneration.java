package edu.maastricht.ginrummy.cards;

public class DeckGeneration {

    public static GameDeckState completeStateRandomly_mock(CardDeck hand, CardDeck discard) {
        return new GameDeckState(new CardDeck(), new CardDeck(), new CardDeck(), new CardDeck());
    }

    public static GameDeckState completeStateRandomly(CardDeck hand, CardDeck discard){

        CardDeck stock = new CardDeck(CardDeck.DeckType.FRENCH, true);
        CardDeck handP2 = new CardDeck();

        //TODO @Tijn Verify that this is correct (preferably rite tests for this).
        //Remove cards in hand from random deck
        for (Card card : hand) {
            stock.remove(card);
        }
        //Remove cards in discard pile from random deck
        for (Card card : discard) {
            stock.remove(card);
        }

        //Build hand of player 2
        handP2.addAll(stock.draw(10));

        return new GameDeckState(hand, handP2, discard, stock);
    }

}
