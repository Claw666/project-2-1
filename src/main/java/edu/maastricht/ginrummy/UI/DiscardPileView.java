package edu.maastricht.ginrummy.UI;

import edu.maastricht.ginrummy.UI.Event.EventListener;
import edu.maastricht.ginrummy.UI.Event.Events.PlayerPickedEvent;
import edu.maastricht.ginrummy.UI.Event.Subscribe;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.cards.CardImages;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class DiscardPileView extends VBox implements EventListener {

    private MouseGestures mg = new MouseGestures();
    private ImageView discardPile = new ImageView();

    public DiscardPileView(GameUI gameUI) {
        gameUI.getGameGUI().getEventHandler().registerListener(this);

        setAlignment(Pos.TOP_CENTER);
        //mg.makeDraggable(discardPile);

        updateCards(gameUI.getGameGUI().getRound().getDiscardDeck());
        discardPile.setFitHeight(180);
        discardPile.setPreserveRatio(true);

        Label discardLabel = new Label("DISCARDED"); //Add game mode label
        discardLabel.setStyle("-fx-font-size: 2em;");
        discardLabel.setPadding(new Insets(10,0,0,0));

        getChildren().addAll(discardPile,discardLabel);
    }

    @Subscribe
    public void onDiscardPileUpdate(PlayerPickedEvent.Discard event)
    {
        updateCards(event.getPile());
    }

    public void updateCards(CardDeck discardPile)
    {
        Platform.runLater(() -> {
            if(!discardPile.isEmpty())
            {
                this.discardPile.setImage(CardImages.getCard(discardPile.getLast()));
            }
            else
            {
                this.discardPile.setImage(null); // TODO empty image
            }
        });
    }
}
