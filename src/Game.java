import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Game {
    private final ArrayList<Card> onTable = new ArrayList<>();

    private final ArrayList<Card> onBoard = new ArrayList<>();

    public static ArrayList<ImageIcon> icons = new ArrayList<>();

    private final ArrayList<Player> players;

    private final Scanner scanner = new Scanner(System.in);

    public Game() {
        players = new ArrayList<>();
    }

    public void startGame() throws IOException {
        ArrayList<Card> game_deck = createStandardDeck(true);
        for (int i = 0; i < 4; i++) {
            Card card = game_deck.get((int) (Math.random() * game_deck.size()));
            game_deck.remove(card);
            onTable.add(card);
        }

        int count = 1;
        for (Player.Order order : Player.Order.values()) {
            Player player = new Player("Player " + count, order);
            count++;
            players.add(player);
        }

        while (!game_deck.isEmpty()) {
            for (Player player : players) {
                if (!game_deck.isEmpty()) {
                    player.getHand().add(game_deck.get(0));
                    game_deck.remove(0);
                }
            }
        }


        for (Player player : players) {
            if (player.getOrder().getValue() == 8) {
                player.setTurn(true);
                System.out.println("Please choose joker from your cards.");
                player.displayHand();
                String joker = scanner.nextLine();
                switch (joker) {
                    case "clubs" -> setJoker(Card.suits[0]);
                    case "diamonds" -> setJoker(Card.suits[1]);
                    case "hearts" -> setJoker(Card.suits[2]);
                    case "spades" -> setJoker(Card.suits[3]);
                    default -> System.out.println("Wrong input");
                }
                System.out.println("Please remove 4 undesired card from your deck.");
                player.displayHand();
                for (int i = 0; i < 4; i++) {
                    int input = scanner.nextInt();
                    Card card = player.getHand().remove(input - 1);
                    onTable.add(card);
                    System.out.println(card + " is deleted");
                    player.displayHand();
                }
                for (int i = 0; i < 4; i++) {
                    player.getHand().add(onTable.get(i));
                }
                System.out.println(onTable + " has been added to your deck.");


            }

        }
        displayPlayersAndHands();


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

    public static ArrayList<Card> createStandardDeck(boolean shuffle) throws IOException {
        ArrayList<Card> cards = new ArrayList<>();
        List<File> files = Files.walk(Paths.get("images")).filter(Files::isRegularFile).map(Path::toFile).toList();
        for (String suit : Card.suits) {
            for (String rank : Card.ranks) {
                Card card = new Card(suit, rank);
                card.setFaceDown(true);
                cards.add(card);
            }
        }
        for (int i = 0; i < 52; i++) {
            cards.get(i).setPngname(files.get(i).getPath());
        }
        for (Card card : cards) {
            File path = new File(card.getPngname());
            BufferedImage bufferedImage = ImageIO.read(path);
            ImageIcon icon = new ImageIcon(bufferedImage);
            icons.add(icon);
        }


        if (shuffle) {
            Collections.shuffle(cards);
        }
        return cards;
    }
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void play() {

    }

}
