import java.util.*;

public class Game {
    private final ArrayList<Card> onTable = new ArrayList<>();

    private final HashMap<String, Card> onBoard = new HashMap<>();

    private final ArrayList<Player> players;

    private boolean isJokerPlayed = false;

    private String joker;

    public Game(ArrayList<Player> playersList) {
        players = playersList;
    }

    public void prepareGameState(){
        ArrayList<Card> game_deck = createStandardDeck(true);
        for (int i = 0; i < 4; i++) {
            Card card = game_deck.get((int) (Math.random() * game_deck.size()));
            game_deck.remove(card);
            onTable.add(card);
        }

        while (!game_deck.isEmpty()) {
            for (Player player : players) {
                if (!game_deck.isEmpty()) {
                    player.getHand().add(game_deck.get(0));
                    game_deck.remove(0);
                }
            }
        }

    }

    public void prepareGame2(String jokerChoice, ArrayList<Card> throwedCardList) {
        for (Player player : players) {
            if (player.getOrder().getValue() == 8) {
                player.setTurn(true);
                System.out.println("Please choose joker from your cards.");
                player.displayHand();
                joker = jokerChoice;
                switch (joker) {
                    case "clubs" -> setJoker(Card.suits[0]);
                    case "diamonds" -> setJoker(Card.suits[1]);
                    case "hearts" -> setJoker(Card.suits[2]);
                    case "spades" -> setJoker(Card.suits[3]);
                    default -> System.out.println("Wrong input");
                }
                System.out.println("Please remove 4 undesired card from your deck.");
                player.displayHand();

                for (Card throwedCard : throwedCardList){
                for (int i=0; i<player.getHand().size();i++){
                    if ((throwedCard.getRank()+throwedCard.getValue()).equals(player.getHand().get(i).getRank()+player.getHand().get(i).getValue())){
                        Card card = player.getHand().remove(i);
                        onTable.add(card);
                    }
                }
                }

                for (int i = 0; i < 4; i++) {
                    player.getHand().add(onTable.get(i));
                }
                System.out.println(onTable + " has been added to your deck.");
            }
        }
        displayPlayersAndHands();
        play();
    }

    public void displayPlayersAndHands() {
        for (Player player : players) {
            System.out.println("--------------------------------------");
            System.out.println(player.getName());
            player.displayHand();
            System.out.println("--------------------------------------");
        }
    }

    public void setJoker(String suit) {
        for (Player player : players) {
            for (int i = 0; i < player.getHand().size(); i++) {
                Card card = player.getHand().get(i);
                if (card.getSuit().equals(suit)) {
                    card.setJoker(true);
                    card.increaseValue();
                }
            }
        }
        for (Card card : onTable) {
            if (card.getSuit().equals(suit)) {
                card.setJoker(true);
                card.increaseValue();
            }
        }
    }

    public static ArrayList<Card> createStandardDeck(boolean shuffle) {
        ArrayList<Card> cards = new ArrayList<>();
        for (String suit : Card.suits) {
            for (String rank : Card.ranks) {
                Card card = new Card(suit, rank);
                card.setFaceDown(true);
                cards.add(card);
            }
        }
        if (shuffle) {
            Collections.shuffle(cards);
        }
        return cards;
    }

    public void play() {
        for (int i = 0; i < 16; i++) {
            for (Player currentPlayer : players) {
                Card thrownCard;
                if (currentPlayer.isTurn()) {
                    // The starting player chooses any card to throw
                    thrownCard = currentPlayer.throwCard();
                    if (thrownCard.isJoker() && !isJokerPlayed) {
                        System.out.println("No joker cards has been played and you are trying to start with a joker.Please choose a different card.");
                        thrownCard = currentPlayer.throwCard();
                        System.out.println(thrownCard);
                    }
                } else {
                    // Non-starting players choose cards based on rules
                    thrownCard = chooseCardToThrow(currentPlayer);
                }

                onBoard.put(currentPlayer.getName(), thrownCard);
                currentPlayer.getHand().remove(thrownCard);
            }

            String winnerOfTheRound = findKeyWithMaxValue(onBoard);
            System.out.println(winnerOfTheRound);
            increaseWinCountOfWinnerPlayer(winnerOfTheRound);
            onBoard.clear();


            // Set the turn of the winner for the next round
            for (Player player : players) {
                player.setTurn(player.getName().equals(winnerOfTheRound));
            }
        }
    }

    private String findKeyWithMaxValue(HashMap<String, Card> hashMap) {
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

    private Card chooseCardToThrow(Player player) {
        boolean hasSameSuit = false;
        boolean noHigher = true;
        Card thrown = player.throwCard();
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
                            if (playerCard.getValue() > card.getValue()) {
                                thrown = playerCard;
                                break;
                            }
                            else {
                                thrown=smallestCard(player,card.getSuit());
                            }
                        }
                    }
                }
            }
            // aynı takımdan kart atılmadı.
            if (!thrown.getSuit().equals(card.getSuit())) {
                System.out.println("IFDEYIM");
                // Ele bakılıyor.
                for (Card playerCard : player.getHand()) {
                    // Elinde masadaki takımdan var ama onu atmamışsa:
                    if (playerCard.getSuit().equals(card.getSuit())) {
                        hasSameSuit = true;
                        System.out.println("2.IFDEYIM");
                        // Daha büyüğü varsa oynat.
                        if (playerCard.getValue() > card.getValue()) {
                            System.out.println("3.IFDEYIM");
                            thrown = playerCard;
                            noHigher = false;
                            break;
                        }
                    }
                    //Daha büyüğü oynanmamışsa,
                    if (noHigher) {
                        System.out.println("GİRDİM");
                        thrown = smallestCard(player, card.getSuit());
                    }
                    // Elinde aynı takımdan kart yok,joker varsa joker atmalı
                    if (!hasSameSuit) {
                        // Joker var oyna
                        if (player.hasJoker()) {
                            thrown = smallestCard(player,joker);
                            isJokerPlayed = true;
                        }
                        // Joker yok attığı kartı oynasın.
                    }
                }
            }
        }
        System.out.println(thrown.getSuit()+thrown.getRank());
        return thrown;
    }

    private void increaseWinCountOfWinnerPlayer(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                player.setWinCount(player.getWinCount() + 1);
            }
        }
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

    public ArrayList<Card> getOnTable() {
        return onTable;
    }

    public HashMap<String, Card> getOnBoard() {
        return onBoard;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
