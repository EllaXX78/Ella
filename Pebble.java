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

    	public int getVx() {
		return vx;
	}

	public int getVy() {
		return vy;
	}
	
	public void setVx(int vx) {
		this.vx = vx;
	}
	
	public void setVy(int vy) {
		this.vy = vy;
	}

}
