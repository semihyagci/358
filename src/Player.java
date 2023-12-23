import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Player {
    private static final int SERVER_PORT = 1233;
    public String userName;
    private Socket socket;
    private final ArrayList<Card> hand;

    private ArrayList<Card> onBoard;

    private boolean isTurn;

    private int winCount;
    private DataOutputStream outputStream;
    private boolean isGameStarting = true;
    private JFrame frame;
    private JTextField usernameField;


    public Player() {
        hand = new ArrayList<>();
        isTurn = false;
        winCount = 0;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    public int getWinCount() {
        return winCount;
    }

    public String getName() {
        return userName;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public void prepareGameForm(String jokerChoice) {
        switch (jokerChoice) {
            case "clubs" -> setJoker(Card.suits[0]);
            case "diamonds" -> setJoker(Card.suits[1]);
            case "hearts" -> setJoker(Card.suits[2]);
            case "spades" -> setJoker(Card.suits[3]);
        }
    }

    public void setJoker(String suit) {
        for (Card card : this.hand) {
            if (card.getSuit().equals(suit)) {
                card.setJoker(true);
                card.increaseValue();
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Player().initialize();
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

    public void startPlay() throws IOException, ClassNotFoundException, InterruptedException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        boolean isTurnFromServer = inputStream.readBoolean();
        setTurn(isTurnFromServer);

        for (int i = 0; i < 16; i++) {
            String cardName = inputStream.readUTF();
            Card card = UtilityService.createCard(cardName);
            this.hand.add(card);
        }

        if (isGameStarting) {
            frame.getContentPane().removeAll();
            frame.setSize(1080, 720);
            GamePanel gamePanel = new GamePanel(this.hand, new ArrayList<>(), this);
            frame.add(gamePanel, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
            if (this.isTurn) {
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
                outputStream.writeUTF(chosenValue);

                OnTableCardSelectionDialog thrownCardSelection = new OnTableCardSelectionDialog(frame, this.hand, 0);

                ArrayList<Card> thrownCards = thrownCardSelection.getThrownCards();

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < this.hand.size(); j++) {
                        if (thrownCards.get(i).toString().equals(this.hand.get(j).toString())) {
                            this.hand.remove(j);
                        }
                    }
                }

                for (int i = 0; i < 4; i++) {
                    String cardName = inputStream.readUTF();
                    Card card = UtilityService.createCard(cardName);
                    this.hand.add(card);
                }
                frame.getContentPane().removeAll();
                GamePanel gamePanel2 = new GamePanel(this.hand, new ArrayList<>(), this);
                frame.add(gamePanel2, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
            isGameStarting = false;

            String jokerType = inputStream.readUTF();

            prepareGameForm(jokerType);
        }
        int i = 0;
        ArrayList<String> onTableReplayCards;
        ArrayList<String> playerNamesForReplay;
        ArrayList<String> winnerOfTheRounds;
        while (true) {
            boolean isMyTurn = inputStream.readBoolean();
            setTurn(isMyTurn);
            int onBoardSize = inputStream.readInt();
            onBoard = new ArrayList<>();
            if (onBoardSize != 0) {
                for (int j = 0; j < onBoardSize; j++) {
                    String onBoardCardName = inputStream.readUTF();
                    Card card = UtilityService.createCard(onBoardCardName);
                    onBoard.add(card);
                }

                frame.getContentPane().removeAll();
                GamePanel gamePanel2 = new GamePanel(this.hand, onBoard, this);
                frame.add(gamePanel2, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }

            if (isMyTurn) {
                OnTableCardSelectionDialog thrownCardSelection = new OnTableCardSelectionDialog(frame, this.hand, 1);

                ArrayList<Card> thrownCards = thrownCardSelection.getThrownCards();

                Card thrownCard = thrownCards.get(0);

                for (int t = 0; t < hand.size(); t++) {
                    if (hand.get(t).toString().equals(thrownCard.toString())) {
                        hand.remove(t);
                    }
                }

                frame.getContentPane().removeAll();
                GamePanel gamePanel3 = new GamePanel(this.hand, onBoard, this);
                frame.add(gamePanel3, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();

                outputStream.writeUTF(thrownCard.toString());
            }

            onBoardSize = inputStream.readInt();

            if (onBoardSize != 0) {
                for (int j = 0; j < onBoardSize; j++) {
                    String onBoardCardName = inputStream.readUTF();
                    Card card = UtilityService.createCard(onBoardCardName);
                    boolean cardExist = false;
                    for (Card testCard : onBoard) {
                        if (card.toString().equals(testCard.toString())) {
                            cardExist = true;
                        }
                    }
                    if (!cardExist) {
                        onBoard.add(card);
                    }
                }

                frame.getContentPane().removeAll();
                GamePanel gamePanel2 = new GamePanel(this.hand, onBoard, this);
                frame.add(gamePanel2, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }

            if (onBoardSize == 3) {
                String winnerOfTheRound = inputStream.readUTF();
                i++;
                onBoard.clear();
                if (userName.equals(winnerOfTheRound)) {
                    winCount++;
                }
                SwingUtilities.invokeLater(() -> {
                    frame.getContentPane().removeAll();
                    GamePanel gamePanel2;
                    try {
                        gamePanel2 = new GamePanel(this.hand, onBoard, this);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    frame.add(gamePanel2, BorderLayout.CENTER);
                    frame.revalidate();
                    frame.repaint();
                });
            }
            if (i == 4) {
                onTableReplayCards = new ArrayList<>();
                for (int x = 0; x < 12; x++) {
                    String playedCardName = inputStream.readUTF();
                    onTableReplayCards.add(playedCardName);
                }

                playerNamesForReplay = new ArrayList<>();
                for (int q = 0; q < 3; q++) {
                    String playerName = inputStream.readUTF();
                    playerNamesForReplay.add(playerName);
                }

                winnerOfTheRounds = new ArrayList<>();
                for (int q = 0; q < 4; q++) {
                    String winnerName = inputStream.readUTF();
                    winnerOfTheRounds.add(winnerName);
                }

                break;
            }
        }
        String winnerOfTheGame = inputStream.readUTF();

        EndGameDialog endGameDialog = new EndGameDialog(frame, winnerOfTheGame);

        boolean replay = endGameDialog.getReplayChoice();

        frame.dispose();

        if (replay) {
            ArrayList<ArrayList<String>> roundCards = new ArrayList<>();
            int x = 0;
            for (int l = 0; l < 4; l++) {
                ArrayList<String> tempOnTable = new ArrayList<>();
                for (int z = x; z < x + 3; z++) {
                    tempOnTable.add(onTableReplayCards.get(z));
                }

                roundCards.add(tempOnTable);
                x += 3;
            }
          new ReplayPanel(roundCards, playerNamesForReplay, winnerOfTheRounds);
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
                socket = new Socket("localhost", SERVER_PORT);
                outputStream = new DataOutputStream(socket.getOutputStream());

                outputStream.writeUTF(username);

                startPlay();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}