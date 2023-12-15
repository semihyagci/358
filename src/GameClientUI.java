import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GameClientUI {
    private static final int SERVER_PORT = 1233;

    private static int playerCounter =0;

    private boolean isGameStarting=true;

    private int playerID;

    private JFrame frame;
    private JTextField usernameField;

    public String userName;

    public GameClientUI() {
        playerID = playerCounter++;
    }
    private Socket socket;

    private ObjectOutputStream outputStream;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameClientUI().initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void initialize() throws IOException {
        frame = new JFrame("Game Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel usernameLabel = new JLabel("Enter your username: ");
        usernameField = new JTextField(20);
        JButton connectButton = new JButton("Connect");

        connectButton.addActionListener(new ConnectButtonListener());

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(connectButton);

        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
    public void startPlay() throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        while (true) {
            Game gameState = (Game) inputStream.readObject();;
            if (gameState != null && isGameStarting){
                frame.getContentPane().removeAll();
                GamePanel gamePanel = new GamePanel(gameState,playerID);
                frame.add(gamePanel, BorderLayout.CENTER);
                frame.validate();
                frame.repaint();
                if (playerID == 0){
                    String[] options = {"Spades", "Hearts", "Clubs", "Diamonds"};
                    String chosedValue = "";
                    Object selectedValue = JOptionPane.showInputDialog(
                            null,
                            "Choose joker",
                            "Please choose joker.",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]); // Default selection

                    if (selectedValue != null) {
                        chosedValue = selectedValue.toString();
                    }
                    outputStream.writeObject(chosedValue);

                    OnTableCardSelectionDialog throwedCardSelection = new OnTableCardSelectionDialog(frame,gameState.getPlayers().get(playerID).getHand());

                    ArrayList<Card> throwedCards = throwedCardSelection.getThrowedCards();
                    System.out.println(throwedCards);

                    outputStream.writeObject(throwedCards);
                }
                isGameStarting=false;
            }
            frame.getContentPane().removeAll();
            GamePanel gamePanel = new GamePanel(gameState,playerID);
            frame.add(gamePanel, BorderLayout.CENTER);
            frame.validate();
            frame.repaint();
        }
    }

    private class ConnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            userName = username;

            try {
                // Connect to the server
                socket = new Socket("localhost",SERVER_PORT);
                outputStream = new ObjectOutputStream(socket.getOutputStream());


                // Send the username to the server
                outputStream.writeObject(username);

                // Display the "Waiting for other players" message
                frame.getContentPane().removeAll();
                JLabel waitingLabel = new JLabel("Waiting for other players to start!");
                frame.add(waitingLabel, BorderLayout.CENTER);
                frame.validate();
                frame.repaint();

                startPlay();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
