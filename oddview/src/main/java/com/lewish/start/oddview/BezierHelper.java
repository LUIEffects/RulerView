package com.lewish.start.oddview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

/**
 * Created by Administrator on 2017/11/29 10:18.
 */

public class BezierHelper {
    private Path mScaleBezierPath;
    private PathMeasure mScaleBezierPathMeasure;
    private float mScaleBezierPos[];

    float scaleBezierStartX;
    float scaleBezierStartY;
    float scaleBezierControlX;
    float scaleBezierControlY;
    float scaleBezierEndX;
    float scaleBezierEndY;
    float pathLength;
    float targetPer;

    public BezierHelper() {
        mScaleBezierPath = new Path();
        mScaleBezierPathMeasure = new PathMeasure();
        mScaleBezierPos = new float[2];
    }

    public void init(float scaleBezierStartX, float scaleBezierStartY, float scaleBezierControlX, float scaleBezierControlY, float scaleBezierEndX, float scaleBezierEndY) {
        this.scaleBezierStartX = scaleBezierControlX;
        this.scaleBezierStartY = scaleBezierStartY;
        this.scaleBezierControlX = scaleBezierControlX;
        this.scaleBezierControlY = scaleBezierControlY;
        this.scaleBezierEndX = scaleBezierEndX;
        this.scaleBezierEndY = scaleBezierEndY;
        mScaleBezierPath.reset();
        mScaleBezierPath.moveTo(scaleBezierStartX, scaleBezierStartY);
        mScaleBezierPath.quadTo(scaleBezierControlX, scaleBezierControlY, scaleBezierEndX, scaleBezierEndY);
        mScaleBezierPathMeasure.setPath(mScaleBezierPath, false);
        pathLength = mScaleBezierPathMeasure.getLength();
    }

    public float getX(float targetPer) {
        mScaleBezierPathMeasure.getPosTan(pathLength * targetPer, mScaleBezierPos, null);
        mScaleBezierPathMeasure.getSegment(0, pathLength * targetPer, mScaleBezierPath, true);
        return mScaleBezierPos[0];
    }

    public float[] getPos(float targetPer) {
        mScaleBezierPathMeasure.getPosTan(pathLength * targetPer, mScaleBezierPos, null);
        mScaleBezierPathMeasure.getSegment(0, pathLength * targetPer, mScaleBezierPath, true);
        return mScaleBezierPos;
    }

    public void drawPath(Canvas canvas, Paint paint){
        canvas.drawPath(mScaleBezierPath,paint);
    }
}
