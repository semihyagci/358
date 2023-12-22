import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ThrowCardActionListener implements ActionListener {
    private DataOutputStream outputStream;

    private Player player;

    public ThrowCardActionListener(DataOutputStream os, Player player) {
        this.outputStream=os;
        this.player = player;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        String cardName=e.getActionCommand();
        if (player.isTurn()){
            try {
                outputStream.writeUTF(cardName);
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
}