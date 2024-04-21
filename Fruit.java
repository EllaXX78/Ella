public class Fruit {
    private int x, y; // Position of the fruit
    private String type; // Type of fruit ("Strawberry" or "Blueberry")

    public Fruit(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    // Getter methods for the position and type of the fruit
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }
}
