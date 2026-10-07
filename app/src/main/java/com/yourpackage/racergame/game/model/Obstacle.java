package com.yourpackage.racergame.game.model;

public class Obstacle {
    public enum Type {
        ROCK, FENCE, BARRIER
    }

    public float x, y;
    public int width, height;
    public Type type;
    public int lane;

    public Obstacle(float x, float y, int width, int height, Type type, int lane) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.lane = lane;
    }
}