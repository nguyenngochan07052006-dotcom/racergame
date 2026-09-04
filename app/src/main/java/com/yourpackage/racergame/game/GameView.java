package com.yourpackage.racergame.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yourpackage.racergame.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private float carX, carY;
    private final int carWidth = 85;
    private final int carHeight = 145;
    private int screenWidth, screenHeight;

    private float roadSpeed = 13f;
    private boolean isGameOver = false;
    private boolean isPaused = false;

    private int score = 0;
    private int coinsCollected = 0;
    private int gemsCollected = 0;
    private int distance = 0;
    private int comboCount = 0;
    private boolean hasShield = false;
    private long shieldTime = 0;
    private int selectedCarId = 1;

    private float roadLeft, roadRight, laneWidth;
    private final float[] laneCenters = new float[3];

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<GameItem> gameItems = new ArrayList<>();
    private final Random random = new Random();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private GameOverCallback gameOverCallback;

    public interface GameOverCallback {
        void onGameOver(int score, int distance, int coins, int gems);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);
        setZOrderOnTop(false);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        float totalRoadWidth = screenWidth * 0.78f;
        roadLeft = (screenWidth - totalRoadWidth) / 2f;
        roadRight = roadLeft + totalRoadWidth;
        laneWidth = totalRoadWidth / 3f;

        laneCenters[0] = roadLeft + laneWidth * 0.5f;
        laneCenters[1] = roadLeft + laneWidth * 1.5f;
        laneCenters[2] = roadLeft + laneWidth * 2.5f;

        carX = laneCenters[1] - carWidth / 2f;
        carY = screenHeight - carHeight - 130;

        try {
            selectedCarId = new DatabaseHelper(getContext()).getSelectedCarId();
        } catch (Exception e) {
            selectedCarId = 1;
        }

        gameThread = new GameThread(holder);
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
            float min = Float.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                float d = Math.abs(touchX - laneCenters[i]);
                if (d < min) { min = d; nearest = i; }
            }
            carX = laneCenters[nearest] - carWidth / 2f;
        }
        return true;
    }

    private void update() {
        if (isGameOver || isPaused) return;

        distance += (int) roadSpeed;
        score = distance / 10;

        if (hasShield && System.currentTimeMillis() - shieldTime > 5000) {
            hasShield = false;
        }

        // Sinh chướng ngại vật
        if (random.nextInt(100) < 3) {
            int lane = random.nextInt(3);
            obstacles.add(new Obstacle(laneCenters[lane] - 40, -130, 80, 120));
        }

        // Sinh vật phẩm (tránh trùng)
        if (random.nextInt(100) < 5) {
            boolean[] busy = new boolean[3];
            for (Obstacle o : obstacles) {
                if (o.y < 450) {
                    for (int i = 0; i < 3; i++) {
                        if (Math.abs(o.x + 40 - laneCenters[i]) < 50) busy[i] = true;
                    }
                }
            }
            List<Integer> free = new ArrayList<>();
            for (int i = 0; i < 3; i++) if (!busy[i]) free.add(i);

            if (!free.isEmpty()) {
                int lane = free.get(random.nextInt(free.size()));
                float x = laneCenters[lane] - 22;
                int r = random.nextInt(4);
                if (r == 0) gameItems.add(new GameItem(GameItem.ItemType.COIN, x, -50, 45, 45));
                else if (r == 1) gameItems.add(new GameItem(GameItem.ItemType.GEM, x, -45, 40, 40));
                else if (r == 2) gameItems.add(new GameItem(GameItem.ItemType.SHIELD, x, -50, 50, 50));
                else gameItems.add(new GameItem(GameItem.ItemType.STAR, x, -45, 45, 45));
            }
        }

        // Chướng ngại vật
        Iterator<Obstacle> oit = obstacles.iterator();
        while (oit.hasNext()) {
            Obstacle o = oit.next();
            o.y += roadSpeed;
            if (o.y > screenHeight + 50) {
                oit.remove();
                continue;
            }
            if (!hasShield && checkCollision(carX + 10, carY + 15, carWidth - 20, carHeight - 30,
                    o.x + 8, o.y + 10, o.width - 16, o.height - 25)) {
                gameOver();
                return;
            }
        }

        // Vật phẩm
        Iterator<GameItem> iit = gameItems.iterator();
        while (iit.hasNext()) {
            GameItem item = iit.next();
            item.y += roadSpeed;
            if (item.y > screenHeight) {
                iit.remove();
            } else if (checkCollision(carX, carY, carWidth, carHeight, item.x, item.y, item.width, item.height)) {
                collectItem(item);
                iit.remove();
            }
        }
    }

    private void collectItem(GameItem item) {
        switch (item.type) {
            case COIN: coinsCollected += 10; comboCount++; break;
            case GEM: gemsCollected++; coinsCollected += 50; score += 100; comboCount += 2; break;
            case SHIELD: hasShield = true; shieldTime = System.currentTimeMillis(); break;
            case STAR: score += 500; comboCount += 3; break;
        }
    }

    private boolean checkCollision(float x1, float y1, float w1, float h1,
                                   float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    private void gameOver() {
        isGameOver = true;
        if (gameOverCallback != null) {
            gameOverCallback.onGameOver(score, distance, coinsCollected, gemsCollected);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        super.draw(canvas);

        // Nền
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#0A1628"));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        // Vỉa hè
        paint.setColor(Color.parseColor("#1B2838"));
        canvas.drawRect(0, 0, roadLeft, screenHeight, paint);
        canvas.drawRect(roadRight, 0, screenWidth, screenHeight, paint);

        // Đường
        paint.setColor(Color.parseColor("#2C3E50"));
        canvas.drawRect(roadLeft, 0, roadRight, screenHeight, paint);

        // Vạch làn
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(6);
        float offset = distance % 70;
        for (float y = -70 + offset; y < screenHeight; y += 70) {
            canvas.drawLine(roadLeft + laneWidth, y, roadLeft + laneWidth, y + 35, paint);
            canvas.drawLine(roadLeft + laneWidth * 2, y, roadLeft + laneWidth * 2, y + 35, paint);
        }

        // Viền đường
        paint.setColor(Color.parseColor("#ECEFF1"));
        paint.setStrokeWidth(8);
        canvas.drawLine(roadLeft, 0, roadLeft, screenHeight, paint);
        canvas.drawLine(roadRight, 0, roadRight, screenHeight, paint);

        // Chướng ngại vật
        for (Obstacle o : obstacles) drawEnemyCar(canvas, o);

        // Vật phẩm
        for (GameItem item : gameItems) drawItem(canvas, item);

        // Xe
        drawPlayerCar(canvas);

        // HUD
        drawHUD(canvas);

        // Pause
        if (isPaused && !isGameOver) {
            paint.setColor(Color.parseColor("#99000000"));
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(58);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("TẠM DỪNG", screenWidth / 2f, screenHeight / 2f, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }

    private void drawEnemyCar(Canvas canvas, Obstacle o) {
        paint.setColor(Color.parseColor("#C62828"));
        canvas.drawRoundRect(new RectF(o.x, o.y, o.x + o.width, o.y + o.height), 14, 14, paint);
        paint.setColor(Color.parseColor("#FFCDD2"));
        canvas.drawRoundRect(new RectF(o.x + 12, o.y + 12, o.x + o.width - 12, o.y + 38), 8, 8, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(o.x + 16, o.y + 7, 7, paint);
        canvas.drawCircle(o.x + o.width - 16, o.y + 7, 7, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(o.x + 6, o.y + o.height - 15, o.x + 20, o.y + o.height - 4, paint);
        canvas.drawRect(o.x + o.width - 20, o.y + o.height - 15, o.x + o.width - 6, o.y + o.height - 4, paint);
    }

    private void drawPlayerCar(Canvas canvas) {
        String body, glass;
        switch (selectedCarId) {
            case 1: body = "#E53935"; glass = "#FFCDD2"; break;
            case 2: body = "#1565C0"; glass = "#BBDEFB"; break;
            case 3: body = "#F57C00"; glass = "#FFE0B2"; break;
            case 4: body = "#455A64"; glass = "#B0BEC5"; break;
            case 5: body = "#2E7D32"; glass = "#C8E6C9"; break;
            default: body = "#1565C0"; glass = "#BBDEFB";
        }

        paint.setColor(Color.parseColor(body));
        canvas.drawRoundRect(new RectF(carX, carY, carX + carWidth, carY + carHeight), 16, 16, paint);
        paint.setColor(Color.parseColor(glass));
        canvas.drawRoundRect(new RectF(carX + 14, carY + 14, carX + carWidth - 14, carY + 44), 10, 10, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(carX + 18, carY + 7, 8, paint);
        canvas.drawCircle(carX + carWidth - 18, carY + 7, 8, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(carX + 8, carY + carHeight - 16, carX + 24, carY + carHeight - 5, paint);
        canvas.drawRect(carX + carWidth - 24, carY + carHeight - 16, carX + carWidth - 8, carY + carHeight - 5, paint);

        if (hasShield) {
            paint.setColor(Color.parseColor("#00E676"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(7);
            canvas.drawCircle(carX + carWidth / 2f, carY + carHeight / 2f, carWidth / 2f + 16, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    private void drawItem(Canvas canvas, GameItem item) {
        float cx = item.x + item.width / 2f;
        float cy = item.y + item.height / 2f;

        switch (item.type) {
            case COIN:
                paint.setColor(Color.parseColor("#FFD600"));
                canvas.drawCircle(cx, cy, item.width / 2f, paint);
                paint.setColor(Color.parseColor("#FF6F00"));
                canvas.drawCircle(cx, cy, item.width / 2f - 7, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(24);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("$", cx, cy + 9, paint);
                paint.setTextAlign(Paint.Align.LEFT);
                break;
            case GEM:
                paint.setColor(Color.parseColor("#00E5FF"));
                Path gem = new Path();
                float h = item.width / 2f;
                gem.moveTo(cx, cy - h);
                gem.lineTo(cx + h, cy);
                gem.lineTo(cx, cy + h);
                gem.lineTo(cx - h, cy);
                gem.close();
                canvas.drawPath(gem, paint);
                break;
            case SHIELD:
                paint.setColor(Color.parseColor("#2979FF"));
                canvas.drawCircle(cx, cy, item.width / 2f, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(26);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("S", cx, cy + 10, paint);
                paint.setTextAlign(Paint.Align.LEFT);
                break;
            case STAR:
                paint.setColor(Color.parseColor("#FFD600"));
                drawStar(canvas, cx, cy, item.width / 2f);
                break;
        }
    }

    private void drawStar(Canvas canvas, float cx, float cy, float r) {
        Path p = new Path();
        double a = Math.PI / 5;
        for (int i = 0; i < 5; i++) {
            float x = (float) (cx + r * Math.cos(i * 2 * a - Math.PI / 2));
            float y = (float) (cy + r * Math.sin(i * 2 * a - Math.PI / 2));
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
            float x2 = (float) (cx + (r / 2.3f) * Math.cos((i * 2 + 1) * a - Math.PI / 2));
            float y2 = (float) (cy + (r / 2.3f) * Math.sin((i * 2 + 1) * a - Math.PI / 2));
            p.lineTo(x2, y2);
        }
        p.close();
        canvas.drawPath(p, paint);
    }

    private void drawHUD(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#CC000000"));
        canvas.drawRoundRect(new RectF(16, 100, 300, 280), 20, 20, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("🏁  " + score, 36, 145, paint);
        canvas.drawText("📏  " + distance + " m", 36, 183, paint);
        canvas.drawText("💰  " + coinsCollected, 36, 221, paint);
        canvas.drawText("💎  " + gemsCollected, 36, 259, paint);

        paint.setFakeBoldText(false);

        if (hasShield) {
            paint.setColor(Color.parseColor("#00E676"));
            paint.setTextSize(26);
            canvas.drawText("🛡️ SHIELD", screenWidth - 180, 110, paint);
        }

        if (comboCount >= 5) {
            paint.setColor(Color.parseColor("#FFD600"));
            paint.setTextSize(34);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("🔥 x" + comboCount + " COMBO!", screenWidth / 2f, 75, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }

    private class GameThread extends Thread {
        private final SurfaceHolder holder;
        private boolean running = false;
        private long last = System.currentTimeMillis();

        GameThread(SurfaceHolder h) { holder = h; }

        void setRunning(boolean r) { running = r; }

        @Override
        public void run() {
            while (running) {
                long now = System.currentTimeMillis();
                if (now - last >= 16) {
                    Canvas c = null;
                    try {
                        c = holder.lockCanvas();
                        if (c != null) {
                            synchronized (holder) {
                                update();
                                draw(c);
                            }
                        }
                    } finally {
                        if (c != null) holder.unlockCanvasAndPost(c);
                    }
                    last = now;
                }
            }
        }
    }

    private static class Obstacle {
        float x, y;
        int width, height;
        Obstacle(float x, float y, int w, int h) {
            this.x = x; this.y = y; width = w; height = h;
        }
    }
}