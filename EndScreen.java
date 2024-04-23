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

    private void startGame(boolean isSinglePlayer) {
        this.dispose();
        gameFrame.dispose();
        JFrame frame = new JFrame("Star Man - A Journey through the Galaxy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 620);
        frame.add(new GameBoard(isSinglePlayer));
        frame.setVisible(true);
    }
    private void restartGame() {

            this.dispose();
            gameFrame.dispose();
            startGame(isSinglePlayer);// Restart the game from the start screen

    }

    private void returnHome() {

            this.dispose();
            gameFrame.dispose();
            new StartScreen(); // Return to start screen

    }
}
