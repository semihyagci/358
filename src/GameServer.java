import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

public class GameServer {
    private static final int PORT = 1233;

    private final ArrayList<PlayerHandler> players;

    private final ArrayList<Card> onTable;

    public GameServer() {
        this.players = new ArrayList<>();
        this.onTable = new ArrayList<>();
        new DatabaseService();
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
                    players.add(new PlayerHandler(username, fromClient, toClient));
                    System.out.println("Username '" + username + "' accepted.");
                    DatabaseService.createPlayer(username);
                }

                System.out.println("Client: " + username + " connected: " + players.size() + "/3 players.");
            }

            startGame();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException | SQLException e) {
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

    public void prepareGameState() {
        ArrayList<Card> game_deck = UtilityService.createStandardDeck(true);
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

    private void startGame() throws IOException, ClassNotFoundException, SQLException {
        for (int i = 0; i < players.size(); i++) {
            boolean isTurn = i == 0;
            players.get(i).outputStream.writeBoolean(isTurn);
        }

        prepareGameState();

        PlayerHandler firstPlayer = players.get(0);

        String jokerType = firstPlayer.inputStream.readUTF();
        jokerType = jokerType.toLowerCase();

        for (int i = 0; i < 4; i++) {
            firstPlayer.outputStream.writeUTF(onTable.get(i).toString());
        }

        for (PlayerHandler player : players) {
            player.outputStream.writeUTF(jokerType);
        }

        HashMap<String, Integer> winnerOfTheGame = new HashMap<>();
        ArrayList<String> winnerOfTheRounds = new ArrayList<>();
        int i = 0;
        while (true) {
            HashMap<String, Card> onBoard = new HashMap<>();
            for (int j = 0; j < players.size(); j++) {
                for (int z = 0; z < players.size(); z++) {
                    boolean isPlayerTurn = (z == j);
                    players.get(z).outputStream.writeBoolean(isPlayerTurn);
                    players.get(z).outputStream.writeInt(onBoard.size());
                    if (onBoard.size() != 0) {
                        for (Map.Entry<String, Card> entry : onBoard.entrySet()) {
                            players.get(z).outputStream.writeUTF(entry.getValue().toString());
                        }
                    }
                }

                String thrownCardName = players.get(j).inputStream.readUTF();
                Card thrownCard = UtilityService.createCard(thrownCardName);
                onBoard.put(players.get(j).username, thrownCard);
                DatabaseService.recordPlayMovement(players.get(j).username, thrownCardName);

                for (PlayerHandler player : players) {
                    player.outputStream.writeInt(onBoard.size());
                    if (onBoard.size() != 0) {
                        for (Map.Entry<String, Card> entry : onBoard.entrySet()) {
                            player.outputStream.writeUTF(entry.getValue().toString());
                        }
                    }
                }
            }
            String winnerOfTheRound = UtilityService.findKeyWithMaxValue(onBoard);
            winnerOfTheRounds.add(winnerOfTheRound);
            onBoard.clear();
            for (PlayerHandler player : players) {
                player.outputStream.writeUTF(winnerOfTheRound);
            }

            if (winnerOfTheGame.containsKey(winnerOfTheRound)) {
                int currentValue = winnerOfTheGame.get(winnerOfTheRound);
                winnerOfTheGame.put(winnerOfTheRound, currentValue + 1);
            } else {
                winnerOfTheGame.put(winnerOfTheRound, 1);
            }

            i++;
            if (i == 16) {
                ArrayList<String> playedCardNames = DatabaseService.retrieveAllRecordsFromPlayedCardsTable();
                for (PlayerHandler player : players) {
                    for (String playedCardName : playedCardNames) {
                        player.outputStream.writeUTF(playedCardName);
                    }
                }

                for (PlayerHandler player : players) {
                    for (PlayerHandler playerHandler : players) {
                        player.outputStream.writeUTF(playerHandler.getUsername());
                    }
                }

                for (PlayerHandler player : players) {
                    for (String ofTheRound : winnerOfTheRounds) {
                        player.outputStream.writeUTF(ofTheRound);
                    }
                }


                break;
            }
        }

        String winnerOfTheGameName = UtilityService.findKeyWithMaxValueInteger(winnerOfTheGame);

        for (PlayerHandler player : players) {
            player.outputStream.writeUTF(winnerOfTheGameName);
        }

        DatabaseService.deleteGameRecordsAndTerminateConnection();
    }


    private class PlayerHandler {
        private final String username;
        private final DataInputStream inputStream;
        private final DataOutputStream outputStream;

        public PlayerHandler(String username, DataInputStream inputStream, DataOutputStream outputStream) {
            this.username = username;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        public String getUsername() {
            return username;
        }
    }

}