import javax.swing.JFrame;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Star Man - A Journey through the Galaxy");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
        setResizable(false);

        GameBoard gameBoard = new GameBoard(false); // Assume false for single player mode
        add(gameBoard);
        setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(GameFrame::new);
    }
}
