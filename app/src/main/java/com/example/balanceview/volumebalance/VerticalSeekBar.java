package com.example.balanceview.volumebalance;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.example.balanceview.R;


/**
 * 自定义竖直seek bar
 */
public class VerticalSeekBar extends View {
    private static final String TAG = VerticalSeekBar.class.getSimpleName();
    private int startColor = Color.parseColor("#A71919");
    private int middleColor = Color.parseColor("#A71919");
    private int endColor = Color.parseColor("#A71919");
    private int thumbColor = Color.BLACK;
    private int thumbBorderColor = Color.TRANSPARENT;
    private int colorArray[] = {startColor, middleColor, endColor};
    private int grayColor = Color.parseColor("#20ffffff");
    private int colorArray2[] = {grayColor, grayColor, grayColor};
    private float x, y;
    private float mRadius;
    private float progress;
    private float maxCount = 10f;
    private float minCount = 0f;
    private float sLeft, sTop, sRight, sBottom;
    private float sWidth, sHeight;
    private LinearGradient linearGradient;
    private Paint paint = new Paint();
    protected OnSlideChangeListener onSlideChangeListener;

    private static final int DEFAULT_CIRCLE_RADIUS = 0;
    private static final int DEFAULT_CIRCLE_COLOR = Color.GRAY;

    private int circle_radius = DEFAULT_CIRCLE_RADIUS;

    private int vertical_color = Color.GRAY;
    private int image_background = 0;
    private boolean dragable = true;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar);
        circle_radius = a.getDimensionPixelSize(R.styleable.VerticalSeekBar_circle_radius, DEFAULT_CIRCLE_RADIUS);
        thumbColor = a.getColor(R.styleable.VerticalSeekBar_circle_color, DEFAULT_CIRCLE_COLOR);
        dragable = a.getBoolean(R.styleable.VerticalSeekBar_dragable, true);
        vertical_color = a.getColor(R.styleable.VerticalSeekBar_vertical_color, Color.GRAY);
        image_background = a.getResourceId(R.styleable.VerticalSeekBar_image_background, 0);
        maxCount = a.getInteger(R.styleable.VerticalSeekBar_max_count, 10);
        minCount = a.getInteger(R.styleable.VerticalSeekBar_min_count, 0);
        a.recycle();
        setCircle_color(thumbColor);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param circle_color 滑块的颜色，在没有设置背景图情况下生效
     */
    public void setCircle_color(int circle_color) {
        this.thumbColor = circle_color;
        invalidate();
    }

    /**
     * @param dragable 设置是否可以拖动
     */
    public void setDragable(boolean dragable) {
        this.dragable = dragable;
        invalidate();
    }

    /**
     * @param vertical_color 滑竿的颜色
     */
    public void setVertical_color(int vertical_color) {
        this.vertical_color = vertical_color;
        this.startColor = vertical_color;
        this.middleColor = vertical_color;
        this.endColor = vertical_color;
        colorArray[0] = startColor;
        colorArray[1] = middleColor;
        colorArray[2] = endColor;
        colorArray2[0] = startColor;
        colorArray2[1] = middleColor;
        colorArray2[2] = endColor;
        invalidate();
    }

    /**
     * @param vertical_top_color 滑竿上部分的颜色
     * @param vertical_bom_color 滑竿下部分的颜色
     */
    public void setVertical_color(int vertical_top_color, int vertical_bom_color) {
        colorArray[0] = vertical_bom_color;
        colorArray[1] = vertical_bom_color;
        colorArray[2] = vertical_bom_color;
        colorArray2[0] = vertical_top_color;
        colorArray2[1] = vertical_top_color;
        colorArray2[2] = vertical_top_color;
        invalidate();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    public void setColor(int startColor, int middleColor, int endColor, int thumbColor, int thumbBorderColor) {
        this.startColor = startColor;
        this.middleColor = middleColor;
        this.endColor = endColor;
        this.thumbColor = thumbColor;
        this.thumbBorderColor = thumbBorderColor;
        colorArray[0] = startColor;
        colorArray[1] = middleColor;
        colorArray[2] = endColor;
        invalidate();
    }


    /**
     * @param verticalColor    滑竿的颜色
     * @param thumbColor       滑块的颜色，在没有设置背景图情况下生效
     * @param thumbBorderColor 滑竿边缘背景色，默认透明
     */
    public void setColor(int verticalColor, int thumbColor, int thumbBorderColor) {
        this.startColor = verticalColor;
        this.middleColor = verticalColor;
        this.endColor = verticalColor;
        this.thumbColor = thumbColor;
        this.thumbBorderColor = thumbBorderColor;
        colorArray[0] = startColor;
        colorArray[1] = middleColor;
        colorArray[2] = endColor;
        invalidate();
    }

    /**
     * @param verticalColor 滑竿的颜色
     * @param thumbColor    滑块的颜色，在没有设置背景图情况下生效
     */
    public void setColor(int verticalColor, int thumbColor) {
        this.startColor = verticalColor;
        this.middleColor = verticalColor;
        this.endColor = verticalColor;
        this.thumbColor = thumbColor;
        colorArray[0] = startColor;
        colorArray[1] = middleColor;
        colorArray[2] = endColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int h = getMeasuredHeight();
        int w = getMeasuredWidth();

        if (image_background == 0) {
            if (circle_radius == 0) {
                mRadius = (float) w / 2;
            } else {
                mRadius = circle_radius;
            }
            sLeft = w * 0.25f;
            sRight = w * 0.75f;
            sTop = 0;
            sBottom = h;
            sWidth = sRight - sLeft;
            sHeight = sBottom - sTop;
            x = (float) w / 2;
            y = (float) (1 - (1 / (maxCount - minCount)) * (progress) * sHeight);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = getMeasuredWidth();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image_background, options);
            int outH = (int) (bitmap.getHeight() * getMeasuredWidth() / bitmap.getWidth());
            if (circle_radius == 0) {
                mRadius = (float) outH / 2;
            } else {
                mRadius = outH / 2;
            }
            sLeft = w * 0.25f;
            sRight = w * 0.75f;
            sTop = 0;
            sBottom = h;
            sWidth = sRight - sLeft;
            sHeight = sBottom - sTop;
            x = (float) w / 2;
            y = (float) (1 - (1 / (maxCount - minCount)) * (progress - minCount)) * sHeight;
        }

        drawBackground(canvas);
        drawCircle(canvas);
        paint.reset();
    }

    private void drawBackground(Canvas canvas) {
        RectF rectBlackBg = new RectF(sLeft + sWidth / 2 - 3, sTop, sLeft + sWidth / 2 + 3, y);
        linearGradient = new LinearGradient(sLeft, sTop, sWidth, y, colorArray2, null, Shader.TileMode.MIRROR);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(linearGradient);
        canvas.drawRoundRect(rectBlackBg, sWidth / 2, sWidth / 2, paint);

        RectF rectBlackBg2 = new RectF(sLeft + sWidth / 2 - 3, y, sLeft + sWidth / 2 + 3, sBottom);
        linearGradient = new LinearGradient(sLeft, y, sWidth, sHeight - y, colorArray, null, Shader.TileMode.MIRROR);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(linearGradient);
        canvas.drawRoundRect(rectBlackBg2, sWidth / 2, sWidth / 2, paint);
    }

    private void drawCircle(Canvas canvas) {
        Paint thumbPaint = new Paint();
        y = y < mRadius ? mRadius : y;
        y = y > sHeight - mRadius - 20 ? sHeight - mRadius + 4 : y - 4;
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(thumbColor);
        if (image_background == 0) {
            canvas.drawCircle(x, y, mRadius, thumbPaint);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = getMeasuredWidth();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image_background, options);
            int outH = (int) (bitmap.getHeight() * getMeasuredWidth() / bitmap.getWidth());
            Rect rect = new Rect(0, (int) (y - outH / 2), getMeasuredWidth(), (int) (y + outH / 2));
            canvas.drawBitmap(bitmap, null, rect, null);
            Paint testPaint = new Paint();
            testPaint.setColor(Color.WHITE);
            testPaint.setTextAlign(Paint.Align.CENTER);
            testPaint.setTextSize(20);
            canvas.save();
            if (progress > maxCount) {
                progress = maxCount;
            } else if (progress < minCount) {
                progress = minCount;
            }
            int value = (int) progress;
            canvas.drawText(String.valueOf(value), rect.left + (rect.width()) / 2.0F, rect.top - thumbPaint.ascent() + (rect.height() - (thumbPaint.descent() - thumbPaint.ascent())) / 2.0F, testPaint);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!dragable) {
            return true;
        }
        this.y = event.getY();
        if (minCount < 0) {
            progress = (sHeight - y) / sHeight * (maxCount - minCount) + minCount;
        } else {
            progress = (sHeight - y) / sHeight * (maxCount - minCount);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onSlideProgress(MotionEvent.ACTION_DOWN, progress);
                break;
            case MotionEvent.ACTION_UP:
                onSlideProgress(MotionEvent.ACTION_UP, progress);
                break;
            case MotionEvent.ACTION_MOVE:
                onSlideProgress(MotionEvent.ACTION_MOVE, progress);
                break;

        }

        return true;
    }

    public interface OnSlideChangeListener {
        void OnSlideChangeListener(View view, float progress);


        void onSlideStopTouch(View view, float progress);
    }

    public void setOnSlideChangeListener(OnSlideChangeListener onStateChangeListener) {
        this.onSlideChangeListener = onStateChangeListener;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public void setMaxCount(float max) {
        maxCount = max;
        invalidate();
    }

    public void setMinCount(float minCount) {
        this.minCount = minCount;
    }

    public void onSlideProgress(int event, float progress) {
        if (progress < minCount) {
            progress = minCount;
        }
        if (progress > maxCount) {
            progress = maxCount - minCount;
        }
        switch (event) {
            case MotionEvent.ACTION_UP:
                if (onSlideChangeListener != null) {
                    onSlideChangeListener.onSlideStopTouch(this, progress);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (onSlideChangeListener != null) {
                    onSlideChangeListener.OnSlideChangeListener(this, progress);
                }
                setProgress(progress);
                this.invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                this.invalidate();
                break;
        }

    }
}

