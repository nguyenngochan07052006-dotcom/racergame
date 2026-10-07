package com.yourpackage.racergame.game.manager;

import com.yourpackage.racergame.game.model.Obstacle;
import com.yourpackage.racergame.utils.GameConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ObstacleManager {
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final Random random = new Random();
    private final float[] laneCenters;

    public ObstacleManager(float[] laneCenters) {
        this.laneCenters = laneCenters;
    }

    public void update(float currentSpeed, int screenHeight) {
        if (random.nextInt(100) < GameConstants.OBSTACLE_SPAWN_CHANCE) {
            int lane = random.nextInt(3);
            Obstacle.Type type = getRandomType();
            int width = (type == Obstacle.Type.ROCK) ? 70 : 90;
            int height = (type == Obstacle.Type.ROCK) ? 70 : 55;
            float x = laneCenters[lane] - width / 2f;
            obstacles.add(new Obstacle(x, -130, width, height, type, lane));
        }

        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle o = it.next();
            o.y += currentSpeed;
            if (o.y > screenHeight + 50) {
                it.remove();
            }
        }
    }

    private Obstacle.Type getRandomType() {
        int r = random.nextInt(3);
        if (r == 0) return Obstacle.Type.ROCK;
        if (r == 1) return Obstacle.Type.FENCE;
        return Obstacle.Type.BARRIER;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void clear() {
        obstacles.clear();
    }
}