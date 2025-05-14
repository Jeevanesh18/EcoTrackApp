package com.example.ecotrack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Utils {
    // Generate square bitmap for confetti
    public static Bitmap generateSquareBitmap(int color, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0, 0, size, size, paint); // Draw a square
        return bitmap;
    }

    // Generate circle bitmap for confetti
    public static Bitmap generateCircleBitmap(int color, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint); // Draw a circle
        return bitmap;
    }
}

