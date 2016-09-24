package com.qf.selftiltpic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by lenovo on 2016/9/23.
 */
public class TiltPicView extends ImageView {
    private int imageWidth;//图片宽度
    private int imageHeight;//图片高度
    private double angle = 10 * Math.PI / 180;//三角形角度
    private int triangleHeight;//三角形高度
    private Paint paint;//画笔
    private Path path;//绘制路径
    private int type;//倾斜图片的类型

    public TiltPicView(Context context) {
        this(context,null);
    }

    public TiltPicView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TiltPicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.TiltView);
        type = array.getInteger(R.styleable.TiltView_type,1);
        array.recycle();
    }

    //重测大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        imageWidth = measureSpec(widthMeasureSpec);
        imageHeight = measureSpec(heightMeasureSpec);
        setMeasuredDimension(imageWidth, imageHeight); //设置View的大小
        triangleHeight = (int) (Math.abs(Math.tan(angle) * imageHeight));
    }

    //测量长度
    private int measureSpec(int measureSpec) {
        int minLength = 200;
        int mode = MeasureSpec.getMode(measureSpec);
        int length = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST) {
            length = Math.min(length, minLength);
        }
        return length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initPaint();

        Bitmap mBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888); //初始化Bitmap
        Canvas mCanvas = new Canvas(mBitmap);//创建画布，并绘制mBitmap
        Bitmap mBackBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        mCanvas.drawBitmap(resizeBitmap(mBackBitmap), 0, 0, null);//绘制Bitmap

        setTriangle();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawPath(path, paint);

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    //初始化画笔
    private void initPaint() {
        paint = new Paint();
        paint.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paint.setAntiAlias(true);//设置抗锯齿
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);//圆角
    }


    //设置三角形区域
    private void setTriangle() {
        path = new Path();
        switch (type) {
            case 1://右下角
                path.moveTo(0, imageHeight);
                path.lineTo(imageWidth, imageHeight);
                path.lineTo(imageWidth, imageHeight - triangleHeight);
                path.lineTo(0, imageHeight);
                break;
            case 2://左上角+左下角
                path.moveTo(0, triangleHeight);
                path.lineTo(imageWidth, 0);
                path.lineTo(0, 0);
                path.lineTo(0, imageHeight);
                path.lineTo(imageWidth, imageHeight);
                path.lineTo(0, imageHeight - triangleHeight);
                break;
            case 3://右上角+右下角
                path.moveTo(imageWidth, triangleHeight);
                path.lineTo(0, 0);
                path.lineTo(imageWidth, 0);
                path.lineTo(imageWidth, imageHeight);
                path.lineTo(0, imageHeight);
                path.lineTo(imageWidth, imageHeight - triangleHeight);
                break;
            case 4://右上角
                path.moveTo(0, 0);
                path.lineTo(imageWidth, 0);
                path.lineTo(imageWidth, triangleHeight);
                path.lineTo(0, 0);
                break;
            case 5://左上角
                path.moveTo(0, 0);
                path.lineTo(imageWidth, 0);
                path.lineTo(0, triangleHeight);
                path.lineTo(0, 0);
                break;
        }
    }

    //重新调节图片大小
    private Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 设置想要的大小
        int newWidth = imageWidth;
        int newHeight = imageHeight;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
