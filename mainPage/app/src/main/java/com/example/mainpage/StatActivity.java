package com.example.mainpage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


public class StatActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    private ImageButton refreshButton;
    private LineGraphSeries<DataPoint> soundLevelDataSeries;
    private LineGraphSeries<DataPoint> VOCDataSeries;
    private LineGraphSeries<DataPoint> CO2DataSeries;

    private GraphView soundLevelGraph;
    private GraphView VOCGraph;
    private GraphView CO2Graph;

    private int dataSize = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        toolbar = findViewById(R.id.statsPageToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        GraphView SoundLevel = (GraphView) findViewById(R.id.SoundLevel);
//        BarGraphSeries<DataPoint> SoundLevelData = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//
//        GraphView AirQuality = (GraphView) findViewById(R.id.AirQuality);
//        LineGraphSeries<DataPoint> AirQualityData = new LineGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//
//        AirQuality.addSeries(AirQualityData);
//        SoundLevel.addSeries(SoundLevelData);

        // Assuming the graph views are defined in your layout XML with ids SoundLevel and AirQuality
        soundLevelGraph = findViewById(R.id.SoundLevel);
        VOCGraph = findViewById(R.id.VOCQuality);
        CO2Graph = findViewById(R.id.CO2Quality);

        // Define empty series initially
        soundLevelDataSeries = new LineGraphSeries<>();
        VOCDataSeries = new LineGraphSeries<>();
        CO2DataSeries = new LineGraphSeries<>();

        //Added ability to tap on values and get toast message

        soundLevelDataSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series soundLevelDataSeries, DataPointInterface dataPoint) {
                Toast.makeText(StatActivity.this, "Sound Level: "+ dataPoint.getY() + " dB", Toast.LENGTH_SHORT).show();
            }
        });
        CO2DataSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series CO2DataSeries, DataPointInterface dataPoint) {
                Toast.makeText(StatActivity.this, "CO2 Level: "+ dataPoint.getY() + " ppm", Toast.LENGTH_SHORT).show();
            }
        });
        VOCDataSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series VOCDataSeries, DataPointInterface dataPoint) {
                Toast.makeText(StatActivity.this, "VOC Level: "+ dataPoint.getY() + " ppb", Toast.LENGTH_SHORT).show();
            }
        });

        // Add empty series to graphs
        soundLevelGraph.addSeries(soundLevelDataSeries);
        VOCGraph.addSeries(VOCDataSeries);
        CO2Graph.addSeries(CO2DataSeries);



//        updateGraphs(generateDummyData(dataSize));
        refreshData(); // Initial data retrieval and graph update

//        // Call to fetch data initially
//        receiveDatafromServer("your_date");
//
//        // Schedule periodic data fetching (e.g., every 5 seconds)
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                receiveDatafromServer("your_date");
//                // Schedule next update (optional)
//                // new Handler().postDelayed(this, 5000); // 5 seconds
//            }
//        }, 0); // Call immediately

        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to refresh data
                refreshData();
            }
        });



    }

private void refreshData() {
    // Call method to retrieve sound data
    DataRetrieveWorker.retrieveSoundDataFromServer("2024-03-24", new DataRetrieveWorker.DataCallback() {
        @Override
        public void onDataLoaded(ArrayList<Double> soundDataList) {
            // Log the data size of sound dataset
            Log.d("StatActivity", "Sound Data Loaded: " + soundDataList.size());
            Log.d("HardBLE",String.valueOf(soundDataList.size()));

            // Update sound level graph with the retrieved sound data
            updateSoundLevelGraph(soundDataList);

            // Call method to retrieve VOC data
            DataRetrieveWorker.retrieveVOCDataFromServer("2024-03-24", new DataRetrieveWorker.DataCallback() {
                @Override
                public void onDataLoaded(ArrayList<Double> vocDataList) {
                    // Log the data size of VOC dataset
                    Log.d("StatActivity", "VOC Data Loaded: " + vocDataList.size());
                    Log.d("HardBLE",String.valueOf(vocDataList.size()));

                    // Update VOC graph with the retrieved VOC data
                    updateVOCGraph(vocDataList);


                    // Call method to retrieve CO2 data
                    DataRetrieveWorker.retrieveCO2DataFromServer("2024-03-24", new DataRetrieveWorker.DataCallback() {
                        @Override
                        public void onDataLoaded(ArrayList<Double> co2DataList) {
                            // Log the data size of CO2 dataset
                            Log.d("StatActivity", "CO2 Data Loaded: " + co2DataList.size());
                            Log.d("HardBLE",String.valueOf(co2DataList.size()));

                            // Update CO2 graph with the retrieved CO2 data
                            updateCO2Graph(co2DataList);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // Handle CO2 data retrieval failure
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle VOC data retrieval failure
                }
            });
        }

        @Override
        public void onFailure(String errorMessage) {
            // Handle sound data retrieval failure
        }
    });
}
    private void updateSoundLevelGraph(ArrayList<Double> soundDataList) {
        try {
            DataPoint[] soundLevelDataPoints = new DataPoint[soundDataList.size()];

            for (int i = 0; i < soundDataList.size(); i++) {
                soundLevelDataPoints[i] = new DataPoint(i, soundDataList.get(i));

            }

            // Resets the data and sets with new data to update the graph
            soundLevelDataSeries.resetData(soundLevelDataPoints);

            //Added Dots to data points
            soundLevelDataSeries.setDrawDataPoints(true);
            soundLevelDataSeries.setDataPointsRadius(10);

            // Establishes minimum and max X values
            soundLevelGraph.getViewport().setMinX(0);
            soundLevelGraph.getViewport().setMaxX(soundDataList.size() - 1);

            // Establishes minimum and max Y values
            soundLevelGraph.getViewport().setMinY(0);
            soundLevelGraph.getViewport().setMaxY(2000);

            // Allows to zoom and scroll within the graphs
            soundLevelGraph.getViewport().setScrollable(true); // enables horizontal scrolling
            soundLevelGraph.getViewport().setScrollableY(true); // enables vertical scrolling
            soundLevelGraph.getViewport().setScalable(true);
            soundLevelGraph.getViewport().setScalableY(true);




            // Allows to add padding to frame the Y axis values
            GridLabelRenderer glrSound = soundLevelGraph.getGridLabelRenderer();

            glrSound.setPadding(128); // should allow for 5 digits to fit on screen



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateVOCGraph(ArrayList<Double> vocDataList) {
        try {
            DataPoint[] vocDataPoints = new DataPoint[vocDataList.size()];

            for (int i = 0; i < vocDataList.size(); i++) {
                vocDataPoints[i] = new DataPoint(i, vocDataList.get(i));
            }

            VOCDataSeries.resetData(vocDataPoints);

            //Added Dots to data point
            VOCDataSeries.setDrawDataPoints(true);
            VOCDataSeries.setDataPointsRadius(10);

            // Set X bounds for VOC graph
            VOCGraph.getViewport().setMinX(0);
            VOCGraph.getViewport().setMaxX(vocDataList.size() - 1);

            // Set Y bounds for VOC graph
            VOCGraph.getViewport().setMinY(0);
            VOCGraph.getViewport().setMaxY(2000);

            // Allow zoom and scroll
            VOCGraph.getViewport().setScrollable(true);
            VOCGraph.getViewport().setScrollableY(true);
            VOCGraph.getViewport().setScalable(true);
            VOCGraph.getViewport().setScalableY(true);

            // Allows to add padding to frame the Y axis values
            GridLabelRenderer glrVOC = VOCGraph.getGridLabelRenderer();

            glrVOC.setPadding(128); // should allow for 5 digits to fit on screen

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCO2Graph(ArrayList<Double> co2DataList) {
        try {
            DataPoint[] co2DataPoints = new DataPoint[co2DataList.size()];

            for (int i = 0; i < co2DataList.size(); i++) {
                co2DataPoints[i] = new DataPoint(i, co2DataList.get(i));
            }


            CO2DataSeries.resetData(co2DataPoints);


            //Added Dots to data points
            CO2DataSeries.setDrawDataPoints(true);
            CO2DataSeries.setDataPointsRadius(10);

            Log.d("HardBLE",String.valueOf(co2DataList.size()));

            // Set X bounds for CO2 graph
            CO2Graph.getViewport().setMinX(0);
            CO2Graph.getViewport().setMaxX(co2DataList.size() - 1);

            // Set Y bounds for CO2 graph
            CO2Graph.getViewport().setMinY(0);
            CO2Graph.getViewport().setMaxY(9000);

            // Allow zoom and scroll
            CO2Graph.getViewport().setScrollable(true);
            CO2Graph.getViewport().setScrollableY(true);
            CO2Graph.getViewport().setScalable(true);
            CO2Graph.getViewport().setScalableY(true);

            // Allows to add padding to frame the Y axis values
            GridLabelRenderer glrCO2 = CO2Graph.getGridLabelRenderer();

            glrCO2.setPadding(128); // should allow for 5 digits to fit on screen


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}