public class Wall {
    private int x, y;

    public Wall(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private int width = 40;  // Assuming the width of the wall is 20
    private int height = 40; // Assuming the height of the wall is 20

    public boolean isAdjacentTo(Wall other) {
        int dx = Math.abs(this.x - other.x);
        int dy = Math.abs(this.y - other.y);

        // Check if walls are horizontally or vertically adjacent
        if ((dx == 0 && dy == other.height) || (dy == 0 && dx == other.width)) {
            return true;
        }

        // Check if walls are diagonally adjacent
        if (dx == other.width && dy == other.height) {
            return true;
        }

        return false;
    }


    public void mergeWith(Wall other) {
        // Determine horizontal or vertical adjacency and merge accordingly
        int dx = Math.abs(this.x - other.x);
        int dy = Math.abs(this.y - other.y);

        if (dx == 0 && dy == this.height) {
            // Other is directly below this
            this.height += other.height;
        } else if (dy == 0 && dx == this.width) {
            // Other is directly to the right of this
            this.width += other.width;
        } else if (dx == 0 && dy == -this.height) {
            // Other is directly above this
            this.y = other.y; // Move the y position up to other's y
            this.height += other.height;
        } else if (dy == 0 && dx == -this.width) {
            // Other is directly to the left of this
            this.x = other.x; // Move the x position left to other's x
            this.width += other.width;
        }
        // If the walls are not adjacent, the method does not merge them
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
