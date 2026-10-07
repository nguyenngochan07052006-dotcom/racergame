package com.yourpackage.racergame.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private boolean running = false;
    private long lastTime = System.currentTimeMillis();
    private final long frameTime = 16; // ~60 FPS

    public GameThread(SurfaceHolder holder, GameView gameView) {
        this.surfaceHolder = holder;
        this.gameView = gameView;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            long now = System.currentTimeMillis();
            if (now - lastTime >= frameTime) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        synchronized (surfaceHolder) {
                            gameView.update();
                            gameView.drawGame(canvas);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                lastTime = now;
            }
        }
    }
}