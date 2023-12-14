import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClientUI {
    private static final int SERVER_PORT = 8080;

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
    private ObjectInputStream inputStream;

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
                inputStream = new ObjectInputStream(socket.getInputStream());

                // Send the username to the server
                outputStream.writeObject(username);
                outputStream.flush();

                // Display the "Waiting for other players" message
                frame.getContentPane().removeAll();
                JLabel waitingLabel = new JLabel("Waiting for other players to start!");
                frame.add(waitingLabel, BorderLayout.CENTER);
                frame.validate();
                frame.repaint();

                // Continue to listen for updates from the server
                while (true) {
                    Object serverMessage = inputStream.readObject();
                    Game gameState = (Game) serverMessage;
                    if (serverMessage != null && isGameStarting){
                        frame.getContentPane().removeAll();
                        GamePanel gamePanel = new GamePanel(gameState);
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
                            outputStream.flush();
                        }
                    }
                }

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
}
