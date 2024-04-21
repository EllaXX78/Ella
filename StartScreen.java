import javax.swing.*;
import java.awt.*;

public class StartScreen extends JFrame {
    public StartScreen() {
        setTitle("Star Man - A Journey through the Galaxy");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        JButton singlePlayerButton = new JButton("Single Player");
        JButton twoPlayerButton = new JButton("Two Player");
        singlePlayerButton.addActionListener(e -> startGame(true));
        twoPlayerButton.addActionListener(e -> startGame(false));
        add(singlePlayerButton);
        add(twoPlayerButton);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startGame(boolean isSinglePlayer) {
        this.dispose();
        JFrame frame = new JFrame("Star Man - A Journey through the Galaxy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 620);
        frame.add(new GameBoard(isSinglePlayer));
        frame.setVisible(true);
    }
}