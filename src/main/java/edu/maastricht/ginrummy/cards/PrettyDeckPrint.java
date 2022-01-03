package edu.maastricht.ginrummy.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PrettyDeckPrint {
    public static void pprint(List<Card> deck)
    {
        ArrayList<Integer> lengths = new ArrayList<Integer>();
        for (Card card : deck) {
            String str = String.format("%s\t", card);
            lengths.add(str.length());
            System.out.print(str);
        }
        System.out.print("\n");
        for (int i = 0; i < lengths.size(); i++) {
            System.out.printf("%s\t", i);
        }
        System.out.print("\n");
    }
}
