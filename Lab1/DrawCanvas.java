package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DrawCanvas extends View{

    public Canvas canvas;
    public Paint p;
    private Bitmap og_bitmap, bitmap;
    float x, y;
    int bgColor, strokeWidth;

    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        bgColor = Color.WHITE; // bg color setting

        og_bitmap = Bitmap.createBitmap(
                context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels - 200,
                Bitmap.Config.ARGB_8888);
        bitmap = Bitmap.createBitmap(og_bitmap);

        canvas = new Canvas(bitmap);
        canvas.drawColor(bgColor);

        strokeWidth = 8;

        p = new Paint(Paint.DITHER_FLAG);
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(strokeWidth);
    }



    //touch event
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            //拖动屏幕
            canvas.drawLine(x,y,
                    event.getX(), event.getY(), p);
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            //按下屏幕
            x = event.getX();
            y = event.getY();
            canvas.drawPoint(x, y, p);
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            //松开屏幕
        }
        x = event.getX();
        y = event.getY();
        return true;
    }

    @Override
    public void onDraw(Canvas c){
        super.onDraw(c);
        c.drawBitmap(bitmap, 0, 0, null);
    }

    public void smallByOne(){
        if (strokeWidth > 1){
            strokeWidth -= 1;
            p.setStrokeWidth(strokeWidth);
        }
    }

    public void largeByOne(){
        if (strokeWidth < 20){
            strokeWidth += 1;
            p.setStrokeWidth(strokeWidth);
        }
    }

    public void pencil(){
        p.setColor(Color.RED);
        p.setStrokeWidth(strokeWidth);
    }

    public void eraser(){
        p.setColor(Color.WHITE);
        p.setStrokeWidth(40);
    }

    public void clear(){
        bitmap = Bitmap.createBitmap(og_bitmap);
        invalidate();
        canvas = new Canvas(bitmap);
        canvas.drawColor(bgColor);
    }

}
