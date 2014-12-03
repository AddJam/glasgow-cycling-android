package com.fcd.glasgowcycling.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.fcd.glasgowcycling.LoadingView;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.fragments.WelcomeLast;
import com.fcd.glasgowcycling.fragments.WelcomeText;
import com.fcd.glasgowcycling.listeners.OnFragmentInteractionListener;
import com.fcd.glasgowcycling.utils.ActionBarFontUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WelcomeActivity extends FragmentActivity implements OnFragmentInteractionListener {

    public static final int NEXT = 0;

    @InjectView(R.id.loading_view) LoadingView loadingView;

    List<Fragment> fragments;
    int currentFragmentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.inject(this);
        ActionBarFontUtil.setFont(this);

        currentFragmentIndex = 0;
        fragments = new ArrayList<Fragment>();
        fragments.add(WelcomeText.newInstance());
        fragments.add(WelcomeText.newInstance(getString(R.string.welcome_two)));
        fragments.add(WelcomeText.newInstance(getString(R.string.welcome_three)));
        fragments.add(new WelcomeLast());

        showNextFragment();
    }

    public void showNextFragment() {
        Fragment fragment = fragments.get(currentFragmentIndex++);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onButtonPressed(int flag) {
        if (flag == NEXT) {
            loadingView.animateFor(1500);
            showNextFragment();
        }
    }
}
