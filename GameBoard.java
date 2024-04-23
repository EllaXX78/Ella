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


    public GameBoard(boolean isSinglePlayer) {
        setFocusable(true);
        addKeyListener(this);
        rows = getHeight() / 20;  // Each cell is 20x20 pixels
        cols = getWidth() / 20;

        SwingUtilities.invokeLater(() -> initializeDimensions(isSinglePlayer));
    }

    private boolean isPositionOccupied(int x, int y, ArrayList<RocketBlock> rocketBlocks, ArrayList<LavaBlock> lavaBlocks, ArrayList<Wall> walls) {
        int blockSize = 20; // Assuming each block has a size of 20x20

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
                ghostX = rand.nextInt(800);
                ghostY = rand.nextInt(600);
            } while (collidesWithWall(ghostX, ghostY, walls));
            ghosts.add(new Ghost(ghostX, ghostY));
        }

        // Generate initial pebbles and fruits
        generatePebbles(getWidth(), getHeight());
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
    }
    public void generatePebbles(int boardWidth, int boardHeight) {
        int[][] map = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        int wallWidth = boardWidth / map[0].length;
        int wallHeight = boardHeight / map.length;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 0) {
                    pebbles.add(new Pebble(j * wallWidth + wallWidth / 2, i * wallHeight + wallHeight / 2, 0, 0));
                }
            }
        }
    }

    private void generateFruits() {
        fruits.add(new Fruit(rand.nextInt(800), rand.nextInt(600), "Strawberry"));
        fruits.add(new Fruit(rand.nextInt(800), rand.nextInt(600), "Blueberry"));
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
        for (LavaBlock block : lavaBlocks) {
            if ((player.getX() == block.getX()) && (player.getY() == block.getY())) {
                player.decreaseHealth();
                break;
            }
        }
    }

    private void checkCollisionWithGhosts(StarMan player) {
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), (int)player.getSize(), (int)player.getSize());
        for (Ghost ghost : ghosts) {
            Rectangle ghostRect = new Rectangle(ghost.getX(), ghost.getY(), 20, 20); // Assuming the size of the ghost is 20
            if (playerRect.intersects(ghostRect)) {

                new EndScreen(); // Pass the game frame to the end screen
                break;
            }
        }
    }

    public void startGame() {
        new Timer(100, e -> {
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
            
            for (Pebble pebble : pebbles) {
                bouncePebble(pebble, walls);
            }
            
            updatePebbles();
            repaint();
        }).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);
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
        g.setColor(Color.RED);
        for (LavaBlock lavaBlock : lavaBlocks) {
            g.fillRect(lavaBlock.getX(), lavaBlock.getY(), 20, 20);  // Size of the lava block
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
            Rectangle wallRect = new Rectangle(wall.getX(), wall.getY(), 20, 20);
            if (pebbleRect.intersects(wallRect)) {
                return true;
            }
        }
        return false;
    }

         public void bouncePebble(Pebble pebble, ArrayList<Wall> walls) {
        if (pebblesCollidesWithWall(pebble, walls)) {
            // Check which sides of the pebble the walls are on to determine how to bounce
            int futureX = pebble.getX() + pebble.getVx();
            int futureY = pebble.getY() + pebble.getVy();

            // Check vertical walls
            if (pebblesCollidesWithWall(new Pebble(futureX, pebble.getY(), pebble.getVx(), pebble.getVy()), walls)) {
                pebble.setVx(-pebble.getVx()); // Reverse horizontal velocity
            }

            // Check horizontal walls (top or bottom collision)
            if (pebblesCollidesWithWall(new Pebble(pebble.getX(), futureY, pebble.getVx(), pebble.getVy()), walls)) {
                pebble.setVy(-pebble.getVy()); // Reverse vertical velocity
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
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

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

        // Shooting pebbles
        if (key == KeyEvent.VK_SPACE) {
        	if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        		players.get(0).shootPebble(pebbles);
        		hitGhost(pebbles);
        }
        	}

        // Eating pebbles
        player1.eatPebble(pebbles);
        if (player2 != null) {
            player2.eatPebble(pebbles);
        }
        //check for collision with ghosts
        checkCollisionWithGhosts(players.get(0));
        if (players.size() > 1) {
            checkCollisionWithGhosts(players.get(1));
        }

        // Eating fruits
        player1.eatFruit(fruits);
        if (player2 != null) {
            player2.eatFruit(fruits);
        }

        if (player2 != null) {    // Add controls for player 2 if in two player mode
            switch (key) {
                case KeyEvent.VK_A:
                    player2.moveLeft(walls);
                    break;
                case KeyEvent.VK_D:
                    player2.moveRight(walls);
                    break;
                case KeyEvent.VK_W:
                    player2.moveUp(walls);
                    break;
                case KeyEvent.VK_S:
                    player2.moveDown(walls);
                    break;
                case KeyEvent.VK_E:
                    player2.shootPebble(pebbles);
                    break;
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
