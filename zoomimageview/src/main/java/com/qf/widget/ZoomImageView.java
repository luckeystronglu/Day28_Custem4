package com.qf.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Ken on 2016/9/23.14:09
 * 添加手势缩放的ImageView
 */
public class ZoomImageView extends ImageView {

    private Matrix matrix;

    //控件的宽高
    private int vWidth, vHeight;
    //资源的宽高
    private int imgWidth, imgHeight;

    //缩放的比例
    private float initScale;//初始时的缩放比
    private float minScale;//最小的缩放比
    private float maxScale;//最大的缩放比


    private static final int NONE = 0;//普通模式
    private static final int SINGLE = 1;//单指模式
    private static final int DOUBLE = 2;//双指模式

    //当前的模式
    private int type = NONE;

    /**
     * 手势检测器
     * 1、创建手势检测器的对象
     * 2、创建一个手势监听器
     * 3、把TouchEvent事件交由手势监听器来处理
     */
    private GestureDetector gestureDetector;
    
    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        matrix = new Matrix();

        //设置ImageView的压缩方式
        super.setScaleType(ScaleType.MATRIX);

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                matrix.postScale(getScale()+0.5f,getScale()+0.5f,e.getX(),e.getY());
                setImageMatrix(matrix);
                //双击事件
                Log.d("print", "----------->双击");
                return super.onDoubleTap(e);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //获得ImageView中的资源
        Drawable drawable = getDrawable();
        if(drawable != null){
            //获得控件的宽高
            vWidth = getWidth();
            vHeight = getHeight();

            //获得资源的宽高
            imgWidth = drawable.getIntrinsicWidth();
            imgHeight = drawable.getIntrinsicHeight();

            //设置图片的缩放比例
            float scale = 1.0f;
            scale = Math.min(vWidth * 1.0f / imgWidth, vHeight * 1.0f / imgHeight);

            //设置初始的缩放比
            initScale = scale;
            minScale = scale * 0.6f;
            maxScale = scale * 2.5f;

            //设置变换矩阵
            matrix.postTranslate(vWidth/2 - imgWidth/2, vHeight/2 - imgHeight/2);
            matrix.postScale(scale, scale, vWidth/2, vHeight/2);
            setImageMatrix(matrix);
        }
    }

    /**
     * 事件处理
     *
     * event.getPointerCount() 获得当前手指的个数
     *
     * @param event
     * @return
     */
    //两个手指开始的距离
    float beginDistance;
    //两个手指开始的中心点
    float[] beginPoint;

    float[] onePoint;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                //当第一手指触碰屏幕时触发
                type = SINGLE;//将当前的模式修改为单指模式
                //获得点坐标
                onePoint = new float[]{event.getX(0), event.getY(0)};
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //当第一个手指之后的手指触碰到屏幕时触发，
                type = DOUBLE;//将当前的模式修改为双指模式

                //记录两指间的距离
                beginDistance = getDistance(event);
                //记录两指之间的中心点
                beginPoint = getMiddlePoint(event);

                break;
            case MotionEvent.ACTION_MOVE:
                switch (type){
                    case SINGLE:
                        //单指模式时滑动

                        int x2 = (int) event.getX(0);
                        int y2 = (int) event.getY(0);

                        matrix.postTranslate(x2 - onePoint[0], y2 - onePoint[1]);
                        setImageMatrix(matrix);

                        onePoint[0] = x2;
                        onePoint[1] = y2;

                        break;
                    case DOUBLE:
                        //双指模式时滑动

                        //获得移动时的距离
                        float moveDistance = getDistance(event);

                        //获得图片的缩放比
                        float scale = moveDistance / beginDistance;

                        //获得两指之间的中心点
                        float[] mPoint = getMiddlePoint(event);

                        //设置矩阵
                        matrix.postTranslate(mPoint[0] - beginPoint[0], mPoint[1] - beginPoint[1]);
                        if((getScale() > minScale && getScale() < maxScale)
                                || (getScale() <= minScale && scale > 1)
                                || (getScale() >= maxScale && scale < 1)) {
                            matrix.postScale(scale, scale, mPoint[0], mPoint[1]);
                        }
                        setImageMatrix(matrix);

                        //防止缩放比例过大或者过小的情况
                        beginDistance = moveDistance;
                        beginPoint = mPoint;
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起最后一个手指触发
                type = NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //抬起其他手指触发
                type = SINGLE;
                break;
        }
        return true;
    }

    /**
     * 获得两指间的距离
     * @return
     */
    public float getDistance(MotionEvent event){
        //获得第一个点
        int x1 = (int) event.getX(0);
        int y1 = (int) event.getY(0);

        //获得第二个点
        int x2 = (int) event.getX(1);
        int y2 = (int) event.getY(1);

        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * 获得两指之间的中心点
     * @return
     */
    public float[] getMiddlePoint(MotionEvent event){
        //获得第一个点
        int x1 = (int) event.getX(0);
        int y1 = (int) event.getY(0);

        //获得第二个点
        int x2 = (int) event.getX(1);
        int y2 = (int) event.getY(1);

        int mx = (x1 + x2) / 2;
        int my = (y1 + y2) / 2;

        return new float[]{mx, my};
    }

    /**
     * 获得图片当前的缩放比
     * @return
     */
    public float getScale(){
        //获得matrix中的值
        float[] floats = new float[9];
        matrix.getValues(floats);
        return floats[Matrix.MSCALE_X];
    }
}
