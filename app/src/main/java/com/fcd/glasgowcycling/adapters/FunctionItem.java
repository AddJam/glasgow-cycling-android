package com.fcd.glasgowcycling.adapters;

/**
 * Created by chrissloey on 10/07/2014.
 */
public class FunctionItem {
    private int mIconRes;
    private String mText;

    public FunctionItem(int iconRes, String text) {
        mIconRes = iconRes;
        mText = text;
    }

    public int getIcon() {
        return mIconRes;
    }

    public void setIcon(int icon) {
        this.mIconRes = icon;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }
}
