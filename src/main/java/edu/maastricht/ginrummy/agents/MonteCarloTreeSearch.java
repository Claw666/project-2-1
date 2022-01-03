package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.agents.functions.TreeSearch;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.cards.DeckGeneration;
import edu.maastricht.ginrummy.game.Player;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.ArrayList;
import java.util.List;

public class MonteCarloTreeSearch extends IAgent {
    private TreeSearch.Decision decision;
    private final int maxTreeSearchDepth;

    public MonteCarloTreeSearch() {
        this.maxTreeSearchDepth = 3;
    }

    public MonteCarloTreeSearch(int maxTreeSearchDepth) {
        this.maxTreeSearchDepth = maxTreeSearchDepth;
    }

    private int startDeadwood = -1;

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard) {
        System.out.println(AdvancedMeldDetector.find(hand).deadwoodValue());

        TreeSearch.Decision max = null;

        for(int i = 0; i < 100; i++)
        {
            TreeSearch.Decision tmp = TreeSearch.miniMaxGetBestAction(
                    DeckGeneration.completeStateRandomly(hand,discard), Player.P1, maxTreeSearchDepth, TreeSearch.Evaluation.DEADWOOD);
            if(max == null || max.getScore() < tmp.getScore())
            {
                max = tmp;
            }

        }
        decision = max;
        return decision.getWhatToPick();
    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        return decision.getWhatToDiscard();
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL) {
        return true;
    }

    @Override
    public boolean doYouSkipPick() {
        return false;
    }
}
