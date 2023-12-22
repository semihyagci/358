import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 1233;

    private ArrayList<PlayerHandler> playerz;
    private String joker;

    private ArrayList<Card> onTable;

    private HashMap<String, Card> onBoard;

    public GameServer() {
        this.playerz = new ArrayList<>();
        this.joker ="";
        this.onTable = new ArrayList<>();
    }

    public static void main(String[] args) {
        new GameServer().startServer();
    }


    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is waiting for clients...");

            while (playerz.size() < 3) {
                Socket clientSocket = serverSocket.accept();

                DataInputStream fromClient = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream toClient = new DataOutputStream(clientSocket.getOutputStream());

                String username = fromClient.readUTF();

                if (isUsernameUnique(username)) {
                    playerz.add(new PlayerHandler(username, clientSocket, fromClient, toClient));
                    System.out.println("Username '" + username + "' accepted.");
                }

                System.out.println("Client: "+username+" connected: " + playerz.size() + "/3 players.");
            }

            startGame();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isUsernameUnique(String username) {
        for (PlayerHandler client : playerz) {
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
            for (PlayerHandler player : playerz) {
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
        for (int i=0;i<playerz.size();i++){
            boolean isTurn = (i==0) ? true : false;
            playerz.get(i).outputStream.writeBoolean(isTurn);
        }

        prepareGameState();

        PlayerHandler firstPlayer = playerz.get(0);

        String jokerType = firstPlayer.inputStream.readUTF();
        jokerType = jokerType.toLowerCase();

        for (int i=0;i<4;i++){
            firstPlayer.outputStream.writeUTF(onTable.get(i).toString());
        }

        for (PlayerHandler player: playerz){
            player.outputStream.writeUTF(jokerType);
        }

        int i=0;
        while (true){
            onBoard = new HashMap<>();
            for (int j=0;j<playerz.size();j++){
                for (int z=0;z<playerz.size();z++){
                    boolean isPlayerTurn = (z==j);
                    playerz.get(z).outputStream.writeBoolean(isPlayerTurn);
                    playerz.get(z).outputStream.writeInt(onBoard.size());
                    if (onBoard.size() !=0 ){
                        for (Map.Entry<String, Card> entry : onBoard.entrySet()) {
                          playerz.get(z).outputStream.writeUTF(entry.getValue().toString());
                          }
                    }
                }
                String throwedCardName = playerz.get(j).inputStream.readUTF();
                Card throwedCard = createCard(throwedCardName);
                System.out.println(throwedCardName + " " + playerz.get(j).username);
                onBoard.put(playerz.get(j).username,throwedCard);
                for (int k=0;k<playerz.size();k++){
                    playerz.get(k).outputStream.writeInt(onBoard.size());
                    if (onBoard.size() !=0 ){
                        for (Map.Entry<String, Card> entry : onBoard.entrySet()) {
                            playerz.get(k).outputStream.writeUTF(entry.getValue().toString());
                        }
                    }
                }
            }
            String winnerOfTheRound = findKeyWithMaxValue(onBoard);
            onBoard.clear();
            System.out.println(winnerOfTheRound);
            for (PlayerHandler player : playerz){
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

    private void increaseWinCountOfWinnerPlayer(String name) {
        for (PlayerHandler player : playerz) {
            if (player.username.equals(name)) {
               // player.setWinCount(player.getWinCount() + 1);
            }
        }
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