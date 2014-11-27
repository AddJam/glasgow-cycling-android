package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.LoadingView;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Day;
import com.fcd.glasgowcycling.models.Month;
import com.fcd.glasgowcycling.models.Overall;
import com.fcd.glasgowcycling.models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserStatsActivity extends Activity {

    @Inject
    GoCyclingApiInterface cyclingService;
    private static final String TAG = "StatsActivity";

    @InjectView(R.id.username) TextView username;
    @InjectView(R.id.distance_stat) TextView distanceProfileStat;
    @InjectView(R.id.time_stat) TextView timeStat;
    @InjectView(R.id.profile_image) ImageView profileImage;
    @InjectView(R.id.distance_value) TextView distanceValue;
    @InjectView(R.id.route_value) TextView routeValue;
    @InjectView(R.id.bar_chart) BarChart barChart;
    @InjectView(R.id.line_chart) LineChart lineChart;
    @InjectView(R.id.loading_view) LoadingView loadingView;
    @InjectView(R.id.stats_area) View statsArea;

    private User mUser;
    private Overall mOverallStats;
    private ArrayList<Day> mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);

        loadingView.setBlue(true);
        loadingView.startAnimating();
        loadingView.setRandomMessage();

        mUser = new Select().from(User.class).limit(1).executeSingle();
        Month month = mUser.getMonth();

        username.setText(mUser.getUsername());
        distanceProfileStat.setText(month.getReadableTime());
        timeStat.setText(month.getReadableDistance());
        Bitmap decodedImage;
        if (mUser.getProfilePic() != null){
            byte[] decodedString = Base64.decode(mUser.getProfilePic(), Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedImage);
        }

        getStats();
    }

    public void getStats(){
        // Load from API
        int numDays = 7;
        cyclingService.getStats(numDays, new Callback<Overall>() {

            @Override
            public void success(Overall overall, Response response) {
                Log.d(TAG, "retreived stats");

                // Delete existing users
                new Delete().from(Overall.class).execute();

                // Store
                mOverallStats = overall;

                if(mOverallStats.getRoutesCompleted() < 0){
                    loadingView.stopAnimating();
                    loadingView.setMessage("No activity in the past week");
                }
                else {

                    distanceValue.setText(mOverallStats.getDistance() + " km");
                    routeValue.setText(mOverallStats.getRoutesCompleted() + " completed");

                    setBarChart();
                    setLineChart();
                    loadingView.stopAnimating();
                    loadingView.setVisibility(View.GONE);
                    statsArea.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to get stats");
                loadingView.stopAnimating();
                loadingView.setMessage(" Error getting weekly stats");
            }
        });
    }

    public void setBarChart(){

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < mOverallStats.getDays().size(); i++) {
            if (i == mOverallStats.getDays().size()-1){
                xVals.add("Today");
            }
            else {
                xVals.add("Day " + (i + 1));

            }
            yVals1.add(new BarEntry(mOverallStats.getDays().get(i).getRoutesCompleted(), i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, null);
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        // enable the drawing of values
        barChart.setDrawYValues(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);

        barChart.setDescription("");
        barChart.setDrawLegend(false);

        // disable 3D
        barChart.set3DEnabled(false);
        barChart.setUnit("");
        barChart.setDrawGridBackground(false);
        barChart.setDrawHorizontalGrid(true);
        barChart.setDrawVerticalGrid(false);
        barChart.setValueTextSize(10f);
        barChart.setDrawBorder(false);
        barChart.setDrawBarShadow(false);

        XLabels xl = barChart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setCenterXLabelText(true);

        YLabels yl = barChart.getYLabels();
        yl.setLabelCount(8);
        yl.setPosition(YLabels.YLabelPosition.LEFT);

        barChart.setData(data);
        barChart.refreshDrawableState();
    }

    private void setLineChart() {

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < mOverallStats.getDays().size(); i++) {
            if (i == mOverallStats.getDays().size()-1){
                xVals.add("Today");
            }
            else {
                xVals.add("Day " + (i + 1));

            }
            long yValue = Math.round(mOverallStats.getDays().get(i).getDistance());
            yVals.add(new Entry(yValue, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        int jcBlueColor = getResources().getColor(R.color.jcBlueColor);
        int jcLightBlueColor = getResources().getColor(R.color.jcLightBlueColor);
        set1.setColor(jcBlueColor);
        set1.setDrawCubic(true);
        set1.setLineWidth(1f);
        set1.setFillAlpha(65);
        set1.setDrawFilled(true);
        set1.setFillColor(jcLightBlueColor);
        set1.setDrawCircles(false);

        lineChart.setDrawGridBackground(false);
        lineChart.setDrawVerticalGrid(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawYValues(false);
        lineChart.setDescription("");
        lineChart.setDrawLegend(false);

        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        //Set labels
        XLabels xl = lineChart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setCenterXLabelText(true);

        YLabels yl = lineChart.getYLabels();
        yl.setLabelCount(6);
        yl.setPosition(YLabels.YLabelPosition.LEFT);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        // set data
        lineChart.setData(data);
        lineChart.refreshDrawableState();
    }
}
