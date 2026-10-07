package com.yourpackage.racergame.game.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import com.yourpackage.racergame.game.model.GameItem;

public class ItemRenderer {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void draw(Canvas canvas, GameItem item) {
        float cx = item.x + item.width / 2f;
        float cy = item.y + item.height / 2f;

        if (item.type == GameItem.Type.COIN) {
            paint.setColor(Color.parseColor("#FFD600"));
            canvas.drawCircle(cx, cy, item.width / 2f, paint);
            paint.setColor(Color.parseColor("#FF6F00"));
            canvas.drawCircle(cx, cy, item.width / 2f - 7, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(24);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("$", cx, cy + 9, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        } else {
            paint.setColor(Color.parseColor("#00E5FF"));
            Path gem = new Path();
            float h = item.width / 2f;
            gem.moveTo(cx, cy - h);
            gem.lineTo(cx + h, cy);
            gem.lineTo(cx, cy + h);
            gem.lineTo(cx - h, cy);
            gem.close();
            canvas.drawPath(gem, paint);
        }
    }
}