package edu.maastricht.ginrummy.cards;

import edu.maastricht.ginrummy.game.Player;

public class GameDeckState implements Cloneable{
    private CardDeck deckPlayer1;
    private CardDeck deckPlayer2;
    private CardDeck discard;
    private CardDeck stock;

    public GameDeckState(CardDeck deckPlayer1, CardDeck deckPlayer2, CardDeck discard, CardDeck stock)
    {
        this.deckPlayer1 = deckPlayer1;
        this.deckPlayer2 = deckPlayer2;
        this.discard = discard;
        this.stock = stock;
    }

    public CardDeck getPlayerDeck(Player player) {
        switch (player){
            case P1:
                return getDeckPlayer1();
            case P2:
                return getDeckPlayer2();
            default:
                throw new RuntimeException(String.format("cant get the deck of %s", player));
        }
    }

    public CardDeck getDeckPlayer1() {
        return deckPlayer1;
    }

    public CardDeck getDeckPlayer2() {
        return deckPlayer2;
    }

    public CardDeck getDiscard() {
        return discard;
    }

    public CardDeck getStock() {
        return stock;
    }

    @Override
    public GameDeckState clone() {
        return new GameDeckState(deckPlayer1.clone(), deckPlayer2.clone(), discard.clone(), stock.clone());
    }
}
