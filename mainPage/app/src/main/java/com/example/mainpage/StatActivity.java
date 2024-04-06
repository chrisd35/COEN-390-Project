package com.example.mainpage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.widget.Toolbar;

import com.example.mainpage.API.Model.AccessTime;
import com.example.mainpage.API.ThresholdData;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Random;


public class StatActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    private ImageButton refreshButton;
    private LineGraphSeries<DataPoint> soundLevelDataSeries;
    private LineGraphSeries<DataPoint> VOCDataSeries;
    private LineGraphSeries<DataPoint> CO2DataSeries;
    private LineGraphSeries<DataPoint> thresholdSoundLine;
    private LineGraphSeries<DataPoint> thresholdVOCLine;
    private LineGraphSeries<DataPoint> thresholdCO2Line;
    private int baseTimeInSeconds;
    private GraphView soundLevelGraph;
    private GraphView VOCGraph;
    private GraphView CO2Graph;

    private int dataSize = 0;

    ThresholdData thresholdData;

    // Define class-level variables to store threshold values
    private int soundThresholdValue;
    private int vocThresholdValue;
    private int co2ThresholdValue;

    private ToggleButton toggleButton;
    private boolean isWeeklyView = false; // Default to daily view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        toolbar = findViewById(R.id.statsPageToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setChecked(false); // Set to represent daily view by default

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isWeeklyView = !isChecked; // Update the flag based on the toggle state
                refreshData(); // Refresh data based on the new view mode
            }
        });



        // Assuming the graph views are defined in your layout XML with ids SoundLevel and AirQuality
        soundLevelGraph = findViewById(R.id.SoundLevel);
        VOCGraph = findViewById(R.id.VOCQuality);
        CO2Graph = findViewById(R.id.CO2Quality);

        // Define empty series initially
        soundLevelDataSeries = new LineGraphSeries<>();
        VOCDataSeries = new LineGraphSeries<>();
        CO2DataSeries = new LineGraphSeries<>();

        //Added ability to tap on values and get toast message




        // Add empty series to graphs
        soundLevelGraph.addSeries(soundLevelDataSeries);
        VOCGraph.addSeries(VOCDataSeries);
        CO2Graph.addSeries(CO2DataSeries);

        // Instantiate ThresholdData
        thresholdData = new ThresholdData(this);

        refreshData(); // Initial data retrieval and graph update



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
//        DataRetrieveWorker.retrieveSoundDataFromServer("2024-04-05", new DataRetrieveWorker.DataCallback() {
//            @Override
//            public void onDataLoaded(ArrayList<Double> soundDataList, ArrayList<AccessTime> accessTimeList) {
//                // Log the data size of sound dataset
//                Log.d("StatActivity", "Sound Data Loaded: " + soundDataList.size());
//
//                // Update sound level graph with the retrieved sound data
//                updateSoundLevelGraph(soundDataList,accessTimeList);
//
//                // Update dataSize
//                dataSize = soundDataList.size();
//
//                // Store sound threshold value
//                soundThresholdValue = thresholdData.getSavedThreshold();
//
//                // Call method to retrieve VOC data
//                DataRetrieveWorker.retrieveVOCDataFromServer("2024-04-05", new DataRetrieveWorker.DataCallback() {
//                    @Override
//                    public void onDataLoaded(ArrayList<Double> vocDataList, ArrayList<AccessTime> accessTimeList) {
//                        // Log the data size of VOC dataset
//                        Log.d("StatActivity", "VOC Data Loaded: " + vocDataList.size());
//
//                        // Update VOC graph with the retrieved VOC data
//                        updateVOCGraph(vocDataList,accessTimeList);
//
//                        // Update dataSize
//                        dataSize = Math.max(dataSize, vocDataList.size());
//
//                        // Store VOC threshold value
//                        vocThresholdValue = thresholdData.getSavedThreshold();
//
//                        // Call method to retrieve CO2 data
//                        DataRetrieveWorker.retrieveCO2DataFromServer("2024-04-05", new DataRetrieveWorker.DataCallback() {
//                            @Override
//                            public void onDataLoaded(ArrayList<Double> co2DataList, ArrayList<AccessTime> accessTimeList) {
//                                // Log the data size of CO2 dataset
//                                Log.d("StatActivity", "CO2 Data Loaded: " + co2DataList.size());
//
//                                // Update CO2 graph with the retrieved CO2 data
//                                updateCO2Graph(co2DataList,accessTimeList);
//
//                                // Update dataSize
//                                dataSize = Math.max(dataSize, co2DataList.size());
//
//                                // Store CO2 threshold value
//                                co2ThresholdValue = thresholdData.getSavedThreshold();
//
//                                // Create threshold lines after updating dataSize
//                                createThresholdLines(soundThresholdValue,vocThresholdValue,co2ThresholdValue);
//                            }
//
//                            @Override
//                            public void onFailure(String errorMessage) {
//                                // Handle CO2 data retrieval failure
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(String errorMessage) {
//                        // Handle VOC data retrieval failure
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                // Handle sound data retrieval failure
//            }
//        });

        String date = "2024-04-05"; // Default date, can be updated based on user interaction

        if (!isWeeklyView) { // Prioritize daily view
            // Fetch daily data
            retrieveDailyData(date);
        } else {
            // Fetch weekly data
            retrieveWeeklyData(date); // Call the retrieveWeeklyData() method here
        }
    }

    private void createThresholdLines(int soundThresholdValue, int vocThresholdValue, int co2ThresholdValue) {
        // Define threshold values for each level graphs
//        int soundThresholdValue = 3000;
//        int vocThresholdValue = 1000;
//        int co2ThresholdValue = 2000;

        // Define the color used for the threshold lines
        int color = Color.RED;

        // Establish threshold line for sound level graph
        thresholdSoundLine = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, soundThresholdValue),
                new DataPoint(dataSize - 1, soundThresholdValue) // Adjust dataSize - 1 according to your data size
        });
        setDashedLine(thresholdSoundLine, color);
        soundLevelGraph.addSeries(thresholdSoundLine);

        // Establish threshold line for VOC graph
        thresholdVOCLine = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, vocThresholdValue),
                new DataPoint(dataSize - 1, vocThresholdValue) // Adjust dataSize - 1 according to your data size
        });
        setDashedLine(thresholdVOCLine, color);
        VOCGraph.addSeries(thresholdVOCLine);

        // Establish threshold line for CO2 graph
        thresholdCO2Line = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, co2ThresholdValue),
                new DataPoint(dataSize - 1, co2ThresholdValue) // Adjust dataSize - 1 according to your data size
        });
        setDashedLine(thresholdCO2Line, color);
        CO2Graph.addSeries(thresholdCO2Line);
    }

    private void setDashedLine(LineGraphSeries<DataPoint> series, int color) {
        series.setDrawAsPath(true);
        series.setAnimated(false);
        series.setDrawDataPoints(false);
        series.setThickness(5);
        series.setCustomPaint(new Paint(Paint.ANTI_ALIAS_FLAG) {{
            setStyle(Paint.Style.STROKE);
            setPathEffect(new DashPathEffect(new float[]{10, 20}, 0)); // Adjust the array to change dash pattern
            setColor(color); // Set the color of the line
        }});
    }

    private void updateSoundLevelGraph(ArrayList<Double> soundDataList, ArrayList<AccessTime> accessTimes) {
        try {
            if (soundDataList.size() != accessTimes.size()) {
                throw new IllegalArgumentException("The size of sound data and access times must be the same");
            }
            if (!accessTimes.isEmpty()) {
                AccessTime baseTime = accessTimes.get(0);
                baseTimeInSeconds = baseTime.getHour() * 3600 + baseTime.getMinute() * 60 + baseTime.getSecond();
            }
            DataPoint[] soundLevelDataPoints = new DataPoint[soundDataList.size()];
            AccessTime baseTime = accessTimes.get(0);
            AccessTime lastTime = accessTimes.get(accessTimes.size() - 1);
            double baseTimeInSeconds = baseTime.getHour() * 3600 + baseTime.getMinute() * 60 + baseTime.getSecond();
            double lastTimeInSeconds = lastTime.getHour() * 3600 + lastTime.getMinute() * 60 + lastTime.getSecond();
            double totalTimeSpan = lastTimeInSeconds - baseTimeInSeconds;

            for (int i = 0; i < soundDataList.size(); i++) {
                AccessTime at = accessTimes.get(i);
                double timeInSeconds = at.getHour() * 3600 + at.getMinute() * 60 + at.getSecond();
                double timeOffset = timeInSeconds - baseTimeInSeconds;
                soundLevelDataPoints[i] = new DataPoint(timeOffset, soundDataList.get(i));
            }

            soundLevelDataSeries.resetData(soundLevelDataPoints);

            soundLevelDataSeries.setDrawDataPoints(true);
            soundLevelDataSeries.setDataPointsRadius(10);

            soundLevelGraph.getViewport().setMinX(soundLevelDataPoints[0].getX());
            soundLevelGraph.getViewport().setMaxX(soundLevelDataPoints[soundDataList.size() - 1].getX());
            soundLevelGraph.getViewport().setScrollable(true);
            soundLevelGraph.getViewport().setScalable(true);

            GridLabelRenderer glrSound = soundLevelGraph.getGridLabelRenderer();
            glrSound.setNumHorizontalLabels(5);

            double labelInterval = totalTimeSpan / 4; // 4 intervals for 5 labels

            soundLevelGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        double adjustedValue = baseTimeInSeconds + value;
                        // Check if the value is close to one of the label positions
                        for (int i = 0; i <= 4; i++) {
                            double labelTimeInSeconds = baseTimeInSeconds + (labelInterval * i);
                            if (Math.abs(adjustedValue - labelTimeInSeconds) < (labelInterval / 2)) {
                                int hours = (int) (labelTimeInSeconds / 3600);
                                int minutes = (int) ((labelTimeInSeconds % 3600) / 60);
                                return String.format("%02d:%02d", hours, minutes);
                            }
                        }
                        return ""; // Return empty string to not draw the label
                    } else {
                        return super.formatLabel(value, false);
                    }
                }
            });
            soundLevelDataSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series soundLevelDataSeries, DataPointInterface dataPoint) {
                    double totalSeconds = baseTimeInSeconds + dataPoint.getX();
                    int hours = (int) (totalSeconds / 3600);
                    int minutes = (int) ((totalSeconds % 3600) / 60);
                    int seconds = (int) (totalSeconds % 60);

                    String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    Toast.makeText(StatActivity.this, "Time: " + timeString + ", Sound Level: " + dataPoint.getY() + " dB", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateVOCGraph(ArrayList<Double> vocDataList, ArrayList<AccessTime> accessTimes) {
        try {
            if (vocDataList.size() != accessTimes.size()) {
                throw new IllegalArgumentException("The size of VOC data and access times must be the same");
            }
            if (!accessTimes.isEmpty()) {
                AccessTime baseTime = accessTimes.get(0);
                baseTimeInSeconds = baseTime.getHour() * 3600 + baseTime.getMinute() * 60 + baseTime.getSecond();
            }
            DataPoint[] vocDataPoints = new DataPoint[vocDataList.size()];

            for (int i = 0; i < vocDataList.size(); i++) {
                AccessTime at = accessTimes.get(i);
                double timeInSeconds = at.getHour() * 3600 + at.getMinute() * 60 + at.getSecond();
                double timeOffset = timeInSeconds - baseTimeInSeconds;
                vocDataPoints[i] = new DataPoint(timeOffset, vocDataList.get(i));
            }

            VOCDataSeries.resetData(vocDataPoints);

            VOCDataSeries.setDrawDataPoints(true);
            VOCDataSeries.setDataPointsRadius(10);

            VOCGraph.getViewport().setMinX(vocDataPoints[0].getX());
            VOCGraph.getViewport().setMaxX(vocDataPoints[vocDataList.size() - 1].getX());
            VOCGraph.getViewport().setScrollable(true);
            VOCGraph.getViewport().setScalable(true);

            GridLabelRenderer glrVOC = VOCGraph.getGridLabelRenderer();
            glrVOC.setNumHorizontalLabels(5);

            VOCGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        double totalSeconds = baseTimeInSeconds + value;
                        int hours = (int) (totalSeconds / 3600);
                        int minutes = (int) ((totalSeconds % 3600) / 60);
                        return String.format("%02d:%02d", hours, minutes);
                    } else {
                        return super.formatLabel(value, false);
                    }
                }
            });

            VOCDataSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    double totalSeconds = baseTimeInSeconds + dataPoint.getX();
                    int hours = (int) (totalSeconds / 3600);
                    int minutes = (int) ((totalSeconds % 3600) / 60);
                    int seconds = (int) (totalSeconds % 60);

                    String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    Toast.makeText(StatActivity.this, "Time: " + timeString + ", VOC Level: " + dataPoint.getY() + " ppb", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCO2Graph(ArrayList<Double> co2DataList, ArrayList<AccessTime> accessTimes) {
        try {
            if (co2DataList.size() != accessTimes.size()) {
                throw new IllegalArgumentException("The size of CO2 data and access times must be the same");
            }
            if (!accessTimes.isEmpty()) {
                AccessTime baseTime = accessTimes.get(0);
                baseTimeInSeconds = baseTime.getHour() * 3600 + baseTime.getMinute() * 60 + baseTime.getSecond();
            }
            DataPoint[] co2DataPoints = new DataPoint[co2DataList.size()];

            for (int i = 0; i < co2DataList.size(); i++) {
                AccessTime at = accessTimes.get(i);
                double timeInSeconds = at.getHour() * 3600 + at.getMinute() * 60 + at.getSecond();
                double timeOffset = timeInSeconds - baseTimeInSeconds;
                co2DataPoints[i] = new DataPoint(timeOffset, co2DataList.get(i));
            }

            CO2DataSeries.resetData(co2DataPoints);

            CO2DataSeries.setDrawDataPoints(true);
            CO2DataSeries.setDataPointsRadius(10);

            CO2Graph.getViewport().setMinX(co2DataPoints[0].getX());
            CO2Graph.getViewport().setMaxX(co2DataPoints[co2DataList.size() - 1].getX());
            CO2Graph.getViewport().setScrollable(true);
            CO2Graph.getViewport().setScalable(true);

            GridLabelRenderer glrCO2 = CO2Graph.getGridLabelRenderer();
            glrCO2.setNumHorizontalLabels(5);

            CO2Graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        double totalSeconds = baseTimeInSeconds + value;
                        int hours = (int) (totalSeconds / 3600);
                        int minutes = (int) ((totalSeconds % 3600) / 60);
                        return String.format("%02d:%02d", hours, minutes);
                    } else {
                        return super.formatLabel(value, false);
                    }
                }
            });

            CO2DataSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    double totalSeconds = baseTimeInSeconds + dataPoint.getX();
                    int hours = (int) (totalSeconds / 3600);
                    int minutes = (int) ((totalSeconds % 3600) / 60);
                    int seconds = (int) (totalSeconds % 60);

                    String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    Toast.makeText(StatActivity.this, "Time: " + timeString + ", CO2 Level: " + dataPoint.getY() + " ppm", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void retrieveWeeklyData(String date) {
//        // Implement logic to fetch weekly data for sound, VOC, and CO2
//        // Call your existing data retrieval methods with appropriate parameters
//        DataRetrieveWorker.retrieveWeeklySoundDataFromServer(date, new DataRetrieveWorker.DataCallback() {
//            @Override
//            public void onDataLoaded(ArrayList<Double> soundDataList, ArrayList<AccessTime> accessTimeList) {
//                // Handle the loaded sound data for weekly view
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                // Handle failure to retrieve sound data for weekly view
//            }
//        });
//
//        // Similar implementation for VOC and CO2
//    }

    private void retrieveDailyData(String date) {
        // Call method to retrieve sound data
        DataRetrieveWorker.retrieveSoundDataFromServer("2024-04-05", new DataRetrieveWorker.DataCallback() {
            @Override
            public void onDataLoaded(ArrayList<Double> soundDataList, ArrayList<AccessTime> accessTimeList) {
                // Log the data size of sound dataset
                Log.d("StatActivity", "Sound Data Loaded: " + soundDataList.size());

                // Update sound level graph with the retrieved sound data
                updateSoundLevelGraph(soundDataList,accessTimeList);

                // Update dataSize
                dataSize = soundDataList.size();

                // Store sound threshold value
                soundThresholdValue = thresholdData.getSavedThreshold();

                // Call method to retrieve VOC data
                DataRetrieveWorker.retrieveVOCDataFromServer("2024-04-05", new DataRetrieveWorker.DataCallback() {
                    @Override
                    public void onDataLoaded(ArrayList<Double> vocDataList, ArrayList<AccessTime> accessTimeList) {
                        // Log the data size of VOC dataset
                        Log.d("StatActivity", "VOC Data Loaded: " + vocDataList.size());

                        // Update VOC graph with the retrieved VOC data
                        updateVOCGraph(vocDataList,accessTimeList);

                        // Update dataSize
                        dataSize = Math.max(dataSize, vocDataList.size());

                        // Store VOC threshold value
                        vocThresholdValue = thresholdData.getSavedThreshold();

                        // Call method to retrieve CO2 data
                        DataRetrieveWorker.retrieveCO2DataFromServer("2024-04-05", new DataRetrieveWorker.DataCallback() {
                            @Override
                            public void onDataLoaded(ArrayList<Double> co2DataList, ArrayList<AccessTime> accessTimeList) {
                                // Log the data size of CO2 dataset
                                Log.d("StatActivity", "CO2 Data Loaded: " + co2DataList.size());

                                // Update CO2 graph with the retrieved CO2 data
                                updateCO2Graph(co2DataList,accessTimeList);

                                // Update dataSize
                                dataSize = Math.max(dataSize, co2DataList.size());

                                // Store CO2 threshold value
                                co2ThresholdValue = thresholdData.getSavedThreshold();

                                // Create threshold lines after updating dataSize
                                createThresholdLines(soundThresholdValue,vocThresholdValue,co2ThresholdValue);
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

    private void retrieveWeeklyData(String date) {
        // For demonstration purposes, let's generate dummy data for a week
        ArrayList<Double> dummySoundData = generateDummyDataForAWeek();
        ArrayList<Double> dummyVOCData = generateDummyDataForAWeek();
        ArrayList<Double> dummyCO2Data = generateDummyDataForAWeek();

        // Dummy access times for a week
        ArrayList<AccessTime> dummyAccessTimes = generateDummyAccessTimesForAWeek();

        // Update level graphs with dummy data
        updateSoundLevelGraph(dummySoundData, dummyAccessTimes);
        updateVOCGraph(dummyVOCData, dummyAccessTimes);
        updateCO2Graph(dummyCO2Data, dummyAccessTimes);
    }

    // Method to generate dummy data for a week
    private ArrayList<Double> generateDummyDataForAWeek() {
        ArrayList<Double> dummyData = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            // Generate random data points for each day of the week
            dummyData.add((double) random.nextInt(100)); // Adjust range as per your requirement
        }
        return dummyData;
    }

    // Method to generate dummy access times for a week
    private ArrayList<AccessTime> generateDummyAccessTimesForAWeek() {
        ArrayList<AccessTime> dummyAccessTimes = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            // Generate dummy access times for each day of the week
            dummyAccessTimes.add(new AccessTime(0, 0, 0)); // Dummy access time (replace with actual implementation)
        }
        return dummyAccessTimes;
    }




    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}