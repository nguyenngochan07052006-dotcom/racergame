package com.yourpackage.racergame.game.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import com.yourpackage.racergame.game.model.Car;

public class CarRenderer {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void draw(Canvas canvas, Car car) {
        String body, glass;
        switch (car.carId) {
            case 1: body = "#E53935"; glass = "#FFCDD2"; break;
            case 2: body = "#1565C0"; glass = "#BBDEFB"; break;
            case 3: body = "#F57C00"; glass = "#FFE0B2"; break;
            case 4: body = "#455A64"; glass = "#B0BEC5"; break;
            case 5: body = "#2E7D32"; glass = "#C8E6C9"; break;
            default: body = "#1565C0"; glass = "#BBDEFB";
        }

        paint.setColor(Color.parseColor(body));
        canvas.drawRoundRect(new RectF(car.x, car.y, car.x + car.width, car.y + car.height), 16, 16, paint);

        paint.setColor(Color.parseColor(glass));
        canvas.drawRoundRect(new RectF(car.x + 14, car.y + 14, car.x + car.width - 14, car.y + 44), 10, 10, paint);

        paint.setColor(Color.YELLOW);
        canvas.drawCircle(car.x + 18, car.y + 7, 8, paint);
        canvas.drawCircle(car.x + car.width - 18, car.y + 7, 8, paint);

        paint.setColor(Color.BLACK);
        canvas.drawRect(car.x + 8, car.y + car.height - 16, car.x + 24, car.y + car.height - 5, paint);
        canvas.drawRect(car.x + car.width - 24, car.y + car.height - 16, car.x + car.width - 8, car.y + car.height - 5, paint);

        if (car.hasShield) {
            paint.setColor(Color.parseColor("#00E676"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(7);
            canvas.drawCircle(car.x + car.width / 2f, car.y + car.height / 2f, car.width / 2f + 16, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }
}