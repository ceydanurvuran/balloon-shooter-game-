package swingproject;

import java.awt.*;
import java.util.Random;

public class Balloon {
    public enum Type { NORMAL, AMMO, BOMB }
    
    protected double x, y;
    protected int startY, value;
    protected String operation, colorWord;
    protected Color balloonColor, targetColor;
    protected Type type;
    protected double angle;
    protected int radius = 30;
    protected final Random rand = new Random();
    protected double baseSpeed = 1.0;
    protected boolean zeroValueOperation = false;

    
    public Balloon(int x, int y, Type type, int level) {
        this.x = x; 
        this.y = y; 
        this.startY = y; 
        this.type = type;
        this.angle = rand.nextDouble() * Math.PI * 2;
        this.baseSpeed = 0.8 + rand.nextDouble() * 0.4;

        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        String[] names = {"RED", "BLUE", "GREEN", "YELLOW"};
        
        int colorIndex = rand.nextInt(colors.length);
        this.balloonColor = colors[colorIndex];
        
        int wordIndex;
        do {
            wordIndex = rand.nextInt(names.length);
        } while (wordIndex == colorIndex && rand.nextDouble() < 0.7);
        
        this.colorWord = names[wordIndex];
        this.targetColor = colors[wordIndex];

        if (type == Type.NORMAL) {
            generateComplexOperation(level);
        } else if (type == Type.AMMO) {
            value = 1;
            operation = "AMMO";
            balloonColor = Color.ORANGE;
            colorWord = "AMMO";
            targetColor = Color.ORANGE;
        } else if (type == Type.BOMB) {
            value = 1;
            operation = "BOMB";
            balloonColor = Color.BLACK;
            colorWord = "BOMB";
            targetColor = Color.BLACK;
        }
    }
    
    private void generateComplexOperation(int level) {
        int maxResult = 15;
        
        if (level <= 3) {
            generateSimpleOperation();
        } else if (level <= 6) {
            if (rand.nextBoolean()) {
                generateMediumOperation();
            } else {
                generateSimpleOperation();
            }
        } else {
            int choice = rand.nextInt(3);
            switch (choice) {
                case 0: generateSimpleOperation(); break;
                case 1: generateMediumOperation(); break;
                case 2: generateHardOperation(); break;
            }
        }
        
        if (value > maxResult) {
            value = maxResult;
        }
        if (value == 0) {
            zeroValueOperation = true;
            value = 1;
        } else {
            zeroValueOperation = false;
        }
    }
    
    private void generateSimpleOperation() {
        int a, b;
        do {
            a = rand.nextInt(10) + 1;
            b = rand.nextInt(10) + 1;
            
            if (rand.nextBoolean()) {
                value = a + b;
                operation = a + " + " + b;
            } else {
                if (a < b) { int t = a; a = b; b = t; }
                value = a - b;
                operation = a + " - " + b;
            }
        } while (value > 15);
    }
    
    private void generateMediumOperation() {
        int a = rand.nextInt(5) + 1;
        int b = rand.nextInt(5) + 1;
        int c = rand.nextInt(5) + 1;
        
        int pattern = rand.nextInt(4);
        switch (pattern) {
            case 0:
                value = (a + b) * c;
                operation = "(" + a + " + " + b + ") × " + c;
                break;
            case 1:
                value = a * b + c;
                operation = a + " × " + b + " + " + c;
                break;
            case 2:
                if (b > c) {
                    value = a * (b - c);
                    operation = a + " × (" + b + " - " + c + ")";
                } else {
                    value = a * (c - b);
                    operation = a + " × (" + c + " - " + b + ")";
                }
                break;
            case 3:
                if (a > b) {
                    value = (a - b) * c;
                    operation = "(" + a + " - " + b + ") × " + c;
                } else {
                    value = (b - a) * c;
                    operation = "(" + b + " - " + a + ") × " + c;
                }
                break;
        }
        
        if (value > 15 || value < 0) {
            generateSimpleOperation();
        }
    }
    
    private void generateHardOperation() {
        int a = rand.nextInt(4) + 1;
        int b = rand.nextInt(4) + 1;
        int c = rand.nextInt(4) + 1;
        
        int pattern = rand.nextInt(3);
        switch (pattern) {
            case 0:
                value = a * b - c;
                operation = a + " × " + b + " - " + c;
                break;
            case 1:
                value = a + b * c;
                operation = a + " + " + b + " × " + c;
                break;
            case 2:
                do {
                    a = rand.nextInt(8) + 1;
                    b = rand.nextInt(8) + 1;
                    c = rand.nextInt(4) + 2;
                } while ((a + b) % c != 0);
                value = (a + b) / c;
                operation = "(" + a + " + " + b + ") ÷ " + c;
                break;
        }
        
        if (value > 15 || value <= 0) {
            generateMediumOperation();
        }
    }
    
    public void move(int level) {
        double speed = baseSpeed;
        speed *= (0.8 + level * 0.15);
        x -= speed;
        y = startY + Math.sin(angle) * 25;
        angle += 0.05;
    }
    
    public void draw(Graphics g) {
        // Balon gövdesi
        if (type == Type.BOMB) {
            g.setColor(Color.BLACK);
        } else if (type == Type.AMMO) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(balloonColor);
        }
        
        g.fillOval((int) x, (int) y, radius * 2, radius * 2);
        
        // Balon aydınlatma efekti (daha sade)
        g.setColor(new Color(255, 255, 255, 70));
        g.fillOval((int) x + 8, (int) y + 8, radius * 2 - 16, radius * 2 - 16);
        
        // Balon ipi
        g.setColor(new Color(139, 69, 19));
        g.drawLine((int) x + radius, (int) y + radius * 2, 
                  (int) x + radius, (int) y + radius * 2 + 20);
        
        // İçerik - HC/HP YAZISI KALDIRILDI
        g.setColor(Color.WHITE);
        if (type == Type.NORMAL) {
            // İşlem (daha büyük ve ortada)
            g.setFont(new Font("Arial", Font.BOLD, 14));
            int opWidth = g.getFontMetrics().stringWidth(operation);
            g.drawString(operation, (int) x + radius - opWidth/2, (int) y + 28);
            
            // Renk kelimesi (daha büyük ve alt kısımda)
            g.setFont(new Font("Arial", Font.BOLD, 16));
            int wordWidth = g.getFontMetrics().stringWidth(colorWord);
            g.drawString(colorWord, (int) x + radius - wordWidth/2, (int) y + 50);
            
            // HP yazısı KALDIRILDI - bu satırı sildim
            // if (value > 1) {
            //     g.setFont(new Font("Arial", Font.BOLD, 10));
            //     g.drawString("HP: " + value, (int) x + 10, (int) y + 60);
            // }
        } else {
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String text = type.toString();
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (int) x + radius - textWidth/2, (int) y + 38);
        }
    }
    
    public void escape() { 
        x += 80 + rand.nextInt(40); 
    }
    
    public void hit() { 
        value--; 
        if (value > 0) {
            x += rand.nextInt(5) - 2;
            y += rand.nextInt(5) - 2;
        }
    }

    public void randomTransform() {
        if (type != Type.NORMAL) {
            return;
        }
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        String[] names = {"RED", "BLUE", "GREEN", "YELLOW"};

        int colorIndex = rand.nextInt(colors.length);
        this.balloonColor = colors[colorIndex];

        int wordIndex;
        do {
            wordIndex = rand.nextInt(names.length);
        } while (wordIndex == colorIndex && rand.nextDouble() < 0.7);

        this.colorWord = names[wordIndex];
        this.targetColor = colors[wordIndex];
    }
    
    public boolean isDestroyed() { 
        return value <= 0; 
    }
    
    public java.awt.Rectangle getBounds() { 
        return new java.awt.Rectangle((int) x, (int) y, radius * 2, radius * 2); 
    }
    
    public Color getTargetColor() { 
        return targetColor; 
    }
    
    public int getX() { 
        return (int) x; 
    }
    
    public int getY() { 
        return (int) y; 
    }
    
    public Type getType() { 
        return type; 
    }
    
    public int getValue() { 
        return value; 
    }

    public boolean isZeroValueOperation() {
        return zeroValueOperation;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
}
