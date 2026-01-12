package swingproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
import java.util.List;

public class GamePanel extends JPanel {
    private final ArrayList<Balloon> balloons = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final Player player = new Player();
    private int ammo = 30, score = 0, level = 1, lives = 3;
    private boolean gameOver = false, showInstructions = true, waitingForKeyPress = false;
    private Color currentBulletColor = Color.BLUE;
    private final Random rand = new Random();

    // Power-up durumları
    private boolean rapidFire = false;
    private int rapidFireTimer = 0;
    private boolean doublePoints = false;
    private int doublePointsTimer = 0;
    private boolean infiniteAmmo = false;
    private int infiniteAmmoTimer = 0;
    private boolean slowTime = false;
    private int slowTimeTimer = 0;
    private boolean shieldActive = false;
    private int shieldTimer = 0;

    // Level geçiş efekti
    private boolean levelTransition = false;
    private int transitionTimer = 0;
    private String transitionMessage = "";

    // Power-up mesajı
    private String powerUpMessage = "";
    private int powerUpMessageTimer = 0;

    // Boss hasar efekti
    private int bossDamageEffect = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 500));
        setFocusable(true);
        setupKeyBindings();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    resetGame();
                    repaint();
                } else if (waitingForKeyPress) {
                    startGame();
                    repaint();
                }
            }
        });

        // İlk başta talimatları göster
        showWelcomeMessage();
        requestFocusInWindow();

        Timer gameTimer = new Timer(16, e -> {
            if (!gameOver && !showInstructions && !waitingForKeyPress) {
                if (levelTransition) {
                    handleLevelTransition();
                } else {
                    gameLoop();
                }
            }
            repaint();
        });
        gameTimer.start();
    }

    private void showWelcomeMessage() {
        String instructions = """
                🎈 BALLOON MATH SHOOTER 🎯

                🎮 CONTROLS:
                • UP/DOWN: Move Player
                • SPACE: Shoot
                • 1: BLUE Bullet
                • 2: RED Bullet
                • 3: GREEN Bullet
                • 4: YELLOW Bullet

                🎯 OBJECTIVE:
                • Shoot the COLOR NAME on balloons
                • Solve math operations (max result: 15)
                • Avoid bombs (BLACK)
                • Collect ammo boxes (ORANGE)

                💡 FEATURES:
                • Every 5 levels: BOSS Battle!
                • Power-ups: Rapid Fire, Double Points, etc.
                • Smart ammo system

                After clicking OK, press ANY KEY to start!
                """;

        JOptionPane.showMessageDialog(this, instructions, "Welcome!",
                JOptionPane.INFORMATION_MESSAGE);

        waitingForKeyPress = true;
        showInstructions = true;
        startGame();
    }

    private void startGame() {
        showInstructions = false;
        waitingForKeyPress = false;
        startLevel();
    }

    private void resetGame() {
        score = 0;
        level = 1;
        ammo = 30;
        lives = 3;
        gameOver = false;
        waitingForKeyPress = true;
        showInstructions = true;
        requestFocusInWindow();

        rapidFire = false;
        doublePoints = false;
        infiniteAmmo = false;
        slowTime = false;
        shieldActive = false;
    }

    private void startLevel() {
        balloons.clear();
        bullets.clear();
        particles.clear();

        if (level > 1) {
            levelTransition = true;
            transitionTimer = 90;
            transitionMessage = "LEVEL " + level;

            if (level % 5 == 0) {
                transitionMessage = "BOSS LEVEL " + level;
            }
        }

        int baseCount;
        if (level % 5 == 0) {
            baseCount = 0;
        } else {
            baseCount = 4 + (level / 2);
        }

        for (int i = 0; i < baseCount; i++) {
            createBalloon(i);
        }

        if (level % 5 == 0) {
            int x = 850;
            int y = 200 + rand.nextInt(100);
            balloons.add(new BossBalloon(x, y, level));
        }

        if (ammo < 8 && level % 5 != 0) {
            int x = 820 + baseCount * 250;
            int y = 100 + rand.nextInt(300);
            balloons.add(new Balloon(x, y, Balloon.Type.AMMO, level));
        }
    }

    private void createBalloon(int index) {
        int x = 800 + index * 250 + rand.nextInt(120);
        int y = 60 + rand.nextInt(320);

        int r = rand.nextInt(100);

        int ammoChance = (ammo < 5) ? 60 : (ammo < 10) ? 30 : 15;

        if (level > 3 && rand.nextInt(100) < 6 && level % 5 != 0) {
            balloons.add(new PowerUpBalloon(x, y, level));
            return;
        }

        Balloon.Type type = Balloon.Type.NORMAL;

        if (r < 5 + (level / 2)) {
            type = Balloon.Type.BOMB;
        } else if (r < 5 + (level / 2) + ammoChance) {
            type = Balloon.Type.AMMO;
        }

        balloons.add(new Balloon(x, y, type, level));
    }

    private void handleLevelTransition() {
        transitionTimer--;
        if (transitionTimer <= 0) {
            levelTransition = false;
        }
    }

    private void gameLoop() {
        updatePowerUps();

        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            bullet.move();
            if (bullet.getX() > 800) {
                bulletIt.remove();
            }
        }

        checkCollisions();
        updateParticles();

        Iterator<Balloon> balloonIt = balloons.iterator();
        while (balloonIt.hasNext()) {
            Balloon balloon = balloonIt.next();

            if (slowTime) {
                balloon.move((int) (level * 0.5));
            } else {
                balloon.move(level);
            }

            if (balloon.getX() < -100) {
                boolean isBoss = balloon instanceof BossBalloon;
                boolean isNormal = balloon.getType() == Balloon.Type.NORMAL && !isBoss;
                boolean isZeroValue = isNormal && balloon.isZeroValueOperation();
                boolean shouldDamagePlayer = isNormal && !isZeroValue;
                if (shouldDamagePlayer) {
                    if (!shieldActive) {
                        lives--;
                    }
                    createExplosion(balloon.getX(), balloon.getY(), Color.GRAY, 8);
                }
                balloonIt.remove();
                if (lives <= 0) {
                    gameOver = true;
                }
            }
        }

        if (!infiniteAmmo && ammo <= 0 && bullets.isEmpty() && !balloons.isEmpty() && !levelTransition) {
            gameOver = true;
        }

        if (ammo < 5 && level % 5 != 0) {
            int ammoBalloonCount = 0;
            int totalBalloons = balloons.size();

            for (Balloon b : balloons) {
                if (b.getType() == Balloon.Type.AMMO) {
                    ammoBalloonCount++;
                }
            }

            if (ammoBalloonCount == 0 && totalBalloons > 0) {
                int lastX = balloons.isEmpty() ? 820 : balloons.get(balloons.size() - 1).getX() + 200;
                balloons.add(new Balloon(lastX, 100 + rand.nextInt(300), Balloon.Type.AMMO, level));
                ammoBalloonCount = 1;
                totalBalloons++;
            }

            if (ammoBalloonCount < 2 && totalBalloons < 8) {
                boolean addAmmo = rand.nextInt(100) < 40;
                if (addAmmo) {
                    int lastX = balloons.isEmpty() ? 820 : balloons.get(balloons.size() - 1).getX() + 200;
                    balloons.add(new Balloon(lastX, 100 + rand.nextInt(300),
                            Balloon.Type.AMMO, level));
                }
            }
        }

        if (balloons.isEmpty() && !levelTransition && !gameOver) {
            level++;
            ammo += 15;
            if (lives < 3)
                lives++;
            startLevel();
        }

        if (powerUpMessageTimer > 0) {
            powerUpMessageTimer--;
        }

        if (bossDamageEffect > 0) {
            bossDamageEffect--;
        }

        repaint();
    }

    private void updatePowerUps() {
        if (rapidFire && rapidFireTimer > 0) {
            rapidFireTimer--;
            if (rapidFireTimer == 0)
                rapidFire = false;
        }

        if (doublePoints && doublePointsTimer > 0) {
            doublePointsTimer--;
            if (doublePointsTimer == 0)
                doublePoints = false;
        }

        if (infiniteAmmo && infiniteAmmoTimer > 0) {
            infiniteAmmoTimer--;
            if (infiniteAmmoTimer == 0)
                infiniteAmmo = false;
        }

        if (slowTime && slowTimeTimer > 0) {
            slowTimeTimer--;
            if (slowTimeTimer == 0)
                slowTime = false;
        }

        if (shieldActive && shieldTimer > 0) {
            shieldTimer--;
            if (shieldTimer == 0)
                shieldActive = false;
        }
    }

    private void checkCollisions() {
        // Aynı anda listeden eleman silmek hata verdireceği için "silinecekler" listesi
        // tutuyoruz
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Balloon> balloonsToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            for (Balloon balloon : balloons) {
                // 1. ADIM: Mermi ile balonun çarpışma kutuları (Bounds) kesişiyor mu?
                if (bullet.getBounds().intersects(balloon.getBounds())) {

                    // Renk isimlerini al (Örn: "RED", "BLUE")
                    String bulletColorName = getColorName(bullet.getColor());
                    String balloonColorName = getColorName(balloon.getTargetColor());

                    boolean isAmmoBalloon = balloon.getType() == Balloon.Type.AMMO;
                    boolean isZeroValueBalloon = balloon.isZeroValueOperation();

                    // 2. ADIM: Renkler Uyuşuyor mu? (AMMO ve 0 degerli balonlar renk kontrolu
                    // istemez)
                    if (isAmmoBalloon || isZeroValueBalloon || bulletColorName.equals(balloonColorName)) {
                        // DOĞRU RENK!

                        balloon.hit(); // Balonun canını 1 düşürür ve titretir
                        if (isZeroValueBalloon && !shieldActive) {
                            lives = Math.max(0, lives - 1);
                            showPowerUpMessage("ZERO VALUE HIT! -1 LIFE");
                        }
                        bulletsToRemove.add(bullet); // Mermiyi yok et

                        if (balloon instanceof BossBalloon) {
                            BossBalloon boss = (BossBalloon) balloon;
                            if (boss.consumePhaseAdvance()) {
                                ammo += 8;
                                showPowerUpMessage("BOSS PHASE " + boss.getPhase() + "! +8 AMMO");
                            }
                        }

                        // 3. ADIM: Balonun canı bitti mi?
                        if (balloon.isDestroyed()) {
                            // Balon tamamen patladı
                            int points = 100;
                            if (doublePoints)
                                points *= 2; // Power-up kontrolü
                            score += points;

                            if (balloon.getType() == Balloon.Type.AMMO) {
                                ammo += 10;
                            }

                            balloonsToRemove.add(balloon); // Balonu silinecekler listesine ekle
                            createExplosion(balloon.getX() + 30, balloon.getY() + 30, bullet.getColor(), 25);
                            System.out.println("It's happened! New Score: " + score);
                        } else {
                            // Balon henüz patlamadı, sadece isabet aldı
                            score += 10; // Her vuruş için küçük puan
                            createSmallExplosion(balloon.getX() + 30, balloon.getY() + 30, bullet.getColor(), 5);
                            System.out.println("HIT! Remaining lives: " + balloon.getValue());
                        }
                    } else {
                        // YANLIŞ RENK!
                        score = Math.max(0, score - 5); // Ceza puanı
                        bulletsToRemove.add(bullet); // Yanlış mermi de balona çarpınca yok olur
                        balloon.randomTransform();
                        System.out.println("YANLIŞ RENK! Gereken: " + balloonColorName);
                    }

                    // Bu mermi bir balona çarptığı için döngüden çıkıp sıradaki mermiye geçiyoruz
                    break;
                }
            }
        }

        // İşlem bittikten sonra toplu silme yapıyoruz
        bullets.removeAll(bulletsToRemove);
        balloons.removeAll(balloonsToRemove);
    }

    
    private void showPowerUpMessage(String message) {
        powerUpMessage = message;
        powerUpMessageTimer = 120;
    }

    private void createExplosion(int x, int y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(x, y, color));
        }
    }

    private void createSmallExplosion(int x, int y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(x, y, color, true));
        }
    }

    private void updateParticles() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (!p.isAlive()) {
                it.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, 800, 500);

        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 450, 800, 50);

        if (waitingForKeyPress) {
            drawPressAnyKey(g);
            return;
        }

        if (!showInstructions) {
            for (Particle p : particles) {
                p.draw(g);
            }

            List<Balloon> sortedBalloons = new ArrayList<>(balloons);
            sortedBalloons.sort((a, b) -> {
                boolean aBoss = a instanceof BossBalloon;
                boolean bBoss = b instanceof BossBalloon;
                return Boolean.compare(bBoss, aBoss);
            });

            for (Balloon b : sortedBalloons) {
                if (b instanceof BossBalloon && bossDamageEffect > 0) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    float alpha = 0.5f + (float) Math.sin(System.currentTimeMillis() / 100.0) * 0.3f;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    b.draw(g2d);
                    g2d.dispose();
                } else {
                    b.draw(g);
                }
            }

            for (Bullet b : bullets) {
                b.draw(g);
            }

            player.draw(g, currentBulletColor);

            drawUI(g);

            if (shieldActive) {
                drawShieldEffect(g);
            }

            if (levelTransition) {
                drawLevelTransition(g);
            }

            if (powerUpMessageTimer > 0) {
                drawPowerUpMessage(g);
            }

            if (gameOver) {
                drawGameOver(g);
            }
        }
    }

    private void drawPressAnyKey(Graphics g) {
        g.setColor(new Color(0, 0, 0, 230));
        g.fillRect(0, 0, 800, 500);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("BALLOON MATH SHOOTER", 200, 100);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));

        String[] instructions = {
                "Game is ready to start!",
                "",
                "Press ANY KEY to begin!"
        };

        int y = 150;
        for (String line : instructions) {
            g.drawString(line, 100, y);
            y += 25;
        }

        if (System.currentTimeMillis() % 1000 < 500) {
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("▼ PRESS ANY KEY ▼", 280, 450);
        }
    }

    private void drawUI(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        int padding = 10;
        int boxPadding = 8;

        Font titleFont = new Font("Arial", Font.BOLD, 16);
        Font infoFont = new Font("Arial", Font.PLAIN, 12);

        // Top-left: level + power-ups
        String levelText = "LEVEL: " + level;
        FontMetrics titleMetrics = g.getFontMetrics(titleFont);
        FontMetrics infoMetrics = g.getFontMetrics(infoFont);

        java.util.List<String> powerLines = new ArrayList<>();
        java.util.List<Color> powerColors = new ArrayList<>();
        if (rapidFire) {
            powerLines.add("⚡ Rapid Fire: " + (rapidFireTimer / 60) + "s");
            powerColors.add(Color.YELLOW);
        }
        if (doublePoints) {
            powerLines.add("2× Double Points: " + (doublePointsTimer / 60) + "s");
            powerColors.add(Color.GREEN);
        }
        if (slowTime) {
            powerLines.add("⏱ Slow Time: " + (slowTimeTimer / 60) + "s");
            powerColors.add(Color.CYAN);
        }
        if (shieldActive) {
            powerLines.add("🛡 Shield: " + (shieldTimer / 60) + "s");
            powerColors.add(new Color(0, 200, 255));
        }

        int leftBoxWidth = titleMetrics.stringWidth(levelText);
        for (String line : powerLines) {
            leftBoxWidth = Math.max(leftBoxWidth, infoMetrics.stringWidth(line));
        }
        int leftBoxHeight = titleMetrics.getHeight() + (powerLines.size() * infoMetrics.getHeight());
        leftBoxWidth += boxPadding * 2;
        leftBoxHeight += boxPadding * 2;

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(padding, padding, leftBoxWidth, leftBoxHeight, 12, 12);
        g.setFont(titleFont);
        g.setColor(Color.CYAN);
        int textY = padding + boxPadding + titleMetrics.getAscent();
        g.drawString(levelText, padding + boxPadding, textY);
        g.setFont(infoFont);
        textY += titleMetrics.getDescent() + infoMetrics.getAscent();
        for (int i = 0; i < powerLines.size(); i++) {
            g.setColor(powerColors.get(i));
            g.drawString(powerLines.get(i), padding + boxPadding, textY);
            textY += infoMetrics.getHeight();
        }

        // Top-right: score + lives
        String scoreText = "SCORE: " + score;
        String livesText = "LIVES: " + lives;
        int rightBoxWidth = Math.max(titleMetrics.stringWidth(scoreText), titleMetrics.stringWidth(livesText));
        int rightBoxHeight = titleMetrics.getHeight() * 2;
        rightBoxWidth += boxPadding * 2;
        rightBoxHeight += boxPadding * 2;
        int rightBoxX = width - padding - rightBoxWidth;
        int rightBoxY = padding;

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(rightBoxX, rightBoxY, rightBoxWidth, rightBoxHeight, 12, 12);
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        int rightTextY = rightBoxY + boxPadding + titleMetrics.getAscent();
        g.drawString(scoreText, rightBoxX + boxPadding, rightTextY);
        rightTextY += titleMetrics.getHeight();
        if (shieldActive) {
            g.setColor(new Color(0, 200, 255));
        } else {
            g.setColor(lives < 2 ? Color.RED : Color.GREEN);
        }
        g.drawString(livesText, rightBoxX + boxPadding, rightTextY);

        // Bottom-left: ammo
        String ammoText;
        if (infiniteAmmo) {
            ammoText = "AMMO: INFINITE!";
        } else {
            ammoText = "AMMO: " + ammo;
        }
        int bottomLeftWidth = titleMetrics.stringWidth(ammoText) + boxPadding * 2;
        int bottomLeftHeight = titleMetrics.getHeight() + boxPadding * 2;
        int bottomLeftX = padding;
        int bottomLeftY = height - padding - bottomLeftHeight;

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(bottomLeftX, bottomLeftY, bottomLeftWidth, bottomLeftHeight, 12, 12);
        if (infiniteAmmo) {
            g.setColor(Color.MAGENTA);
        } else if (ammo < 5 && System.currentTimeMillis() % 500 < 250) {
            g.setColor(Color.RED);
        } else {
            g.setColor(ammo < 10 ? Color.ORANGE : Color.YELLOW);
        }
        g.setFont(titleFont);
        g.drawString(ammoText, bottomLeftX + boxPadding, bottomLeftY + boxPadding + titleMetrics.getAscent());

        // Bottom-right: bullet color
        String colorName = getColorName(currentBulletColor);
        String bulletText = "BULLET: " + colorName;
        int bottomRightWidth = titleMetrics.stringWidth(bulletText) + boxPadding * 2;
        int bottomRightHeight = titleMetrics.getHeight() + boxPadding * 2;
        int bottomRightX = width - padding - bottomRightWidth;
        int bottomRightY = height - padding - bottomRightHeight;

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(bottomRightX, bottomRightY, bottomRightWidth, bottomRightHeight, 12, 12);
        g.setColor(currentBulletColor);
        g.drawString(bulletText, bottomRightX + boxPadding, bottomRightY + boxPadding + titleMetrics.getAscent());
    }

    private void drawShieldEffect(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 200, 255, 60));
        g2d.setStroke(new BasicStroke(3));

        int playerX = 20;
        int playerY = player.getY();

        g2d.drawOval(playerX - 10, playerY - 35, 70, 90);
        g2d.drawOval(playerX - 15, playerY - 40, 80, 100);
    }

    private void drawLevelTransition(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, 800, 500);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));

        int textWidth = g.getFontMetrics().stringWidth(transitionMessage);
        g.drawString(transitionMessage, 400 - textWidth / 2, 250);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Get ready!", 350, 290);
    }

    private void drawPowerUpMessage(Graphics g) {
        int alpha = Math.min(255, powerUpMessageTimer * 2);
        g.setColor(new Color(255, 255, 0, alpha));
        g.setFont(new Font("Arial", Font.BOLD, 20));

        int textWidth = g.getFontMetrics().stringWidth(powerUpMessage);
        g.drawString(powerUpMessage, 400 - textWidth / 2, 100);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, 800, 500);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("GAME OVER", 250, 200);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Final Score: " + score, 300, 250);
        g.drawString("Level Reached: " + level, 300, 285);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press ANY KEY to Restart", 260, 370);
    }

    private String getColorName(Color color) {
        if (color.equals(Color.RED))
            return "RED";
        if (color.equals(Color.BLUE))
            return "BLUE";
        if (color.equals(Color.GREEN))
            return "GREEN";
        if (color.equals(Color.YELLOW))
            return "YELLOW";
        if (color.equals(Color.ORANGE))
            return "ORANGE";
        if (color.equals(Color.CYAN))
            return "CYAN";
        if (color.equals(Color.BLACK))
            return "BLACK";
        return "UNKNOWN";
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("ANY"), "startGame");
        im.put(KeyStroke.getKeyStroke("UP"), "up");
        im.put(KeyStroke.getKeyStroke("DOWN"), "down");
        im.put(KeyStroke.getKeyStroke("SPACE"), "shoot");
        im.put(KeyStroke.getKeyStroke("ENTER"), "restart");
        im.put(KeyStroke.getKeyStroke('1'), "color1");
        im.put(KeyStroke.getKeyStroke('2'), "color2");
        im.put(KeyStroke.getKeyStroke('3'), "color3");
        im.put(KeyStroke.getKeyStroke('4'), "color4");

        am.put("startGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) {
                    resetGame();
                    return;
                }
                if (waitingForKeyPress) {
                    startGame();
                }
            }
        });

        am.put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showInstructions && !waitingForKeyPress && !gameOver) {
                    player.moveUp();
                    repaint();
                }
            }
        });

        am.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showInstructions && !waitingForKeyPress && !gameOver) {
                    player.moveDown();
                    repaint();
                }
            }
        });

        am.put("shoot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showInstructions && !waitingForKeyPress && !gameOver && !levelTransition) {
                    if ((ammo > 0 || infiniteAmmo)) {
                        bullets.add(new Bullet(player.getGunX(), player.getY(), currentBulletColor));
                        if (!infiniteAmmo) {
                            ammo--;
                        }

                        if (rapidFire && (ammo > 1 || infiniteAmmo)) {
                            bullets.add(new Bullet(player.getGunX() + 10, player.getY() + 5, currentBulletColor));
                            if (!infiniteAmmo) {
                                ammo--;
                            }
                        }
                    }
                }
            }
        });

        am.put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) {
                    resetGame();
                }
            }
        });

        am.put("color1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showInstructions && !waitingForKeyPress) {
                    currentBulletColor = Color.BLUE;
                }
            }
        });

        am.put("color2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showInstructions && !waitingForKeyPress) {
                    currentBulletColor = Color.RED;
                }
            }
        });

        am.put("color3", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showInstructions && !waitingForKeyPress) {
                    currentBulletColor = Color.GREEN;
                }
            }
        });

        am.put("color4", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showInstructions && !waitingForKeyPress) {
                    currentBulletColor = Color.YELLOW;
                }
            }
        });
    }
}
