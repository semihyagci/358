import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

class GamePanel extends JPanel {
    Game gameState;
    String playerName;
    int playerIndex = -1;

    public GamePanel(Game gameState, String playerName,ObjectOutputStream os){
        this.gameState = gameState;
        this.playerName = playerName;

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
        JPanel center=new JPanel();
        center.setLayout(new FlowLayout(FlowLayout.RIGHT));
        gameState.getPlayers().get(playerIndex).getHand().sort(new CardComparator());
        for (int i = 0; i < gameState.getPlayers().get(playerIndex).getHand().size(); i++) {
            JButton button = new JButton(gameState.getPlayers().get(playerIndex).getHand().get(i).toString());
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
        List<Card> cardList=new ArrayList<>(gameState.getOnBoard().values());
        for (int i = 0; i < cardList.size(); i++) {
            JButton button = new JButton(cardList.get(i).toString());
            center.add(button);
        }
        add(east, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(center,BorderLayout.CENTER);
        setVisible(true);
    }
}


