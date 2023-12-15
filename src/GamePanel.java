
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Collections;

class GamePanel extends JPanel {

    Game gameState;
    int playerID;
    public GamePanel(Game gameState, int playerID) throws IOException {
        this.gameState = gameState;
        this.playerID = playerID;

        setSize(1536, 864);
        setLayout(new BorderLayout());
        JPanel east = new JPanel();
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        JPanel north = new JPanel();
        north.setLayout(new FlowLayout());
        Collections.sort(gameState.getPlayers().get(playerID).getHand(),new CardComparator());
        for (int i = 0; i < gameState.getPlayers().get(playerID).getHand().size(); i++) {
            JButton button = new JButton();
            button.add(new JLabel(gameState.getPlayers().get(playerID).getHand().get(i).toString()));
            south.add(button);
        }
        for (int i = 0; i < 16; i++) {
            JButton button = new JButton();
            button.add(new JLabel());
            north.add(button);
        }
        for (int i = 0; i < 16; i++) {
            JButton button = new JButton();
            button.add(new JLabel());
            east.add(button);
        }
        add(east, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        setVisible(true);
    }
}


