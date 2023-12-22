import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OnTableCardSelectionDialog extends JDialog {
    private ArrayList<Card> playerHand;
    private ArrayList<Card> throwedCards;

    public OnTableCardSelectionDialog(JFrame parent,ArrayList<Card> playerHand) {
        super(parent, "Throwed Card Selection", true);

        this.playerHand=playerHand;
        throwedCards = new ArrayList<>();

        setSize(400,200);
        setLayout(new GridLayout(1,playerHand.size()));

        for (int i = 0; i < playerHand.size(); i++) {
            JButton button = new JButton(playerHand.get(i).toString());
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleCardClick(button.getText());
                }
            });
            add(button);
        }
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void handleCardClick(String cardName) {
        Card throwedCard = createCard(cardName);
        throwedCards.add(throwedCard);

        if (throwedCards.size() == 4) {
            dispose();
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

    public ArrayList<Card> getThrowedCards() {
        setVisible(true);
        return throwedCards;
    }

}