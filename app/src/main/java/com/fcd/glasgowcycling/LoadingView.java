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
import android.widget.TextView;

public class LoadingView extends RelativeLayout {

    private boolean isBlue;
    private View mLoadingIndicator;
    private ImageView mBackWheel;
    private ImageView mFrontWheel;
    private TextView mMessage;
    private RotateAnimation mWheelAnimation;

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
        loadView();

        // Animation
        mWheelAnimation = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
                0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
        mWheelAnimation.setDuration(230);
        mWheelAnimation.setRepeatMode(Animation.RESTART);
        mWheelAnimation.setRepeatCount(Animation.INFINITE);
        mWheelAnimation.setInterpolator(new LinearInterpolator());
    }

    public void setBlue(boolean blue) {
        isBlue = blue;
        loadView();
    }

    private void loadView() {
        LayoutInflater mInflater = (LayoutInflater)super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoadingIndicator = mInflater.inflate(R.layout.loading_indicator, this, true);
        mBackWheel = (ImageView)findViewById(R.id.back_wheel);
        mFrontWheel = (ImageView)findViewById(R.id.front_wheel);
        mMessage = (TextView)findViewById(R.id.message);

        ImageView bike = (ImageView)findViewById(R.id.bike);
        if (isBlue) {
            bike.setImageResource(R.drawable.bike_rider_blue);
            mBackWheel.setImageResource(R.drawable.wheel_blue);
            mFrontWheel.setImageResource(R.drawable.wheel_blue);
            mMessage.setTextColor(getResources().getColor(R.color.jcBlueColor));
        } else {
            bike.setImageResource(R.drawable.bike_rider);
            mBackWheel.setImageResource(R.drawable.wheel);
            mFrontWheel.setImageResource(R.drawable.wheel);
            mMessage.setTextColor(getResources().getColor(R.color.jcWhiteColor));
        }
    }

    public void startAnimating() {
        mBackWheel.startAnimation(mWheelAnimation);
        mFrontWheel.startAnimation(mWheelAnimation);
    }

    public void stopAnimating() {
        mWheelAnimation.cancel();
        mWheelAnimation.reset();
    }

    public void setMessage(String message) {
        mMessage.setText(message);
        mMessage.setVisibility(VISIBLE);
    }

    public void hideMessage() {
        mMessage.setText("");
        mMessage.setVisibility(GONE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}