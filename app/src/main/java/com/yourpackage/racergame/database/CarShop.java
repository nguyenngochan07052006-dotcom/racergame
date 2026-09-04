package com.yourpackage.racergame.database;

public class CarShop {
    public int id;
    public String carName;
    public int price;
    public boolean isUnlocked;

    public CarShop(int id, String carName, int price, boolean isUnlocked) {
        this.id = id;
        this.carName = carName;
        this.price = price;
        this.isUnlocked = isUnlocked;
    }
}