package edu.maastricht.ginrummy.agents.heuristic;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardTracker {

    private Map<Long, Status> statuses = new HashMap<>();

    public CardTracker()
    {
    }

    public void put(Card card, Status status)
    {
        this.statuses.put(card.getByteIndex(), status);
    }

    public void put(List<Card> cards, Status status)
    {
        cards.forEach(card -> this.statuses.put(card.getByteIndex(), status));
    }

    public List<Card> get(Status status)
    {
        return this.statuses.entrySet().stream()
                    .filter(e -> e.getValue() == status)
                    .map(e -> Card.fromSingleByteIndex(e.getKey(), AdvancedMeldDetector.Combination.Type.RUN)).
                    collect(Collectors.toList());
    }


    public Status get(Card card)
    {
        return this.statuses.getOrDefault(card.getByteIndex(), Status.UNKNOWN);
    }

    public void reset() {
        this.put(new CardDeck(CardDeck.DeckType.FRENCH, false), Status.IN_STOCK);
    }

    public enum Status {

        IN_OWN_HAND,
        IN_ENEMY_HAND,
        IN_DISCARD,
        IN_STOCK,
        UNKNOWN,

    }

}
