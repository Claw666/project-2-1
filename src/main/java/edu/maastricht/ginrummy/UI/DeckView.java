package edu.maastricht.ginrummy.UI;

import edu.maastricht.ginrummy.cards.CardImages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class DeckView extends VBox {
    private static final String BUTTON_STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;";
    private static final String BUTTON_STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 6 4 4 6;";
    final Button button = new Button();


    public DeckView() {
        setAlignment(Pos.TOP_CENTER);
        ImageView deckBackground = new ImageView(CardImages.getBack());
        deckBackground.setFitHeight(180);
        deckBackground.setPreserveRatio(true);
        button.setGraphic(deckBackground);
        button.setStyle(BUTTON_STYLE_NORMAL);
        button.setOnMousePressed(e ->{
                ((Button)e.getSource()).setStyle(BUTTON_STYLE_PRESSED);
            }
        );
        button.setOnMouseReleased(e -> {
                ((Button)e.getSource()).setStyle(BUTTON_STYLE_NORMAL);


            }
        );
        Label stockLabel = new Label("STOCK"); //Add game mode label
        stockLabel.setStyle("-fx-font-size: 2em;");
        stockLabel.setPadding(new Insets(10,0,0,0));
        getChildren().addAll(button, stockLabel);
    }

}
