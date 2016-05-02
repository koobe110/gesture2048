package com.along.a2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Administrator on 2016/4/19.
 */
public class ItemVIEW extends View {
    private Paint paint;
    private int num;
    Rect textBound;

    public ItemVIEW(Context context) {
        super(context);
        paint = new Paint();
    }

    public void setNum(int num) {
        this.num = num;
        paint.setTextSize(40.0f);
        textBound = new Rect();
        paint.getTextBounds(String.valueOf(num), 0, String.valueOf(num).length(), textBound);
        invalidate();
    }

    public int getNum() {
        return num;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String color = " ";
        int backGroundPic;
        switch (num) {
            case 0:
                color = "#CCC0B3";
                break;
            case 2:
                color = "#EEE4DA";
                backGroundPic = R.drawable.xulongxi;
                break;
            case 4:
                color = "#EDE0C8";
                break;
            case 8:
                color = "#F2B179";
                break;
            case 16:
                color = "#F49563";
                break;
            case 32:
                color = "#F5794D";
                break;
            case 64:
                color = "#F55D37";
                break;
            case 128:
                color = "#EEE863";
                break;
            case 256:
                color = "#EDB04D";
                break;
            case 512:
                color = "#ECB04D";
                break;
            case 1024:
                color = "#EB9437";
                break;
            case 2048:
                color = "#EA7821";
                break;
            default:
                color = "#EA7821";
                break;
        }
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 10, 10, paint);
//        setBackgroundResource(backGroundPic);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        paint.setColor(Color.BLACK);
        float x = (getWidth() - paint.measureText(String.valueOf(num)))/2;
        float y = (getHeight() + textBound.height())/2;
        if(num != 0){
            canvas.drawText(String.valueOf(num), x, y, paint);

        }else{
            canvas.drawText("", x, y, paint);

        }
    }

}
