import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    private ArrayList<Card> onTable = new ArrayList<>();

    private HashMap<String, Card> onBoard = new HashMap<>();

    private ArrayList<Player> players;

    private String joker;



    /*
     private Card chooseCardToThrow(Card card1,Player player) {
        boolean hasSameSuit = false;
        boolean noHigher = true;
        Card thrown = card1;
        Card firstCard = onBoard.entrySet().iterator().next().getValue();
        for (Card card : onBoard.values()) {
            // aynı takımdan kart atıldı
            if (thrown.getSuit().equals(card.getSuit())) {
                // Attığı daha düşükse:
                if (thrown.getValue() < card.getValue()) {
                    // Ele bakılıyor.
                    for (Card playerCard : player.getHand()) {
                        // Elde aynı takımdan başka kart var mı bak
                        if (playerCard.getSuit().equals(thrown.getSuit())) {
                            // Var,varsa daha büyüğünü attır.
                            if (playerCard.getValue() > card.getValue() && playerCard.getValue() > firstCard.getValue()) {
                                thrown = playerCard;
                                break;
                            } else if ((playerCard.getValue() > card.getValue() && playerCard.getValue() < firstCard.getValue())) {
                                thrown = smallestCard(player, card.getSuit());
                            }
                        }
                    }
                }
            }
            // aynı takımdan kart atılmadı.
            if (!thrown.getSuit().equals(card.getSuit())) {
                // Ele bakılıyor.
                for (Card playerCard : player.getHand()) {
                    // Elinde masadaki takımdan var ama onu atmamışsa:
                    if (playerCard.getSuit().equals(card.getSuit())) {
                        hasSameSuit = true;
                        // Daha büyüğü varsa oynat.
                        if (playerCard.getValue() > card.getValue()) {
                            thrown = playerCard;
                            noHigher = false;
                            break;
                        }
                    }
                    //Daha büyüğü oynanmamışsa,
                    if (noHigher) {
                        thrown = smallestCard(player, card.getSuit());
                    }
                    // Elinde aynı takımdan kart yok,joker varsa joker atmalı
                    if (!hasSameSuit) {
                        // Joker var oyna
                        if (player.hasJoker()) {
                            thrown = smallestCard(player, joker);
                            isJokerPlayed = true;
                        }
                        // Joker yok attığı kartı oynasın.
                    }
                }
            }
        }
        System.out.println(thrown);
        return thrown;
    }

    public String advancedThrowCard(Card card,Player player) {
        Card thrown=card;
        if (player.isTurn()){
            if (thrown.isJoker() && !isJokerPlayed){
                do {
                    System.out.println("No joker cards has been played and you are trying to start with a joker.Please choose a different card.");
                } while (thrown.isJoker());
                System.out.println(thrown);
            }
        }
        else {
            thrown=chooseCardToThrow(thrown,player);
        }
        return thrown.toString();
    }


   private Card smallestCard(Player player, String suit) {
        Card temp = null;
        for (Card card : player.getHand()) {
            if (card.getSuit().equals(suit)) {
                temp = card;
                break;
            }
        }
        return temp;
    }

    public void displayPlayersAndHands() {
        for (Player player : players) {
            System.out.println("--------------------------------------");
            System.out.println(player.getName());
            player.displayHand();
            System.out.println("--------------------------------------");
        }
    }
     */
}