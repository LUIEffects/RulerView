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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.List;

/**
 * @author taitsun
 * @since 2019-09-28
 */
public class SeekView extends View {

    //每10分钟对应多少dp
    private static final int PER_10MIN_2_DP = 20;

    private enum SlideType {
        PANEL, PROGRESS
    }

    private static final String TAG = "SeekView";
    /**
     * 文案高度
     */
    private int txtHeight = 50;
    /**
     * 尺子和屏幕顶部以及结果之间的高度
     */
    private int topGap = 20;
    private int progressGap = 20;
    /**
     * 刻度平分多少份
     */
    private int scaleCount = 2;  //刻度评分多少份
    /**
     * 刻度间距
     */
    private int per10Min2Px;
    /**
     * 刻度最小值
     */
    private long minScale = 0;
    /**
     * 第一次显示的刻度
     */
    private long firstScale;
    /**
     * 刻度最大值
     */
    private long maxScale;

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
     * 刻度字体大小
     */
    private int scaleNumTextSize = 16;
    private int scaleHeight = 3;

    private long liveProgress = 72;
    private long seekProgress = 72;

    private int screenStartPos;
    private int screenEndPos;

    private List<SeekViewDataObj.ScaleMsgObj> dataList;
    private SeekViewDataObj seekViewDataObj;


    public void refreshData(SeekViewDataObj data) {
        this.seekViewDataObj = data;
        this.dataList = seekViewDataObj.getScaleMsgObjList();
        minScale = data.getPlayBackStart();
        liveProgress = data.getPlayBackTime();
        seekProgress = data.getPlayBackTime();
        firstScale = liveProgress;
        maxScale = Math.max(data.getPlayEndTime(), minScale+getScreenWidth2Sec());
        invalidate();
    }

    /**
     * 结果回调
     */
    private OnInteractListener onInteractListener;
    private ValueAnimator valueAnimator;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private Paint testPaint;
    private Paint bgPaint;
    private Paint scalePaint;
    private Paint progressPaint;
    private Paint txtPaint;
    private Rect timeTxtRect;
    private Rect programTxtRect;
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
    private SlideType curSlideType;
    private boolean isDebug = false;

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

    public void setOnInteractListener(OnInteractListener onInteractListener) {
        this.onInteractListener = onInteractListener;
    }

    private void setAttr(AttributeSet attrs, int defStyleAttr) {

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SeekView, defStyleAttr, 0);

        scaleHeight = typedArray.getDimensionPixelSize(R.styleable.SeekView_scaleHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scaleHeight, getResources().getDisplayMetrics()));

        txtHeight = typedArray.getDimensionPixelSize(R.styleable.SeekView_txtHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, txtHeight, getResources().getDisplayMetrics()));

        topGap = typedArray.getDimensionPixelSize(R.styleable.SeekView_topGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, topGap, getResources().getDisplayMetrics()));

        progressGap = typedArray.getDimensionPixelSize(R.styleable.SeekView_progressGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, progressGap, getResources().getDisplayMetrics()));

        scaleCount = typedArray.getInt(R.styleable.SeekView_scaleCount, scaleCount);

        per10Min2Px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PER_10MIN_2_DP, getResources().getDisplayMetrics());

        bgColor = typedArray.getColor(R.styleable.SeekView_bgColor, bgColor);

        txtColor = typedArray.getColor(R.styleable.SeekView_txtColor, txtColor);

        progressGrooveStroke = typedArray.getDimensionPixelSize(R.styleable.SeekView_progressGrooveStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, progressGrooveStroke, getResources().getDisplayMetrics()));

        scaleNumTextSize = typedArray.getDimensionPixelSize(R.styleable.SeekView_scaleNumTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, scaleNumTextSize, getResources().getDisplayMetrics()));
        typedArray.recycle();
    }


    private void init() {
        testPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        testPaint.setColor(0xff00ff00);
        bgPaint.setColor(bgColor);
        scalePaint.setColor(scaleColor);
        txtPaint.setColor(txtColor);

        testPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStyle(Paint.Style.FILL);
        scalePaint.setStyle(Paint.Style.FILL);
        progressPaint.setStyle(Paint.Style.FILL);

        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        scalePaint.setStrokeCap(Paint.Cap.ROUND);

        testPaint.setStrokeWidth(progressGrooveStroke);
        scalePaint.setStrokeWidth(scaleStroke);
        progressPaint.setStrokeWidth(progressGrooveStroke);

        txtPaint.setTextSize(scaleNumTextSize);

        bgRect = new RectF();
        timeTxtRect = new Rect();
        programTxtRect = new Rect();

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
        if (seekViewDataObj != null)
            drawScaleAndNum(canvas);
    }

    private SlideType getSlideType(MotionEvent downEvent) {
        float x = downEvent.getX();
        float y = downEvent.getY();
        if (y < getPaddingTop() + topGap || y > getHeight() - getPaddingTop() - topGap - progressGap) {
            return SlideType.PANEL;
        } else
            return SlideType.PROGRESS;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = event.getX();
        isUp = false;
        velocityTracker.computeCurrentVelocity(500);
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                curSlideType = getSlideType(event);
                Log.d(TAG, "onTouchEvent: getSlideType=" + curSlideType.name());
                if (curSlideType == SlideType.PANEL) {
                    //按下时如果属性动画还没执行完,就终止,记录下当前按下点的位置
                    if (valueAnimator != null && valueAnimator.isRunning()) {
                        valueAnimator.end();
                        valueAnimator.cancel();
                    }
                } else if (curSlideType == SlideType.PROGRESS) {
                    seekProgress = Math.min((int) (screenStartPos + currentX*1.0f / per10Min2Px*600), liveProgress);
                    if (onInteractListener != null)
                        onInteractListener.onProgressUpdate(seekProgress);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (curSlideType == SlideType.PANEL) {
                    //滑动时候,通过假设的滑动距离,做超出左边界以及右边界的限制。
                    moveX = currentX - downX + lastMoveX;
                    if (moveX >= 0) {
                        moveX = 0;
                    } else if (moveX <= getWhichScaleMovePx(maxScale) + width / 2) {
                        moveX = getWhichScaleMovePx(maxScale) + width / 2;
                    }
                } else if (curSlideType == SlideType.PROGRESS) {
                    seekProgress = Math.min((int) (screenStartPos + currentX*1.0f / per10Min2Px*600), liveProgress);
                    if (onInteractListener != null)
                        onInteractListener.onProgressUpdate(seekProgress);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (curSlideType == SlideType.PANEL) {
                    //手指抬起时候制造惯性滑动
                    lastMoveX = moveX;
//                    xVelocity = (int) velocityTracker.getXVelocity();
//                    autoVelocityScroll(xVelocity);
//                    velocityTracker.clear();
                }
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
        valueAnimator = ValueAnimator.ofInt(0, xVelocity / 50).setDuration(Math.abs(xVelocity / 10));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveX += (int) animation.getAnimatedValue();
                if (moveX >= 0) {
                    moveX = 0;
                } else if (moveX <= getWhichScaleMovePx(maxScale) + width / 2) {
                    moveX = getWhichScaleMovePx(maxScale) + width / 2;
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
     * 计算出屏幕左侧相较于内容左侧的偏移距离 单位：px
     *
     * @param scale
     * @return
     */
    private float getWhichScaleMovePx(float scale) {
        return width / 2 - per10Min2Px * ((scale - minScale)*1.0f/600);
    }

    private void drawScaleAndNum(Canvas canvas) {
        canvas.translate(0, topGap);
        if (isDebug) {
            canvas.drawLine(0, 0, width, 0, testPaint);
        }
        canvas.translate(0, progressGap / 2);//移动画布到结果值的下面

        int num1;//确定刻度位置
        float offsetX;

        if (firstScale != -1) {   //第一次进来的时候计算出默认刻度对应的假设滑动的距离moveX
            moveX = getWhichScaleMovePx(firstScale);////如果设置了默认滑动位置，计算出需要滑动的距离
            lastMoveX = moveX;
            firstScale = -1;//将结果置为-1，下次不再计算初始位置
        }

        num1 = -(int) (moveX*1.0f /per10Min2Px)*600;//小刻度值——>左侧最小的刻度值  //滑动刻度的整数部分
        offsetX = (moveX*1.0f % per10Min2Px)*600;//偏移量   //滑动刻度的小数部分
        screenStartPos = (int) (minScale+num1);
        screenEndPos = (int) (screenStartPos + (width*1.0f / per10Min2Px)*600);
        curPos = 0;  //准备开始绘制当前屏幕,从最左面开始
        /**
         * 这部分代码主要是计算手指抬起时，惯性滑动结束时，刻度需要停留的位置
         */
//        if (isUp) {
//            offsetX = ((moveX - width / 2 % per10Min2Px) % per10Min2Px);
//            if (offsetX <= 0) {
//                offsetX = per10Min2Px - Math.abs(offsetX);
//            }
//            leftScroll = (int) Math.abs(offsetX);
//            rightScroll = (int) (per10Min2Px - Math.abs(offsetX));
//
//            float moveX2 = offsetX <= per10Min2Px / 2 ? moveX - leftScroll : moveX + rightScroll;
//            moveX = moveX2;
//            if (valueAnimator != null && !valueAnimator.isRunning()) {
//                isUp = false;
//            }
//
//            num1 = (int) -(moveX / per10Min2Px);
//            offsetX = (moveX % per10Min2Px);
//        }
        canvas.save();
        canvas.translate(offsetX, progressGrooveStroke);    //不加该偏移的话，滑动时刻度不会落在0~1之间只会落在整数上面,其实这个都能设置一种模式了，毕竟初衷就是指针不会落在小数上面
//        canvas.drawPoint(0,0,testPaint);
        /**
         * 绘制当前屏幕可见刻度
         */
        float posX;
        if (dataList != null && !dataList.isEmpty()) {
            for (int curIndex = 0; curIndex < dataList.size(); curIndex++) {
                SeekViewDataObj.ScaleMsgObj scaleMsgObj = dataList.get(curIndex);
                if (scaleMsgObj.pos >= screenStartPos && scaleMsgObj.pos <= screenEndPos) {
                    posX = (scaleMsgObj.pos - screenStartPos)*1.0f/600 * per10Min2Px;
                    //画那条破线
                    canvas.drawLine(posX, 0, posX, scaleHeight * 2, scalePaint);
                    //画时间
                    txtPaint.getTextBounds(scaleMsgObj.time, 0, scaleMsgObj.time.length(), timeTxtRect);
                    canvas.drawText(scaleMsgObj.time, posX, timeTxtRect.height() + scaleHeight * 3, txtPaint);
                    //画节目预告
                    txtPaint.getTextBounds(scaleMsgObj.txt, 0, scaleMsgObj.txt.length(), programTxtRect);
                    canvas.drawText(scaleMsgObj.txt, posX, programTxtRect.height() + timeTxtRect.height() + scaleHeight * 4, txtPaint);
                }
            }
        }
//        while (curPos < width) {
//            if (num1 % scaleCount == 0) {
//                String txtSrc = num1 / scaleCount + minScale + "";
//                canvas.drawLine(0, 0, 0, scaleHeight * 2, scalePaint);
//                txtPaint.getTextBounds(txtSrc, 0, txtSrc.length(), timeTxtRect);
//                canvas.drawText(txtSrc, -timeTxtRect.width() / 2, timeTxtRect.height() + scaleHeight * 3, txtPaint);
//            } else {
//                canvas.drawLine(0, 0, 0, scaleHeight, scalePaint);
//            }
//            ++num1;//刻度加1
//            curPos += per10Min2Px;//绘制屏幕的距离在原有基础上+1个刻度间距
//            canvas.translate(per10Min2Px, 0); //移动画布到下一个刻度
//        }
        canvas.restore();
        Log.d(TAG, "screenStartPos = " + screenStartPos + "     screenEndPos = " + screenEndPos);
        /**
         * 绘制进度条
         */
        if (liveProgress < screenStartPos) {
            progressPaint.setColor(progressGrooveColor);
            canvas.drawLine(0, 0, width, 0, progressPaint);
        } else if (liveProgress <= screenEndPos) {
            progressPaint.setColor(progressColor);
            canvas.drawLine(0, 0, (liveProgress - screenStartPos)*1.0f/600 * per10Min2Px + offsetX, 0, progressPaint);

            progressPaint.setColor(progressGrooveColor);
            canvas.drawLine((liveProgress - screenStartPos)*1.0f/600* per10Min2Px + offsetX, 0, width, 0, progressPaint);

            progressPaint.setColor(progressColor);
            progressPaint.setStrokeWidth(progressGrooveStroke * 3);
            canvas.drawPoint((seekProgress - screenStartPos) *1.0f/600* per10Min2Px + offsetX, 0, progressPaint);

            progressPaint.setStrokeWidth(progressGrooveStroke);
        } else if (liveProgress > screenEndPos) {
            progressPaint.setColor(progressColor);
            canvas.drawLine(0, 0, width, 0, progressPaint);

            progressPaint.setColor(progressColor);
            progressPaint.setStrokeWidth(progressGrooveStroke * 3);
            canvas.drawPoint((seekProgress - screenStartPos)*1.0f/600 * per10Min2Px + offsetX, 0, progressPaint);

            progressPaint.setStrokeWidth(progressGrooveStroke);
        }
        if (isDebug) {
            canvas.translate(0, progressGap / 2);
            canvas.drawLine(0, 0, width, 0, testPaint);
        }
        //绘制屏幕中间用来选中刻度的最大刻度
//        canvas.drawLine(width / 2, 0, width / 2, lagScaleHeight, progressPaint);

    }

    private void drawBg(Canvas canvas) {
        bgRect.set(0, 0, width, height);
        canvas.drawRect(bgRect, bgPaint);
    }

    public interface OnChooseResulterListener {
        void onEndResult(String result);

        void onScrollResult(String result);
    }

    public interface OnInteractListener {
        void onProgressUpdate(long progress);
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

    public void setPer10Min2Px(int per10Min2Px) {
        this.per10Min2Px = per10Min2Px;
        invalidate();
    }

    public void setMinScale(int minScale) {
        this.minScale = minScale;
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

    public void setScaleNumTextSize(int scaleNumTextSize) {
        this.scaleNumTextSize = scaleNumTextSize;
        invalidate();
    }

    public int getScreenWidth2Sec() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) ((displayMetrics.widthPixels * 1.0 / per10Min2Px) * 60);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
