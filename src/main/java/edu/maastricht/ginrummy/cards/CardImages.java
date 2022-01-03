package edu.maastricht.ginrummy.cards;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public final class CardImages {

    private static final String IMAGE_LOCATION = "images/";
    private static final String IMAGE_SUFFIX = ".png";
    private static final String[] SUIT_CODES = {"C", "D", "H", "S"};
    private static final String[] RANK_CODES = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "A", "J", "Q", "K"};


    private static Map<String, Image> aCards = new HashMap<String, Image>();

    private CardImages() {
    }

    /**
     * Return the image of a card.
     *
     * @param pCard the target card
     * @return An icon representing the chosen card.
     */
    public static Image getCard(Card pCard) {
        assert pCard != null;
        return getCard(getCode(pCard));
    }

    /**
     * Return an image of the back of a card.
     *
     * @return An icon representing the back of a card.
     */
    public static Image getBack() {
        return getCard("back1");
    }

    private static String getCode(Card pCard) {
        return SUIT_CODES[pCard.getSuit().ordinal()] + RANK_CODES[pCard.getRank().ordinal()];
    }

    private static Image getCard(String pCode) {
        Image image = aCards.get(pCode);
        if (image == null) {
            image = new Image(CardImages.class.getClassLoader().getResourceAsStream(IMAGE_LOCATION + pCode + IMAGE_SUFFIX));
            aCards.put(pCode, image);
        }
        return image;
    }
}