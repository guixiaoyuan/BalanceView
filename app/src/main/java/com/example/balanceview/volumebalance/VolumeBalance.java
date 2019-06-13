package com.example.balanceview.volumebalance;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.example.balanceview.R;

import java.math.BigDecimal;

/**
 * 自定义音场平衡view
 */
public class VolumeBalance extends LinearLayout {
    public interface Callback {
        void updatePointInfo(PointInfo pointInfo);
    }

    private static String TAG = VolumeBalance.class.getSimpleName();

    private Context mContext;

    private ImageView pointView;

    private int moveStepX;

    private int moveStepY;

    private VerticalSeekBar mVerticalSeekBar;

    private HorizontalSeeker mHorizonSeekBar;

    private int level;
    int max_min;

    /**
     * 滑动背景图宽度
     */
    private int WIDTH;
    /**
     * 滑动背景图高度
     */
    private int HEIGHT;
    /**
     * 滑动背景图到子视图的距离
     */
    private static final int MARGIN_LEFT = 100;

    /**
     * 滑动背景图到子视图的距离
     */
    private static final int MARGIN_TOP = 0;
    /**
     * 实际滑动背景padding背景的值（为了小球球不超出范围）
     */
    private static final int VERTICAL_PADDING = 50;

    /**
     * 滑动圆圈的直径 根据布局 目前直径113
     */
    private static final int POINT_DIAMETER = 113;

    /**
     * 手指滑动到最上部的范围 子视图最上部分到手指滑动到最上部距离
     */
    private int TOP = MARGIN_TOP + VERTICAL_PADDING;

    /**
     * 手指滑动到最底部的范围 子视图最上部分到手指滑动到最底部距离
     */
    private int BOTTOM = MARGIN_TOP + HEIGHT - VERTICAL_PADDING;

    /**
     * 手指滑动到最左侧的范围 子视图左侧到手指滑动到最左侧距离
     */
    private static int LEFT = MARGIN_LEFT;

    /**
     * 手指滑动到最右侧的范围 子视图左侧到手指滑动到最右侧距离
     */
    private int RIGHT = MARGIN_LEFT + WIDTH;

    /**
     * 车厢正中心的x轴坐标值
     */
    private int CENTER_X = MARGIN_LEFT + (WIDTH / 2) - 1;

    /**
     * 车厢正中心的y轴坐标值
     */
    private int CENTER_Y = MARGIN_TOP + (HEIGHT / 2) - 1;

    private static final int EMPTY_MSG = 0;

    private boolean isefficiency = true;

    private Callback callback;

    private PointInfo mCurPointInfo = new PointInfo();

    private final PointInfo mCenterPointInfo = new PointInfo(CENTER_X, CENTER_Y, 0, 0);

    private final PointInfo originCenterInfo = new PointInfo(CENTER_X, CENTER_Y, 0, 0);

    public VolumeBalance(Context context) {
        super(context);
        mContext = context;
    }

    public VolumeBalance(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void init(PointInfo pointInfo) {
        level = 18;
        max_min = (level - 1) / 2;
        /**
         * 每次移动的偏移量，根据音频策略，左7右7，总的偏移量为14
         * 为了控制滑动圆圈在车厢内侧滑动，所以去除掉圆圈的直径
         */
        moveStepX = (int) div((RIGHT - LEFT), level - 1, 2);
        moveStepY = (int) div((BOTTOM - TOP), level - 1, 2);
        Log.d(TAG, "TOP : " + TOP + " BOTTOM : " + BOTTOM + " LEFT : " + LEFT
                + " RIGHT : " + RIGHT + " CENTER_X : " + CENTER_X + " CENTER_Y : " + CENTER_Y + " moveStepX : " + moveStepX + " moveStepY : " + moveStepY);
        mCurPointInfo = pointInfo;
        initView(mCurPointInfo);
        initListener();
    }

    public void resetDefault() {
        mCurPointInfo = this.mCenterPointInfo;
        initView(mCurPointInfo);
    }

    private void initView(PointInfo pointInfo) {
        super.addView(LayoutInflater.from(mContext).inflate(
                R.layout.volum_balance, null));
        pointView = (ImageView) this
                .findViewById(R.id.volum_balance_point_view);
        mVerticalSeekBar = findViewById(R.id.vsb_balance_top);
        mHorizonSeekBar = findViewById(R.id.hsb_balance_left);
        final View bgView = findViewById(R.id.img_balance_bg);
        bgView.post(new Runnable() {
            @Override
            public void run() {
                WIDTH = bgView.getWidth();
                HEIGHT = bgView.getHeight();

                TOP = MARGIN_TOP + VERTICAL_PADDING;

                BOTTOM = MARGIN_TOP + HEIGHT - VERTICAL_PADDING;

                LEFT = MARGIN_LEFT;

                RIGHT = WIDTH + 50;

                CENTER_X = MARGIN_LEFT + (WIDTH / 2) - 1;

                CENTER_Y = MARGIN_TOP + (HEIGHT / 2) - 1;

                if (moveStepX == 0) {
                    moveStepX = (int) div((RIGHT - LEFT), level - 1, 2);
                    moveStepY = (int) div((BOTTOM - TOP), level - 1, 2);
                }

                PointInfo pointInfo = new PointInfo(CENTER_X, CENTER_Y, 0, 0);
                mCurPointInfo = pointInfo;
                updatePointPosition(pointInfo);
            }
        });
    }

    private void initListener() {
        mVerticalSeekBar.setOnSlideChangeListener(new VerticalSeekBar.OnSlideChangeListener() {
            @Override
            public void OnSlideChangeListener(View view, float progress) {
                if (mCurPointInfo.getBalanceY() > progress) {
                    moveBottom((int) (mCurPointInfo.getBalanceY() - progress));
                } else {
                    moveTop((int) (mCurPointInfo.getBalanceY() - progress));
                }
            }

            @Override
            public void onSlideStopTouch(View view, float progress) {

            }
        });
        mVerticalSeekBar.setProgress(mCurPointInfo.getBalanceY());

        mHorizonSeekBar.setProgress(mCurPointInfo.getBalanceX() + 9);
        mHorizonSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mCurPointInfo.getBalanceX() > progress - 9) {
                        moveLeft((int) (mCurPointInfo.getBalanceX() - progress + 9));
                    } else {
                        moveRight((int) (mCurPointInfo.getBalanceX() - progress + 9));
                    }
                }
                mHorizonSeekBar.SetValue("" + (progress - 9));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void backCenter() {
        Message centerMsg = centerHandler.obtainMessage();
        centerMsg.obj = originCenterInfo;
        centerHandler.sendMessageDelayed(centerMsg, 150);
    }

    Handler centerHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PointInfo p = (PointInfo) msg.obj;
            mCurPointInfo = new PointInfo(CENTER_X, CENTER_Y, 0, 0);
            Log.d(TAG, "backCenter---updatePointPosition:" + mCurPointInfo);
            updatePointPosition(mCurPointInfo);
            if (callback != null) {
                callback.updatePointInfo(p);
            }
        }

    };

    private void moveLeft(int progress) {
        Log.d(TAG, "moveLeft");
        if (progress < 0) {
            progress = -progress;
        }
        int mCurX = this.mCurPointInfo.getY();
        int mCurY = this.mCurPointInfo.getY();
        int mCurBalanceX = this.mCurPointInfo.getBalanceX();
        int mCurBalanceY = this.mCurPointInfo.getBalanceY();

        mCurBalanceX = mCurBalanceX - progress;
        if (mCurBalanceX < -max_min) {
            mCurBalanceX = -max_min;
            return;
        }
        mCurX = CENTER_X - (-mCurBalanceX) * moveStepX;
        move(mCurX, mCurY, mCurBalanceX, mCurBalanceY);
    }

    private void moveRight(int progress) {
        Log.d(TAG, "moveRight");
        if (progress < 0) {
            progress = -progress;
        }
        int mCurX = this.mCurPointInfo.getY();
        int mCurY = this.mCurPointInfo.getY();
        int mCurBalanceX = this.mCurPointInfo.getBalanceX();
        int mCurBalanceY = this.mCurPointInfo.getBalanceY();

        mCurBalanceX = mCurBalanceX + progress;
        if (mCurBalanceX > max_min) {
            mCurBalanceX = max_min;
            return;
        }
        mCurX = CENTER_X + mCurBalanceX * moveStepX;
        move(mCurX, mCurY, mCurBalanceX, mCurBalanceY);
    }

    private void moveTop(int progress) {
        if (progress < 0) {
            progress = -progress;
        }
        Log.d(TAG, "moveTop");
        int mCurX = this.mCurPointInfo.getX();
        int mCurY = this.mCurPointInfo.getY();
        int mCurBalanceX = this.mCurPointInfo.getBalanceX();
        int mCurBalanceY = this.mCurPointInfo.getBalanceY();

        mCurBalanceY = mCurBalanceY + progress;
        if (mCurBalanceY > max_min) {
            mCurBalanceY = max_min;
            return;
        }
        mCurY = CENTER_Y - mCurBalanceY * moveStepY;
        move(mCurX, mCurY, mCurBalanceX, mCurBalanceY);
    }

    private void moveBottom(int progress) {
        if (progress < 0) {
            progress = -progress;
        }
        Log.d(TAG, "moveBottom");
        int mCurX = this.mCurPointInfo.getX();
        int mCurY = this.mCurPointInfo.getY();
        int mCurBalanceX = this.mCurPointInfo.getBalanceX();
        int mCurBalanceY = this.mCurPointInfo.getBalanceY();
        mCurBalanceY = mCurBalanceY - progress;
        if (mCurBalanceY < -max_min) {
            mCurBalanceY = -max_min;
            return;
        }
        mCurY = CENTER_Y + (-mCurBalanceY) * moveStepY;
        move(mCurX, mCurY, mCurBalanceX, mCurBalanceY);
    }

    private void move(int x, int y, int balanceX, int balanceY) {
        Log.d(TAG, "move, x = " + x + ", y = " + y + ", balanceX = " + balanceX + ", balanceY = " + balanceY);
        this.mCurPointInfo.setX(x);
        this.mCurPointInfo.setY(y);
        this.mCurPointInfo.setBalanceX(balanceX);
        this.mCurPointInfo.setBalanceY(balanceY);
        Message msg = handler.obtainMessage();
        msg.obj = this.mCurPointInfo;
        handler.sendMessage(msg);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.d(TAG, "onTouchEvent: x = " + x + " y = " + y + " action = " + event.getAction() + " isefficiency = " + isefficiency);
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            delayhandler.removeMessages(EMPTY_MSG);
            touchHandler.removeMessages(EMPTY_MSG);
            isefficiency = true;
            if (y > (MARGIN_TOP + HEIGHT + 20) || y < (MARGIN_TOP - 20) || x > (MARGIN_LEFT + WIDTH + 40)
                    || x < (MARGIN_LEFT - 40)) {
                isefficiency = false;
                return false;
            }

            if (y >= BOTTOM) {
                y = BOTTOM;

            } else if (y <= TOP) {
                y = TOP;
            }
            if (x >= RIGHT) {
                x = RIGHT;
            } else if (x <= LEFT) {
                x = LEFT;
            }

            getParent().requestDisallowInterceptTouchEvent(true);
            Message msg = handler.obtainMessage();
            this.mCurPointInfo.setX(x);
            this.mCurPointInfo.setY(y);
            msg.obj = this.mCurPointInfo;
            handler.sendMessage(msg);
            return true;
        }

        if ((event.getAction() == MotionEvent.ACTION_UP) && isefficiency) {
            // TODO计算move后的balance
            if (x > (CENTER_X - moveStepX) && x < (CENTER_X + moveStepX)
                    && y > (CENTER_Y - moveStepY) && y < (CENTER_Y + moveStepY)) {
                backCenter();
            } else {
                touchHandler.sendEmptyMessage(EMPTY_MSG);
                getParent().requestDisallowInterceptTouchEvent(true);
                delayhandler.sendEmptyMessageDelayed(EMPTY_MSG, 300);
            }
        }
        return super.onTouchEvent(event);
    }

    private Handler delayhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (callback != null) {
                callback.updatePointInfo(mCurPointInfo);
            }
        }

    };

    private void updatePointPosition(PointInfo pointInfo) {
        Log.d(TAG, "updatePointPosition:" + pointInfo);
        if (pointInfo != null) {
            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) pointView
                    .getLayoutParams();
            param.leftMargin = pointInfo.getX() - POINT_DIAMETER / 2;
            param.topMargin = pointInfo.getY() - POINT_DIAMETER / 2;
            pointView.setLayoutParams(param);
            mVerticalSeekBar.setProgress(pointInfo.getBalanceY());
            mHorizonSeekBar.setProgress(pointInfo.getBalanceX() + 9);
        }

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            final PointInfo pointInfo = (PointInfo) msg.obj;
            updatePointPosition(pointInfo);
            PointInfo Info = generatePointInfo();
            if (callback != null) {
                callback.updatePointInfo(Info);
            }
        }

    };

    private Handler touchHandler = new Handler() {
        public void handleMessage(Message msg) {
            final PointInfo pointInfo = generatePointInfo();
            updatePointPosition(pointInfo);
            if (callback != null) {
                callback.updatePointInfo(pointInfo);
            }
        }
    };

    public PointInfo generatePointInfo() {
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) pointView
                .getLayoutParams();
        int x = param.leftMargin;
        int y = param.topMargin;

        int x1 = 0;
        int y1 = 0;
        if (moveStepX == 0) {
            moveStepX = (int) div((RIGHT - LEFT), level - 1, 2);
            moveStepY = (int) div((BOTTOM - TOP), level - 1, 2);
        }
        x1 = (int) ((x - LEFT + POINT_DIAMETER / 2) / moveStepX);
        if (x1 > level - 1) {
            x1 = level - 1;
        }
        if (x1 < 0) {
            x1 = 0;
        }
        x1 -= max_min;

        y1 = (int) ((BOTTOM - y - POINT_DIAMETER / 2) / moveStepY);

        if (y1 > level - 1) {
            y1 = level - 1;
        }
        if (y1 < 0) {
            y1 = 0;
        }
        y1 -= max_min;

        if (x1 < 0)
            x = CENTER_X - x1 * (-1) * moveStepX;
        else
            x = CENTER_X + x1 * moveStepX;

        if (y1 < 0)
            y = CENTER_Y + y1 * (-1) * moveStepY;
        else
            y = CENTER_Y - y1 * moveStepY;

        /*mCurPointInfo.setX(x);
        mCurPointInfo.setY(y);
        mCurPointInfo.setBalanceX(x1);
        mCurPointInfo.setBalanceY(y1);*/

        PointInfo pi = new PointInfo(x, y, x1, y1);
        return pi;
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * <p>
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    private double div(double v1, double v2, int scale) {

        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 刷新下UI
     *
     * @param pointInfo
     */
    public void refresh(PointInfo pointInfo) {
        int x, y;
        if (pointInfo != null) {
            int balanceX = pointInfo.getBalanceX();
            int balanceY = pointInfo.getBalanceY();
            if (balanceX < 0)
                x = CENTER_X - Math.abs(balanceX) * moveStepX;
            else
                x = CENTER_X + balanceX * moveStepX;

            if (balanceY < 0)
                y = CENTER_Y + Math.abs(balanceY) * moveStepY;
            else
                y = CENTER_Y - balanceY * moveStepY;

            move(x, y, pointInfo.getBalanceX(), pointInfo.getBalanceY());
        }
    }
}
