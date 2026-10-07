package com.yourpackage.racergame.game.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import com.yourpackage.racergame.game.model.Obstacle;

public class ObstacleRenderer {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void draw(Canvas canvas, Obstacle o) {
        switch (o.type) {
            case ROCK:
                paint.setColor(Color.parseColor("#5D4037"));
                canvas.drawRoundRect(new RectF(o.x, o.y, o.x + o.width, o.y + o.height), 18, 18, paint);
                paint.setColor(Color.parseColor("#3E2723"));
                canvas.drawCircle(o.x + o.width * 0.3f, o.y + o.height * 0.4f, 9, paint);
                break;

            case FENCE:
                paint.setColor(Color.parseColor("#8D6E63"));
                canvas.drawRect(o.x, o.y + 8, o.x + o.width, o.y + o.height - 8, paint);
                paint.setColor(Color.parseColor("#5D4037"));
                for (int i = 0; i < 4; i++) {
                    float fx = o.x + 8 + i * 22;
                    canvas.drawRect(fx, o.y, fx + 8, o.y + o.height, paint);
                }
                break;

            case BARRIER:
                paint.setColor(Color.parseColor("#EF6C00"));
                canvas.drawRoundRect(new RectF(o.x, o.y, o.x + o.width, o.y + o.height), 8, 8, paint);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(4);
                canvas.drawLine(o.x + 10, o.y + o.height / 2f, o.x + o.width - 10, o.y + o.height / 2f, paint);
                break;
        }
    }
}