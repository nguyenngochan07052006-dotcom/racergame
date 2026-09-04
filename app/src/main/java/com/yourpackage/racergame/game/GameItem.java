package com.yourpackage.racergame.game;

public class GameItem {
    public enum ItemType {
        COIN, GEM, SHIELD, STAR
    }

    public ItemType type;
    public float x, y;
    public int width, height;
    public boolean isCollected;
    public int value;

    public GameItem(ItemType type, float x, float y, int width, int height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isCollected = false;
        this.value = getItemValue(type);
    }

    private int getItemValue(ItemType type) {
        switch (type) {
            case COIN: return 10;
            case GEM: return 50;
            case SHIELD: return 0;
            case STAR: return 0;
            default: return 0;
        }
    }
}