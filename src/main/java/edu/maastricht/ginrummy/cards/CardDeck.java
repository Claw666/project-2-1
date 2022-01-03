package edu.maastricht.ginrummy.cards;

import edu.maastricht.ginrummy.game.GameGUI;

import java.util.*;

public class CardDeck extends LinkedList<Card> implements Cloneable {

    public CardDeck() { }

    public CardDeck(DeckType deckType, boolean shuffle)
    {
        fillDeck(deckType);
        if (shuffle) { shuffle(); }
    }

    @Override
    public boolean addAll(Collection<? extends Card> c) {
        if (!Collections.disjoint(this, c)) {
            throw new AssertionError();
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(Card card) {
        assert !this.contains(card);
        return super.add(card);
    }

    @Override
    public void addLast(Card card) {
        assert !this.contains(card);
        super.addLast(card);
    }

    @Override
    public void addFirst(Card card) {
        assert !this.contains(card);
        super.addFirst(card);
    }


    @Override
    public CardDeck clone()
    {
        CardDeck cloned = new CardDeck();
        this.forEach(c -> cloned.add(c.clone()));
        return cloned;
    }

    public void shuffle()
    {
        for (int i=this.size()-1; i>=1; i--)
        {
            super.set(i, super.set(GameGUI._random.nextInt(i), super.get(i)));
        }
    }


    public void shuffle(int rounds)
    {
        for (int i = 0; i < rounds; i++)
        {
            this.shuffle();
        }
    }

    public boolean allSameSuit()
    {
        CardDeck deck = this;
        if (deck.size() == 0)
        {
            return true;
        }

        Card.Suit suit = deck.get(0).getSuit();
        for(Card card : deck)
        {
            if (card.getSuit() != suit)
            {
                return false;
            }
        }
        return true;
    }

    public boolean allSameRank()
    {
        CardDeck deck = this;
        if (deck.size() == 0)
        {
            return true;
        }

        Card.Rank suit = deck.get(0).getRank();
        for(Card card : deck)
        {
            if (card.getRank() != suit)
            {
                return false;
            }
        }
        return true;
    }

    public Card draw()
    {
        assert !this.isEmpty();
        return this.removeLast();
    }

    public List<Card> draw(int amount)
    {
        assert amount > 0 && this.size() >= amount;
        List<Card> result = new ArrayList<>();
        for(int i = 0; i < amount; i++)
        {
            result.add(this.removeLast());
        }
        return Collections.unmodifiableList(result);
    }

    public CardDeck drawByIdxList(List<Integer> order)
    {
        assert order.size() <= this.size() : "Order list has to many entries.";
        CardDeck nDeck = new CardDeck();
        for (int i : order)
        {
            nDeck.add(this.get(i));
        }
        return nDeck;
    }

    public boolean contains(Card.Suit suit, Card.Rank rank) {
        return this.contains(new Card(suit, rank));
    }

    private void fillDeck(DeckType deckType)
    {
        assert this.isEmpty();

        switch (deckType)
        {
            case EMPTY: break;
            case FRENCH: {
                for (Card.Suit suit : Card.Suit.values()) {
                    for (Card.Rank rank : Card.Rank.values()) {
                        this.add(new Card(suit, rank));
                    }
                }
                break;
            }
        }

    }

    public enum DeckType
    {
        EMPTY,
        FRENCH
    }

}
