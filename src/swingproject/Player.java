package swingproject;

import java.awt.*;

public class Player {
    private int x = 20;
    private int y = 250;

    public void draw(Graphics g, Color color) {
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(x, y - 25, 30, 50, 10, 10);
        
        g.setColor(color);
        g.fillOval(x + 5, y - 15, 20, 30);
        
        g.setColor(Color.BLACK);
        g.fillRect(x + 25, y - 5, 25, 12);
        
        g.setColor(Color.WHITE);
        g.drawOval(x + 5, y - 15, 20, 30);
    }

    public void moveUp() { if (y > 60) y -= 25; }
    public void moveDown() { if (y < 430) y += 25; }
    public int getY() { return y; }
    public int getGunX() { return x + 45; }
}