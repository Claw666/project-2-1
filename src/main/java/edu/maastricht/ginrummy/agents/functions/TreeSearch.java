package edu.maastricht.ginrummy.agents.functions;

import edu.maastricht.ginrummy.UI.Event.EventHandler;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.agents.RandomAgent;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.cards.DeckGeneration;
import edu.maastricht.ginrummy.cards.GameDeckState;
import edu.maastricht.ginrummy.game.Player;

import edu.maastricht.ginrummy.agents.IAgent.CardPickD;
import edu.maastricht.ginrummy.game.Round;
import edu.maastricht.ginrummy.game.Score;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

public class TreeSearch {
    public static class Decision {
        private final IAgent.CardPickD whatToPick;
        private final int whatToDiscard;
        private final int score;

        public Decision(CardPickD whatToPick, int whatToDiscard, int score) {
            this.whatToPick = whatToPick;
            this.whatToDiscard = whatToDiscard;
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public CardPickD getWhatToPick() {
            return whatToPick;
        }

        public int getWhatToDiscard() {
            return whatToDiscard;
        }
    }

    public static Decision miniMaxGetBestAction(GameDeckState state, Player playerInTurn, int maxDepth, Evaluation evaluation) {
        GameDeckState cState = state.clone();
        Decision d = null;
        int bestDeadwoodScore = playerInTurn == Player.P2 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (var wtp: IAgent.CardPickD.values()) {
            for (int wtd = 0; wtd < 10; wtd++) {
                transmuteGameState(cState, cState.getPlayerDeck(playerInTurn), wtp, wtd);
                var score = miniMaxSearch(cState, playerInTurn.getNextPlayer(), maxDepth - 1, evaluation);
                switch (playerInTurn){
                    case P1:
                        if (score > bestDeadwoodScore) {
                            bestDeadwoodScore = score;
                            d = new Decision(wtp, wtd, score);
                        }
                        break;
                    case P2:
                        if (score < bestDeadwoodScore) {
                            bestDeadwoodScore = score;
                            d = new Decision(wtp, wtd, score);
                        }
                        break;
                    default:
                        throw new RuntimeException("Cant use None as player");
                }
            }
        }
        return d;
    }

    public static int miniMaxSearch(GameDeckState state, Player playerInTurn, int maxDepth, Evaluation evaluation)
    {
        var cState = state.clone();
        var playerHand = cState.getPlayerDeck(playerInTurn);

        if (maxDepth <= 0){
            return getScore(state, evaluation);
        }

        int bestDeadwoodScore = playerInTurn == Player.P2 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (var wtp: IAgent.CardPickD.values()) {
            for (int wtd = 0; wtd < 10; wtd++) {
                var transmuteFailed = !transmuteGameState(cState, playerHand, wtp, wtd);
                switch (playerInTurn){
                    case P1:
                        if (transmuteFailed){
                            bestDeadwoodScore = Math.max(getScore(state, evaluation), bestDeadwoodScore);
                        }
                        else {
                            bestDeadwoodScore = Math.max(miniMaxSearch(
                                    cState, playerInTurn.getNextPlayer(), maxDepth - 1, evaluation), bestDeadwoodScore);
                        }
                        break;
                    case P2:
                        if (transmuteFailed){
                            bestDeadwoodScore = Math.min(getScore(state, evaluation), bestDeadwoodScore);
                        }
                        else {
                            bestDeadwoodScore = Math.min(miniMaxSearch(
                                    cState, playerInTurn.getNextPlayer(), maxDepth - 1, evaluation), bestDeadwoodScore);
                        }
                        break;
                    default:
                        throw new RuntimeException("Cant use None as player");
                }

            }
        }
        return bestDeadwoodScore;
    }

    private static int getScore(GameDeckState state, Evaluation evaluation) {
        int dwP1 = 0;
        int dwP2 = 0;
        switch (evaluation)
        {
            case DEADWOOD:
            {
                dwP1 = AdvancedMeldDetector.find(state.getDeckPlayer1()).deadwoodValue();
                dwP2 = AdvancedMeldDetector.find(state.getDeckPlayer2()).deadwoodValue();
                break;
            }
            case PROBABILITY:
            {
                for(int i = 0; i < 400; i++)
                {
                    IAgent aAgent = new RandomAgent();
                    IAgent bAgent = new RandomAgent();
                    Round round = new Round(new Score(1, 0), new EventHandler(), aAgent, bAgent);

                    round.playFromGameDeckState(state.clone(), Player.P1, 6, true);
                    dwP1 += AdvancedMeldDetector.find(aAgent.getCardDeck()).deadwoodValue();
                    dwP2 += AdvancedMeldDetector.find(bAgent.getCardDeck()).deadwoodValue();
                }
            } break;

            default: throw new IllegalStateException();

        }

        return -(dwP2 - dwP1);


    }

    private static boolean transmuteGameState(GameDeckState cState, CardDeck playerHand, CardPickD wtp, int wtd) {
        switch (wtp){
            case DISCARD:
                final Card card = cState.getDiscard().draw();
                playerHand.addLast(card);
                break;
            case STACK:
                if (cState.getStock().size() == 0){
                    return false;
                }
                final Card card2 = cState.getStock().draw();
                playerHand.addLast(card2);
                break;
            default:
                throw new IllegalStateException();
        }
        var discardedCard = playerHand.remove(wtd);
        cState.getDiscard().addLast(discardedCard);
        return true;
    }

    public static enum Evaluation
    {
        DEADWOOD,
        PROBABILITY
    }

}
