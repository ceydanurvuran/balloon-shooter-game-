package swingproject;

import java.awt.*;
import java.util.Random;

public class BossBalloon extends Balloon {
    private int health;
    private boolean isShielded = false;
    private int shieldTimer = 0;
    private int attackCooldown = 0;
    private final Random rand = new Random();
    private int moveDirection = 0;
    private int directionTimer = 0;
    private int phase = 1;
    private boolean defeated = false;
    private boolean phaseJustAdvanced = false;
    
    public BossBalloon(int x, int y, int level) {
        super(x, y, Type.NORMAL, level);
        setupPhase();
        this.radius = 50;
        this.balloonColor = new Color(128, 0, 128);
        this.colorWord = "BOSS";
        this.targetColor = Color.RED;
        this.baseSpeed = 0.3;
    }
    
    @Override
    public void move(int level) {
        super.move(level);
        
        if (shieldTimer > 0) {
            shieldTimer--;
            if (shieldTimer == 0) {
                isShielded = false;
            }
        }
        
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        
        directionTimer--;
        if (directionTimer <= 0) {
            moveDirection = rand.nextInt(3) - 1;
            directionTimer = 30 + rand.nextInt(60);
        }
        
        if (moveDirection == 1 && y > 80) {
            y -= 1.5;
        } else if (moveDirection == -1 && y < 380) {
            y += 1.5;
        }
        
        if (!isShielded && rand.nextInt(300) == 0) {
            isShielded = true;
            shieldTimer = 80 + rand.nextInt(80);
        }
    }
    
    @Override
    public void draw(Graphics g) {
        Color bossColor = isShielded ? new Color(0, 150, 255, 200) : 
                          new Color(128, 0, 128, 220);
        g.setColor(bossColor);
        g.fillOval((int) x, (int) y, radius * 2, radius * 2);
        
        g.setColor(Color.YELLOW);
        g.drawOval((int) x + 10, (int) y + 10, radius * 2 - 20, radius * 2 - 20);
        g.drawOval((int) x + 20, (int) y + 20, radius * 2 - 40, radius * 2 - 40);
        
        g.setColor(Color.WHITE);
        g.fillOval((int) x + 25, (int) y + 25, 15, 20);
        g.fillOval((int) x + 60, (int) y + 25, 15, 20);
        g.setColor(Color.RED);
        g.fillOval((int) x + 28, (int) y + 30, 8, 10);
        g.fillOval((int) x + 63, (int) y + 30, 8, 10);
        
        g.setColor(Color.RED);
        g.fillArc((int) x + 35, (int) y + 50, 30, 20, 0, 180);
        
        if (isShielded) {
            g.setColor(new Color(0, 200, 255, 80));
            g.fillOval((int) x - 10, (int) y - 10, radius * 2 + 20, radius * 2 + 20);
            g.setColor(new Color(0, 200, 255, 150));
            g.drawOval((int) x - 10, (int) y - 10, radius * 2 + 20, radius * 2 + 20);
        }
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("PHASE " + phase, (int) x + radius - 30, (int) y + radius - 8);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(operation, (int) x + radius - 25, (int) y + radius + 16);
        
        drawHealthBar(g);
    }
    
    private void drawHealthBar(Graphics g) {
        int barWidth = 120;
        int barHeight = 12;
        int barX = (int) x + radius - barWidth/2;
        int barY = (int) y - 25;
        
        g.setColor(Color.GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);
        
        float healthPercent = (float) health / (float) value;
        Color healthColor;
        if (healthPercent > 0.6) {
            healthColor = Color.GREEN;
        } else if (healthPercent > 0.3) {
            healthColor = Color.YELLOW;
        } else {
            healthColor = Color.RED;
        }
        
        g.setColor(healthColor);
        g.fillRect(barX, barY, (int)(barWidth * healthPercent), barHeight);
        
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        String healthText = health + "/" + value;
        int textWidth = g.getFontMetrics().stringWidth(healthText);
        g.drawString(healthText, barX + barWidth/2 - textWidth/2, barY - 3);
    }
    
    @Override
    public void hit() {
        if (!isShielded) {
            health--;
        }
        balloonColor = Color.RED;
        if (health <= 0 && !defeated) {
            if (phase < 3) {
                phase++;
                setupPhase();
                phaseJustAdvanced = true;
            } else {
                defeated = true;
            }
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return defeated;
    }
    
    public int getHealth() {
        return health;
    }

    public int getPhase() {
        return phase;
    }

    public boolean consumePhaseAdvance() {
        if (phaseJustAdvanced) {
            phaseJustAdvanced = false;
            return true;
        }
        return false;
    }
    
    public boolean isShielded() {
        return isShielded;
    }
    
    public void resetColor() {
        this.balloonColor = new Color(128, 0, 128);
    }
    
    @Override
    public void escape() {
        x += 100;
    }

    private void setupPhase() {
        int a;
        int b;
        if (phase == 1) {
            a = rand.nextInt(3) + 2;
            b = rand.nextInt(3) + 1;
            value = a + b;
            operation = a + " + " + b;
        } else if (phase == 2) {
            a = rand.nextInt(3) + 2;
            b = rand.nextInt(3) + 2;
            value = a * b;
            operation = a + " x " + b;
        } else {
            a = rand.nextInt(4) + 3;
            b = rand.nextInt(4) + 1;
            value = a + b;
            operation = a + " + " + b;
        }
        health = value;
    }
}
