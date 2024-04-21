public class Pebble {
    private int x, y; // Position of the pebble
    private int vx, vy; // Velocity of the Pebble
    public Pebble(int x, int y, int vx, int vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void updatePosition() {
        x += vx;
        y += vy;
    }
    // Getter methods for the position of the pebble
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
