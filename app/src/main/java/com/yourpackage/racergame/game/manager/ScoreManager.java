package com.yourpackage.racergame.game.manager;

public class ScoreManager {
    private int score = 0;
    private int distance = 0;
    private int coins = 0;
    private int gems = 0;
    private int level = 1;
    private int combo = 0;

    public void addDistance(int value) {
        distance += value;
        score = distance / 10;
        int newLevel = (distance / 1500) + 1;
        if (newLevel > level) level = newLevel;
    }

    public void collectCoin() {
        coins += 10;
        combo++;
        if (combo > 8) coins += 5;
    }

    public void collectGem() {
        gems++;
        coins += 50;
        score += 100;
        combo += 2;
    }

    public void resetCombo() {
        combo = 0;
    }

    public int getScore() { return score; }
    public int getDistance() { return distance; }
    public int getCoins() { return coins; }
    public int getGems() { return gems; }
    public int getLevel() { return level; }
    public int getCombo() { return combo; }
}