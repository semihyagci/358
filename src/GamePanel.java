
import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class GamePanel extends JPanel {

    public GamePanel(ArrayList<Card> playerHand, ArrayList<Card> onTable, DataOutputStream os, Player player) throws IOException {
        setSize(1536, 864);
        setLayout(new BorderLayout());
        JPanel onTableCardsPanel = new JPanel();
        onTableCardsPanel.setLayout(new FlowLayout());

        JPanel playerCardPanel = new JPanel();
        playerCardPanel.setLayout(new FlowLayout());

        playerHand.sort(new CardComparator());
        for (int i = 0; i < playerHand.size(); i++) {
            JButton button = new JButton(playerHand.get(i).toString());
            button.addActionListener(new ThrowCardActionListener(os,player));
            playerCardPanel.add(button);
        }

        for (int i = 0; i < onTable.size(); i++) {
            JButton button = new JButton(onTable.get(i).toString());
            onTableCardsPanel.add(button);
            revalidate();
            repaint();
        }

        add(onTableCardsPanel, BorderLayout.CENTER);
        add(playerCardPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}

