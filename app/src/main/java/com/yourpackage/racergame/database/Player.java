package com.yourpackage.racergame.database;

public class Player {
    public int id;
    public String username;
    public int totalCoins;
    public int selectedCarId;   // ← thêm

    public Player(int id, String username, int totalCoins) {
        this.id = id;
        this.username = username;
        this.totalCoins = totalCoins;
        this.selectedCarId = 1;
    }

    public Player(int id, String username, int totalCoins, int selectedCarId) {
        this.id = id;
        this.username = username;
        this.totalCoins = totalCoins;
        this.selectedCarId = selectedCarId;
    }
}