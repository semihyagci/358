import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UtilityService {
    public static ArrayList<Card> createStandardDeck(boolean shuffle) {
        ArrayList<Card> cards = new ArrayList<>();
        for (String suit : Card.suits) {
            for (String rank : Card.ranks) {
                Card card = new Card(suit, rank);
                cards.add(card);
            }
        }
        if (shuffle) {
            Collections.shuffle(cards);
        }
        return cards;
    }

    public static String findKeyWithMaxValue(HashMap<String, Card> hashMap) {
        String maxKey = null;
        int maxValue = Integer.MIN_VALUE;

        for (Map.Entry<String, Card> entry : hashMap.entrySet()) {
            int currentValue = entry.getValue().getValue();
            if (currentValue > maxValue) {
                maxValue = currentValue;
                maxKey = entry.getKey();
            }
        }
        return maxKey;
    }

    public static Card createCard(String cardName) {
        String suit;
        String rank;
        if (cardName.length() == 3) {
            suit = cardName.substring(0, 1);
            rank = cardName.substring(1, 3);
        } else {
            suit = cardName.substring(0, 1);
            rank = cardName.substring(1, 2);
        }
        switch (rank) {
            case "J" -> rank = "Jack";
            case "A" -> rank = "Ace";
            case "K" -> rank = "King";
            case "Q" -> rank = "Queen";
        }
        switch (suit) {
            case "C" -> suit = "clubs";
            case "D" -> suit = "diamonds";
            case "H" -> suit = "hearts";
            case "S" -> suit = "spades";
        }
        return new Card(suit, rank);
    }
}
