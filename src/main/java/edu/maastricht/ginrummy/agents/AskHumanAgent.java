package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.cards.PrettyDeckPrint;
import edu.maastricht.ginrummy.game.GameSettings;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.Scanner;

public class AskHumanAgent extends IHumanAgent {

    private Scanner input = new Scanner(System.in);
    private boolean verbose;

    public AskHumanAgent(CardDeck cardDeck) {
        verbose = true;
    }

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        PrettyDeckPrint.pprint(hand);
        System.out.printf("Top on discard pile: %s\n", discard.peek());
        System.out.print("Pick from the stack (1) or discard (2)?: ");
        var a = getOneOrTwo() == 1 ? CardPickD.STACK : CardPickD.DISCARD;
        System.out.println();
        return a;
    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        PrettyDeckPrint.pprint(hand);
        System.out.print("What card do you want to discard [0-10]? ");
        if (input.hasNextInt())
        {
            int ni = input.nextInt();
            if (ni < hand.size()) {
                System.out.println();
                return ni;
            }
            else
            {
                System.out.println("You need to enter a card number.");
                return whatToDiscard(hand, discard, lastDiscardedCard);
            }
        }
        else
        {
            System.out.println("You need to enter a card number.");
            input = new Scanner(System.in);
            return whatToDiscard(hand, discard, lastDiscardedCard);
        }
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL) {
        int deadwoodValue = AdvancedMeldDetector.find(hand).deadwoodValue();
        if (verbose)
        {
            System.out.printf("You have %s points of deadwood.\n", deadwoodValue);
        }
        if (deadwoodValue <= GameSettings.minDeadWoodToKnock)
        {
            System.out.print("You have less than 10 deadwood. Do you want to Knock? [1 = no, 2 = yes]");
            return getOneOrTwo() == 1 ? false : true;
        }
        return false;
    }

    @Override
    public boolean doYouSkipPick() {
        System.out.println("Do you not want to pick a card in the first round? (1 = Skip, 2 = Proceed to pick)");
        return getOneOrTwo() == 1;
    }

    private int getOneOrTwo()
    {
        String answer = input.next();
        switch (answer) {
            case "1":
                return 1;
            case "2":
                return 2;
            default:
                System.out.print("You need to enter 1 or 2.");
                return getOneOrTwo();
        }
    }

    @Override
    public CardDeck howToReorderDeck(CardDeck hand) {
        while (true)
        {
            System.out.println("Select card to move: ");
            PrettyDeckPrint.pprint(hand);
            if (!input.hasNextInt()) {
                System.out.println("No card selected, reordering terminated.");
                return hand;
            }
            int cardPos = input.nextInt();
            if (cardPos < 0 || cardPos >= hand.size())
            {
                System.out.println("No position selected, reordering terminated.");
                return hand;
            }

            System.out.println("Select position to move to: ");
            if (!input.hasNextInt()) {
                System.out.println("No position selected, reordering terminated.");
                return hand;
            }
            int targetPos = input.nextInt();
            if (targetPos < 0 || targetPos >= hand.size())
            {
                System.out.println("No position selected, reordering terminated.");
                return hand;
            }

            var c = hand.get(cardPos);
            hand.remove(cardPos);
            hand.add(targetPos, c);
        }
    }

    @Override
    public void roundOverHook(CardDeck hand) {
        System.out.println("Your hand is now:");
        PrettyDeckPrint.pprint(hand);
    }
}
