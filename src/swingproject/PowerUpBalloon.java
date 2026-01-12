package swingproject;

import java.awt.*;
import java.util.Random;

public class PowerUpBalloon extends Balloon {
    public enum PowerType {
        RAPID_FIRE, DOUBLE_POINTS, EXTRA_LIFE, 
        SLOW_TIME, SHIELD, INFINITE_AMMO
    }
    
    private PowerType powerType;
    private final Random rand = new Random();
    private float pulse = 0;
    private boolean collected = false;
    
    public PowerUpBalloon(int x, int y, int level) {
        super(x, y, Type.NORMAL, level);
        
        PowerType[] types = PowerType.values();
        this.powerType = types[rand.nextInt(types.length)];
        
        switch (powerType) {
            case RAPID_FIRE:
                this.colorWord = "RAPID";
                this.targetColor = Color.YELLOW;
                this.balloonColor = Color.YELLOW;
                this.operation = "⚡";
                this.value = 1;
                break;
            case DOUBLE_POINTS:
                this.colorWord = "DOUBLE";
                this.targetColor = Color.GREEN;
                this.balloonColor = Color.GREEN;
                this.operation = "2×";
                this.value = 1;
                break;
            case EXTRA_LIFE:
                this.colorWord = "LIFE";
                this.targetColor = Color.RED;
                this.balloonColor = Color.RED;
                this.operation = "❤";
                this.value = 1;
                break;
            case SLOW_TIME:
                this.colorWord = "SLOW";
                this.targetColor = Color.CYAN;
                this.balloonColor = Color.CYAN;
                this.operation = "⏱";
                this.value = 1;
                break;
            case SHIELD:
                this.colorWord = "SHIELD";
                this.targetColor = Color.BLUE;
                this.balloonColor = Color.BLUE;
                this.operation = "🛡";
                this.value = 1;
                break;
            case INFINITE_AMMO:
                this.colorWord = "AMMO∞";
                this.targetColor = Color.ORANGE;
                this.balloonColor = Color.ORANGE;
                this.operation = "∞";
                this.value = 1;
                break;
        }
        
        this.baseSpeed = 0.5;
    }
    
    @Override
    public void move(int level) {
        super.move(level);
        pulse += 0.15;
        y = startY + Math.sin(pulse * 0.5) * 15;
    }
    
    @Override
    public void draw(Graphics g) {
        int pulseSize = (int)(Math.sin(pulse) * 8);
        int glowSize = pulseSize + 5;
        
        for (int i = 3; i > 0; i--) {
            int size = glowSize + i * 3;
            float alpha = 0.2f - i * 0.05f;
            Color glowColor = new Color(
                balloonColor.getRed(),
                balloonColor.getGreen(),
                balloonColor.getBlue(),
                (int)(alpha * 255)
            );
            g.setColor(glowColor);
            g.fillOval((int) x - size/2, (int) y - size/2, 
                      radius * 2 + size, radius * 2 + size);
        }
        
        g.setColor(balloonColor);
        g.fillOval((int) x, (int) y, radius * 2, radius * 2);
        
        g.setColor(new Color(255, 255, 255, 150));
        g.fillOval((int) x + 8, (int) y + 8, radius * 2 - 16, radius * 2 - 16);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        
        String symbol = operation;
        int symbolWidth = g.getFontMetrics().stringWidth(symbol);
        g.drawString(symbol, (int) x + radius - symbolWidth/2, (int) y + radius + 8);
        
        g.setFont(new Font("Arial", Font.BOLD, 12));
        int textWidth = g.getFontMetrics().stringWidth(colorWord);
        g.drawString(colorWord, (int) x + radius - textWidth/2, (int) y + radius * 2 - 5);
        
        g.setColor(new Color(255, 215, 0));
        g.drawOval((int) x - 2, (int) y - 2, radius * 2 + 4, radius * 2 + 4);
        
        g.setColor(new Color(255, 255, 200));
        g.drawLine((int) x + radius, (int) y + radius * 2, 
                  (int) x + radius, (int) y + radius * 2 + 25);
    }
    
    @Override
    public void escape() {
        x += 0;
    }
    
    @Override
    public void hit() {
        collected = true;
        value = 0;
    }
    
    @Override
    public boolean isDestroyed() {
        return collected;
    }
    
    public PowerType getPowerType() {
        return powerType;
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public int getDuration() {
        switch (powerType) {
            case RAPID_FIRE: return 300;
            case DOUBLE_POINTS: return 450;
            case SLOW_TIME: return 240;
            case SHIELD: return 360;
            case INFINITE_AMMO: return 180;
            default: return 0;
        }
    }
    
    public String getDescription() {
        switch (powerType) {
            case RAPID_FIRE: return "Rapid Fire: Shoot twice as fast!";
            case DOUBLE_POINTS: return "Double Points: Earn double points!";
            case EXTRA_LIFE: return "Extra Life: Gain +1 life!";
            case SLOW_TIME: return "Slow Time: Balloons move slower!";
            case SHIELD: return "Shield: Temporary invincibility!";
            case INFINITE_AMMO: return "Infinite Ammo: No ammo cost for shooting!";
            default: return "Unknown Power-up";
        }
    }
}