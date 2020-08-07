package com.example.arrebol.widget.Reader1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PageTurnView extends View {

    //位图数据列表
    private List<Bitmap> mBitmaps = new ArrayList<>();

    //标准文字尺寸和大号文字尺寸的占比
    private static final float TEXT_SIZE_NORMAL = 1 / 40F, TEXT_SIZE_LARGER = 1 / 20F;

    //文字画笔
    private TextPaint mTextPaint;
    //上下文环境引用
    private Context mContext;

    //当前显示mBitmaps数据的下标
    private int pageIndex;
    //控件宽高
    private int mViewWidth, mViewHeight;

    //标准文字尺寸和大号文字尺寸
    private float mTextSizeNormal, mTextSizeLarger;
    //裁剪右端点坐标
    private float mClipX;
    //控件左侧和右侧自动吸附的区域
    private float mAutoAreaLeft, mAutoAreaRight;
    //指尖触碰屏幕时X的坐标值
    private float mCurPointX;
    //移动事件的有效距离
    private float mMoveValid;

    private float oldX, oldY;

    private int type;

    //是否显示下一页，是否最后一页的标识值
    private boolean isNextPage, isLastPage, isFirstPage;

    private SlideHandler mSlideHandler;

    public PageTurnView(Context context) {
        super(context);
    }
    public PageTurnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        /**
         * 实例化文本画笔并设置参数
         */
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        //实例化滑动Handler处理
        mSlideHandler = new SlideHandler();
    }

    public PageTurnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置位图数据
     * @param mBitmaps 位图数据列表
     */
    public synchronized void setBitmaps(List<Bitmap> mBitmaps){
        /**
         * 如果数据为空则抛出异常
         */
        if(null == mBitmaps || mBitmaps.size() == 0){
            throw new IllegalArgumentException("no bitmap to display");
        }

        /**
         * 如果数据长度小于2
         */
        if(mBitmaps.size() < 2){
            //两张的画
            throw new IllegalArgumentException("length is not enough");
        }

        this.mBitmaps = mBitmaps;
        invalidate();
    }

    /**
     * 默认显示
     * @param canvas canvas对象
     */
    private void defaultDisplay(Canvas canvas){
        //绘制底色
        canvas.drawColor(Color.WHITE);

        //绘制标题文本
        mTextPaint.setTextSize(mTextSizeLarger);
        mTextPaint.setColor(Color.RED);
        canvas.drawText("FBI WARNING", mViewWidth / 2, mViewHeight / 4, mTextPaint);

        //绘制提示文本
        mTextPaint.setTextSize(mTextSizeNormal);
        mTextPaint.setColor(Color.BLACK);
        canvas.drawText("Please set data", mViewWidth / 2, mViewHeight / 3, mTextPaint);
    }

    /**
     * 初始化位图数据
     * 缩放位图尺寸与屏幕适配
     */
    private void initBitmaps(){
        List<Bitmap> temp = new ArrayList<>();
        for(int i = 0; i < mBitmaps.size(); i++){
            Bitmap bitmap = Bitmap.createScaledBitmap(mBitmaps.get(i), mViewWidth, mViewHeight, true);
            temp.add(bitmap);
        }
        mBitmaps = temp;
        Collections.reverse(mBitmaps);
    }

    /**
     * 绘制位图
     * @param canvas canvas对象
     */
    private void drawBitmaps(Canvas canvas){

        // 绘制位图前重置isLastPage为false
        isLastPage = false;

        // 限制pageIndex的值范围
        pageIndex = Math.max(pageIndex, 0);
        pageIndex = Math.min(pageIndex, mBitmaps.size());

        //计算数据起始位置
        int start = mBitmaps.size() - 2 - pageIndex;
        int end = mBitmaps.size() - pageIndex;

        //说明为第一页
        isFirstPage = pageIndex == 0;
        /*
         * 如果数据起点位置小于0则表示当前已经到了最后一张图片
         */
        if(start < 0){
            // 此时设置isLastPage为true
            isLastPage = true;

            // 并显示提示信息
            //showToast("This is the last page");
            //Log.d("zcc", "drawBitmaps: last");

            // 强制重置起始位置
            start = 0;
            end = 1;
        }

        for(int i = start; i < end; i++){
            canvas.save();

            /*
             * 仅裁剪位于最顶层的画布区域
             */
            if(i == end - 1 && !isLastPage){
                canvas.clipRect(0, 0, mClipX, mViewHeight);
            }

            canvas.drawBitmap(mBitmaps.get(i), 0, 0, null);

            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取控件宽高
        mViewHeight = h;
        mViewWidth = w;

        //初始化位图数据
        initBitmaps();

        //计算尺寸
        mTextSizeNormal = TEXT_SIZE_NORMAL * mViewHeight;
        mTextSizeLarger = TEXT_SIZE_LARGER * mViewHeight;

        //初始化裁剪右端点坐标
        mClipX = mViewWidth;

        //计算控件左侧和右侧自动吸附的区域
        mAutoAreaLeft = mViewWidth * 1 / 5F;
        mAutoAreaRight = mViewWidth * 4 / 5F;

        //计算一度的有效距离
        mMoveValid = mViewWidth * 1 / 100F;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 每次触发TouchEvent重置isNextPage为true
        isNextPage = true;

        /*
         * 判断当前事件类型
         */
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN://触摸屏幕时
                oldX = event.getX();
                oldY = event.getY();

                // 获取当前事件x坐标
                mCurPointX = event.getX();

//                /*
//                 * 如果事件点位于回滚区域
//                 */
//                if(mCurPointX < mAutoAreaLeft){
//                    // 那就不翻下一页而是上一页
//                    isNextPage = false;
//                    pageIndex--;
//                    mClipX = mCurPointX;
//                    type = 0;
//                    mSlideHandler.sleep(25);
//                }
//                if(mCurPointX > mAutoAreaRight){
//                    isNextPage = true;
//                    pageIndex++;
//                    mClipX = mViewWidth;
//                    type = 1;
//                    mSlideHandler.sleep(25);
//                }
                if(isFirstPage){
                    //showToast("This is the first page");
                    Log.d("zcc", "drawBitmaps: first");
                }
                if(isLastPage){
                    Log.d("zcc", "drawBitmaps: last");
                }
                break;
            case MotionEvent.ACTION_MOVE:// 滑动时

                break;
            case MotionEvent.ACTION_UP://触点抬起时

                if(oldX - event.getX() > 0){
                    Log.d("zcc", "从右向左滑动");
                    isNextPage = false;
                    pageIndex--;
                    mClipX = mCurPointX;
                    type = 0;
                    mSlideHandler.sleep(25);

                }else if(event.getX() - oldX > 0){
                    Log.d("zcc", "从左向右滑动");
                    isNextPage = true;
                    pageIndex++;
                    mClipX = mViewWidth;
                    type = 1;
                    mSlideHandler.sleep(25);
                }
                Log.d("zcc", "pageIndex：" + pageIndex);
//                // 判断是否需要自动滑动
//                judgeSlideAuto();

//                /*
//                 * 如果当前页不是最后一页
//                 * 如果是需要翻下一页
//                 * 并且上一页已被clip掉
//                 */
//                if(!isLastPage && isNextPage && mClipX <= 0){
//                    pageIndex++;
//                    mClipX = mViewWidth;
//                    invalidate();
//                }
                break;
        }
        return true;
    }

    private void autoSlide(){
        if(type == 0){
            //向右展开
            while (mClipX < mViewWidth){
                mClipX++;
                invalidate();
            }
        }else if(type == 1){
            //向左展开
            while (mClipX > 0){
                mClipX--;
                invalidate();
            }
        }
    }

    /**
     * 判断是否需要自动滑动
     * 根据参数的当前值判断绘制
     */
    private void judgeSlideAuto() {
        /*
         * 如果裁剪的右端点坐标在控件左端五分之一的区域内，那么直接让其自动滑动到控件左端
         */
        if(mClipX < mAutoAreaLeft){
            while (mClipX > 0){
                mClipX--;
                invalidate();
            }
        }
        /*
         * 如果裁剪的右端点坐标在控件右端五分之一的区域内，那么直接让其自动滑动到控件右端
         */
        if(mClipX > mAutoAreaRight){
            while (mClipX < mViewWidth){
                mClipX++;
                invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 如果数据为空则显示默认提示文本
         */
        if(null == mBitmaps || mBitmaps.size() == 0){
            defaultDisplay(canvas);
            return;
        }

        //绘制位图
        drawBitmaps(canvas);
    }

    private void showToast(Object msg){
        Toast.makeText(mContext, msg.toString(), Toast.LENGTH_SHORT).show();
    }


    public class SlideHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // 循环调用滑动计算
            PageTurnView.this.autoSlide();

            // 重绘视图
            PageTurnView.this.invalidate();
        }

        /**
         * 延迟向Handler发送消息实现时间间隔
         *
         * @param delayMillis
         *            间隔时间
         */
        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }
}
