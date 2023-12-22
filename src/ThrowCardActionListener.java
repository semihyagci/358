import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ThrowCardActionListener implements ActionListener {
    private ObjectOutputStream outputStream;

    private Game gameState;


    private Player player;

    public ThrowCardActionListener(Game gameState,ObjectOutputStream os, Player player) {
        this.outputStream=os;
        this.gameState=gameState;
        this.player = player;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        String cardName=e.getActionCommand();
        Card clickedCard = createCard(cardName);
        if (player.isTurn()){
            String throwedCardName=gameState.advancedThrowCard(clickedCard,player);
            Card throwedCard = createCard(throwedCardName);
            try {
                outputStream.writeObject(throwedCard);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            JButton pressedButton = (JButton) e.getSource();
            Container parent = pressedButton.getParent();
            parent.remove(pressedButton);
            parent.revalidate();
            parent.repaint();
        }
    }

    public Card createCard(String cardName){
        String suit;
        String rank;
        if (cardName.length()==3){
            suit= cardName.substring(0,1);
            rank= cardName.substring(1,3);
        }else {
            suit= cardName.substring(0,1);
            rank= cardName.substring(1,2);
        }
        switch (rank){
            case "J" -> rank = "Jack";
            case "A" -> rank = "Ace";
            case "K" -> rank = "King";
            case "Q" -> rank = "Queen";
        }
        switch (suit){
            case "C" -> suit = "clubs";
            case "D" -> suit = "diamonds";
            case "H" -> suit = "hearts";
            case "S" -> suit = "spades";
        }
        Card throwedCard = new Card(suit,rank);
        return throwedCard;
    }
}