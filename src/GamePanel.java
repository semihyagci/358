
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;

class GamePanel extends JPanel {
    private ObjectOutputStream os;

    Game gameState;
    String playerName;

    int playerIndex = -1;

    public GamePanel(Game gameState, String playerName,ObjectOutputStream os) throws IOException {
        this.gameState = gameState;
        this.playerName = playerName;
        this.os=os;

        for (int i=0;i<gameState.getPlayers().size();i++){
            if (gameState.getPlayers().get(i).getName().equals(playerName)){
                playerIndex=i;
            }
        }

        setSize(1536, 864);
        setLayout(new BorderLayout());
        JPanel east = new JPanel();
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        JPanel north = new JPanel();
        north.setLayout(new FlowLayout());
        Collections.sort(gameState.getPlayers().get(playerIndex).getHand(),new CardComparator());
        for (int i = 0; i < gameState.getPlayers().get(playerIndex).getHand().size(); i++) {
            JButton button = new JButton();
            button.add(new JLabel(gameState.getPlayers().get(playerIndex).getHand().get(i).toString()));
            button.addActionListener(new ThrowCardActionListener(gameState,os,gameState.getPlayers().get(playerIndex)));
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


