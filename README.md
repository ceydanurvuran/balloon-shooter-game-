# 🎈 Balloon Math Shooter 🎯

**Balloon Math Shooter** is a mini game developed using **Java Swing**, combining **reflex-based gameplay** with **basic math and color matching mechanics**.  
The player must shoot balloons using the **correct bullet color** while managing ammo, lives, and power-ups.

---

## 🧩 Game Objective

- Shoot balloons using the **correct color**
- Pay attention to **math operations** on balloons
- Avoid **dangerous balloons** and bad decisions
- Manage your **ammo and lives**
- Defeat a **Boss Balloon every 5 levels**

---

## 🎮 Controls

| Key | Action |
|----|------|
| ⬆ UP | Move player up |
| ⬇ DOWN | Move player down |
| SPACE | Shoot |
| 1 | Bullet color: **Blue** |
| 2 | Bullet color: **Red** |
| 3 | Bullet color: **Green** |
| 4 | Bullet color: **Yellow** |
| ENTER / Any Key | Start / Restart game |

---

## 🎈 Balloon Types

- **NORMAL**
  - Must be shot with the correct color
  - Damages the player if missed
- **AMMO (Orange)**
  - Grants +10 ammo
  - Color does not matter
- **BOMB (Black)**
  - Dangerous balloon
  - Causes damage if missed
- **ZERO VALUE (Math result = 0)**
  - Shooting causes **-1 life**
- **BOSS (Every 5 levels)**
  - Multi-phase enemy
  - Rewards ammo on each phase change

---

## 🔫 Ammo System

- Starting ammo: **30**
- Running out of ammo while balloons remain results in **Game Over**
- The game dynamically spawns **AMMO balloons** when ammo is low
- Some power-ups grant **infinite ammo**

---

## 💥 Scoring System

| Action | Score |
|------|------|
| Destroy balloon | +100 |
| Successful hit | +10 |
| Wrong color hit | -5 |
| Double Points active | x2 |

---

## ⚡ Power-Ups

Special **Power-Up Balloons** may appear during gameplay:

- ⚡ **Rapid Fire** – Shoots double bullets
- 2️⃣ **Double Points** – Score multiplier
- ♾ **Infinite Ammo** – Unlimited bullets
- ⏱ **Slow Time** – Slows down balloons
- 🛡 **Shield** – Temporary damage immunity

Active power-ups and remaining durations are displayed on screen.

---

## ❤️ Life System

- Starting lives: **3**
- Missing a normal balloon: **-1 life**
- Shooting a zero-value balloon: **-1 life**
- Shield prevents damage while active
- Reaching 0 lives results in **GAME OVER**

---

## 📈 Level Progression

- Difficulty increases each level
- Balloon count and speed scale with level
- At the end of each level:
  - +15 ammo
  - +1 life (if below max)
- Every **5th level** is a **Boss Level**

---

## 🖥 Visual Features

- Particle-based explosion effects
- Boss damage animation
- Level transition screens
- Power-up notifications
- Dynamic HUD (score, ammo, lives, power-ups)

---

## 🛠 Technologies Used

- **Java**
- **Java Swing**
- `JPanel`, `Timer`, `Key Bindings`
- Object-Oriented Programming (Inheritance & Polymorphism)
- Collision Detection
- Custom rendering using `paintComponent`

---

## ▶️ How to Run

1. Open the project in a Java IDE (IntelliJ IDEA, Eclipse, or NetBeans)
2. Run the main JFrame class
3. Press **any key** to start the game
4. Enjoy playing 🎮

---

## 👤 Developer Notes

This project was created for learning purposes, focusing on **Java Swing**, **game loops**, and **object-oriented design**.  
The codebase is modular and suitable for adding new features or mechanics.

---

🎯 **Have fun and aim smart!**
