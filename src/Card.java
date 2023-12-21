import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;

public class Card implements Serializable {
    private final String suit;
    private final String rank;
    private boolean faceDown;
    private boolean isJoker;
    private int value;
    public final static String[] suits = {"clubs", "diamonds", "hearts", "spades"};
    public final static String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

    public static HashMap<String,Integer> cardRanks=new HashMap<>(){{
        put("2",2);
        put("3",3);
        put("4",4);
        put("5",5);
        put("6",6);
        put("7",7);
        put("8",8);
        put("9",9);
        put("10",10);
        put("Jack",11);
        put("Queen",12);
        put("King",13);
        put("Ace",14);
    }};


    public boolean isJoker() {
        return isJoker;
    }

    public void setJoker(boolean joker) {
        isJoker = joker;
    }

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        this.value=cardRanks.get(rank);
        this.isJoker=false;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public void setFaceDown(boolean b) {
        this.faceDown = b;
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    @Override
    public String toString() {
        return suit.substring(0,1).toUpperCase() + "" + (rank.length() > 2 ? rank.substring(0,1) : rank);
    }

    private void setValue(int value) {
        this.value = value;
    }
    public void increaseValue() {
        setValue(value+13);
    }
    public int getValue() {
       return value;
    }
}
class CardComparator implements Comparator<Card>{
    @Override
    public int compare(Card card1, Card card2) {
        // First, compare suits
        int suitComparison = card1.getSuit().compareTo(card2.getSuit());

        // If suits are the same, compare ranks
        if (suitComparison == 0) {
            return Integer.compare(card1.getValue(), card2.getValue());
        }

        return suitComparison;
    }
}