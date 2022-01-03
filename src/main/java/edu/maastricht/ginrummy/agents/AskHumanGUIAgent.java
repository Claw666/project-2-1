package edu.maastricht.ginrummy.agents;

import edu.maastricht.ginrummy.UI.GameUI;
import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.game.GameGUI;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;
import edu.maastricht.ginrummy.melding.advanced.Callback;

public class AskHumanGUIAgent extends IHumanAgent {

    private final Thread thread;
    private final GameGUI gameGUI;

    public AskHumanGUIAgent(GameGUI gameGUI, Thread thread)
    {
        this.thread = thread;
        this.gameGUI = gameGUI;
    }

    public Thread getThread() {
        return thread;
    }

    public void wakeup(Callback<?> pre)
    {
        synchronized (thread)
        {
            pre.apply(null);
            thread.notify();
            this.gameGUI.getStartScreen().getGameUI().setGameState(GameUI.GameState.IDLE);
        }

    }

    public void wait(GameUI.GameState gameState)
    {
        this.gameGUI.getStartScreen().getGameUI().setGameState(gameState);
        try
        {
            synchronized (this.thread)
            {
                this.thread.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CardDeck howToReorderDeck(CardDeck hand) {
        //wait(Game.GameState.REORDER_DECK);
        synchronized (hand)
        {
            MeldGrouping meldGrouping = AdvancedMeldDetector.find(hand);
            if(meldGrouping.getDeadwood().size() != hand.size())
            {
                hand.clear();
                hand.addAll(meldGrouping.getDeadwood());
                meldGrouping.getRuns().forEach(hand::addAll);
                meldGrouping.getSets().forEach(hand::addAll);
            }
        }
        return hand;
    }

    @Override
    public void roundOverHook(CardDeck hand) {

    }

    @Override
    public CardPickD whatToPick(CardDeck hand, CardDeck discard)
    {
        wait(GameUI.GameState.PICK_FROM_DISCARD_OR_STOCK);
        return this.gameGUI.getStartScreen().getGameUI().getCardPickD();

    }

    @Override
    public int whatToDiscard(CardDeck hand, CardDeck discard, Card lastDiscardedCard) {
        wait(GameUI.GameState.WHAT_TO_DISCARD);
        return this.gameGUI.getStartScreen().getGameUI().getDiscardedCard();
    }

    @Override
    public boolean doYouKnock(CardDeck hand, CardDeck discard, boolean isBigGinBECAREFULLLLL) {
        wait(GameUI.GameState.DO_YOU_KNOCK);
        return this.gameGUI.getStartScreen().getGameUI().isKnocking();
    }

    @Override
    public boolean doYouSkipPick() {
        wait(GameUI.GameState.DO_YOU_SKIP);
        return this.gameGUI.getStartScreen().getGameUI().isSkipping();
    }

    public void waitForPlayersToSwitch()
    {
        System.out.println(this);
        System.out.println("a");
        wait(GameUI.GameState.WAIT_FOR_PLAYER_SWITCH);
        System.out.println("b");
    }

}
