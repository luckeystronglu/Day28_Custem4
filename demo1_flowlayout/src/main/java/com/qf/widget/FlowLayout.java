package com.qf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ken on 2016/9/23.9:48
 * 流式布局
 */
public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 指定该自定义布局所使用的布局参数
     * 如果没有重写该方法，则默认布局参数为ViewGroup.LayoutParams
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //测量子控件的宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int wsize = MeasureSpec.getSize(widthMeasureSpec);
        int wmode = MeasureSpec.getMode(widthMeasureSpec);
        int hsize = MeasureSpec.getSize(heightMeasureSpec);
        int hmode = MeasureSpec.getMode(heightMeasureSpec);


        int aWidth = 0;//控件的总宽度
        int aHeight = 0;//控件的总高度
        int lineWidth = 0;//当前行的宽度
        int lineHeight = 0;//当前行的高度

        //遍历循环每个子控件
        for(int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
            //当前控件所占的宽度
            int width = view.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            //当前控件所占的高度
            int height = view.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;

            //判断控件是否需要换行
            if(lineWidth + width <= wsize){
                //宽度进行累加
                lineWidth += width;
                //高度取最大的高度
                lineHeight = Math.max(lineHeight, height);
            } else {
                //这一行放不下了，需要换行

                //将当前行的宽高情况，添加进总宽高中
                aWidth = Math.max(aWidth, lineWidth);
                aHeight += lineHeight;

                //重新开启新的一行
                lineWidth = width;
                lineHeight = height;
            }

            //计算到最后一个控件
            if(i == getChildCount() - 1){
                //将最后一行的宽高情况添加进总宽高
                aWidth = Math.max(aWidth, lineWidth);
                aHeight += lineHeight;
            }
        }

        Log.d("print", "获得布局的宽高：" + aWidth + "  " + aHeight);
        setMeasuredDimension(
                wmode == MeasureSpec.EXACTLY ? wsize : aWidth,
                hmode == MeasureSpec.EXACTLY ? hsize : aHeight);
    }

    /**
     * 控制子控件位置摆放的
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineWidth = 0;//当前行的宽度
        int lineHeigth = 0;//当前行的高度
        int aHegiht = 0;//控件的总高度

        for (int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();

            //当前控件的宽度 和 高度
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();

            if((lineWidth + width + layoutParams.leftMargin + layoutParams.rightMargin) > getWidth()){
                //说明需要换行
                aHegiht += lineHeigth;
                lineWidth = 0;
                lineHeigth = 0;
            }

            l = lineWidth + layoutParams.leftMargin;
            t = aHegiht + layoutParams.topMargin;
            r = lineWidth + layoutParams.leftMargin + width;
            b = aHegiht + layoutParams.topMargin + height;

            //把当前控件的宽高数据加入到行的宽高数据中
            lineWidth += width + layoutParams.leftMargin + layoutParams.rightMargin;
            lineHeigth = Math.max(lineHeigth, height + layoutParams.topMargin + layoutParams.bottomMargin);

            //摆放当前控件
            view.layout(l, t, r, b);
        }

    }
}
