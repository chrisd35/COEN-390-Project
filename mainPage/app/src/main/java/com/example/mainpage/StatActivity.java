package com.example.mainpage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toolbar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class StatActivity extends AppCompatActivity {

    protected Toolbar stattoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);




        GraphView SoundLevel = (GraphView) findViewById(R.id.SoundLevel);
        BarGraphSeries<DataPoint> SoundLevelData = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        GraphView AirQuality = (GraphView) findViewById(R.id.AirQuality);
        LineGraphSeries<DataPoint> AirQualityData = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        AirQuality.addSeries(AirQualityData);
        SoundLevel.addSeries(SoundLevelData);

    }

    private void setSupportActionBar(Toolbar stattoolbar) {
    }
}