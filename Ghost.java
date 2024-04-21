import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Ghost {
    private int x, y; // Position of the Ghost
    private int hitPoints; // Hit points to track damage from pebbles
    private static final Random rand = new Random();

    public Ghost(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.hitPoints = 5; // Ghosts need to be hit with 5 pebbles to die
    }

    // Move the ghost in a random direction
    public void moveRandomly(ArrayList<Wall> walls) {
        int direction = rand.nextInt(10); // Random number between 0 and 3
        int newX = x, newY = y;

        switch (direction) {
            case 0: // Move left
                newX -= 10;
                break;
            case 1: // Move right
                newX += 10;
                break;
            case 2: // Move up
                newY -= 10;
                break;
            case 3: // Move down
                newY += 10;
                break;
        }

        // Only update the position if it doesn't result in a collision
        if (!collidesWithWall(newX, newY, walls)) {
            x = newX;
            y = newY;
        }

    }

    // Check if moving to a new position collides with a wall
    private boolean collidesWithWall(int newX, int newY, ArrayList<Wall> walls) {
        int ghostSize = 15; // Assuming the size of the ghost is 10
        for (Wall wall : walls) {
            if ((newX < wall.getX() + wall.getWidth() && newX + ghostSize > wall.getX() && newY < wall.getY() + wall.getHeight() && newY + ghostSize > wall.getY()) ||
                    (newX + ghostSize > wall.getX() && newX + ghostSize < wall.getX() + wall.getWidth() && newY + ghostSize > wall.getY() && newY + ghostSize < wall.getY() + wall.getHeight()) ||
                    (newX + ghostSize > wall.getX() && newX + ghostSize < wall.getX() + wall.getWidth() && newY < wall.getY() + wall.getHeight() && newY + ghostSize > wall.getY()) ||
                    (newX < wall.getX() + wall.getWidth() && newX + ghostSize > wall.getX() && newY + ghostSize > wall.getY() && newY + ghostSize < wall.getY() + wall.getHeight())) {
                return true;
            }
        }
        return false;
    }

    // Ghost gets hit by a pebble
    public void hitByPebble() {
        if (hitPoints > 0) {
            hitPoints--;
        }
    }

    // Check if the ghost is alive
    public boolean isAlive() {
        return hitPoints > 0;
    }

    // Getters for position and color
    public int getX() { return x; }
    public int getY() { return y; }

    // Setters for position (might be needed for resetting or special effects)
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}