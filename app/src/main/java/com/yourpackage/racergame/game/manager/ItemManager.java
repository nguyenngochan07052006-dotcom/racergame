package com.yourpackage.racergame.game.manager;

import com.yourpackage.racergame.game.model.GameItem;
import com.yourpackage.racergame.game.model.Obstacle;
import com.yourpackage.racergame.utils.GameConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ItemManager {
    private final List<GameItem> items = new ArrayList<>();
    private final Random random = new Random();
    private final float[] laneCenters;

    public ItemManager(float[] laneCenters) {
        this.laneCenters = laneCenters;
    }

    public void update(float currentSpeed, int screenHeight, List<Obstacle> obstacles) {
        if (random.nextInt(100) < GameConstants.ITEM_SPAWN_CHANCE) {
            boolean[] busy = new boolean[3];
            for (Obstacle o : obstacles) {
                if (o.y < 450) {
                    busy[o.lane] = true;
                }
            }

            List<Integer> freeLanes = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                if (!busy[i]) freeLanes.add(i);
            }

            if (!freeLanes.isEmpty()) {
                int lane = freeLanes.get(random.nextInt(freeLanes.size()));
                float x = laneCenters[lane] - 22;
                if (random.nextBoolean()) {
                    items.add(new GameItem(GameItem.Type.COIN, x, -50, 45, 45));
                } else {
                    items.add(new GameItem(GameItem.Type.GEM, x, -45, 40, 40));
                }
            }
        }

        Iterator<GameItem> it = items.iterator();
        while (it.hasNext()) {
            GameItem item = it.next();
            item.y += currentSpeed;
            if (item.y > screenHeight) {
                it.remove();
            }
        }
    }

    public List<GameItem> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }
}