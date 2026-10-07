package com.yourpackage.racergame.game.model;

public class Car {
    public float x, y;
    public int width, height;
    public int carId;
    public boolean hasShield;
    public long shieldEndTime;

    public Car(float x, float y, int width, int height, int carId) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.carId = carId;
        this.hasShield = false;
    }

    public void activateShield(long durationMs) {
        hasShield = true;
        shieldEndTime = System.currentTimeMillis() + durationMs;
    }

    public void updateShield() {
        if (hasShield && System.currentTimeMillis() > shieldEndTime) {
            hasShield = false;
        }
    }
}