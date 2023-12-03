import java.util.ArrayList;
import java.util.Scanner;

public class Player {
    private final String name;
    private ArrayList<Card> hand;
    private Order order;
    private boolean isTurn;
    private int winCount;

    private Scanner input = new Scanner(System.in);

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order newOrder) {
        order = newOrder;
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
        isTurn = false;
        winCount = 0;

    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void displayHand() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).isJoker()) {
                System.out.println((i + 1) + ".**card is " + hand.get(i));
                System.out.println(hand.get(i).getValue());
            } else {
                System.out.println((i + 1) + ".card is " + hand.get(i));
                System.out.println(hand.get(i).getValue());
            }
        }
    }

    public Card throwCard() {
        System.out.println("SELECT THE CARD THAT YOU WANT TO THROW ON THE BOARD: ");
        displayHand();
        int choice = input.nextInt();
        return hand.remove(choice - 1);
    }

    public String getName() {
        return name;
    }

    public boolean isTurn() {
        return isTurn;
    }
}
