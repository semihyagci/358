import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private Game gameState;
    private static final int PORT = 8080;
    private List<ClientHandler> players = new ArrayList<>();

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
                String username = (String) inputStream.readObject();

                if (isUsernameUnique(username)) {
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    players.add(new ClientHandler(username, clientSocket, inputStream, outputStream));
                    System.out.println("Username '" + username + "' accepted.");
                } else {
                    System.out.println("Username '" + username + "' already exists. Connection rejected.");
                    sendToClient(clientSocket, "Username already exists. Please choose another username.");
                    clientSocket.close();
                }
                System.out.println("Client: "+username+" connected: " + players.size() + "/3 players.");
            }

            startGame();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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

    private void startGame() {
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

        //Taking string as object might be necessary.
        sendToClient(firstClient.getSocket(), "Please provide your joker based on the GameState: ");

        //We remove players 4 card and send that ArrayList of cards to server and server would take
        sendToClient(firstClient.getSocket(), "Please drop your 4 cards on the GameState: ");

        // Handle the input received from the client
        // You can implement a method to handle the input and update the GameState accordingly
        // For example: handleClientInput(randomClient.readObject());
    }

    private void sendToAllClients(Object data) {
        for (ClientHandler client : players) {
            sendToClient(client.getSocket(), data);
        }
    }

    private void sendToClient(Socket socket, Object data) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler {
        private String username;
        private Socket socket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;

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
