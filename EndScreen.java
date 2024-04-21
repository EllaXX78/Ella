import javax.swing.*;
import java.awt.*;

public class EndScreen extends JFrame {
    public EndScreen() {
        setTitle("Game Over");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        JButton playAgainButton = new JButton("Play Again");
        JButton homeButton = new JButton("Home");
        playAgainButton.addActionListener(e -> restartGame());
        homeButton.addActionListener(e -> returnHome());
        add(playAgainButton);
        add(homeButton);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void restartGame() {
        this.dispose();

        new StartScreen(); // Restart the game from the start screen
    }

    private void returnHome() {
        this.dispose();
        new StartScreen(); // Return to start screen
    }
}