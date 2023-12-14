
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
class GamePanel extends JPanel {

    Game gameState;
    public GamePanel(Game gameState) throws IOException {
        this.gameState = gameState;

        setSize(1536, 864);
        setLayout(new BorderLayout());
        JPanel east = new JPanel();
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        JPanel north = new JPanel();
        north.setLayout(new FlowLayout());
        for (int i = 0; i < gameState.getPlayers().get(0).getHand().size(); i++) {
            JButton button = new JButton();
            button.add(new JLabel("Button" + (i + 1)));
            south.add(button);
        }
        for (int i = 0; i < 16; i++) {
            JButton button = new JButton();
            button.add(new JLabel("Button" + (i + 1)));
            north.add(button);
        }
        for (int i = 0; i < 16; i++) {
            JButton button = new JButton();
            button.add(new JLabel("Button" + (i + 1)));
            east.add(button);
        }
        add(east, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        setVisible(true);
    }
}


