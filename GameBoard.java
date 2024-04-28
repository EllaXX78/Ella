import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.*;

import static java.awt.Color.DARK_GRAY;

// Game panel where all drawings and game mechanics are handled
public class GameBoard extends JPanel implements KeyListener {
    private ArrayList<StarMan> players;
    private ArrayList<Ghost> ghosts;
    private ArrayList<Pebble> pebbles;
    private ArrayList<Fruit> fruits;
    private Random rand = new Random();
    private ArrayList<Wall> walls;
    private ArrayList<RocketBlock> rocketBlocks;
    private ArrayList<LavaBlock> lavaBlocks;
    private int cols;
    private int rows;
    private Set<Integer> pressedKeys = new HashSet<>();
    private boolean isSinglePlayer;
    private Camera camera1;
    private Camera camera2;
    private boolean PowerOn;
    private int TotalPoints;
    private JLabel scoreLabel;


    public GameBoard(boolean isSinglePlayer) {
	this.isSinglePlayer = isSinglePlayer;
        setFocusable(true);
        addKeyListener(this);
        rows = getHeight() / 20;  // Each cell is 20x20 pixels
        cols = getWidth() / 20;

	    scoreLabel = new JLabel("Score: 0");
	    scoreLabel.setForeground(Color.WHITE); // Set the text color
	    scoreLabel.setFont(new Font("Serif", Font.BOLD, 18)); // Set the font style and size
	    add(scoreLabel); // Add the label to the panel

        SwingUtilities.invokeLater(() -> initializeDimensions(isSinglePlayer));
    }

    private boolean isPositionOccupied(int x, int y, ArrayList<RocketBlock> rocketBlocks, ArrayList<LavaBlock> lavaBlocks, ArrayList<Wall> walls) {
        int blockSize = 20; 

        // Check against Rocket Blocks
        for (RocketBlock block : rocketBlocks) {
            if (Math.abs(block.getX() - x) < blockSize && Math.abs(block.getY() - y) < blockSize) {
                return true;
            }
        }

        // Check against Lava Blocks
        for (LavaBlock block : lavaBlocks) {
            if (Math.abs(block.getX() - x) < blockSize && Math.abs(block.getY() - y) < blockSize) {
                return true;
            }
        }

        // Check against Walls
        for (Wall wall : walls) {
            if (Math.abs(wall.getX() - x) < blockSize && Math.abs(wall.getY() - y) < blockSize) {
                return true;
            }
        }

        return false;
    }

    private void initializeDimensions(boolean isSinglePlayer) {
        rows = Math.max(1, getHeight() / 20);
        cols = Math.max(1, getWidth() / 20);
        initializeGame(isSinglePlayer);
    }




    private void generateRocketBlocks() {
        // Define the boundaries of the game area
        int minX = 0;
        int maxX = 800;
        int minY = 0;
        int maxY = 600;

        // Generate a random number of rocket blocks
        int numRocketBlocks = rand.nextInt(10) + 5;

        for (int i = 0; i < numRocketBlocks; i++) {
            int x = rand.nextInt(maxX - minX + 1) + minX;
            int y = rand.nextInt(maxY - minY + 1) + minY;

            // Ensure that the rocket block position does not overlap with existing blocks
            if (!isPositionOccupied(x, y, rocketBlocks, lavaBlocks, walls)) {
                lavaBlocks.add(new LavaBlock(x, y));
            } else {
                i--; // Decrement to retry the placement
            }
        }
    }

    private void generateLavaBlocks() {
        int minX = 0;
        int maxX = 800;
        int minY = 0;
        int maxY = 600;

        // Generate a random number of lava blocks (e.g., between 8 and 15)
        int numLavaBlocks = rand.nextInt(7) + 5;

        for (int i = 0; i < numLavaBlocks; i++) {
            int x = rand.nextInt(maxX - minX + 1) + minX;
            int y = rand.nextInt(maxY - minY + 1) + minY;

            // Ensure that the lava block position does not overlap with existing blocks
            if (!isPositionOccupied(x, y, rocketBlocks, lavaBlocks, walls)) {
                lavaBlocks.add(new LavaBlock(x, y));
            } else {
                i--; // Decrement to retry the placement
            }
        }
    }


    private void initializeGame(boolean isSinglePlayer) {
        players = new ArrayList<>();
        ghosts = new ArrayList<>();
        pebbles = new ArrayList<>();
        fruits = new ArrayList<>();
        walls = new ArrayList<>();
        rocketBlocks = new ArrayList<>();
        lavaBlocks = new ArrayList<>();
        pressedKeys = new HashSet<>();
	camera1 = new Camera(0, 0, 120, 120);
	camera2 = new Camera(0, 0, 220, 120);
	PowerOn = false;
	TotalPoints = 0;

        generateWalls();

        generateRocketBlocks();
        generateLavaBlocks();


        // Initialize players
        int startX1 = 100, startY1 = 100;
        int startX2 = 200, startY2 = 100;
        players.add(new StarMan(startX1, startY1));
        if (!isSinglePlayer) {
            players.add(new StarMan(startX2, startY2));
        }

        // Initialize ghosts with different colors
        //Color[] ghostColors = {Color.RED, Color.BLUE, Color.GREEN, Color.PINK, Color.ORANGE};
        int numberOfGhosts = isSinglePlayer ? 5 : 10;
        for (int i = 0; i < numberOfGhosts; i++) {
            int ghostX, ghostY;
            do {
                ghostX = rand.nextInt(400)+100;
                ghostY = rand.nextInt(300)+100;
            } while (collidesWithWall(ghostX, ghostY, walls));
            ghosts.add(new Ghost(ghostX, ghostY));
        }

        // Generate initial pebbles and fruits
        generatePebbles();
        generateFruits();
        startGame();
    }

    private boolean collidesWithWall(int newX, int newY, ArrayList<Wall> walls) {
        for (Wall wall : walls) {
            if (newX == wall.getX() && newY == wall.getY()) {
                return true;
            }
        }
        return false;
    }


    public void generateWalls() {
        int wallWidth = 20;
        int wallHeight = 20;
        int gameWidth = getWidth();
        int gameHeight = getHeight();

        // Generate border walls
        for (int x = 0; x < gameWidth; x += wallWidth) {
            walls.add(new Wall(x, 0)); // Top border
            walls.add(new Wall(x, gameHeight - wallHeight)); // Bottom border
        }
        for (int y = 0; y < gameHeight; y += wallHeight) {
            walls.add(new Wall(0, y)); // Left border
            walls.add(new Wall(gameWidth - wallWidth, y)); // Right border

        }

        // Generate inner walls
        boolean[][] wallPattern = {
                {false, true, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, true, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, true, true, false, false, true, true, false, false, false, true, false, false, false, false, true, true, true, true, false, false, true, true, false, false, false, false, false, true, true, true, true, false, false, false},
                {false, false, false, true, true, true, false, false, false, false, false, false, false, true, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, true, false, false, true, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, true, true, false, false, false, false, false, false, false, false, false},
                {false, false, false, true, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, true, true},
                {false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, true, false, false, false, false, true, false, false, true, true, true, false, false, false, false, false, true, true, false, false, false, false, true, false, false, true, true, true, true, false, false, false, false, false, false},
                {false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, true, false, false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, true, false, false, true, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, true, false, false, true, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, true, true, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, true, true, true, true, false, false, false, true, false, false, true, true, true, true},
                {false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true},
                {false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, true, true, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true},
                {false, false, false, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, false, false, false, false, false, true, false, false, true, true},
                {false, false, false, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, true},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, true},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, true, false, false, true, false, false, false, false},
                {false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, true, false, false, true, true, true, true, true},
                {false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, true, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, true, false, false, false, false, false, false, false, true, true, true, true, true, false, false, true, false, false, false, false, false, true, false, false, false, false, true, true, true, false, false, true, true, true, true, true},
                {false, false, false, false, false, false, false, false, false, false, true, true, false, false, true, false, false, true, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false},
                {false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}
        };

        for (int y = 0; y < wallPattern.length; y++) {
            for (int x = 0; x < wallPattern[y].length; x++) {
                if (wallPattern[y][x]) {
                    walls.add(new Wall(x * wallWidth, y * wallHeight));
                }
            }
        }
    }
	
    public void generatePebbles() {
        int wallWidth = 20;
        int wallHeight = 20;

        boolean[][] wallPattern = {
                {false, true, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, true, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, true, true, false, false, true, true, false, false, false, true, false, false, false, false, true, true, true, true, false, false, true, true, false, false, false, false, false, true, true, true, true, false, false, false},
                {false, false, false, true, true, true, false, false, false, false, false, false, false, true, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, true, false, false, true, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, true, true, false, false, false, false, false, false, false, false, false},
                {false, false, false, true, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, true, true, true},
                {false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, true, false, false, false, false, true, false, false, true, true, true, false, false, false, false, false, true, true, false, false, false, false, true, false, false, true, true, true, true, false, false, false, false, false, false},
                {false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, true, false, false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, true, false, false, true, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, true, false, false, true, false, false, false, true, false, false, false, false, false, false},
                {false, false, false, true, true, true, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, true, false, false, true, true, true, true, false, false, false, true, false, false, true, true, true, true},
                {false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true},
                {false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, true, true, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true},
                {false, false, false, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, false, false, false, false, false, true, false, false, true, true},
                {false, false, false, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, true},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, true, false, false, true, false, false, true, false, false, true, false, false, true, true},
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, true, false, false, true, false, false, false, false},
                {false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, true, false, false, true, true, true, true, true},
                {false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, true, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, true, false, false, false, false, false, false, false, true, true, true, true, true, false, false, true, false, false, false, false, false, true, false, false, false, false, true, true, true, false, false, true, true, true, true, true},
                {false, false, false, false, false, false, false, false, false, false, true, true, false, false, true, false, false, true, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false},
                {false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}
        };

        for (int y = 0; y < wallPattern.length; y+=2) {
            for (int x = 0; x < wallPattern[y].length; x+=2) {
                if (!wallPattern[y][x]) {
                    pebbles.add(new Pebble(x * (wallWidth) + 7 , y * (wallHeight)+ 7, 0, 0));
                }
            }
        }
    }

    private void generateFruits() {
        int minX = 20;
        int maxX = 780;
        int minY = 10;
        int maxY = 780;


        int numFruits = rand.nextInt(10) + 2;

        for (int i = 0; i < numFruits; i++) {
            int x = rand.nextInt(maxX - minX + 1) + minX;
            int y = rand.nextInt(maxY - minY + 1) + minY;

            // ensure that the rocket block position does not overlap with existing blocks
            if (!isPositionOccupied(x, y, rocketBlocks, lavaBlocks, walls)) {
                fruits.add(new Fruit(rand.nextInt(800), rand.nextInt(600), "Strawberry"));
                fruits.add(new Fruit(rand.nextInt(800), rand.nextInt(600), "Blueberry"));
            } else {
                i--; 
            }
        }
    }

    private void checkRocketBlockInteraction(StarMan player) {
        for (RocketBlock block : rocketBlocks) {
            if (player.getX() >= block.getX() && player.getX()<= block.getX()+20 && player.getY() >= block.getY() && player.getY() <= block.getY()+20) ) {
                int newX, newY;
                do {
                    newX = rand.nextInt(800);
                    newY = rand.nextInt(600);
                } while (collidesWithWall(newX, newY, walls));
                player.setX(newX);
                player.setY(newY);
                break;
            }
        }
    }

    private void checkLavaBlockInteraction(StarMan player) {
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), (int)player.getSize(), (int)player.getSize());
        for (LavaBlock block : lavaBlocks) {
            Rectangle lavaBlockRect = new Rectangle(block.getX(), block.getY(), 20, 20);
            if (playerRect.intersects(lavaBlockRect)) {
                JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                new EndScreen(gameFrame, isSinglePlayer);
                break;
            }
        }
    }

    private void checkCollisionWithGhosts(StarMan player) {
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), (int)player.getSize(), (int)player.getSize());
        for (Ghost ghost : ghosts) {
            Rectangle ghostRect = new Rectangle(ghost.getX(), ghost.getY(), 20, 20); // Assuming the size of the ghost is 20
            if (playerRect.intersects(ghostRect)) {
		JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                new EndScreen(gameFrame, isSinglePlayer);
                break;
            }
        }
    }

    public void startGame() {
        new Timer(100, e -> {

			camera1.update(players.get(0));
			camera2.update(players.get(1));
		
            for (Ghost ghost : ghosts) {
                ghost.moveRandomly(walls);
            }
            for (Pebble pebble : pebbles) {
                pebble.updatePosition();
            }

            for (StarMan player : players) {
                hitGhost(player.getShootingPebble());
            }
            for (Ghost ghost : ghosts) {
                ghost.updateBlink();  }
            
            
            updatePebbles();
            repaint();
        }).start();
    }

	    public void applyGraphicsTransformations(Graphics g, Camera camera1, Camera camera2) {
		int unionX = Math.min(camera1.getX(), camera2.getX());
		int unionY = Math.min(camera1.getY(), camera2.getY());
		int unionWidth = Math.max(camera1.getX() + camera1.getWidth(), camera2.getX() + camera2.getWidth()) - unionX;
		int unionHeight = Math.max(camera1.getY() + camera1.getHeight(), camera2.getY() + camera2.getHeight()) - unionY;
		
        g.clipRect(unionX, unionY, unionWidth, unionHeight);
        g.translate(-(int)0.8*unionX, -(int)0.8*unionY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);
	    
		applyGraphicsTransformations(g,camera1,camera2);
	    
        // Draw all pebbles
        for (Pebble pebble : pebbles) {
            g.setColor(Color.GRAY);
            g.fillOval(pebble.getX(), pebble.getY(), 5, 5);
        }
        
        for (StarMan playerX : players) {
            for (Pebble pebble : playerX.getShootingPebble()) {
                g.setColor(Color.WHITE);
                g.fillOval(pebble.getX(), pebble.getY(), 5, 5);

            }


        // Draw all fruits
        for (Fruit fruit : fruits) {
            if (fruit.getType().equals("Strawberry")) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.BLUE);
            }
            g.fillOval(fruit.getX(), fruit.getY(), 10, 10);
        }

        // Draw all ghosts
        for (Ghost ghost : ghosts) {
            if (ghost.isVisible()) {
                if (ghost.isBlinking()) {
                	g.setColor(Color.RED); 
                    g.fillArc(ghost.getX(), ghost.getY(), 20, 20, 0, 180); // Top half of the ghost
                    g.fillRect(ghost.getX(), ghost.getY() + 10, 20, 10); // Bottom half of the ghost
                }else {
            g.setColor(Color.magenta);
            g.fillArc(ghost.getX(), ghost.getY(), 20, 20, 0, 180); // Top half of the ghost
            g.fillRect(ghost.getX(), ghost.getY() + 10, 20, 10); // Bottom half of the ghost
            g.setColor(Color.WHITE);
            g.fillOval(ghost.getX() + 5, ghost.getY() + 5, 5, 5); // Left eye
            g.fillOval(ghost.getX() + 10, ghost.getY() + 5, 5, 5); // Right eye
        }
            }
        }

        // Draw all players
        for (StarMan player : players) {
            g.setColor(Color.YELLOW);
            Polygon starShape = player.getStarShape(player.getX(), player.getY(), (int)player.getSize() * 10);
            g.fillPolygon(starShape);
        }
        // Draw walls
        g.setColor(Color.GRAY);
        for (Wall wall : walls) {
            g.fillRect(wall.getX(), wall.getY(), 20, 20);
        }

        // Draw rocket blocks
        g.setColor(DARK_GRAY);
        for (RocketBlock rocketBlock : rocketBlocks) {
            g.fillRect(rocketBlock.getX(), rocketBlock.getY(), 20, 20);  // Size of the rocket block
        }

        // Draw lava blocks
       for (LavaBlock lavaBlock : lavaBlocks) {
            g.setColor(Color.RED);
            g.fillRect(lavaBlock.getX(), lavaBlock.getY(), 20, 20);// Size of the lava block
            g.setColor(Color.ORANGE);
            g.fillOval(lavaBlock.getX() + 5, lavaBlock.getY() + 5, 3, 3);
            g.fillOval(lavaBlock.getX() + 10, lavaBlock.getY() + 5, 5, 5);
            g.fillOval(lavaBlock.getX() + 3, lavaBlock.getY() + 15, 5, 5);
            g.fillOval(lavaBlock.getX() + 7, lavaBlock.getY() + 12, 3, 3);
            g.fillOval(lavaBlock.getX() + 12, lavaBlock.getY() + 15, 5, 5);

        }
    }

     public void updatePebbles() {
        for (StarMan player : players) {
            for (Pebble pebble : player.getShootingPebble()) {
                pebble.updatePosition();
            }
        }
    }
        
    public void hitGhost(List<Pebble> shootingPebbles) {
        Iterator<Pebble> pebbleIterator = shootingPebbles.iterator();
        while (pebbleIterator.hasNext()) {
            Pebble pebble = pebbleIterator.next();
    		Rectangle pebbleRect = new Rectangle(pebble.getX(), pebble.getY(), 5, 5); 
            Iterator<Ghost> ghostIterator = ghosts.iterator();
            boolean hitDetected = false;
            
            while (ghostIterator.hasNext()) {
                Ghost ghost = ghostIterator.next();
                Rectangle ghostRect = new Rectangle(ghost.getX(), ghost.getY(), 40, 40);
                
                if(pebbleRect.intersects(ghostRect)) {
                    ghost.hit();
                    hitDetected = true;
                    if (!ghost.isAlive()) {
                        ghostIterator.remove();
                    }
                    
                }
            }if(hitDetected) {
            	pebbleIterator.remove();
            }
        }
    }
	public boolean pebblesCollidesWithWall(Pebble pebble, List<Wall> walls) {
		Rectangle pebbleRect = new Rectangle(pebble.getX(), pebble.getY(), 5, 5);
		for (Wall wall : walls) {
			Rectangle wallRect = new Rectangle(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
			if (pebbleRect.intersects(wallRect)) {
				return true;
			}
		}
		return false;
	}

        	public void bouncePebble(Pebble pebble, ArrayList<Wall> walls) {
		if(pebble.getVx()!=0 || pebble.getVy() != 0) {
		if (pebblesCollidesWithWall(pebble, walls)) {
			System.out.println("hitWall!");
			Pebble upPebble = new Pebble(pebble.getX(), pebble.getY()-5, pebble.getVx(),pebble.getVy());
			Pebble downPebble = new Pebble(pebble.getX(), pebble.getY()+5, pebble.getVx(),pebble.getVy());
			Pebble leftPebble = new Pebble(pebble.getX()-5, pebble.getY(), pebble.getVx(),pebble.getVy());
			Pebble rightPebble = new Pebble(pebble.getX()+5, pebble.getY(), pebble.getVx(),pebble.getVy());

			// Check horizontal walls
			if ((pebblesCollidesWithWall(upPebble, walls)&&!pebblesCollidesWithWall(downPebble, walls))||(pebblesCollidesWithWall(downPebble, walls)&&!pebblesCollidesWithWall(upPebble, walls))) {
				if((pebblesCollidesWithWall(leftPebble, walls)&&!pebblesCollidesWithWall(rightPebble, walls))||(pebblesCollidesWithWall(rightPebble, walls)&&!pebblesCollidesWithWall(leftPebble, walls))) {
					pebble.setVx(-pebble.getVx());
					pebble.setVy(-pebble.getVy());
				}else {
				pebble.setVx(-pebble.getVx()); // Reverse horizontal velocity
			}
			}

			// Check vertical walls
			if ((pebblesCollidesWithWall(leftPebble, walls)&&!pebblesCollidesWithWall(rightPebble, walls))||(pebblesCollidesWithWall(rightPebble, walls)&&!pebblesCollidesWithWall(leftPebble, walls))) {
				if ((pebblesCollidesWithWall(upPebble, walls)&&!pebblesCollidesWithWall(downPebble, walls))||(pebblesCollidesWithWall(downPebble, walls)&&!pebblesCollidesWithWall(upPebble, walls))) {
					pebble.setVx(-pebble.getVx());
					pebble.setVy(-pebble.getVy());
				}else {
				pebble.setVy(-pebble.getVy()); // Reverse vertical velocity
			}
			}
			
			
		}
	}
	}

        private void updateShootingPebbles() {
        // Update positions of all shooting pebbles
        for (StarMan player : players) {
            Iterator<Pebble> it = player.getShootingPebble().iterator();
            while (it.hasNext()) {
                Pebble pebble = it.next();
                pebble.updatePosition();
                if (pebble.getX() > getWidth() || pebble.getX() < 0 || pebble.getY() > getHeight() || pebble.getY() < 0) {
                    it.remove(); // Remove pebbles that go out of bounds
                }
                    bouncePebble(pebble, walls);
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        hitGhost(pebbles);
        updateShootingPebbles();
//	camera.update(players.get(0));
        repaint();
    }
		@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		pressedKeys.add(e.getKeyCode());
		int key = e.getKeyCode();
		StarMan player1 = players.get(0);
		StarMan player2 = null; // Initialize player2 to null
		if (players.size() > 1) {
			player2 = players.get(1);
		}

		boolean left = pressedKeys.contains(KeyEvent.VK_LEFT);
		boolean right = pressedKeys.contains(KeyEvent.VK_RIGHT);
		boolean up = pressedKeys.contains(KeyEvent.VK_UP);
		boolean down = pressedKeys.contains(KeyEvent.VK_DOWN);

		if (left && up) {
			// Move left-down
			players.get(0).moveLeftUp(walls);
			
		} else if (left && down) {
			// Move left-up
			players.get(0).moveLeftDown(walls);
		} else if (right && up) {
			// Move right-down
			players.get(0).moveRightUp(walls);
		} else if (right && down) {
			// Move right-up
			players.get(0).moveRightDown(walls);
		} else if (left) {
			players.get(0).moveLeft(walls);
		} else if (right) {
			players.get(0).moveRight(walls);
		} else if (up) {
			players.get(0).moveUp(walls);
		} else if (down) {
			players.get(0).moveDown(walls);
		}
		camera1.update(players.get(0));
		if(players.get(0).getIfEat() == true) {
			TotalPoints += 10;
			scoreLabel.setText("Score: " + TotalPoints);
		}

		// Shooting pebbles
		if (key == KeyEvent.VK_SPACE) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				players.get(0).shootPebble();
				hitGhost(pebbles);
				updateShootingPebbles();
			}
		}

		// Eating pebbles
		player1.eatPebble(pebbles);
		if (player2 != null) {
			player2.eatPebble(pebbles);
		}
		// check for collision with ghosts
		if(PowerOn = false) {
		checkCollisionWithGhosts(players.get(0));
		if (players.size() > 1) {
			checkCollisionWithGhosts(players.get(1));
		}
		}

		// Eating fruits
		player1.eatFruit(fruits);
		if (player2 != null) {
			player2.eatFruit(fruits);
		}
		
		boolean left2 = pressedKeys.contains(KeyEvent.VK_A);
		boolean right2 = pressedKeys.contains(KeyEvent.VK_D);
		boolean up2 = pressedKeys.contains(KeyEvent.VK_W);
		boolean down2 = pressedKeys.contains(KeyEvent.VK_S);

		if (player2 != null) { // Add controls for player 2 if in two player mode
			if (left2 && up2) {
				// Move left-down
				players.get(1).moveLeftUp(walls);
				
			} else if (left2 && down2) {
				// Move left-up
				players.get(1).moveLeftDown(walls);
			} else if (right2 && up2) {
				// Move right-down
				players.get(1).moveRightUp(walls);
			} else if (right2 && down2) {
				// Move right-up
				players.get(1).moveRightDown(walls);
			} else if (left2) {
				players.get(1).moveLeft(walls);
			} else if (right2) {
				players.get(1).moveRight(walls);
			} else if (up2) {
				players.get(1).moveUp(walls);
			} else if (down2) {
				players.get(1).moveDown(walls);
			}
			camera2.update(players.get(1));
			if(players.get(1).getIfEat() == true) {
				TotalPoints += 10;
				scoreLabel.setText("Score: " + TotalPoints);
			}
			
			if (key == KeyEvent.VK_E) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					players.get(1).shootPebble();
					hitGhost(pebbles);
					updateShootingPebbles();
				}
			}
			
		}

        checkRocketBlockInteraction(player1);
        checkLavaBlockInteraction(player1);

        if (player2 != null) {
            checkRocketBlockInteraction(player2);
            checkLavaBlockInteraction(player2);
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
    }


}
