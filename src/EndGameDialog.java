import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class EndGameDialog extends JDialog {

    private boolean replay;

    public EndGameDialog(JFrame parent, String winner) {
        super(parent, "Game Over", true);
        setSize(300, 150);
        setLayout(new BorderLayout());

        JLabel winnerLabel = new JLabel("Winner of The Game: " + winner);
        add(winnerLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replay = true;
                dispose();
            }
        });

        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replay = false;
                dispose();
            }
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        add(buttonPanel, BorderLayout.CENTER);

        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public boolean getReplayChoice() {
        setVisible(true);
        return replay;
    }
}
