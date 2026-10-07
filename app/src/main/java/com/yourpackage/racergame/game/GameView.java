package com.yourpackage.racergame.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yourpackage.racergame.database.DatabaseHelper;
import com.yourpackage.racergame.game.manager.CollisionManager;
import com.yourpackage.racergame.game.manager.ItemManager;
import com.yourpackage.racergame.game.manager.ObstacleManager;
import com.yourpackage.racergame.game.manager.ScoreManager;
import com.yourpackage.racergame.game.manager.SoundManager;
import com.yourpackage.racergame.game.model.Car;
import com.yourpackage.racergame.game.model.GameItem;
import com.yourpackage.racergame.game.renderer.CarRenderer;
import com.yourpackage.racergame.game.renderer.HUDRenderer;
import com.yourpackage.racergame.game.renderer.ItemRenderer;
import com.yourpackage.racergame.game.renderer.ObstacleRenderer;
import com.yourpackage.racergame.game.renderer.RoadRenderer;
import com.yourpackage.racergame.utils.GameConstants;

import java.util.Iterator;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private int screenWidth, screenHeight;
    private boolean isPaused = false;
    private boolean isGameOver = false;

    private float roadLeft, roadRight, laneWidth;
    private final float[] laneCenters = new float[3];

    private Car playerCar;
    private float currentSpeed = GameConstants.BASE_SPEED;

    private ScoreManager scoreManager;
    private ObstacleManager obstacleManager;
    private ItemManager itemManager;
    private CollisionManager collisionManager;
    private SoundManager soundManager;

    private RoadRenderer roadRenderer;
    private CarRenderer carRenderer;
    private ObstacleRenderer obstacleRenderer;
    private ItemRenderer itemRenderer;
    private HUDRenderer hudRenderer;

    private GameOverCallback gameOverCallback;

    public interface GameOverCallback {
        void onGameOver(int score, int distance, int coins, int gems);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);
        setZOrderOnTop(false);

        scoreManager = new ScoreManager();
        collisionManager = new CollisionManager();
        roadRenderer = new RoadRenderer();
        carRenderer = new CarRenderer();
        obstacleRenderer = new ObstacleRenderer();
        itemRenderer = new ItemRenderer();
        hudRenderer = new HUDRenderer();
    }

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        float totalRoadWidth = screenWidth * GameConstants.ROAD_WIDTH_RATIO;
        roadLeft = (screenWidth - totalRoadWidth) / 2f;
        roadRight = roadLeft + totalRoadWidth;
        laneWidth = totalRoadWidth / 3f;

        laneCenters[0] = roadLeft + laneWidth * 0.5f;
        laneCenters[1] = roadLeft + laneWidth * 1.5f;
        laneCenters[2] = roadLeft + laneWidth * 2.5f;

        int carId = 1;
        try {
            carId = new DatabaseHelper(getContext()).getSelectedCarId();
        } catch (Exception ignored) {}

        playerCar = new Car(
                laneCenters[1] - GameConstants.CAR_WIDTH / 2f,
                screenHeight - GameConstants.CAR_HEIGHT - 130,
                GameConstants.CAR_WIDTH,
                GameConstants.CAR_HEIGHT,
                carId
        );

        obstacleManager = new ObstacleManager(laneCenters);
        itemManager = new ItemManager(laneCenters);

        gameThread = new GameThread(holder, this);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameThread != null) {
            gameThread.setRunning(false);
            try { gameThread.join(); } catch (InterruptedException ignored) {}
        }
    }

    public void pauseGame() { isPaused = true; }
    public void resumeGame() { isPaused = false; }

    public void setGameOverCallback(GameOverCallback callback) {
        this.gameOverCallback = callback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isPaused || isGameOver) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float touchX = event.getX();
            int nearest = 1;
            float minDist = Float.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                float d = Math.abs(touchX - laneCenters[i]);
                if (d < minDist) {
                    minDist = d;
                    nearest = i;
                }
            }
            playerCar.x = laneCenters[nearest] - playerCar.width / 2f;
        }
        return true;
    }

    public void update() {
        if (isPaused || isGameOver) return;

        currentSpeed = GameConstants.BASE_SPEED + (scoreManager.getLevel() - 1) * GameConstants.SPEED_INCREASE_PER_LEVEL;

        scoreManager.addDistance((int) currentSpeed);
        playerCar.updateShield();

        obstacleManager.update(currentSpeed, screenHeight);
        itemManager.update(currentSpeed, screenHeight, obstacleManager.getObstacles());

        // Va chạm chướng ngại vật
        if (collisionManager.checkObstacleCollision(playerCar, obstacleManager.getObstacles())) {
            if (soundManager != null) soundManager.playSound("rock");
            gameOver();
            return;
        }

        // Va chạm vật phẩm + phát tiếng
        Iterator<GameItem> it = itemManager.getItems().iterator();
        while (it.hasNext()) {
            GameItem item = it.next();
            if (com.yourpackage.racergame.utils.CollisionUtils.isCollide(
                    playerCar.x, playerCar.y, playerCar.width, playerCar.height,
                    item.x, item.y, item.width, item.height)) {

                if (item.type == GameItem.Type.COIN) {
                    scoreManager.collectCoin();
                    if (soundManager != null) soundManager.playSound("coin");
                } else {
                    scoreManager.collectGem();
                    if (soundManager != null) soundManager.playSound("gem");
                }
                it.remove();
            }
        }
    }

    public void drawGame(Canvas canvas) {
        if (canvas == null) return;

        roadRenderer.draw(canvas, screenWidth, screenHeight, roadLeft, roadRight, laneWidth, scoreManager.getDistance());

        for (var o : obstacleManager.getObstacles()) {
            obstacleRenderer.draw(canvas, o);
        }
        for (var item : itemManager.getItems()) {
            itemRenderer.draw(canvas, item);
        }

        carRenderer.draw(canvas, playerCar);
        hudRenderer.draw(canvas, scoreManager, screenWidth, playerCar.hasShield);
    }

    private void gameOver() {
        isGameOver = true;
        if (gameOverCallback != null) {
            gameOverCallback.onGameOver(
                    scoreManager.getScore(),
                    scoreManager.getDistance(),
                    scoreManager.getCoins(),
                    scoreManager.getGems()
            );
        }
    }
}