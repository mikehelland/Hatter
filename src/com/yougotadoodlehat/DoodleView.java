package com.yougotadoodlehat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * User: m
 * Date: 10/31/14
 * Time: 3:52 AM
 */
public class DoodleView extends View {

    Paint mPaint = new Paint();
    List<DoodleLine> doodleLines = new ArrayList<DoodleLine>();
    DoodleLine currentLine;

    private boolean isTouching = false;

    private Path currentPath;
    private List<Path> paths = new ArrayList<Path>();

    private OnChangeListener mOnChangeListener;

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint.setARGB(255, 255, 0, 0);
        mPaint.setShadowLayer(10, 10, 10, Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        float xp = x / getWidth();
        float yp = y / getHeight();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            currentPath = new Path();
            currentPath.moveTo(x, y);
            paths.add(currentPath);

            currentLine = new DoodleLine();
            doodleLines.add(currentLine);
            currentLine.add(new DoodleTouch(xp, yp));

            isTouching = true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isTouching){
                currentPath.lineTo(x, y);
                currentPath.moveTo(x, y);

                currentLine.add(new DoodleTouch(xp, yp));
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isTouching){
                currentPath.lineTo(x, y);
                currentPath.moveTo(x, y);

                currentLine.add(new DoodleTouch(xp, yp));
                onChange();
            }

            isTouching = false;
            currentLine = null;
        }

        invalidate();
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.d("MGH draing path", "?");
        for (Path path: paths) {
            canvas.drawPath(path, mPaint);
        }
    }

    public void clear() {
        doodleLines.clear();
        paths.clear();
        postInvalidate();
    }

    public void setPaths(List<Path> newPaths) {
        paths = newPaths;
        postInvalidate();
    }

    private void onChange() {
        if (mOnChangeListener != null)
            mOnChangeListener.onChange(doodleLines);
    }

    static abstract public class OnChangeListener {
        abstract public void onChange(List<DoodleLine> doodle);
    }

    public void setOnChangeListener(OnChangeListener listener) {
        mOnChangeListener = listener;
    }
}
