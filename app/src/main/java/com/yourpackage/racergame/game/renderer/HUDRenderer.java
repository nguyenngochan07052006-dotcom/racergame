package com.yourpackage.racergame.game.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import com.yourpackage.racergame.game.manager.ScoreManager;

public class HUDRenderer {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void draw(Canvas canvas, ScoreManager score, int screenWidth, boolean hasShield) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#CC000000"));
        canvas.drawRoundRect(new RectF(16, 100, 310, 320), 20, 20, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(28);
        paint.setFakeBoldText(true);

        canvas.drawText("🏁  " + score.getScore(), 36, 145, paint);
        canvas.drawText("📏  " + score.getDistance() + " m", 36, 185, paint);
        canvas.drawText("💰  " + score.getCoins(), 36, 225, paint);
        canvas.drawText("💎  " + score.getGems(), 36, 265, paint);
        canvas.drawText("⭐  Level " + score.getLevel(), 36, 305, paint);

        paint.setFakeBoldText(false);

        if (hasShield) {
            paint.setColor(Color.parseColor("#00E676"));
            paint.setTextSize(26);
            canvas.drawText("🛡️ SHIELD", screenWidth - 180, 110, paint);
        }

        if (score.getCombo() >= 5) {
            paint.setColor(Color.parseColor("#FFD600"));
            paint.setTextSize(34);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("🔥 x" + score.getCombo() + " COMBO!", screenWidth / 2f, 75, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }
}