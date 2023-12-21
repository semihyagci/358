import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class GameServer {
    private Game gameState;
    private static final int PORT = 1233;
    private final List<ClientHandler> players = new ArrayList<>();

    public static void main(String[] args) {
        new GameServer().startServer();
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is waiting for clients...");

            while (players.size() < 3) {
                Socket clientSocket = serverSocket.accept();

                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                String username = (String) inputStream.readObject();

                if (isUsernameUnique(username)) {
                    players.add(new ClientHandler(username, clientSocket, inputStream, outputStream));
                    System.out.println("Username '" + username + "' accepted.");
                } else {
                    System.out.println("Username '" + username + "' already exists. Connection rejected.");
                    sendToClient(outputStream, "Username already exists. Please choose another username.");
                    clientSocket.close();
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
        for (ClientHandler client : players) {
            if (client.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    private void startGame() throws IOException, ClassNotFoundException {
        int count =0;
        ArrayList<Player> playerList = new ArrayList<>();
        for (int i=2; i>=0 ; i--){
            Player newPlayer = new Player(players.get(count).username,Player.Order.values()[i]);
            playerList.add(newPlayer);
            count++;
        }

        gameState = new Game(playerList);
        gameState.prepareGameState();

        sendToAllClients(gameState);

        ClientHandler firstClient = players.get(0);

        String jokerType = (String) firstClient.inputStream.readObject();
        jokerType = jokerType.toLowerCase();

        ArrayList<Card> thrownCards = (ArrayList<Card>) firstClient.inputStream.readObject();

        System.out.println(thrownCards);

        gameState.prepareGameForm(jokerType,thrownCards);

        sendToAllClients(gameState);

        ServerThread serverThread = new ServerThread(players.get(0).inputStream);
        ServerThread serverThread2 = new ServerThread(players.get(1).inputStream);
        ServerThread serverThread3 = new ServerThread(players.get(2).inputStream);
        serverThread.start();
        serverThread2.start();
        serverThread3.start();

        /*
        for (int i=0;i<16;i++){
            HashMap<String,Card> thrownCardMap = new HashMap<>();
            for (ClientHandler player : players){
                Card thrownCard = (Card) player.inputStream.readObject();
                System.out.println(thrownCard.toString());
                thrownCardMap.put(player.username,thrownCard);
                System.out.println(thrownCard);
                gameState.getOnBoard().put(player.getUsername(),thrownCard);
                gameState.getPlayers().get(j)
                sendToAllClients(gameState);

            }
        }
         */
    }

    private void sendToAllClients(Object data) {
        for (ClientHandler client : players) {
            sendToClient(client.outputStream, data);
        }
    }

    private void sendToClient(ObjectOutputStream os, Object data) {
        try {
            os.writeObject(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ServerThread extends Thread{

        //THREADE SOCKET VER DATA AKMIYO
        private ObjectInputStream inputStream;
        public ServerThread(ObjectInputStream inputStream) {
            this.inputStream = inputStream;
        }
        public void run(){
            while (true){
                try {
                    for (int i=0;i<16;i++){
                        HashMap<String,Card> thrownCardMap = new HashMap<>();
                        for (int j=0; j<players.size();j++){
                            Card thrownCard = (Card) inputStream.readObject();
                            System.out.println(thrownCard.toString());
                            thrownCardMap.put(players.get(j).username,thrownCard);
                            gameState.getOnBoard().put(players.get(j).getUsername(),thrownCard);
                            System.out.println(gameState.getOnBoard());
                            gameState.getPlayers().get(j).setTurn(false);
                            int x=(j==2)?0:j+1;
                            gameState.getPlayers().get(x).setTurn(true);
                            sendToAllClients(gameState);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientHandler {
        private final String username;
        private final Socket socket;
        private final ObjectInputStream inputStream;
        private final ObjectOutputStream outputStream;

        public ClientHandler(String username, Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
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

        // You may add getter methods for inputStream and outputStream if needed

        // You can add more methods as needed for handling client communication
    }
}
