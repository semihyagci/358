import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class OnTableCardSelectionDialog extends JDialog {
    private final int type;
    private final ArrayList<Card> thrownCards;

    public OnTableCardSelectionDialog(JFrame parent, ArrayList<Card> playerHand, int choice) {
        super(parent, "Thrown Card Selection", true);

        this.type = choice == 0 ? 4 : 1;

        thrownCards = new ArrayList<>();

        setSize(1000, 300);
        setLayout(new GridLayout(1, playerHand.size()));

        for (Card card : playerHand) {
            JButton button = new JButton(card.toString());
            button.addActionListener(e -> handleCardClick(button.getText()));
            add(button);
        }
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void handleCardClick(String cardName) {
        Card thrownCard = UtilityService.createCard(cardName);
        thrownCards.add(thrownCard);

        Container container = getContentPane();
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JButton button) {
                if (button.getText().equals(cardName)) {
                    container.remove(button);
                    revalidate();
                    repaint();
                    break;
                }
            }
        }

        if (thrownCards.size() == type) {
            dispose();
        }
    }


    public ArrayList<Card> getThrownCards() {
        setVisible(true);
        return thrownCards;
    }

}