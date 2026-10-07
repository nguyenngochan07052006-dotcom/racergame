package com.yourpackage.racergame.game.model;

public class GameItem {
    public enum Type {
        COIN, GEM
    }

    public Type type;
    public float x, y;
    public int width, height;
    public boolean isCollected;

    public GameItem(Type type, float x, float y, int width, int height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isCollected = false;
    }

    public int getValue() {
        return type == Type.COIN ? 10 : 50;
    }
}