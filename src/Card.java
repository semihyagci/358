import java.util.HashMap;

public class Card {
    private final String suit;
    private final String rank;
    private boolean faceDown;
    private boolean isJoker;
    private String pngname;
    private int value;
    public final static String[] suits = {"clubs", "diamonds", "hearts", "spades"};
    public final static String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

    public static HashMap<String,Integer> cardRanks=new HashMap<>();


    public boolean isJoker() {
        return isJoker;
    }

    public void setJoker(boolean joker) {
        isJoker = joker;


    }
    public void insertIntoMap(){
        cardRanks.put("2",2);
        cardRanks.put("3",3);
        cardRanks.put("4",4);
        cardRanks.put("5",5);
        cardRanks.put("6",6);
        cardRanks.put("7",7);
        cardRanks.put("8",8);
        cardRanks.put("9",9);
        cardRanks.put("10",10);
        cardRanks.put("Jack",11);
        cardRanks.put("Queen",12);
        cardRanks.put("King",13);
        cardRanks.put("Ace",14);
    }

    public Card(String suit, String rank) {
        insertIntoMap();
        this.suit = suit;
        this.rank = rank;
        value=cardRanks.get(rank);
        pngname="";
        isJoker=false;
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
        return rank + " of " + suit;
    }


    public String getPngname() {
        return pngname;
    }
    public void setPngname(String newPng) {
        pngname=newPng;
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
