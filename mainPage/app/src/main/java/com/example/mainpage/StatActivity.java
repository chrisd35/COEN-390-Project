package com.example.mainpage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

//    private ArrayList<Double> generateDummyData(int size) {
//        ArrayList<Double> dataList = new ArrayList<>();
//        for (int i = 0; i < size; i++) {
//            double value = Math.random() * 100; // Generate random value between 0 and 100
//            dataList.add(value);
//        }
//        return dataList;
//    }

    private void updateGraphs(ArrayList<Double> dataList) {
        try {
            DataPoint[] soundLevelDataPoints = new DataPoint[dataList.size()];
            DataPoint[] VOCDataPoints = new DataPoint[dataList.size()];
            DataPoint[] CO2DataPoints = new DataPoint[dataList.size()];

            for (int i = 0; i < dataList.size(); i++) {
                soundLevelDataPoints[i] = new DataPoint(i, dataList.get(i));
                VOCDataPoints[i] = new DataPoint(i, dataList.get(i));
                CO2DataPoints[i] = new DataPoint(i, dataList.get(i));

            }

            soundLevelDataSeries.resetData(soundLevelDataPoints);
            VOCDataSeries.resetData(VOCDataPoints);
            CO2DataSeries.resetData(CO2DataPoints);

            soundLevelGraph.getViewport().setMinX(0);
            soundLevelGraph.getViewport().setMaxX(dataList.size() - 1);
            VOCGraph.getViewport().setMinX(0);
            VOCGraph.getViewport().setMaxX(dataList.size() - 1);
            CO2Graph.getViewport().setMinX(0);
            CO2Graph.getViewport().setMaxX(dataList.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve data from the server and update graphs
    private void refreshData() {
        DataRetrieveWorker.retrieveSoundDataFromServer("your_date", new DataRetrieveWorker.DataCallback() {
            @Override
            public void onDataLoaded(ArrayList<Double> dataList) {
                // Update graphs with the retrieved data
                updateGraphs(dataList);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure, e.g., show error message
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    // Function to update data points in existing series
//    private void updateGraphs(ArrayList<Double> dataList) {
//        // Clear existing data points
//        soundLevelDataSeries.resetData(new DataPoint[0]);
//        airQualityDataSeries.resetData(new DataPoint[0]);
//
//        // Create new DataPoints and add them to the series
//        DataPoint[] soundLevelDataPoints = new DataPoint[dataList.size()];
//        for (int i = 0; i < dataList.size(); i++) {
//            soundLevelDataPoints[i] = new DataPoint(i, dataList.get(i));
//        }
//
//        DataPoint[] airQualityDataPoints = new DataPoint[dataList.size()];
//        for (int i = 0; i < dataList.size(); i++) {
//            airQualityDataPoints[i] = new DataPoint(i, dataList.get(i));
//        }
//
//        // Set new data points to the series
//        soundLevelDataSeries.resetData(soundLevelDataPoints);
//        airQualityDataSeries.resetData(airQualityDataPoints);
//
//
//        // Refresh the graph by invalidating it
//        soundLevelGraph.invalidate();
//        airQualityGraph.invalidate();
//    }

    // Replace with your actual Retrofit call and error handling
//    private void receiveDatafromServer(String date) {
//        // Call your Retrofit method here to fetch data from server as shown in your previous code
//        // RetrofitClient.getInstance().getApi().retrieveSoundData(new SoundRetrieveData(date));
//
//        // Simulate data retrieval for demonstration
//        ArrayList<Double> dataList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            dataList.add(Math.random() * 100); // Replace with actual data retrieval logic
//        }
//
//        // Update graphs on successful data retrieval
//        updateGraphs(dataList);
//    }

}