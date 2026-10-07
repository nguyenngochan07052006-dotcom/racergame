package com.yourpackage.racergame.game.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.yourpackage.racergame.utils.GameConstants;

public class RoadRenderer {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void draw(Canvas canvas, int screenWidth, int screenHeight,
                     float roadLeft, float roadRight, float laneWidth, int distance) {

        // Nền
        paint.setColor(Color.parseColor(GameConstants.COLOR_BACKGROUND));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        // Vỉa hè
        paint.setColor(Color.parseColor(GameConstants.COLOR_ROADSIDE));
        canvas.drawRect(0, 0, roadLeft, screenHeight, paint);
        canvas.drawRect(roadRight, 0, screenWidth, screenHeight, paint);

        // Đường
        paint.setColor(Color.parseColor(GameConstants.COLOR_ROAD));
        canvas.drawRect(roadLeft, 0, roadRight, screenHeight, paint);

        // Vạch phân làn
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
    }
}