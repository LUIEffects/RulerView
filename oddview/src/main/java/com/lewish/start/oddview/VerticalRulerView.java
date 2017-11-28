package com.lewish.start.oddview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

/**
 * Created by lsp on 2017/10/13 10 47
 * Email:6161391073@qq.com
 */

public class VerticalRulerView extends View {
    private static final String TAG = "RulerView";
    /**
     * 尺子高度
     */
    private int rulerHeight = 50;
    /**
     * 尺子和屏幕顶部以及结果之间的高度
     */
    private int rulerToResultgap = rulerHeight / 4;
    /**
     * 刻度平分多少份
     */
    private int scaleCount = 10;  //刻度评分多少份
    /**
     * 刻度间距
     */
    private int scaleGap = 20;
    /**
     * 刻度最小值
     */
    private int minScale = 0;
    /**
     * 第一次显示的刻度
     */
    private float firstScale = 50f;
    /**
     * 刻度最大值
     */
    private int maxScale = 100;

    /**
     * 背景颜色
     */
    private int bgColor = 0xfffcfffc;
    /**
     * 小刻度的颜色
     */
    private int smallScaleColor = 0xff999999;
    /**
     * 中刻度的颜色
     */
    private int midScaleColor = 0xff666666;
    /**
     * 大刻度的颜色
     */
    private int largeScaleColor = 0xff50b586;
    /**
     * 刻度颜色
     */
    private int scaleNumColor = 0xff333333;
    /**
     * 结果值颜色
     */
    private int resultNumColor = 0xff50b586;
    /**
     * kg颜色
     */
    private String unit = "kg";
    /**
     * kg颜色
     */
    private int unitColor = 0xff50b586;
    /**
     * 小刻度粗细大小
     */
    private int smallScaleStroke = 1;
    /**
     * 中刻度粗细大小
     */
    private int midScaleStroke = 2;
    /**
     * 大刻度粗细大小
     */
    private int largeScaleStroke = 3;
    /**
     * 结果字体大小
     */
    private int resultNumTextSize = 20;
    /**
     * 刻度字体大小
     */
    private int scaleNumTextSize = 16;
    /**
     * 单位字体大小
     */
    private int unitTextSize = 13;
    /**
     * 是否显示刻度结果
     */
    private boolean showScaleResult = true;
    /**
     * 是否背景显示圆角
     */
    private boolean isBgRoundRect = true;

    /**
     * 结果回调
     */
    private OnChooseResulterListener onChooseResulterListener;
    private ValueAnimator valueAnimator;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private String resultText = String.valueOf(firstScale);
    private Paint bgPaint;
    private Paint smallScalePaint;
    private Paint midScalePaint;
    private Paint lagScalePaint;
    private Paint scaleNumPaint;
    private Paint resultNumPaint;
    private Paint kgPaint;
    private Rect scaleNumRect;
    private Rect resultNumRect;
    private Rect kgRect;
    private RectF bgRect;
    private int height, width;
    private int smallScaleHeight;
    private int midScaleHeight;
    private int lagScaleHeight;
    private int rulerBottom = 0;
    private int resultNumRight;
    private float downY;
    private float moveY = 0;
    private float currentY;
    private float lastMoveY = 0;
    private boolean isUp = false;
    private int topScroll;
    private int bottomScroll;
    private int yVelocity;

    public VerticalRulerView(Context context) {
        this(context, null);
    }

    public VerticalRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttr(attrs, defStyleAttr);
        init();
    }

    public void setOnChooseResulterListener(OnChooseResulterListener onChooseResulterListener) {
        this.onChooseResulterListener = onChooseResulterListener;
    }

    private void setAttr(AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RulerView, defStyleAttr, 0);

        rulerHeight = a.getDimensionPixelSize(R.styleable.RulerView_rulerHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, rulerHeight, getResources().getDisplayMetrics()));

        rulerToResultgap = a.getDimensionPixelSize(R.styleable.RulerView_rulerToResultgap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, rulerToResultgap, getResources().getDisplayMetrics()));

        scaleCount = a.getInt(R.styleable.RulerView_scaleCount, scaleCount);

        scaleGap = a.getDimensionPixelSize(R.styleable.RulerView_scaleGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scaleGap, getResources().getDisplayMetrics()));

        minScale = a.getInt(R.styleable.RulerView_minScale, minScale);

        firstScale = a.getFloat(R.styleable.RulerView_firstScale, firstScale);

        maxScale = a.getInt(R.styleable.RulerView_maxScale, maxScale);

        bgColor = a.getColor(R.styleable.RulerView_bgColor, bgColor);

        smallScaleColor = a.getColor(R.styleable.RulerView_smallScaleColor, smallScaleColor);

        midScaleColor = a.getColor(R.styleable.RulerView_midScaleColor, midScaleColor);

        largeScaleColor = a.getColor(R.styleable.RulerView_largeScaleColor, largeScaleColor);

        scaleNumColor = a.getColor(R.styleable.RulerView_scaleNumColor, scaleNumColor);

        resultNumColor = a.getColor(R.styleable.RulerView_resultNumColor, resultNumColor);

        unitColor = a.getColor(R.styleable.RulerView_unitColor, unitColor);

        String tempUnit = unit;
        unit = a.getString(R.styleable.RulerView_unit);
        if (TextUtils.isEmpty(unit)) {
            unit = tempUnit;
        }

        smallScaleStroke = a.getDimensionPixelSize(R.styleable.RulerView_smallScaleStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, smallScaleStroke, getResources().getDisplayMetrics()));

        midScaleStroke = a.getDimensionPixelSize(R.styleable.RulerView_midScaleStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, midScaleStroke, getResources().getDisplayMetrics()));
        largeScaleStroke = a.getDimensionPixelSize(R.styleable.RulerView_largeScaleStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, largeScaleStroke, getResources().getDisplayMetrics()));
        resultNumTextSize = a.getDimensionPixelSize(R.styleable.RulerView_resultNumTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, resultNumTextSize, getResources().getDisplayMetrics()));

        scaleNumTextSize = a.getDimensionPixelSize(R.styleable.RulerView_scaleNumTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, scaleNumTextSize, getResources().getDisplayMetrics()));

        unitTextSize = a.getDimensionPixelSize(R.styleable.RulerView_unitTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, unitTextSize, getResources().getDisplayMetrics()));


        showScaleResult = a.getBoolean(R.styleable.RulerView_showScaleResult, showScaleResult);
        isBgRoundRect = a.getBoolean(R.styleable.RulerView_isBgRoundRect, isBgRoundRect);

        a.recycle();
    }


    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        midScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lagScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        resultNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        kgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        bgPaint.setColor(bgColor);
        smallScalePaint.setColor(smallScaleColor);
        midScalePaint.setColor(midScaleColor);
        lagScalePaint.setColor(largeScaleColor);
        scaleNumPaint.setColor(scaleNumColor);
        resultNumPaint.setColor(resultNumColor);
        kgPaint.setColor(unitColor);

        resultNumPaint.setStyle(Paint.Style.FILL);
        kgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStyle(Paint.Style.FILL);
        smallScalePaint.setStyle(Paint.Style.FILL);
        midScalePaint.setStyle(Paint.Style.FILL);
        lagScalePaint.setStyle(Paint.Style.FILL);

        lagScalePaint.setStrokeCap(Paint.Cap.ROUND);
        midScalePaint.setStrokeCap(Paint.Cap.ROUND);
        smallScalePaint.setStrokeCap(Paint.Cap.ROUND);

        smallScalePaint.setStrokeWidth(smallScaleStroke);
        midScalePaint.setStrokeWidth(midScaleStroke);
        lagScalePaint.setStrokeWidth(largeScaleStroke);

        resultNumPaint.setTextSize(resultNumTextSize);
        kgPaint.setTextSize(unitTextSize);
        scaleNumPaint.setTextSize(scaleNumTextSize);

        bgRect = new RectF();
        resultNumRect = new Rect();
        scaleNumRect = new Rect();
        kgRect = new Rect();

        resultNumPaint.getTextBounds(resultText, 0, resultText.length(), resultNumRect);
        kgPaint.getTextBounds(resultText, 0, 1, kgRect);

        smallScaleHeight = rulerHeight / 4;
        midScaleHeight = rulerHeight / 2;
        lagScaleHeight = rulerHeight / 2 + 5;
        valueAnimator = new ValueAnimator();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightModule = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        switch (heightModule) {
            case MeasureSpec.AT_MOST:
                height = rulerHeight + (showScaleResult ? resultNumRect.height() : 0) + rulerToResultgap * 2 + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                height = heightSize + getPaddingTop() + getPaddingBottom();
                break;
        }
        height = heightSize + getPaddingTop() + getPaddingBottom();
        width = widthSize + getPaddingLeft() + getPaddingRight();

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBg(canvas);
        drawScaleAndNum(canvas);
//        drawResultText(canvas, resultText);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentY = event.getY();
        isUp = false;
        velocityTracker.computeCurrentVelocity(500);
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下时如果属性动画还没执行完,就终止,记录下当前按下点的位置
                if (valueAnimator != null && valueAnimator.isRunning()) {
                    valueAnimator.end();
                    valueAnimator.cancel();
                }
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //滑动时候,通过假设的滑动距离,做超出左边界以及右边界的限制。
                moveY = currentY - downY + lastMoveY;
                if (moveY >= height / 2) {
                    moveY = height / 2;
                } else if (moveY <= getWhichScalMoveY(maxScale)) {
                    moveY = getWhichScalMoveY(maxScale);
                }
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起时候制造惯性滑动
                lastMoveY = moveY;
                yVelocity = (int) velocityTracker.getYVelocity();
                autoVelocityScroll(yVelocity);
                velocityTracker.clear();
                break;
        }
        invalidate();
        return true;
    }

    private void autoVelocityScroll(int yVelocity) {
        //惯性滑动的代码,速率和滑动距离,以及滑动时间需要控制的很好,应该网上已经有关于这方面的算法了吧。。这里是经过N次测试调节出来的惯性滑动
        if (Math.abs(yVelocity) < 50) {
            isUp = true;
            return;
        }
        if (valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofInt(0, yVelocity / 20).setDuration(Math.abs(yVelocity / 10));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveY += (int) animation.getAnimatedValue();
                if (moveY >= height / 2) {
                    moveY = height / 2;
                } else if (moveY <= getWhichScalMoveY(maxScale)) {
                    moveY = getWhichScalMoveY(maxScale);
                }
                lastMoveY = moveY;
                invalidate();
            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isUp = true;
                invalidate();
            }
        });

        valueAnimator.start();
    }

    private float getWhichScalMoveY(float scale) {

        return height / 2 - scaleGap * scaleCount * (scale - minScale);
    }

    private void drawScaleAndNum(Canvas canvas) {
        canvas.translate((showScaleResult ? resultNumRect.width() : 0) + rulerToResultgap, 0);//移动画布到结果值的下面

        int num1;//确定刻度位置
        float num2;

        if (firstScale != -1) {   //第一次进来的时候计算出默认刻度对应的假设滑动的距离moveX
            moveY = getWhichScalMoveY(firstScale);
            lastMoveY = moveY;
            firstScale = -1;
        }

        num1 = -(int) (moveY / scaleGap);//小刻度值
        num2 = (moveY % scaleGap);//偏移量

        canvas.save();

        rulerBottom = 0;

        if (isUp) {   //这部分代码主要是计算手指抬起时，惯性滑动结束时，刻度需要停留的位置
            num2 = ((moveY - height / 2 % scaleGap) % scaleGap);
            if (num2 <= 0) {
                num2 = scaleGap - Math.abs(num2);
            }
            topScroll = (int) Math.abs(num2);
            bottomScroll = (int) (scaleGap - Math.abs(num2));

            float moveY2 = num2 <= scaleGap / 2 ? moveY - topScroll : moveY + bottomScroll;

            if (valueAnimator != null && !valueAnimator.isRunning()) {
                valueAnimator = ValueAnimator.ofFloat(moveY, moveY2);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        moveY = (float) animation.getAnimatedValue();
                        lastMoveY = moveY;
                        invalidate();
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //这里是滑动结束时候回调给使用者的结果值
                        if (onChooseResulterListener != null) {
                            onChooseResulterListener.onEndResult(resultText);
                        }
                    }
                });
                valueAnimator.setDuration(300);
                valueAnimator.start();
                isUp = false;
            }

            num1 = (int) -(moveY / scaleGap);
            num2 = (moveY % scaleGap);
        }
        canvas.translate(0, num2);    //不加该偏移的话，滑动时刻度不会落在0~1之间只会落在整数上面,其实这个都能设置一种模式了，毕竟初衷就是指针不会落在小数上面
        //这里是滑动时候不断回调给使用者的结果值
        resultText = String.valueOf(new WeakReference<>(new BigDecimal((height / 2 - moveY) / (scaleGap * scaleCount))).get().setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + minScale);
        if (onChooseResulterListener != null) {
            onChooseResulterListener.onScrollResult(resultText);
        }
        //绘制当前屏幕可见刻度,不需要裁剪屏幕,while循环只会执行·屏幕宽度/刻度宽度·次
        while (rulerBottom < height) {
            if (num1 % scaleCount == 0) {
                if ((moveY >= 0 && rulerBottom < moveY - scaleGap) || height / 2 - rulerBottom <= getWhichScalMoveY(maxScale + 1) - moveY) {   //去除上下边界

                } else {
                    canvas.drawLine(0, 0, midScaleHeight, 0, midScalePaint);
                    scaleNumPaint.getTextBounds(num1 / scaleGap + minScale + "", 0, (num1 / scaleGap + minScale + "").length(), scaleNumRect);
                    canvas.drawText(num1 / scaleCount + minScale + "",
                            lagScaleHeight + (rulerHeight - lagScaleHeight) / 2 + scaleNumRect.width(),
                            +scaleNumRect.height() / 2,
                            scaleNumPaint);


                }

            } else {
                if ((moveY >= 0 && rulerBottom < moveY) || height / 2 - rulerBottom < getWhichScalMoveY(maxScale) - moveY) {   //去除左右边界

                } else {
                    canvas.drawLine(0, 0, smallScaleHeight, 0, smallScalePaint);
                }
            }
            ++num1;
            rulerBottom += scaleGap;
            canvas.translate(0, scaleGap);
        }

        canvas.restore();
        //绘制屏幕中间用来选中刻度的最大刻度
        canvas.drawLine(0, height / 2, lagScaleHeight, height / 2, lagScalePaint);

    }

    //绘制上面的结果 结果值+单位
    private void drawResultText(Canvas canvas, String resultText) {
        if (!showScaleResult) {
            return;
        }
        canvas.translate(-resultNumRect.width() - rulerToResultgap / 2, 0);
        resultNumPaint.getTextBounds(resultText, 0, resultText.length(), resultNumRect);
        canvas.drawText(resultText, resultNumRect.width(), height / 2 - resultNumRect.height(),
                resultNumPaint);
    }

    private void drawBg(Canvas canvas) {
        bgRect.set(0, 0, width, height);
        if (isBgRoundRect) {
            canvas.drawRoundRect(bgRect, 20, 20, bgPaint); //20->椭圆的用于圆形角x-radius
        } else {
            canvas.drawRect(bgRect, bgPaint);
        }
    }

    public interface OnChooseResulterListener {
        void onEndResult(String result);

        void onScrollResult(String result);
    }

//    public void setRulerHeight(int rulerHeight) {
//        this.rulerHeight = rulerHeight;
//        invalidate();
//    }
//
//    public void setRulerToResultgap(int rulerToResultgap) {
//        this.rulerToResultgap = rulerToResultgap;
//        invalidate();
//    }
//
//    public void setScaleCount(int scaleCount) {
//        this.scaleCount = scaleCount;
//        invalidate();
//    }
//
//    public void setScaleGap(int scaleGap) {
//        this.scaleGap = scaleGap;
//        invalidate();
//    }
//
//    public void setMinScale(int minScale) {
//        this.minScale = minScale;
//        invalidate();
//    }
//
//    public void setFirstScale(float firstScale) {
//        this.firstScale = firstScale;
//        invalidate();
//    }
//
//    public void setMaxScale(int maxScale) {
//        this.maxScale = maxScale;
//        invalidate();
//    }
//
//    public void setBgColor(int bgColor) {
//        this.bgColor = bgColor;
//        invalidate();
//    }
//
//    public void setSmallScaleColor(int smallScaleColor) {
//        this.smallScaleColor = smallScaleColor;
//        invalidate();
//    }
//
//    public void setMidScaleColor(int midScaleColor) {
//        this.midScaleColor = midScaleColor;
//        invalidate();
//    }
//
//    public void setLargeScaleColor(int largeScaleColor) {
//        this.largeScaleColor = largeScaleColor;
//    }
//
//    public void setScaleNumColor(int scaleNumColor) {
//        this.scaleNumColor = scaleNumColor;
//        invalidate();
//    }
//
//    public void setResultNumColor(int resultNumColor) {
//        this.resultNumColor = resultNumColor;
//        invalidate();
//    }
//
//    public void setUnit(String unit) {
//        this.unit = unit;
//        invalidate();
//    }
//
//    public void setUnitColor(int unitColor) {
//        this.unitColor = unitColor;
//        invalidate();
//    }
//
//    public void setSmallScaleStroke(int smallScaleStroke) {
//        this.smallScaleStroke = smallScaleStroke;
//        invalidate();
//    }
//
//    public void setMidScaleStroke(int midScaleStroke) {
//        this.midScaleStroke = midScaleStroke;
//        invalidate();
//    }
//
//    public void setLargeScaleStroke(int largeScaleStroke) {
//        this.largeScaleStroke = largeScaleStroke;
//        invalidate();
//    }
//
//    public void setResultNumTextSize(int resultNumTextSize) {
//        this.resultNumTextSize = resultNumTextSize;
//        invalidate();
//    }
//
//    public void setScaleNumTextSize(int scaleNumTextSize) {
//        this.scaleNumTextSize = scaleNumTextSize;
//        invalidate();
//    }
//
//    public void setUnitTextSize(int unitTextSize) {
//        this.unitTextSize = unitTextSize;
//        invalidate();
//    }
//
//    public void setShowScaleResult(boolean showScaleResult) {
//        this.showScaleResult = showScaleResult;
//        invalidate();
//    }
//
//    public void setIsBgRoundRect(boolean bgRoundRect) {
//        isBgRoundRect = bgRoundRect;
//        invalidate();
//    }
}
