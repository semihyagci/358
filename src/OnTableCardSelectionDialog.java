import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OnTableCardSelectionDialog extends JDialog {
    private final int type;
    private final ArrayList<Card> throwedCards;

    public OnTableCardSelectionDialog(JFrame parent, ArrayList<Card> playerHand, int choice) {
        super(parent, "Throwed Card Selection", true);

        this.type = choice == 0 ? 4 : 1;

        throwedCards = new ArrayList<>();

        setSize(1000, 300);
        setLayout(new GridLayout(1, playerHand.size()));

        for (Card card : playerHand) {
            JButton button = new JButton(card.toString());
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
        Card throwedCard = UtilityService.createCard(cardName);
        throwedCards.add(throwedCard);

        Container container = getContentPane();
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(cardName)) {
                    container.remove(button);
                    revalidate();
                    repaint();
                    break;
                }
            }
        }

        if (throwedCards.size() == type) {
            dispose();
        }
    }


    public ArrayList<Card> getThrowedCards() {
        setVisible(true);
        return throwedCards;
    }

}