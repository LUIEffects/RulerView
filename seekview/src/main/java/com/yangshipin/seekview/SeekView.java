package com.yangshipin.seekview;

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
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

/**
 * @author taitsun
 * @since 2019-09-28
 */
public class SeekView extends View {
    private static final String TAG = "SeekView";
    /**
     * 文案高度
     */
    private int txtHeight = 50;
    /**
     * 尺子和屏幕顶部以及结果之间的高度
     */
    private int topGap = 20;
    private int progressGap = 10;
    /**
     * 刻度平分多少份
     */
    private int scaleCount = 2;  //刻度评分多少份
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
     * 刻度颜色
     */
    private int scaleColor = 0xff999999;
    /**
     * 文字颜色
     */
    private int txtColor = 0xff333333;
    private int scaleStroke = 1;
    /**
     * 进度条
     */
    private int progressGrooveStroke = 1;
    private int progressGrooveColor = 0xff999999;
    private int progressColor = 0xffff0000;
    /**
     * 结果字体大小
     */
    private int resultNumTextSize = 20;
    /**
     * 刻度字体大小
     */
    private int scaleNumTextSize = 16;
    private int scaleHeight = 3;

    private int progress = 72;

    private int screenStartPos;
    private int screenEndPos;

    /**
     * 结果回调
     */
    private OnChooseResulterListener onChooseResulterListener;
    private ValueAnimator valueAnimator;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private String resultText = String.valueOf(firstScale);
    private Paint bgPaint;
    private Paint scalePaint;
    private Paint progressPaint;
    private Paint txtPaint;
    private Paint resultValPaint;
    private Rect scaleNumRect;
    private Rect resultNumRect;
    private RectF bgRect;
    private int height, width;
    private int curPos = 0;
    private float downX;
    private float moveX = 0;
    private float currentX;
    private float lastMoveX = 0;
    private boolean isUp = false;
    private int leftScroll;
    private int rightScroll;
    private int xVelocity;

    public SeekView(Context context) {
        this(context, null);
    }

    public SeekView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttr(attrs, defStyleAttr);
        init();
    }

    public void setOnChooseResulterListener(OnChooseResulterListener onChooseResulterListener) {
        this.onChooseResulterListener = onChooseResulterListener;
    }

    private void setAttr(AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SeekView, defStyleAttr, 0);

        scaleHeight = a.getDimensionPixelSize(R.styleable.SeekView_scaleHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scaleHeight, getResources().getDisplayMetrics()));

        txtHeight = a.getDimensionPixelSize(R.styleable.SeekView_txtHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, txtHeight, getResources().getDisplayMetrics()));

        topGap = a.getDimensionPixelSize(R.styleable.SeekView_topGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, topGap, getResources().getDisplayMetrics()));

        progressGap = a.getDimensionPixelSize(R.styleable.SeekView_progressGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, progressGap, getResources().getDisplayMetrics()));

        scaleCount = a.getInt(R.styleable.SeekView_scaleCount, scaleCount);

        scaleGap = a.getDimensionPixelSize(R.styleable.SeekView_scaleGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scaleGap, getResources().getDisplayMetrics()));

        minScale = a.getInt(R.styleable.SeekView_minScale, minScale);

        firstScale = a.getFloat(R.styleable.SeekView_firstScale, firstScale);

        maxScale = a.getInt(R.styleable.SeekView_maxScale, maxScale);

        bgColor = a.getColor(R.styleable.SeekView_bgColor, bgColor);

        txtColor = a.getColor(R.styleable.SeekView_txtColor, txtColor);

        progressGrooveStroke = a.getDimensionPixelSize(R.styleable.SeekView_progressGrooveStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, progressGrooveStroke, getResources().getDisplayMetrics()));

        resultNumTextSize = a.getDimensionPixelSize(R.styleable.SeekView_resultNumTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, resultNumTextSize, getResources().getDisplayMetrics()));

        scaleNumTextSize = a.getDimensionPixelSize(R.styleable.SeekView_scaleNumTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, scaleNumTextSize, getResources().getDisplayMetrics()));
        a.recycle();
    }


    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        resultValPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        bgPaint.setColor(bgColor);
        scalePaint.setColor(scaleColor);
        txtPaint.setColor(txtColor);
        resultValPaint.setColor(txtColor);

        resultValPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStyle(Paint.Style.FILL);
        scalePaint.setStyle(Paint.Style.FILL);
        progressPaint.setStyle(Paint.Style.FILL);

        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        scalePaint.setStrokeCap(Paint.Cap.ROUND);

        scalePaint.setStrokeWidth(scaleStroke);
        progressPaint.setStrokeWidth(progressGrooveStroke);

        resultValPaint.setTextSize(resultNumTextSize);
        txtPaint.setTextSize(scaleNumTextSize);

        bgRect = new RectF();
        resultNumRect = new Rect();
        scaleNumRect = new Rect();

        resultValPaint.getTextBounds(resultText, 0, resultText.length(), resultNumRect);

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
                height = topGap + progressGap + txtHeight + topGap / 2 + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                height = heightSize + getPaddingTop() + getPaddingBottom();
                break;
        }

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
        currentX = event.getX();
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
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //滑动时候,通过假设的滑动距离,做超出左边界以及右边界的限制。
                moveX = currentX - downX + lastMoveX;
                if (moveX >= 0) {
                    moveX = 0;
                } else if (moveX <= getWhichScalMovex(maxScale) + width / 2) {
                    moveX = getWhichScalMovex(maxScale) + width / 2;
                }
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起时候制造惯性滑动
                lastMoveX = moveX;
                xVelocity = (int) velocityTracker.getXVelocity();
                autoVelocityScroll(xVelocity);
                velocityTracker.clear();
                break;
        }
        invalidate();
        return true;
    }

    private void autoVelocityScroll(int xVelocity) {
        //惯性滑动的代码,速率和滑动距离,以及滑动时间需要控制的很好,应该网上已经有关于这方面的算法了吧。。这里是经过N次测试调节出来的惯性滑动
        if (Math.abs(xVelocity) < 50) {
            isUp = true;
            return;
        }
        if (valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofInt(0, xVelocity / 20).setDuration(Math.abs(xVelocity / 10));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveX += (int) animation.getAnimatedValue();
                if (moveX >= 0) {
                    moveX = 0;
                } else if (moveX <= getWhichScalMovex(maxScale) + width / 2) {
                    moveX = getWhichScalMovex(maxScale) + width / 2;
                }
                lastMoveX = moveX;
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

    /**
     * 计算出屏幕左侧相较于内容左侧的偏移距离
     *
     * @param scale
     * @return
     */
    private float getWhichScalMovex(float scale) {

        return width / 2 - scaleGap * (scale - minScale);
    }

    private void drawScaleAndNum(Canvas canvas) {
        canvas.translate(0, topGap);//移动画布到结果值的下面

        int num1;//确定刻度位置
        float offsetX;

        if (firstScale != -1) {   //第一次进来的时候计算出默认刻度对应的假设滑动的距离moveX
            moveX = getWhichScalMovex(firstScale);////如果设置了默认滑动位置，计算出需要滑动的距离
            lastMoveX = moveX;
            firstScale = -1;//将结果置为-1，下次不再计算初始位置
        }

        num1 = -(int) (moveX / scaleGap);//小刻度值——>左侧最小的刻度值  //滑动刻度的整数部分
        screenStartPos = num1;
        offsetX = (moveX % scaleGap);//偏移量   //滑动刻度的小数部分
        curPos = 0;  //准备开始绘制当前屏幕,从最左面开始
        if (isUp) {   //这部分代码主要是计算手指抬起时，惯性滑动结束时，刻度需要停留的位置
            offsetX = ((moveX - width / 2 % scaleGap) % scaleGap);
            if (offsetX <= 0) {
                offsetX = scaleGap - Math.abs(offsetX);
            }
            leftScroll = (int) Math.abs(offsetX);
            rightScroll = (int) (scaleGap - Math.abs(offsetX));

            float moveX2 = offsetX <= scaleGap / 2 ? moveX - leftScroll : moveX + rightScroll;

            if (valueAnimator != null && !valueAnimator.isRunning()) {
                valueAnimator = ValueAnimator.ofFloat(moveX, moveX2);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        moveX = (float) animation.getAnimatedValue();
                        lastMoveX = moveX;
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

            num1 = (int) -(moveX / scaleGap);
            offsetX = (moveX % scaleGap);
        }
        canvas.save();
        canvas.translate(offsetX, progressGrooveStroke);    //不加该偏移的话，滑动时刻度不会落在0~1之间只会落在整数上面,其实这个都能设置一种模式了，毕竟初衷就是指针不会落在小数上面
        //这里是滑动时候不断回调给使用者的结果值
        resultText = String.valueOf(new WeakReference<>(new BigDecimal((width / 2 - moveX) / scaleGap)).get().setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + minScale);
        if (onChooseResulterListener != null) {
            onChooseResulterListener.onScrollResult(resultText);
        }
        //绘制当前屏幕可见刻度,不需要裁剪屏幕,while循环只会执行·屏幕宽度/刻度宽度·次
        while (curPos < width) {
            if (num1 % scaleCount == 0) {
                //绘制整点刻度以及文字
                boolean isSlide2LeftBound = (moveX >= 0 && curPos < moveX - scaleGap);
                boolean isSlide2RightBound = (curPos >= width / 2 + moveX - getWhichScalMovex(maxScale + 1));
                if (isSlide2LeftBound || isSlide2RightBound) {   //去除左右边界
                    Log.d(TAG, "drawScaleAndNum: ");
                    //当滑动出范围的话，不绘制，去除左右边界
                } else {
                    //绘制刻度，绘制刻度数字
                    String txtSrc = num1 / scaleCount + minScale + "";
                    canvas.drawLine(0, 0, 0, scaleHeight * 2, scalePaint);

                    txtPaint.getTextBounds(txtSrc, 0, txtSrc.length(), scaleNumRect);

                    canvas.drawText(txtSrc, -scaleNumRect.width() / 2, scaleNumRect.height() + scaleHeight * 3, txtPaint);
                }

            } else {
                //绘制每10分钟刻度
                if ((moveX >= 0 && curPos < moveX) || width / 2 - curPos < getWhichScalMovex(maxScale) - moveX) {   //去除左右边界
                    //当滑动出范围的话，不绘制，去除左右边界
                } else {
                    canvas.drawLine(0, 0, 0, scaleHeight, scalePaint);
                }
            }
            ++num1;//刻度加1
            curPos += scaleGap;//绘制屏幕的距离在原有基础上+1个刻度间距
            canvas.translate(scaleGap, 0); //移动画布到下一个刻度
        }

//        while (curPos < width) {
//            if (num1 % scaleCount == 0) {
//                //绘制整点刻度以及文字
//                if ((moveX < 0 ||curPos > moveX - scaleGap) || width / 2 - curPos > getWhichScalMovex(maxScale + 1) - moveX) {   //去除左右边界
//                    //当滑动出范围的话，不绘制，去除左右边界
//                    //绘制刻度，绘制刻度数字
//                    canvas.drawLine(0, 0, 0, midScaleHeight, midScalePaint);
//                    txtPaint.getTextBounds(num1 / scaleGap + minScale + "", 0, (num1 / scaleGap + minScale + "").length(), scaleNumRect);
//                    canvas.drawText(num1 / scaleCount + minScale + "", -scaleNumRect.width() / 2, lagScaleHeight +
//                            (txtHeight - lagScaleHeight) / 2 + scaleNumRect.height(), txtPaint);
//                }
//            } else {
//                //绘制每10分钟刻度
//                if ((moveX < 0 || curPos > moveX) || width / 2 - curPos > getWhichScalMovex(maxScale) - moveX) {   //去除左右边界
//                    //当滑动出范围的话，不绘制，去除左右边界
//                    canvas.drawLine(0, 0, 0, smallScaleHeight, smallScalePaint);
//                }
//            }
//            ++num1;//刻度加1
//            curPos += scaleGap;//绘制屏幕的距离在原有基础上+1个刻度间距
//            canvas.translate(scaleGap, 0); //移动画布到下一个刻度
//        }
        screenEndPos = num1;
        canvas.restore();
        Log.d(TAG, "screenStartPos = " + screenStartPos + "     screenEndPos = " + screenEndPos);
        //绘制进度条
        if (progress < screenStartPos) {
            progressPaint.setColor(progressGrooveColor);
            canvas.drawLine(0, 0, width, 0, progressPaint);
        } else if (progress <= screenEndPos) {
            progressPaint.setColor(progressColor);
            canvas.drawLine(0, 0, (progress - screenStartPos) * scaleGap + offsetX, 0, progressPaint);

            progressPaint.setColor(progressGrooveColor);
            canvas.drawLine((progress - screenStartPos) * scaleGap + offsetX, 0, width, 0, progressPaint);

            progressPaint.setColor(progressColor);
            progressPaint.setStrokeWidth(progressGrooveStroke * 3);
            canvas.drawPoint((progress - screenStartPos) * scaleGap + offsetX, 0, progressPaint);

            progressPaint.setStrokeWidth(progressGrooveStroke);
        } else if (progress > screenEndPos) {
            progressPaint.setColor(progressColor);
            canvas.drawLine(0, 0, width, 0, progressPaint);
        }
        //绘制屏幕中间用来选中刻度的最大刻度
//        canvas.drawLine(width / 2, 0, width / 2, lagScaleHeight, progressPaint);

    }

    //绘制上面的结果 结果值+单位
    private void drawResultText(Canvas canvas, String resultText) {
        canvas.translate(0, -resultNumRect.height() - topGap / 2);
        resultValPaint.getTextBounds(resultText, 0, resultText.length(), resultNumRect);
        canvas.drawText(resultText, width / 2 - resultNumRect.width() / 2, resultNumRect.height(),
                resultValPaint);
    }

    private void drawBg(Canvas canvas) {
        bgRect.set(0, 0, width, height);
        canvas.drawRect(bgRect, bgPaint);
    }

    public interface OnChooseResulterListener {
        void onEndResult(String result);

        void onScrollResult(String result);
    }

    public void setTxtHeight(int txtHeight) {
        this.txtHeight = txtHeight;
        invalidate();
    }

    public void setTopGap(int topGap) {
        this.topGap = topGap;
        invalidate();
    }

    public void setScaleCount(int scaleCount) {
        this.scaleCount = scaleCount;
        invalidate();
    }

    public void setScaleGap(int scaleGap) {
        this.scaleGap = scaleGap;
        invalidate();
    }

    public void setMinScale(int minScale) {
        this.minScale = minScale;
        invalidate();
    }

    public void setFirstScale(float firstScale) {
        this.firstScale = firstScale;
        invalidate();
    }

    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
        invalidate();
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    public void setTxtColor(int txtColor) {
        this.txtColor = txtColor;
        invalidate();
    }

    public void setProgressGrooveStroke(int progressGrooveStroke) {
        this.progressGrooveStroke = progressGrooveStroke;
        invalidate();
    }

    public void setResultNumTextSize(int resultNumTextSize) {
        this.resultNumTextSize = resultNumTextSize;
        invalidate();
    }

    public void setScaleNumTextSize(int scaleNumTextSize) {
        this.scaleNumTextSize = scaleNumTextSize;
        invalidate();
    }
}
