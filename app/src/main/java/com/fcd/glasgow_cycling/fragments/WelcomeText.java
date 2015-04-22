package com.fcd.glasgow_cycling.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fcd.glasgow_cycling.R;
import com.fcd.glasgow_cycling.activities.WelcomeActivity;
import com.fcd.glasgow_cycling.listeners.OnFragmentInteractionListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WelcomeText extends Fragment {
    private OnFragmentInteractionListener mListener;

    private String mText;

    @InjectView(R.id.continue_button) Button continueButton;
    @InjectView(R.id.text) TextView textView;

    public static WelcomeText newInstance() {
        return new WelcomeText();
    }

    public static WelcomeText newInstance(String text) {
        WelcomeText fragment = new WelcomeText();
        fragment.setText(text);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome_text, container, false);
        ButterKnife.inject(this, view);
        Typeface customFont = android.graphics.Typeface.createFromAsset(getActivity().getAssets(), "fonts/FutureCitySemiBold.otf");
        textView.setTypeface(customFont);
        if (mText != null) {
            textView.setText(mText);
        }

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonPressed(WelcomeActivity.NEXT);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnFragmentInteractionListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }
}
