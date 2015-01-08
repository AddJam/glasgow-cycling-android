package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.LoadingView;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Day;
import com.fcd.glasgowcycling.models.Month;
import com.fcd.glasgowcycling.models.Overall;
import com.fcd.glasgowcycling.models.Stats;
import com.fcd.glasgowcycling.models.User;
import com.fcd.glasgowcycling.utils.ActionBarFontUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    @InjectView(R.id.overview_label)TextView overviewLabel;
    @InjectView(R.id.distance_value) TextView distanceValue;
    @InjectView(R.id.route_value) TextView routeValue;
    @InjectView(R.id.bar_chart) BarChart barChart;
    @InjectView(R.id.line_chart_label)TextView lineChartLabel;
    @InjectView(R.id.line_chart) LineChart lineChart;
    @InjectView(R.id.bar_chart_label)TextView barChartLabel;

    @InjectView(R.id.loading_view) LoadingView loadingView;
    @InjectView(R.id.stats_area) View statsArea;

    private User mUser;
    private Stats mStats;
    private ArrayList<Day> mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);
        ActionBarFontUtil.setFont(this);

        loadingView.setBlue(true);
        loadingView.startAnimating();
        loadingView.setRandomMessage();

        mUser = ((CyclingApplication) getApplication()).getCurrentUser();
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

        Typeface regularFont = Typeface.createFromAsset(getAssets(), "fonts/FutureCityRegular.otf");
        Typeface semiBoldFont = Typeface.createFromAsset(getAssets(), "fonts/FutureCitySemiBold.otf");
        username.setTypeface(regularFont);
        distanceProfileStat.setTypeface(regularFont);
        timeStat.setTypeface(regularFont);
        distanceValue.setTypeface(semiBoldFont);
        routeValue.setTypeface(semiBoldFont);
        overviewLabel.setTypeface(semiBoldFont);
        barChartLabel.setTypeface(semiBoldFont);
        lineChartLabel.setTypeface(semiBoldFont);
    }

    public void getStats(){
        // Load from API
        int numDays = 7;
        cyclingService.getStats(numDays, new Callback<Stats>() {

            @Override
            public void success(Stats stats, Response response) {
                Log.d(TAG, "retreived stats");

                // Delete existing users
                new Delete().from(Stats.class).execute();

                // Store
                mStats = stats;
                Overall overallStats = mStats.getOverall();

                if(overallStats.getRoutesCompleted() < 1){
                    loadingView.stopAnimating();
                    loadingView.setMessage("No activity in the past week");
                }
                else {

                    distanceValue.setText(String.format("%.01f km", overallStats.getDistance()));
                    routeValue.setText(overallStats.getRoutesCompleted() + " completed");

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

    public void setBarChart() {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < mStats.getDays().size(); i++) {
            String day = getDaysAgoName(i);
            xVals.add(0, day);
            yVals1.add(new BarEntry(mStats.getDays().get(i).getRoutesCompleted(), i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, null);
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        barChart.setDrawYValues(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDescription("");
        barChart.setDrawLegend(false);
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
        // Set axis values
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < mStats.getDays().size(); i++) {
            String day = getDaysAgoName(i);
            xVals.add(0, day);
            long yValue = Math.round(mStats.getDays().get(i).getDistance());
            yVals.add(new Entry(yValue, i));
        }
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");

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

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1);

        //Set labels
        XLabels xl = lineChart.getXLabels();
        xl.setAvoidFirstLastClipping(true);
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);

        YLabels yl = lineChart.getYLabels();
        yl.setPosition(YLabels.YLabelPosition.LEFT);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        lineChart.setData(data);
        lineChart.refreshDrawableState();
    }

    private String getDaysAgoName(int daysAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String[] days = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        days[calendar.get(Calendar.DAY_OF_WEEK) - 1] = "Today";
        int todayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int dayOfWeek = (todayIndex - daysAgo) % 7;
        if (dayOfWeek < 0) {
            dayOfWeek += 7;
        }
        return days[dayOfWeek];
    }
}
