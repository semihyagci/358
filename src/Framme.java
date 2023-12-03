
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Framme {
    public static void main(String[] args) throws IOException {
        SimpleFrame frame = new SimpleFrame();
    }

}

class SimpleFrame extends JFrame {
    public SimpleFrame() throws IOException {
        setTitle("SE360 Project");
        setSize(1536, 864);
        setLayout(new BorderLayout());
        JPanel east = new JPanel();
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        JPanel north = new JPanel();
        north.setLayout(new FlowLayout());
        for (int i = 0; i < 16; i++) {
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}


