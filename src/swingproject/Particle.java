package swingproject;

import java.awt.*;
import java.util.Random;

public class Particle {
    private double x, y;
    private double vx, vy;
    private Color color;
    private int life;
    private final int maxLife;
    private final Random rand = new Random();
    private final int size;
    private boolean small;
    
    public Particle(double x, double y, Color color) {
        this(x, y, color, false);
    }
    
    public Particle(double x, double y, Color color, boolean small) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.small = small;
        
        if (small) {
            this.maxLife = 20 + rand.nextInt(20);
            this.size = 1 + rand.nextInt(3);
        } else {
            this.maxLife = 30 + rand.nextInt(30);
            this.size = 2 + rand.nextInt(5);
        }
        
        this.life = maxLife;
        
        double angle = rand.nextDouble() * Math.PI * 2;
        double speed = small ? 0.5 + rand.nextDouble() * 2 : 1 + rand.nextDouble() * 4;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
        
        this.vy -= 0.1;
    }
    
    public void update() {
        x += vx;
        y += vy;
        vy += 0.05;
        vx *= 0.97;
        vy *= 0.97;
        life--;
    }
    
    public void draw(Graphics g) {
        if (life <= 0) return;
        
        float alpha = (float) life / maxLife;
        Color particleColor = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            (int)(alpha * 200)
        );
        
        g.setColor(particleColor);
        g.fillOval((int)x, (int)y, size, size);
        
        if (!small && rand.nextDouble() < 0.3) {
            g.setColor(new Color(255, 255, 255, (int)(alpha * 100)));
            g.fillOval((int)x - 1, (int)y - 1, size + 2, size + 2);
        }
    }
    
    public boolean isAlive() {
        return life > 0;
    }
}