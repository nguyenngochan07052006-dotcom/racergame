package com.yourpackage.racergame.game.manager;

import com.yourpackage.racergame.game.model.Car;
import com.yourpackage.racergame.game.model.GameItem;
import com.yourpackage.racergame.game.model.Obstacle;
import com.yourpackage.racergame.utils.CollisionUtils;

import java.util.Iterator;
import java.util.List;

public class CollisionManager {

    public boolean checkObstacleCollision(Car car, List<Obstacle> obstacles) {
        if (car.hasShield) return false;

        for (Obstacle o : obstacles) {
            if (CollisionUtils.isCollide(
                    car.x + 10, car.y + 15, car.width - 20, car.height - 30,
                    o.x + 5, o.y + 5, o.width - 10, o.height - 10)) {
                return true;
            }
        }
        return false;
    }

    public void checkItemCollision(Car car, List<GameItem> items, ScoreManager scoreManager) {
        Iterator<GameItem> it = items.iterator();
        while (it.hasNext()) {
            GameItem item = it.next();
            if (CollisionUtils.isCollide(car.x, car.y, car.width, car.height,
                    item.x, item.y, item.width, item.height)) {
                if (item.type == GameItem.Type.COIN) {
                    scoreManager.collectCoin();
                } else {
                    scoreManager.collectGem();
                }
                it.remove();
            }
        }
    }
}