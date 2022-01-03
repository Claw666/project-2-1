package edu.maastricht.ginrummy.game;

import edu.maastricht.ginrummy.UI.Event.Event;
import edu.maastricht.ginrummy.UI.Event.EventHandler;
import edu.maastricht.ginrummy.UI.Event.Events.PlayerPickedEvent;
import edu.maastricht.ginrummy.UI.Event.Events.PlayerSwitchEvent;
import edu.maastricht.ginrummy.UI.Event.Events.RoundStartEvent;
import edu.maastricht.ginrummy.agents.AskHumanGUIAgent;
import edu.maastricht.ginrummy.agents.IAgent;
import edu.maastricht.ginrummy.agents.IHumanAgent;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.cards.GameDeckState;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;
import edu.maastricht.ginrummy.melding.advanced.MergeResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class Round  {

    private static Logger logger = LogManager.getLogger(Round.class);

    private final EventHandler eventHandler;

    private CardDeck stock = new CardDeck(CardDeck.DeckType.FRENCH, true);
    private CardDeck discard = new CardDeck(CardDeck.DeckType.EMPTY, false);

    private IAgent currentPlayer;
    private IAgent opponentPlayer;

    private IAgent player1;
    private IAgent player2;

    private Card lastPickedCard = null;

    private final boolean player1Human;
    private final boolean player2Human;
    private final boolean humanVsHuman;

    private int roundCounter = 0;
    private Score globalScore;

    public Round(Score globalScore, EventHandler eventHandler, IAgent player1, IAgent player2)
    {
        this.eventHandler = eventHandler;
        this.globalScore = globalScore;

        this.player1Human = player1 instanceof IHumanAgent;
        this.player2Human = player2 instanceof IHumanAgent;
        this.humanVsHuman = (this.player1Human && this.player2Human);

        this.currentPlayer = this.player1 = player1;
        this.opponentPlayer = this.player2 = player2;

    }

    public CardDeck getStock() {
        return stock;
    }

    public IAgent getPlayer1() {
        return player1;
    }

    public IAgent getPlayer2() {
        return player2;
    }

    public IAgent getCurrentPlayer() {
        return currentPlayer;
    }

    public IAgent getOpponentPlayer() {
        return opponentPlayer;
    }

    public CardDeck getDiscardDeck() {
        return discard;
    }

    public int getRoundCounter() {
        return roundCounter;
    }

    public Card getLastPickedCard() {
        return lastPickedCard;
    }

    public Score play()
    {
        var stock = new CardDeck(CardDeck.DeckType.FRENCH, true);
        var p1CardDeck = new CardDeck();
        p1CardDeck.addAll(stock.draw(10));
        var p2CardDeck = new CardDeck();
        p2CardDeck.addAll(stock.draw(10));
        var discard = new CardDeck();
        discard.add(stock.draw());

        return playFromGameState(p1CardDeck, p2CardDeck, stock, discard, Player.P1, true, -1, false);
    }

    public Score playFromGameDeckState(GameDeckState state, Player playerInTurn, final int turnLimit, boolean skipInitialPick)
    {
        return playFromGameState(state.getDeckPlayer1(), state.getDeckPlayer2(), state.getStock(),
                state.getDiscard(), playerInTurn, false, turnLimit, skipInitialPick);
    }

    public Score playFromGameState(CardDeck p1CardDeck, CardDeck p2CardDeck, CardDeck argStock, CardDeck argDiscard,
                                   Player playerInTurn, boolean askToSkip, final int turnLimit, boolean skipInitialPick)
    {
        skipInitialPick = false;
        askToSkip = false;
        player1.getCardDeck().clear();
        player2.getCardDeck().clear();
        this.stock = argStock;
        this.discard = argDiscard;
        player1.getCardDeck().addAll(p1CardDeck);
        player2.getCardDeck().addAll(p2CardDeck);
        if(this.discard.isEmpty())
        {
            this.discard.add(stock.draw());
        }

        Score score = new Score(0, 0);
        trigger(new RoundStartEvent(this));

        while (true) {

            boolean skipPick = false;
            if(roundCounter == 0 && askToSkip)
            {
                skipPick = currentPlayer.doYouSkipPick();
            }

            if(!skipPick)
            {
                if(!skipInitialPick)
                {
                    //--- Draw Card
                    IAgent.CardPickD pick = currentPlayer.whatToPick(currentPlayer.getCardDeck(), this.discard);
                    if(pick == IAgent.CardPickD.DISCARD)
                    {
                        final Card card = this.discard.draw();
                        lastPickedCard = card;
                        currentPlayer.getCardDeck().add(card);
                        trigger(new PlayerPickedEvent.Discard(this.discard, null, Collections.singletonList(card)));
                    } else if(pick == IAgent.CardPickD.STACK) {
                        final Card card = this.stock.draw();
                        lastPickedCard = card;
                        currentPlayer.getCardDeck().add(card);
                        trigger(new PlayerPickedEvent.Stock(this.stock, null, Collections.singletonList(card)));
                    }
                    else
                    {
                        throw new IllegalStateException();
                    }
                }

                assert currentPlayer.getCardDeck().size() == 10 || currentPlayer.getCardDeck().size() == 11;

                //--- Big Gin Opportunity
                if(AdvancedMeldDetector.find(currentPlayer.getCardDeck()).getDeadwood().isEmpty())
                {
                    if(currentPlayer.doYouKnock(currentPlayer.getCardDeckClone(), this.discard, true))
                    {
                        knocking(score);
                        //
                        Score tmp = new Score(globalScore.getScoreP1(), globalScore.getScoreP2());
                        tmp.add(score);
                        switchPlayers(tmp);
                        if(turnLimit > 0 && roundCounter >= turnLimit)
                        {
                            return score;
                        }
                        break;
                    }
                }

                //--- Discard a card
                {
                    Card discardedCard;
                    do {

                        discardedCard = currentPlayer.getCardDeck().get(currentPlayer.whatToDiscard(currentPlayer.getCardDeckClone(), discard, lastPickedCard));

                        /*if(currentPlayer == player1 && !player1Human)
                        {
                            break;
                        }
                        else if(currentPlayer == player2 && !player2Human)
                        {
                            break;
                        }*/
                    } while (discardedCard == lastPickedCard);
                    currentPlayer.getCardDeck().remove(discardedCard);
                    discard.addLast(discardedCard);
                    trigger(new PlayerPickedEvent.Discard(discard, Collections.singletonList(discardedCard), null));
                }

                assert currentPlayer.getCardDeck().size() == 10;

                //--- Cancelling the hands if there are less than 2 cards
                if(stock.size() <= 2)
                {
                    switchPlayers(globalScore);
                    if(turnLimit > 0 && roundCounter >= turnLimit)
                    {
                        return score;
                    }
                    refillStockIf(2);
                    break;
                }

            }
            skipInitialPick = false;

            //--- Knock
            final MeldGrouping meldGrouping = AdvancedMeldDetector.find(currentPlayer.getCardDeck());
            if(meldGrouping.deadwoodValue() <= GameSettings.minDeadWoodToKnock)
            {
                if(currentPlayer.doYouKnock(currentPlayer.getCardDeck(), discard, meldGrouping.getDeadwood().isEmpty()))
                {
                    knocking(score);
                    assert currentPlayer.getCardDeck().size() < 12;

                    //
                    Score tmp = new Score(globalScore.getScoreP1(), globalScore.getScoreP2());
                    tmp.add(score);
                    switchPlayers(tmp);
                    if(turnLimit > 0 && roundCounter >= turnLimit)
                    {
                        return score;
                    }
                    break;
                }
            }

            if (currentPlayer instanceof IHumanAgent) {
                IHumanAgent p = (IHumanAgent) currentPlayer;
                CardDeck reorderDeck = p.howToReorderDeck(currentPlayer.getCardDeckClone()); //TODO @Johannes why is this not in-place?
                p.roundOverHook(currentPlayer.getCardDeckClone());
            }

            switchPlayers(globalScore);
            if(turnLimit > 0 && roundCounter >= turnLimit)
            {
                return score;
            }

            logger.debug(score);
            if (humanVsHuman) {
                System.out.printf("Change to player <TODO:name> in %s seconds.", GameSettings.humanVsHumanPlayerSwitchDelay);
                try {
                    TimeUnit.SECONDS.sleep(GameSettings.humanVsHumanPlayerSwitchDelay);
                } catch (InterruptedException ex){
                    logger.trace(ex);
                }
                System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                //System.out.print("\033[H\033[2J");
                //System.out.flush();
            }
            logger.debug("Changing to player {}", currentPlayer);
        }

        return score;
    }

    private void switchPlayers(Score score)
    {
        lastPickedCard = null;
        //--- player switch
        if(humanVsHuman)
        {
            ((AskHumanGUIAgent) currentPlayer).waitForPlayersToSwitch();
        }

        IAgent tmp = currentPlayer;
        currentPlayer = opponentPlayer;
        opponentPlayer = tmp;
        trigger(new PlayerSwitchEvent(this, score, currentPlayer, opponentPlayer));
        roundCounter++;
    }

    private void refillStockIf(int cards)
    {
        if(stock.size() <= cards){
            lastPickedCard = null;

            //decks = false;
            logger.debug("Refilling stock deck. Limit is {}. Stock size is {}", cards, stock.size());
            Card last = this.discard.draw();

            this.stock.addAll(this.discard);
            this.stock.shuffle();

            this.discard.clear();
            this.discard.add(last);
        }

    }

    private void knocking(Score score)
    {
        boolean isGin = isGin(currentPlayer.getCardDeck());

        if (!isGin)
        {
            MergeResult mergeResult = opponentPlayer.doLayoff(currentPlayer.getCardDeck());
            if(mergeResult != null)
            {
                opponentPlayer.getCardDeck().removeAll(mergeResult.getMergingCards());
                for(CardDeck run : mergeResult.getMeldGrouping().getRuns())
                {
                    currentPlayer.getCardDeck().removeAll(run);
                    this.discard.addAll(run);
                }
                for(CardDeck set : mergeResult.getMeldGrouping().getSets())
                {
                    this.discard.addAll(set);
                    currentPlayer.getCardDeck().removeAll(set);
                }

                if(isUndercut(currentPlayer.getCardDeck(), opponentPlayer.getCardDeck()))
                {
                    if (currentPlayer == player1)
                    {
                        score.add(new Score(0, GameSettings.undercutBonusPoints));
                    }
                    else
                    {
                        score.add(new Score(GameSettings.undercutBonusPoints, 0));
                    }
                }
            }
        }

        int scoreCurrentPlayer = getScore(currentPlayer.getCardDeck(), opponentPlayer.getCardDeck());
        if(currentPlayer == player1)
        {
            if(scoreCurrentPlayer > 0)
            {
                score.add(new Score(scoreCurrentPlayer, 0));
            }
            else
            {
                score.add(new Score(0, -scoreCurrentPlayer));
            }
        }
        else
        {
            if(scoreCurrentPlayer > 0)
            {
                score.add(new Score(0, scoreCurrentPlayer));
            }
            else
            {
                score.add(new Score(-scoreCurrentPlayer, 0));
            }
        }

        if(isGin)
        {
            discard.addAll(currentPlayer.getCardDeck());
            currentPlayer.getCardDeck().clear();
        }

    }

    private void trigger(Event event)
    {
        this.eventHandler.trigger(event);
    }

    private boolean isGin(CardDeck deck)
    {
        return AdvancedMeldDetector.find(deck).deadwoodValue() == 0;
    }

    private boolean isUndercut(CardDeck myDeck, CardDeck opponentDeck)
    {
        return AdvancedMeldDetector.find(myDeck).deadwoodValue() > AdvancedMeldDetector.find(opponentDeck).deadwoodValue();
    }

    public int getScore(CardDeck myDeck, CardDeck opponentDeck)
    {
        return AdvancedMeldDetector.find(opponentDeck).deadwoodValue() - AdvancedMeldDetector.find(myDeck).deadwoodValue();
    }


}
