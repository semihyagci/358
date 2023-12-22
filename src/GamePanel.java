
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

class GamePanel extends JPanel {

    public GamePanel(ArrayList<Card> playerHand, ArrayList<Card> onTable, Player player) throws IOException {
        setSize(1536, 864);
        setLayout(new BorderLayout());

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BorderLayout());

        JPanel onTableCardsPanel = new JPanel();
        onTableCardsPanel.setLayout(new FlowLayout());

        JPanel playerCardPanel = new JPanel();
        playerCardPanel.setLayout(new FlowLayout());


        playerHand.sort(new CardComparator());
        for (int i = 0; i < playerHand.size(); i++) {
            JButton button = new JButton(playerHand.get(i).toString());
            playerCardPanel.add(button);
        }

        for (int i = 0; i < onTable.size(); i++) {
            JButton button = new JButton(onTable.get(i).toString());
            onTableCardsPanel.add(button);
            revalidate();
            repaint();
        }

        cardsPanel.add(onTableCardsPanel,BorderLayout.CENTER);
        cardsPanel.add(playerCardPanel, BorderLayout.SOUTH);

        JLabel playerNameAndCountLabel = new JLabel( player.getName() +": "+ player.getWinCount());
        playerNameAndCountLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(playerNameAndCountLabel,BorderLayout.SOUTH);
        add(cardsPanel,BorderLayout.CENTER);
        setVisible(true);
    }
}

