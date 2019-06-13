package com.example.balanceview.volumebalance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;
import com.example.balanceview.R;

/**
 * 水平带进度的seek bar
 */
public class HorizontalSeeker extends SeekBar {
    //比例对应的原点分辨率
    private int ScreenWidth = 720;
    private int screenHeight = 1280;
    private Drawable thumb;
    private Resources res;
    private Paint paint;
    private Bitmap bmp;
    private Drawable mThumb;

    public HorizontalSeeker(Context context) {
        this(context, null);
    }

    @SuppressWarnings("deprecation")
    public HorizontalSeeker(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(getResources().getColor(R.color.white));
        res = context.getResources();
        bmp = BitmapFactory.decodeResource(res, R.drawable.knob);
        thumb = new BitmapDrawable(bmp);

        paint.setTextSize(30);
        // 设置拖动的图片
        setThumb(thumb);
        // 图片的位置
        setThumbOffset(thumb.getIntrinsicWidth());
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        this.mThumb = thumb;
    }

    public Drawable getSeekBarThumb() {
        return mThumb;
    }

    //设置thumb的偏移数值
    @Override
    public void setThumbOffset(int thumbOffset) {
        // TODO Auto-generated method stub
        super.setThumbOffset((int) (thumbOffset / 1.2));
    }

    String temp_str = "0";

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int data = Integer.parseInt(temp_str);
        Rect rect = getSeekBarThumb().getBounds();
        float fontwidth = paint.measureText(temp_str);
        if (data < 10) {
            canvas.drawText(temp_str, rect.left + (rect.width()) / 2.0F, rect.top - paint.ascent() + (rect.height() - (paint.descent() - paint.ascent())) / 2.0F, paint);
        } else {
            canvas.drawText(temp_str, rect.left + (rect.width()) / 2.0F, rect.top - paint.ascent() + (rect.height() - (paint.descent() - paint.ascent())) / 2.0F, paint);
        }

        canvas.restore();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public void SetValue(String value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value);
        temp_str = sb.toString();
        invalidate();
    }

    @SuppressLint("NewApi")
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setOnSeekBarChangeListener(final OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (l != null) {
                    l.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


                if (l != null) {
                    l.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (l != null) {
                    l.onStopTrackingTouch(seekBar);
                }
            }
        });
    }
}
