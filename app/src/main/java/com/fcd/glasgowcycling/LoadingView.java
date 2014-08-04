package com.fcd.glasgowcycling;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LoadingView extends RelativeLayout {

    private View mLoadingIndicator;
    private ImageView mBackWheel;
    private ImageView mFrontWheel;
    private RotateAnimation mWheelAnimation;
    private RotateAnimation mFrontWheelAnimation;

    public LoadingView(Context context) {
        super(context);
        init(null, 0);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // View
        LayoutInflater mInflater = (LayoutInflater)super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoadingIndicator = mInflater.inflate(R.layout.loading_indicator, this, true);
        mBackWheel = (ImageView)findViewById(R.id.back_wheel);
        mFrontWheel = (ImageView)findViewById(R.id.front_wheel);

        // Animation
        mWheelAnimation = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
                0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
        mWheelAnimation.setDuration(230);
        mWheelAnimation.setRepeatMode(Animation.RESTART);
        mWheelAnimation.setRepeatCount(Animation.INFINITE);
        mWheelAnimation.setInterpolator(new LinearInterpolator());
    }

    public void startAnimating() {
        mBackWheel.startAnimation(mWheelAnimation);
        mFrontWheel.startAnimation(mWheelAnimation);
    }

    public void stopAnimating() {
        mWheelAnimation.cancel();
        mWheelAnimation.reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
