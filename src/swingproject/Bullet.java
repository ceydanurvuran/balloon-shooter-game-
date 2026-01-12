package swingproject;

import java.awt.*;

public class Bullet {
    private int x, y;
    private final Color color;
    private final int SPEED = 22;

    public Bullet(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void move() {
        x += SPEED;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y - 4, 14, 8);
        g.setColor(Color.WHITE);
        g.drawOval(x, y - 4, 14, 8);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y - 4, 14, 8);
    }

    public int getX() {
        return x;
    }

    public Color getColor() {
        return color;
    }
}