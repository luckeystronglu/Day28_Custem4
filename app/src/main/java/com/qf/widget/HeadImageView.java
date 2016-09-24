package com.qf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qf.day28_custem.R;

/**
 * Created by Ken on 2016/9/23.9:15
 */
public class HeadImageView extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {


    private ImageView iv_bg;
    private CircleImageView iv_head;

    private int vWidth, vHeight;

    private float x = 0.9f;


    public HeadImageView(Context context) {
        this(context, null);
    }

    public HeadImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.custem_headimageview, this, true);
        iv_bg = (ImageView) findViewById(R.id.iv);
        iv_head = (CircleImageView) findViewById(R.id.civ);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        vWidth = getWidth();
        vHeight = getHeight();

        iv_bg.layout(0, 0, vWidth, (int) (vHeight * x));
        iv_head.layout(
                (int)(vWidth/2 - vHeight * (1-x)),
                (int)(vHeight * (1 - (1 - x) * 2)),
                (int)(vWidth/2 + vHeight * (1-x)),
                vHeight);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
}
