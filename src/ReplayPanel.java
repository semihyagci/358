import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class ReplayPanel extends JFrame {

    public ReplayPanel(ArrayList<ArrayList<String>> onTable, ArrayList<String> players, ArrayList<String> winnerOfTheRounds) {
        setSize(700, 600);
        setLayout(new BorderLayout());

        JPanel onTableCardsPanel = new JPanel();
        onTableCardsPanel.setLayout(new GridLayout(17, 5));

        JButton blankButton = new JButton("Round Number");
        onTableCardsPanel.add(blankButton);

        for (String player : players) {
            JButton personButton = new JButton(player);
            onTableCardsPanel.add(personButton);
        }

        JButton winnerOfTheRoundLabel = new JButton("Round Winner");
        onTableCardsPanel.add(winnerOfTheRoundLabel);

        for (int i = 0; i < onTable.size(); i++) {
            JButton labelButton = new JButton((i + 1) + ". Round");
            onTableCardsPanel.add(labelButton);
            for (int j = 0; j < 3; j++) {
                JButton button = new JButton(onTable.get(i).get(j));
                onTableCardsPanel.add(button);
            }
            JButton winnerButton = new JButton(winnerOfTheRounds.get(i));
            onTableCardsPanel.add(winnerButton);
        }

        add(onTableCardsPanel);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

