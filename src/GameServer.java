import org.sqlite.SQLiteDataSource;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class GameServer {
    private static final int PORT = 1233;

    private ArrayList<PlayerHandler> players;

    private ArrayList<Card> onTable;

    private HashMap<String, Card> onBoard;

    static Connection conn;

    public GameServer() {
        this.players = new ArrayList<>();
        this.onTable = new ArrayList<>();

        try{
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl("jdbc:sqlite:358.db");
            conn = ds.getConnection();

            String createUserTableSQL = "CREATE TABLE IF NOT EXISTS UserTable ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "userName TEXT UNIQUE NOT NULL);";

            String createPlayedCardsTableSQL = "CREATE TABLE IF NOT EXISTS PlayedCardsTable ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "userName TEXT,"
                    + "playedCard TEXT NOT NULL,"
                    + "FOREIGN KEY (userName) REFERENCES UserTable(userName));";

            try (Statement statement = conn.createStatement()) {
                statement.execute(createUserTableSQL);

                statement.execute(createPlayedCardsTableSQL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Database connection is successful");
    }

    public static void main(String[] args) {
        new GameServer().startServer();
    }


    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is waiting for clients...");

            while (players.size() < 3) {
                Socket clientSocket = serverSocket.accept();

                DataInputStream fromClient = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream toClient = new DataOutputStream(clientSocket.getOutputStream());

                String username = fromClient.readUTF();

                if (isUsernameUnique(username)) {
                    players.add(new PlayerHandler(username, clientSocket, fromClient, toClient));
                    System.out.println("Username '" + username + "' accepted.");
                }

                System.out.println("Client: "+username+" connected: " + players.size() + "/3 players.");
            }

            startGame();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isUsernameUnique(String username) {
        for (PlayerHandler client : players) {
            if (client.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    public void prepareGameState(){
        ArrayList<Card> game_deck = createStandardDeck(true);
        for (int i = 0; i < 4; i++) {
            Card card = game_deck.get((int) (Math.random() * game_deck.size()));
            game_deck.remove(card);
            onTable.add(card);
        }

        while (!game_deck.isEmpty()) {
            for (PlayerHandler player : players) {
                if (!game_deck.isEmpty()) {
                    try {
                        player.outputStream.writeUTF(game_deck.get(0).toString());
                        game_deck.remove(0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }
    private void startGame() throws IOException, ClassNotFoundException {
        for (int i = 0; i< players.size(); i++){
            boolean isTurn = (i==0) ? true : false;
            players.get(i).outputStream.writeBoolean(isTurn);
        }

        prepareGameState();

        PlayerHandler firstPlayer = players.get(0);

        String jokerType = firstPlayer.inputStream.readUTF();
        jokerType = jokerType.toLowerCase();

        for (int i=0;i<4;i++){
            firstPlayer.outputStream.writeUTF(onTable.get(i).toString());
        }

        for (PlayerHandler player: players){
            player.outputStream.writeUTF(jokerType);
        }

        int i=0;
        while (true){
            onBoard = new HashMap<>();
            for (int j = 0; j< players.size(); j++){
                for (int z = 0; z< players.size(); z++){
                    boolean isPlayerTurn = (z==j);
                    players.get(z).outputStream.writeBoolean(isPlayerTurn);
                    players.get(z).outputStream.writeInt(onBoard.size());
                    if (onBoard.size() !=0 ){
                        for (Map.Entry<String, Card> entry : onBoard.entrySet()) {
                          players.get(z).outputStream.writeUTF(entry.getValue().toString());
                          }
                    }
                }
                String throwedCardName = players.get(j).inputStream.readUTF();
                Card throwedCard = createCard(throwedCardName);
                System.out.println(throwedCardName + " " + players.get(j).username);
                onBoard.put(players.get(j).username,throwedCard);
                for (int k = 0; k< players.size(); k++){
                    players.get(k).outputStream.writeInt(onBoard.size());
                    if (onBoard.size() !=0 ){
                        for (Map.Entry<String, Card> entry : onBoard.entrySet()) {
                            players.get(k).outputStream.writeUTF(entry.getValue().toString());
                        }
                    }
                }
            }
            String winnerOfTheRound = findKeyWithMaxValue(onBoard);
            onBoard.clear();
            for (PlayerHandler player : players){
                player.outputStream.writeUTF(winnerOfTheRound);
            }
            i++;
            if (i==16) break;
        }


    }

    public Card createCard(String cardName){
        String suit;
        String rank;
        if (cardName.length()==3){
            suit= cardName.substring(0,1);
            rank= cardName.substring(1,3);
        }else {
            suit= cardName.substring(0,1);
            rank= cardName.substring(1,2);
        }
        switch (rank){
            case "J" -> rank = "Jack";
            case "A" -> rank = "Ace";
            case "K" -> rank = "King";
            case "Q" -> rank = "Queen";
        }
        switch (suit){
            case "C" -> suit = "clubs";
            case "D" -> suit = "diamonds";
            case "H" -> suit = "hearts";
            case "S" -> suit = "spades";
        }
        Card throwedCard = new Card(suit,rank);
        return throwedCard;
    }

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


    private class PlayerHandler {
        private String username;
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

        public PlayerHandler(String username, Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
            this.username = username;
            this.socket = socket;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        public String getUsername() {
            return username;
        }

        public Socket getSocket() {
            return socket;
        }
    }

}