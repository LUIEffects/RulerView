package com.lewish.start.oddview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * author: sundong
 * created at 2017/11/30 9:36
 */
public class VerticalRulerView extends View {

    private static final String TAG = "RulerView";
    //尺寸相关常量
    private static final int DEFAULT_SCALE_LINE_STROKE_DP = 2;
    private static final int DEFAULT_ALIGNMENT_STROKE_DP = 3;
    private static final int DEFAULT_SCALE_NUM_TEXTSIZE_SP = 16;
    private static final int DEFAULT_SCALE_GAP_DP = 20;
    private static final int DEFAULT_SCALE_LINE_LENGTH_DP = 20;
    //颜色相关常量
    private static final String DEFAULT_RIGHTAREA_COLOR = "#4f4f4f";
    private static final String DEFAULT_MIDDLEAREA_COLOR = "#555555";
    private static final String DEFAULT_LEFTAREA_COLOR = "#4a4a4a";
    private static final String DEFAULT_SCALE_LINE_COLOR = "#525252";
    private static final String DEFAULT_SELECTED_COLOR = "#ffd401";
    private static final String DEFAULT_SCALE_NUM_COLOR = "#bababa";
    //贝塞尔曲线相关常量
    private static final int RIGHT_BEZIER_CONTROL_POINT_OFFSET = -250;
    public static final int RIGHT_BEZIER_OFFSET = -50;
    public static final int MIDDLE_BEZIER_REL_OFFSET = -150;
    public static final int LEFT_BEZIER_REL_OFFSET = -200;
    //其他常量
    private static final int DEFAULT_SCALE_COUNT = 2;
    private static final int DEFAULT_MIN_SCALE = 0;
    private static final float DEFAULT_FIRST_SCALE = 50f;
    private static final int DEFAULT_MAX_SCALE = 100;
    private static final float DEFAULT_ALIGNMENT_POS = 1f / 8;//刻度线位置
    /**
     * Xml配置参数
     */
    //颜色
    private int bgColor = 0xfffcfffc;//背景颜色
    private int rightAreaColor;//右Arc Area颜色
    private int middleAreaColor;//中间Arc Area颜色
    private int leftAreaColor;//左边Arc Area颜色
    private int scaleLineColor;//刻度线颜色
    private int selectedColor;//选中时的颜色
    private int scaleNumColor;//数字颜色
    //画笔线宽
    private float scaleLineStroke;
    private int alignmentStroke;
    //线宽
    private int scaleLineLength;//刻度线
    private int alignmentWidth;//准线
    //其他配置参数
    private int scaleNumTextSize;
    private int scaleGap;
    private int scaleCount;  //刻度平分多少份
    private int minScale;
    private int maxScale;
    private float firstScale = DEFAULT_FIRST_SCALE;
    private boolean isBgRoundRect = true;//是否显示圆角
    /**
     * 代码中变量
     */
    //Paint
    private Paint bgPaint;//背景画笔
    private Paint mAreaPaint;//Arc区域画笔
    private Paint scaleNumPaint;//刻度数画笔
    private Paint scaleLinePaint;//刻度线画笔
    private Paint alignmentPaint;//选中准线画笔
    private Paint debugPaint;
    //Path
    private Path mRightArcPath;
    private Path mMiddleArcPath;
    private Path mLeftArcPath;
    //Rec
    private Rect scaleNumRect;
    private RectF bgRect;
    /**
     * 结果回调
     */
    private OnChooseResulterListener onChooseResulterListener;
    private ValueAnimator valueAnimator;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private String resultText = String.valueOf(firstScale);

    //计算逻辑相关变量
    private int rulerBottom = 0;
    private int height, width;
    private float downY;
    private float moveY = 0;
    private float currentY;
    private float lastMoveY = 0;
    private boolean isUp = false;
    private int topScroll;
    private int bottomScroll;
    private int yVelocity;
    private BezierHelper mBezierHelper;

    //业务逻辑
    private List<Integer> mList = Arrays.asList(500, 1000, 1500, 2000);

    public VerticalRulerView(Context context) {
        this(context, null);
    }

    public VerticalRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttr(attrs, defStyleAttr);
        init();
    }

    public void setOnChooseResulterListener(OnChooseResulterListener onChooseResulterListener) {
        this.onChooseResulterListener = onChooseResulterListener;
    }

    private void setAttr(AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RulerView, defStyleAttr, 0);
        /**
         * 颜色
         */
        bgColor = a.getColor(R.styleable.RulerView_bgColor, bgColor);
        rightAreaColor = a.getColor(R.styleable.RulerView_rightAreaColor, Color.parseColor(DEFAULT_RIGHTAREA_COLOR));
        middleAreaColor = a.getColor(R.styleable.RulerView_middleAreaColor, Color.parseColor(DEFAULT_MIDDLEAREA_COLOR));
        leftAreaColor = a.getColor(R.styleable.RulerView_leftAreaColor, Color.parseColor(DEFAULT_LEFTAREA_COLOR));
        scaleLineColor = a.getColor(R.styleable.RulerView_scaleLineColor, Color.parseColor(DEFAULT_SCALE_LINE_COLOR));
        selectedColor = a.getColor(R.styleable.RulerView_selectedColor, Color.parseColor(DEFAULT_SELECTED_COLOR));
        scaleNumColor = a.getColor(R.styleable.RulerView_scaleNumColor, Color.parseColor(DEFAULT_SCALE_NUM_COLOR));
        /**
         * 尺寸
         */
        scaleGap = a.getDimensionPixelSize(R.styleable.RulerView_scaleGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SCALE_GAP_DP, getResources().getDisplayMetrics()));
        scaleLineLength = a.getDimensionPixelSize(R.styleable.RulerView_scaleLineLength, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SCALE_LINE_LENGTH_DP, getResources().getDisplayMetrics()));
        scaleNumTextSize = a.getDimensionPixelSize(R.styleable.RulerView_scaleNumTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, DEFAULT_SCALE_NUM_TEXTSIZE_SP, getResources().getDisplayMetrics()));
        scaleLineStroke = a.getDimensionPixelSize(R.styleable.RulerView_scaleLineStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SCALE_LINE_STROKE_DP, getResources().getDisplayMetrics()));
        alignmentStroke = a.getDimensionPixelSize(R.styleable.RulerView_largeScaleStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ALIGNMENT_STROKE_DP, getResources().getDisplayMetrics()));
        /**
         * 配置参数
         */
        minScale = a.getInt(R.styleable.RulerView_minScale, DEFAULT_MIN_SCALE);
        firstScale = a.getFloat(R.styleable.RulerView_firstScale, DEFAULT_FIRST_SCALE);
        maxScale = a.getInt(R.styleable.RulerView_maxScale, DEFAULT_MAX_SCALE);
        scaleCount = a.getInt(R.styleable.RulerView_scaleCount, DEFAULT_SCALE_COUNT);
        isBgRoundRect = a.getBoolean(R.styleable.RulerView_isBgRoundRect, isBgRoundRect);
        a.recycle();
    }


    private void init() {
        //画背景的笔
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);
        //弧形区域画笔
        mAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAreaPaint.setStyle(Paint.Style.FILL);
        mAreaPaint.setStrokeWidth(5);
        //画准线的画笔
        alignmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        alignmentPaint.setColor(selectedColor);
        alignmentPaint.setStyle(Paint.Style.FILL);
        alignmentPaint.setStrokeCap(Paint.Cap.ROUND);
        alignmentPaint.setStrokeWidth(scaleLineStroke);
        //画刻度线的笔
        scaleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleLinePaint.setColor(scaleLineColor);
        scaleLinePaint.setStyle(Paint.Style.FILL);
        scaleLinePaint.setStrokeCap(Paint.Cap.ROUND);
        scaleLinePaint.setStrokeWidth(scaleLineStroke);
        //画刻度数的笔
        scaleNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleNumPaint.setColor(scaleNumColor);
        scaleNumPaint.setTextSize(scaleNumTextSize);
        //矩形
        bgRect = new RectF();
        scaleNumRect = new Rect();
        //Path
        mRightArcPath = new Path();
        mMiddleArcPath = new Path();
        mLeftArcPath = new Path();

        alignmentWidth = scaleLineLength + 5;
        valueAnimator = new ValueAnimator();
        mBezierHelper = new BezierHelper();

        //调试
        debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        debugPaint.setColor(Color.RED);
        debugPaint.setStyle(Paint.Style.STROKE);
        debugPaint.setTextSize(20);
        debugPaint.setStrokeWidth(5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        height = heightSize + getPaddingTop() + getPaddingBottom();
        scaleGap = height / 4;
        width = widthSize + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        drawBg(canvas);
        drawArcArea(canvas);
        drawScaleAndNum(canvas);
    }

    private float getWhichScalMoveY(float scale) {
        return height * DEFAULT_ALIGNMENT_POS - scaleGap * scaleCount * (scale - minScale);
    }

    /**
     * 画弧形区域
     */
    private void drawArcArea(Canvas canvas) {
        //画最右边的
        int rightStartPointX = width + RIGHT_BEZIER_OFFSET;
        int rightControlPointX = width + RIGHT_BEZIER_CONTROL_POINT_OFFSET;
        mRightArcPath.moveTo(rightStartPointX, height);
        mRightArcPath.lineTo(width, height);
        mRightArcPath.lineTo(width, 0);
        mRightArcPath.lineTo(rightStartPointX, 0);
        mRightArcPath.quadTo(rightControlPointX, height / 2, rightStartPointX, height);
        mRightArcPath.close();
        mAreaPaint.setColor(rightAreaColor);
        canvas.drawPath(mRightArcPath, mAreaPaint);
        //画中间的
        int middleStartPointX = rightStartPointX + MIDDLE_BEZIER_REL_OFFSET;
        int middleControlPointX = rightControlPointX + MIDDLE_BEZIER_REL_OFFSET;
        mMiddleArcPath.moveTo(middleStartPointX, height);
        mMiddleArcPath.lineTo(rightStartPointX, height);
        mMiddleArcPath.quadTo(rightControlPointX, height / 2, rightStartPointX, 0);
        mMiddleArcPath.lineTo(middleStartPointX, 0);
        mMiddleArcPath.quadTo(middleControlPointX, height / 2, middleStartPointX, height);
        mMiddleArcPath.close();
        mAreaPaint.setColor(middleAreaColor);
        canvas.drawPath(mMiddleArcPath, mAreaPaint);
        //画最左边的
        int leftStartPointX = middleStartPointX + LEFT_BEZIER_REL_OFFSET;
        int leftControlPointX = middleControlPointX + LEFT_BEZIER_REL_OFFSET;
        mLeftArcPath.moveTo(leftStartPointX, height);
        mLeftArcPath.lineTo(middleStartPointX, height);
        mLeftArcPath.quadTo(middleControlPointX, height / 2, middleStartPointX, 0);
        mLeftArcPath.lineTo(leftStartPointX, 0);
        mLeftArcPath.quadTo(leftControlPointX, height / 2, leftStartPointX, height);
        mLeftArcPath.close();
        mAreaPaint.setColor(leftAreaColor);
        canvas.drawPath(mLeftArcPath, mAreaPaint);
    }

    private void drawScaleAndNum(Canvas canvas) {
        canvas.save();
        canvas.translate(width + RIGHT_BEZIER_OFFSET + MIDDLE_BEZIER_REL_OFFSET, 0);
        int num1;//确定刻度位置
        float num2;
        if (firstScale != -1) {   //第一次进来的时候计算出默认刻度对应的假设滑动的距离moveY
            moveY = getWhichScalMoveY(firstScale);
            lastMoveY = moveY;
            firstScale = -1;
        }
        num1 = -(int) (moveY / scaleGap);//小刻度值
        num2 = (moveY % scaleGap);//偏移量
        rulerBottom = 0;
        if (isUp) {   //这部分代码主要是计算手指抬起时，惯性滑动结束时，刻度需要停留的位置
            num2 = ((moveY - height * DEFAULT_ALIGNMENT_POS % scaleGap) % scaleGap);
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
        //这里是滑动时候不断回调给使用者的结果值
        resultText = String.valueOf(new WeakReference<>(new BigDecimal((height * DEFAULT_ALIGNMENT_POS - moveY) / (scaleGap * scaleCount))).get().setScale(1, BigDecimal.ROUND_HALF_UP).intValue() + minScale);
        resultText = mList.get(Integer.parseInt(resultText) % 4).toString();
        if (onChooseResulterListener != null) {
            onChooseResulterListener.onScrollResult(resultText);
        }
        float scaleBezierStartX = 0;
        float scaleBezierStartY = 0;
        float scaleBezierControlX = -250;
        float scaleBezierControlY = height / 2;
        float scaleBezierEndX = scaleBezierStartX;
        float scaleBezierEndY = height;
        mBezierHelper.init(scaleBezierStartX, scaleBezierStartY, scaleBezierControlX, scaleBezierControlY, scaleBezierEndX, scaleBezierEndY);
        //绘制当前屏幕可见刻度,不需要裁剪屏幕,while循环只会执行·屏幕宽度/刻度宽度·次
        int i = 0;
        float offsetX = 0;
        float offsetX2 = 0;
        rulerBottom = (int) num2;
        while (rulerBottom < height) {
            if (i == 0) {
                offsetX = mBezierHelper.getX(1f * (0 - num2) / height);
                offsetX2 = mBezierHelper.getX(1f * (0 - num2 - scaleGap / 2) / height);
            }
            if ((moveY >= 0 && rulerBottom < moveY - scaleGap) || height * DEFAULT_ALIGNMENT_POS - rulerBottom <= getWhichScalMoveY(maxScale + 1) - moveY) {   //去除上下边界

            } else {
                //取内容
                int scaleNum = num1 / scaleCount + minScale;
                String displayContent = mList.get(scaleNum % 4).toString();
                //画长线
                canvas.drawLine(offsetX, rulerBottom, offsetX - scaleLineLength, rulerBottom, scaleLinePaint);
                scaleNumPaint.getTextBounds(displayContent, 0, displayContent.length(), scaleNumRect);
                if(i==1) {
                    scaleNumPaint.setColor(selectedColor);
                }else {
                    scaleNumPaint.setColor(scaleNumColor);
                }
                canvas.drawText(displayContent,
                        offsetX - alignmentWidth - scaleNumRect.width(),
                        rulerBottom + scaleNumRect.height() / 2,
                        scaleNumPaint);

                //画短线
                canvas.drawLine(offsetX2, rulerBottom + scaleGap / 2, offsetX2 - scaleLineLength / 2, rulerBottom + scaleGap / 2, scaleLinePaint);

            }
            ++num1;
            rulerBottom += scaleGap;
            offsetX = mBezierHelper.getX(1f * rulerBottom / height);
            offsetX2 = mBezierHelper.getX(1f * (rulerBottom + scaleGap / 2) / height);
            i++;
        }
        //绘制屏幕中间用来选中刻度的最大刻度
//        canvas.drawLine(0, height / 2, -alignmentWidth, height / 2, alignmentPaint);
        float xOffset = mBezierHelper.getX(DEFAULT_ALIGNMENT_POS);
        canvas.drawLine(xOffset, height * DEFAULT_ALIGNMENT_POS, xOffset - scaleLineLength, height * DEFAULT_ALIGNMENT_POS, alignmentPaint);
//        canvas.drawPoint(,alignmentPaint);
        canvas.drawCircle(xOffset+LEFT_BEZIER_REL_OFFSET+10,height * DEFAULT_ALIGNMENT_POS,5,alignmentPaint);
    }

    private void drawBg(Canvas canvas) {
        bgRect.set(0, 0, width, height);
        if (isBgRoundRect) {
            canvas.drawRoundRect(bgRect, 20, 20, bgPaint); //20->椭圆的用于圆形角x-radius
        } else {
            canvas.drawRect(bgRect, bgPaint);
        }
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
                if (moveY >= height * DEFAULT_ALIGNMENT_POS) {
                    moveY = height * DEFAULT_ALIGNMENT_POS;
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
                if (moveY >= height * DEFAULT_ALIGNMENT_POS) {
                    moveY = height * DEFAULT_ALIGNMENT_POS;
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

    public interface OnChooseResulterListener {
        void onEndResult(String result);

        void onScrollResult(String result);
    }

}
