
package swingproject;


import java.io.*;
import java.util.*;

public class HighScoreManager {
    private static final String FILE_NAME = "highscores.dat";
    private List<ScoreEntry> highScores;
    
    private static class ScoreEntry implements Comparable<ScoreEntry>, Serializable {
        String playerName;
        int score;
        int level;
        Date date;
        
        ScoreEntry(String playerName, int score, int level) {
            this.playerName = playerName;
            this.score = score;
            this.level = level;
            this.date = new Date();
        }
        
        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score); // Azalan sıralama
        }
        
        @Override
        public String toString() {
            return String.format("%s - %d pts (Level %d) - %tF", 
                playerName, score, level, date);
        }
    }
    
    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadScores();
    }
    
    @SuppressWarnings("unchecked")
    private void loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_NAME))) {
            highScores = (List<ScoreEntry>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No previous high scores found.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
        }
    }
    
    public void saveScore(String playerName, int score, int level) {
        highScores.add(new ScoreEntry(playerName, score, level));
        Collections.sort(highScores);
        
        // En iyi 10'u sakla
        if (highScores.size() > 10) {
            highScores = highScores.subList(0, 10);
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_NAME))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }
    
    public List<String> getHighScores() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < highScores.size(); i++) {
            result.add((i + 1) + ". " + highScores.get(i).toString());
        }
        return result;
    }
    
    public int getHighestScore() {
        return highScores.isEmpty() ? 0 : highScores.get(0).score;
    }
}