import javax.swing.*;
import java.awt.*;

public class EndGameDialog extends JDialog {

    private boolean replay;

    public EndGameDialog(JFrame parent, String winner) {
        super(parent, "Game Over", true);
        setSize(300, 150);
        setLayout(new BorderLayout());

        JLabel winnerLabel = new JLabel("Winner of The Game: " + winner);
        add(winnerLabel, BorderLayout.NORTH);

        JLabel replayQuestionLabel = new JLabel("Do you want to watch the replay?");
        add(replayQuestionLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        yesButton.addActionListener(e -> {
            replay = true;
            dispose();
        });

        noButton.addActionListener(e -> {
            replay = false;
            dispose();
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public boolean getReplayChoice() {
        setVisible(true);
        return replay;
    }
}
