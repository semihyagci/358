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
    private ArrayList<Card> hand;

    private ArrayList<Card> onBoard;

    private boolean isTurn;

    private int winCount;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private boolean isGameStarting=true;
    private JFrame frame;
    private JTextField usernameField;


    public Player() {
        hand = new ArrayList<>();
        isTurn = false;
        winCount = 0;
    }


    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public String getName() {
        return userName;
    }

    public boolean isTurn() {
        return isTurn;
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
        inputStream = new DataInputStream(socket.getInputStream());
        boolean isTurnFromServer = inputStream.readBoolean();
        setTurn(isTurnFromServer);

        for (int i=0;i<16;i++){
            String cardName = inputStream.readUTF();
            Card card = createCard(cardName);
            this.hand.add(card);
        }

        if (isGameStarting) {
            frame.getContentPane().removeAll();
            GamePanel gamePanel = new GamePanel(this.hand, new ArrayList<>(),this);
            frame.add(gamePanel, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
                if (this.isTurn) {
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
                    outputStream.writeUTF(chosedValue);

                    OnTableCardSelectionDialog throwedCardSelection = new OnTableCardSelectionDialog(frame,this.hand,0);

                    ArrayList<Card> throwedCards = throwedCardSelection.getThrowedCards();

                    for (int i=0;i<4;i++){
                        for (int j=0;j<this.hand.size();j++){
                            if (throwedCards.get(i).toString().equals(this.hand.get(j).toString())){
                                this.hand.remove(j);
                            }
                        }
                    }

                    for (int i=0;i<4;i++){
                        String cardName = inputStream.readUTF();
                        Card card = createCard(cardName);
                        this.hand.add(card);
                    }
                    frame.getContentPane().removeAll();
                    GamePanel gamePanel2 = new GamePanel(this.hand, new ArrayList<>(),this);
                    frame.add(gamePanel2, BorderLayout.CENTER);
                    frame.revalidate();
                    frame.repaint();
                }
                isGameStarting = false;

                String jokerType = inputStream.readUTF();

                prepareGameForm(jokerType);
            }
        int i=0;
        while (true){
            boolean isMyTurn = inputStream.readBoolean();
            System.out.println(isMyTurn);
            setTurn(isMyTurn);
            int onBoardSize = inputStream.readInt();
            System.out.println(" a  "+ onBoardSize);
            onBoard = new ArrayList<>();
            if (onBoardSize != 0){
                for (int j=0;j<onBoardSize;j++){
                    String onBoardCardName = inputStream.readUTF();
                    Card card = createCard(onBoardCardName);
                    onBoard.add(card);
                }

                frame.getContentPane().removeAll();
                GamePanel gamePanel2 = new GamePanel(this.hand, onBoard,this);
                frame.add(gamePanel2, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }

            if (isMyTurn){
                OnTableCardSelectionDialog throwedCardSelection = new OnTableCardSelectionDialog(frame,this.hand,1);

                ArrayList<Card> throwedCards = throwedCardSelection.getThrowedCards();

                Card throwedCard = throwedCards.get(0);

                for (int t=0; t<hand.size();t++){
                    if (hand.get(t).toString().equals(throwedCard.toString())){
                        hand.remove(t);
                    }
                }

                frame.getContentPane().removeAll();
                GamePanel gamePanel3 = new GamePanel(this.hand, onBoard,this);
                frame.add(gamePanel3, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();

                outputStream.writeUTF(throwedCard.toString());
            }

            onBoardSize = inputStream.readInt();
            System.out.println(" b  "+ onBoardSize);

            if (onBoardSize != 0){
                for (int j=0;j<onBoardSize;j++){
                    String onBoardCardName = inputStream.readUTF();
                    Card card = createCard(onBoardCardName);
                    boolean cardExist = false;
                    for (Card testCard : onBoard){
                        if (card.toString().equals(testCard.toString())){
                            cardExist=true;
                        }
                    }
                    if (!cardExist){
                        onBoard.add(card);
                    }
                }

                frame.getContentPane().removeAll();
                GamePanel gamePanel2 = new GamePanel(this.hand, onBoard,this);
                frame.add(gamePanel2, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }

            if (onBoardSize==3){
                String winnerOfTheRound = inputStream.readUTF();
                onBoard.clear();
                if (userName.equals(winnerOfTheRound)){
                    winCount++;
                }
                System.out.println(winnerOfTheRound);
            }
            i++;
            if (i==16) break;
        }
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
            for (int i = 0; i < this.hand.size(); i++) {
                Card card = this.hand.get(i);
                if (card.getSuit().equals(suit)) {
                    card.setJoker(true);
                    card.increaseValue();
                }
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
                socket = new Socket("localhost",SERVER_PORT);
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