import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Polygon;

public class StarMan {
    private int x, y; // Position of StarMan
    private double size; // Size of StarMan, affects speed and ability to shoot pebbles
    private int health; // Health points of StarMan
    private int pebbleCount; // Number of pebbles StarMan can shoot
    private static final int MAX_SIZE = 5; // Size of the StarMan

    public Polygon getStarShape(int x, int y, int size) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        int startAngle = 270; // Start at the top

        for (int i = 0; i < 10; i++) {
            double angle = Math.toRadians(startAngle + (i * 36));
            int radius = (i % 2 == 0) ? size : size / 2;
            xPoints[i] = (int) (x + radius * Math.cos(angle));
            yPoints[i] = (int) (y + radius * Math.sin(angle));
        }

        return new Polygon(xPoints, yPoints, 10);
    }
    public StarMan(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.size = 1; // Initial size
        this.health = 3; // Initial health
        this.pebbleCount = 0; // Initial pebbles
    }

    // StarMan's movement methods
    public void moveLeft(ArrayList<Wall> walls) {
        int newX = x - 10;
        if (!collidesWithWall(newX, y, walls)) {
            x = newX;
        }
    }

    public void moveRight(ArrayList<Wall> walls) {
        int newX = x + 10;
        if (!collidesWithWall(newX, y, walls)) {
            x = newX;
        }
    }

    public void moveUp(ArrayList<Wall> walls) {
        int newY = y - 10;
        if (!collidesWithWall(x, newY, walls)) {
            y = newY;
        }
    }

    public void moveDown(ArrayList<Wall> walls) {
        int newY = y + 10;
        if (!collidesWithWall(x, newY, walls)) {
            y = newY;
        }
    }

    // Checking for wall collisions
    private boolean collidesWithWall(int newX, int newY, ArrayList<Wall> walls) {
        int starManSize = 10; // Assuming the size of StarMan is 10
        for (Wall wall : walls) {
            if ((newX < wall.getX() + wall.getWidth() && newX + starManSize > wall.getX() && newY < wall.getY() + wall.getHeight() && newY + starManSize > wall.getY()) ||
                    (newX + starManSize > wall.getX() && newX + starManSize < wall.getX() + wall.getWidth() && newY + starManSize > wall.getY() && newY + starManSize < wall.getY() + wall.getHeight()) ||
                    (newX + starManSize > wall.getX() && newX + starManSize < wall.getX() + wall.getWidth() && newY < wall.getY() + wall.getHeight() && newY + starManSize > wall.getY()) ||
                    (newX < wall.getX() + wall.getWidth() && newX + starManSize > wall.getX() && newY + starManSize > wall.getY() && newY + starManSize < wall.getY() + wall.getHeight())) {
                return true;
            }
        }
        return false;
    }

    // Method to eat pebbles and grow
    public void eatPebble(ArrayList<Pebble> pebbles) {
        Iterator<Pebble> it = pebbles.iterator();
        while (it.hasNext()) {
            Pebble pebble = it.next();
            if (Math.abs(x - pebble.getX()) <= 15 && Math.abs(y - pebble.getY()) <= 15){
                it.remove();
                size += 0.5;
                pebbleCount++;
                if (size > MAX_SIZE) {
                    size = MAX_SIZE;
                }
                break;
            }
        }
    }

    // Shooting pebbles
    public void shootPebble(ArrayList<Pebble> pebbles) {
        if (pebbleCount > 0) {
            int pebbleSpeed = 10; // Speed of the pebble
            pebbles.add(new Pebble(x, y, pebbleSpeed, 0)); // Adding a new pebble at StarMan's position
            pebbleCount--;
            size--; // Decrease size when shooting a pebble
        }
    }

    // Interaction with fruits
    public void eatFruit(ArrayList<Fruit> fruits) {
        Iterator<Fruit> it = fruits.iterator();
        while (it.hasNext()) {
            Fruit fruit = it.next();
            if (x == fruit.getX() && y == fruit.getY()) {
                applyFruitEffect(fruit.getType());
                it.remove();
                break;
            }
        }
    }

    // Apply the effects of the fruit
    private void applyFruitEffect(String type) {
        if ("Strawberry".equals(type)) {
            health++;
        } else if ("Blueberry".equals(type)) {
            // add implementation of temporary invincibility or other effects
        }
    }

    // Decrease health if attacked or steps on lava
    public void decreaseHealth() {
        health--;
    }

    // Getter and Setter methods
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public double getSize() { return size; }
    public int getHealth() { return health; }
}
