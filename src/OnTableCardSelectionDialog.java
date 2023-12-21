import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class OnTableCardSelectionDialog extends JDialog {
    private final ArrayList<Card> thrownCards;

    public OnTableCardSelectionDialog(JFrame parent,ArrayList<Card> playerHand) {
        super(parent, "Thrown Card Selection", true);

        thrownCards = new ArrayList<>();

        setSize(400,200);
        setLayout(new GridLayout(1,playerHand.size()));

        for (Card card : playerHand) {
            JButton button = new JButton(card.toString());
            button.addActionListener(e -> handleCardClick(button.getText()));
            add(button);
        }
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void handleCardClick(String cardName) {
        Card thrownCard = createCard(cardName);
        thrownCards.add(thrownCard);

        if (thrownCards.size() == 4) {
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
        return new Card(suit,rank);
    }

    public ArrayList<Card> getThrownCards() {
        setVisible(true);
        return thrownCards;
    }

}
