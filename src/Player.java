import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private final String name;
    private final ArrayList<Card> hand;
    private final Order order;
    private boolean isTurn;
    private int winCount;


    public Order getOrder() {
        return order;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
        }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }



    public enum Order {
        Middle(3), Eldest(5), Dealer(8);

        private final int value;

        Order(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public Player(String name, Order order) {
        this.name = name;
        hand = new ArrayList<>();
        this.order = order;
        isTurn = order.value == 8 ? true: false;
        winCount = 0;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void displayHand() {
        hand.sort(new CardComparator());
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).isJoker()) {
                System.out.println((i + 1) + ".**card is " + hand.get(i));
            } else {
                System.out.println((i + 1) + ".card is " + hand.get(i));
            }
        }
    }

    public Card throwCard() {
        System.out.println("SELECT THE CARD THAT YOU WANT TO THROW ON THE BOARD: ");
        displayHand();
        int choice = 5;
        return hand.get(choice-1);
    }
    public boolean hasJoker(){
        boolean hasJoker=false;
        for (Card card:hand){
            if (card.isJoker()) {
                hasJoker = true;
                break;
            }
        }
        return hasJoker;
    }



    public String getName() {
        return name;
    }

    public boolean isTurn() {
        return isTurn;
    }
}


