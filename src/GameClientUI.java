import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GameClientUI {
    private static final int SERVER_PORT = 1233;

    private boolean isGameStarting=true;

    private JFrame frame;
    private JTextField usernameField;

    public String userName;

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
            Game gameState = (Game) inputStream.readObject();
            if (gameState != null && isGameStarting) {
                frame.getContentPane().removeAll();
                GamePanel gamePanel = new GamePanel(gameState, userName, outputStream);
                frame.add(gamePanel, BorderLayout.CENTER);
                frame.validate();
                frame.repaint();
                if (userName.equals(gameState.getPlayers().get(0).getName())) {
                    String[] options = {"Spades", "Hearts", "Clubs", "Diamonds"};
                    String chosenValue = "";
                    Object selectedValue = JOptionPane.showInputDialog(
                            null,
                            "Choose joker",
                            "Please choose joker.",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]); // Default selection

                    if (selectedValue != null) {
                        chosenValue = selectedValue.toString();
                    }
                    outputStream.writeObject(chosenValue);

                    OnTableCardSelectionDialog droppedCardsSelection = new OnTableCardSelectionDialog(frame, gameState.getPlayers().get(0).getHand());

                    ArrayList<Card> droppedCards = droppedCardsSelection.getThrownCards();

                    outputStream.writeObject(droppedCards);
                }
                isGameStarting = false;

                gameState = (Game) inputStream.readObject();
                frame.getContentPane().removeAll();
                gamePanel = new GamePanel(gameState, userName, outputStream);
                frame.add(gamePanel, BorderLayout.CENTER);
                frame.validate();
                frame.repaint();
                for (int i=0;i<16;i++){
                    for (int j=0;j<3;j++){
                        gameState = (Game) inputStream.readObject();
                        frame.getContentPane().removeAll();
                        gamePanel = new GamePanel(gameState, userName, outputStream);
                        frame.add(gamePanel, BorderLayout.CENTER);
                        frame.validate();
                        frame.repaint();
                    }
                }



            }

            /*
            frame.getContentPane().removeAll();
            GamePanel gamePanel = new GamePanel(gameState,userName,outputStream);
            frame.add(gamePanel, BorderLayout.CENTER);
            frame.validate();
            frame.repaint();
             */
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


                startPlay();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
