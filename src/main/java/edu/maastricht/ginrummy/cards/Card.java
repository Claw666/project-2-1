package edu.maastricht.ginrummy.cards;

import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.Comparator;

public class Card implements Cloneable, Comparable<Card> {

    private final Colour colour;
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank)
    {
        this(((suit == Suit.DIAMONDS || suit == Suit.HEARTS) ? Colour.RED : Colour.BLACK),
                suit, rank);
    }

    public Card(Colour colour, Suit suit, Rank rank)
    {
        this.colour = colour;
        this.suit = suit;
        this.rank = rank;
    }

    public long getByteIndex()
    {
        return toByteIndex(this.rank, this.suit, AdvancedMeldDetector.Combination.Type.RUN);
    }

    public static long toByteIndex(Card card, AdvancedMeldDetector.Combination.Type type)
    {
        return toByteIndex(card.getRank(), card.getSuit(), type);
    }

    public static long toByteIndex(Rank rank, Suit suit, AdvancedMeldDetector.Combination.Type type)
    {
        switch (type)
        {
            case RUN:
                return 1L << ((rank.getNumRank() - 1) + suit.getID() * 13);
            case SET:
                return 1L << ((rank.getNumRank() - 1) * 4 + suit.getID());
        }
        throw new IllegalArgumentException();
    }

    public static Card fromSingleByteIndex(long card, AdvancedMeldDetector.Combination.Type type)
    {
        final int mostRightIndex = (int) (Math.log(card) / Math.log(2));
        switch (type)
        {

            case RUN:
            {
                final int suit = mostRightIndex / 13;
                final int rank = (mostRightIndex % 13) + 1;

                return new Card(Card.Suit.byId(suit), Card.Rank.byNumRank(rank));
            }
            case SET:
            {
                final int rank = (mostRightIndex / 4) + 1;
                final int suit = mostRightIndex % 4;

                return new Card(Card.Suit.byId(suit), Card.Rank.byNumRank(rank));

            }
        }
        throw new IllegalArgumentException();
    }

    public Colour getColour() {
        return this.colour;
    }

    public Suit getSuit() {
        return this.suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getDeadwoodValue() {
        return rank.getDeadwoodValue();
    }

    public String getImageFileName()
    {
        return toImageFileName(this.suit, this.rank);
    }

    public String cardToUnicodeString()
    {
        int id = 0x1F0A0 +
                getSuit().getID() * 16 +
                getRank().getNumRank();
        return new StringBuilder().appendCodePoint(id).toString();
    }

    public String brief_string()
    {
        return String.format("%s%s", this.suit.getAsString(), this.rank.getAsString());
    }

    public String long_string()
    {
        return String.format("[Card;suit=%s, rank=%s, ranking=%d, colour=%s]", this.suit, this.rank, this.rank.getNumRank(), this.colour);
    }

    @Override
    public Card clone()
    {
        return new Card(this.suit, this.rank);
    }

    @Override
    public String toString()
    {
        return brief_string();
    }

    public static String toImageFileName(Suit suit, Rank rank)
    {
        return String.format("%s%s.png", suit.getAsString(), rank.getAsString());
    }

    @Override
    public int compareTo(Card o) {
        return Long.compare(this.getByteIndex(), o.getByteIndex());
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Card))
        {
            return false;
        }

        Card card = (Card) obj;
        return card.getByteIndex() == this.getByteIndex();

    }

    public static enum Colour {
        RED,
        BLACK
    }

    public static enum Suit {
        CLUBS("C", 3),
        SPADES("S", 0),
        HEARTS("H", 1),
        DIAMONDS("D", 2);

        private static Suit[] suits;

        private final String value;
        private final int id;

        Suit(String value, int id)
        {
            this.value = value;
            this.id = id;
        }

        public String getAsString() {
            return value;
        }

        public int getID() {
            return id;
        }

        public static Suit byId(int id)
        {
            if(suits == null)
            {
                suits = new Suit[4];
                for (Suit suit : Suit.values())
                {
                    suits[suit.getID()] = suit;
                }
            }
            return suits[id];
        }
    }

    public static enum Rank
    {
        ACE(1, "A", 1),
        TWO(2, "2", 2),
        THREE(3, "3",3),
        FOUR(4, "4",4),
        FIVE(5, "5",5),
        SIX(6, "6",6),
        SEVEN(7, "7",7),
        EIGHT(8, "8",8),
        NINE(9, "9",9),
        TEN(10, "10",10),
        JACK(11, "J",10),
        QUEEN(12, "Q",10),
        KING(13, "K",10);

        private static Rank[] ranks;

        private final int numRank;
        private final String rank;
        private final int deadwoodValue;

        Rank(int numRank, String rank, int deadwoodValue)
        {
            this.numRank = numRank;
            this.rank = rank;
            this.deadwoodValue = deadwoodValue;
        }


        public int getNumRank() {
            return numRank;
        }

        public String getAsString() {
            return rank;
        }

        public int getDeadwoodValue() {return deadwoodValue;}

        public static Rank byNumRank(int numRank)
        {
            if(ranks == null)
            {
                ranks = new Rank[13];
                for (Rank rank : Rank.values())
                {
                    ranks[rank.getNumRank()-1] = rank;
                }
            }
            return ranks[numRank-1];
        }
    }

    public static class DeadwoodSorter implements Comparator<Card>
    {

        private final boolean ascending;

        public DeadwoodSorter(boolean ascending)
        {
            this.ascending = ascending;
        }

        @Override
        public int compare(Card o1, Card o2) {
            int value = Double.compare(o1.getRank().getDeadwoodValue(), o2.getRank().getDeadwoodValue());
            if(!ascending)
            {
                value *= -1;
            }
            return value;
        }
    }

    public static class GroupBySetSorter implements Comparator<Card>
    {

        private final boolean ascending;

        public GroupBySetSorter(boolean ascending)
        {
            this.ascending = ascending;
        }

        @Override
        public int compare(Card o1, Card o2) {
            int value = Double.compare(Card.toByteIndex(o1.getRank(), o1.getSuit(), AdvancedMeldDetector.Combination.Type.SET),
                    Card.toByteIndex(o1.getRank(), o2.getSuit(), AdvancedMeldDetector.Combination.Type.SET));
            if(!ascending)
            {
                value *= -1;
            }
            return value;
        }
    }

}
