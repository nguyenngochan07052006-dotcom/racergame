package com.yourpackage.racergame.database;

public class HighScore {
    public int id;
    public String playerName;
    public int score;
    public String playDate;

    public HighScore(int id, String playerName, int score, String playDate) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.playDate = playDate;
    }
}