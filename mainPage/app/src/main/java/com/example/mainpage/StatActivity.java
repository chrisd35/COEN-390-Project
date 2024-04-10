package com.example.mainpage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


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

   private String Chosendate;
    private ToggleButton toggleButton;
    private boolean isWeeklyView = false; // Default to daily view
    private DataPoint[] soundLevelDataPoints;
    private DataPoint[] vocLevelDataPoints;
    private DataPoint[] co2LevelDataPoints;

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
        Spinner dynamicSpinner = findViewById(R.id.dynamic_spinner);

        DataRetrieveWorker.getAllDatesFromServer(new DataRetrieveWorker.DataCallbackdates() {
            @Override
            public void onSuccess(List<String> dates) {
                for (String date : dates) {
                    Log.d("Staty",date);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        StatActivity.this, android.R.layout.simple_spinner_item, dates);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dynamicSpinner.setAdapter(adapter);
                int defaultPosition = 0; // or any position you want to be the default
                dynamicSpinner.setSelection(defaultPosition);
                Chosendate = dynamicSpinner.getItemAtPosition(defaultPosition).toString();
                refreshData(); // Call your method to refresh data or execute other actions
                Toast.makeText(StatActivity.this, "Default selected: " + Chosendate, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {

            }
        });



        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get selected item
                String selectedItem = parentView.getItemAtPosition(position).toString();
                Chosendate=selectedItem;
                refreshData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Code to handle when nothing is selected
            }
        });
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

         // Initial data retrieval and graph update



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
        String date = Chosendate; // Default date, can be updated based on user interaction

        if (!isWeeklyView) { // Prioritize daily view
            // Fetch daily data
            retrieveDailyData(date);
        } else {
            // Fetch weekly data
            retrieveWeeklyData(date); // Call the retrieveWeeklyData() method here
        }
    }

    private void createThresholdLines(int soundThresholdValue) {

        // Define the color used for the threshold lines
        int colorSound = Color.BLACK;

        // Define the threshold values for each levels
        int soundLevel1 = Color.YELLOW;
        int soundLevel2 = Color.RED;
        int soundLevel1Limit = 70;
        int soundLevel2Limit = 86;

        int vocLevel1 = Color.YELLOW;
        int vocLevel2 = Color.RED;
        int vocLevel1Limit = 51;
        int vocLevel2Limit = 101;

        int co2Level1 = Color.YELLOW;
        int co2Level2 = Color.RED;
        int co2Leve1Limit = 1001;
        int co2Level2Limit = 1501;


        // Establish threshold line for sound graph from the user's input
        if (soundThresholdValue != 0) {
            thresholdSoundLine = new LineGraphSeries<>(new DataPoint[]{
                    new DataPoint(soundLevelDataPoints[0].getX(), soundThresholdValue),
                    new DataPoint(soundLevelDataPoints[soundLevelDataPoints.length - 1].getX(), soundThresholdValue)
            });
            setDashedLine(thresholdSoundLine, colorSound, 1000);
            soundLevelGraph.addSeries(thresholdSoundLine);
        }

        // Establish separate lines for soundLevel1Limit and soundLevel2Limit
        LineGraphSeries<DataPoint> soundLevel1LimitLine = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(soundLevelDataPoints[0].getX(), soundLevel1Limit),
                new DataPoint(soundLevelDataPoints[soundLevelDataPoints.length - 1].getX(), soundLevel1Limit)
        });
        setDashedLine(soundLevel1LimitLine, soundLevel1, 1000);
        soundLevelGraph.addSeries(soundLevel1LimitLine);

        LineGraphSeries<DataPoint> soundLevel2LimitLine = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(soundLevelDataPoints[0].getX(), soundLevel2Limit),
                new DataPoint(soundLevelDataPoints[soundLevelDataPoints.length - 1].getX(), soundLevel2Limit)
        });
        setDashedLine(soundLevel2LimitLine, soundLevel2, 1000);
        soundLevelGraph.addSeries(soundLevel2LimitLine);


        // Establish threshold line for VOC graph
        LineGraphSeries<DataPoint> vocLevel1LimitLine = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(vocLevelDataPoints[0].getX(), vocLevel1Limit),
                new DataPoint(vocLevelDataPoints[vocLevelDataPoints.length - 1].getX(), vocLevel1Limit)
        });
        setDashedLine(vocLevel1LimitLine, vocLevel1, 1000);
        VOCGraph.addSeries(vocLevel1LimitLine);

        LineGraphSeries<DataPoint> vocLevel2LimitLine = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(vocLevelDataPoints[0].getX(), vocLevel2Limit),
                new DataPoint(vocLevelDataPoints[vocLevelDataPoints.length - 1].getX(), vocLevel2Limit)
        });
        setDashedLine(vocLevel2LimitLine, vocLevel2, 1000);
        VOCGraph.addSeries(vocLevel2LimitLine);

// Establish threshold line for CO2 graph
        LineGraphSeries<DataPoint> co2Level1LimitLine = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(co2LevelDataPoints[0].getX(), co2Leve1Limit),
                new DataPoint(co2LevelDataPoints[co2LevelDataPoints.length - 1].getX(), co2Leve1Limit)
        });
        setDashedLine(co2Level1LimitLine, co2Level1, 1000);
        CO2Graph.addSeries(co2Level1LimitLine);

        LineGraphSeries<DataPoint> co2Level2LimitLine = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(co2LevelDataPoints[0].getX(), co2Level2Limit),
                new DataPoint(co2LevelDataPoints[co2LevelDataPoints.length - 1].getX(), co2Level2Limit)
        });
        setDashedLine(co2Level2LimitLine, co2Level2, 1000);
        CO2Graph.addSeries(co2Level2LimitLine);
    }






    private void setDashedLine(LineGraphSeries<DataPoint> series, int color, int thickness) {
        series.setDrawAsPath(true);
        series.setAnimated(false);
        series.setDrawDataPoints(false);
//        series.setThickness(thickness);
        series.setCustomPaint(new Paint(Paint.ANTI_ALIAS_FLAG) {{
            setStyle(Paint.Style.STROKE);
            setPathEffect(new DashPathEffect(new float[]{10, 20}, 0)); // Adjust the array to change dash pattern
            setColor(color); // Set the color of the line
            series.setThickness(thickness);
        }});
    }

    private void updateSoundLevelGraph(ArrayList<Double> soundDataList, ArrayList<AccessTime> accessTimes) {
        try {
            Log.d("StatActivity", "UpdatedGraphSound Data Loaded: " + soundDataList.size());
            Log.d("StatActivity", "UPdatedGraphAccSound Data Loaded: " +accessTimes.size());
            if (soundDataList.isEmpty() || accessTimes.isEmpty()) {
                // Handle the case where there is no data, perhaps clear the graph or display a message
                soundLevelDataSeries.resetData(new DataPoint[]{}); // Clear the graph
                return; // Stop further processing
            }
            if (soundDataList.size() != accessTimes.size()) {
                throw new IllegalArgumentException("The size of sound data and access times must be the same");
            }
            if (!accessTimes.isEmpty()) {
                AccessTime baseTime = accessTimes.get(0);
                baseTimeInSeconds = baseTime.getHour() * 3600 + baseTime.getMinute() * 60 + baseTime.getSecond();
            }
            Log.d("StatActivity","UP RENDERING");
            DataPoint[] soundLevelDataPoints = new DataPoint[soundDataList.size()];
            AccessTime baseTime = accessTimes.get(0);
            AccessTime lastTime = accessTimes.get(accessTimes.size() - 1);
            double baseTimeInSeconds = baseTime.getHour() * 3600 + baseTime.getMinute() * 60 + baseTime.getSecond();
            double lastTimeInSeconds = lastTime.getHour() * 3600 + lastTime.getMinute() * 60 + lastTime.getSecond();
            double totalTimeSpan = lastTimeInSeconds - baseTimeInSeconds;
            Log.d("StatActivity","IN BetweenUP RENDERING");
            for (int i = 0; i < soundDataList.size(); i++) {
                AccessTime at = accessTimes.get(i);
                double timeInSeconds = at.getHour() * 3600 + at.getMinute() * 60 + at.getSecond();
                double timeOffset = timeInSeconds - baseTimeInSeconds;
                soundLevelDataPoints[i] = new DataPoint(timeOffset, soundDataList.get(i));
            }
            try {
                Log.d("StatActivity","afterloop");
                Arrays.sort(soundLevelDataPoints, new Comparator<DataPoint>() {
                    @Override
                    public int compare(DataPoint dp1, DataPoint dp2) {
                        return Double.compare(dp1.getX(), dp2.getX());
                    }
                });
                soundLevelDataSeries.resetData(soundLevelDataPoints);
                Log.d("StatActivity","afterreset");
                soundLevelDataSeries.setDrawDataPoints(true);
                soundLevelDataSeries.setDataPointsRadius(10);
                Log.d("StatActivity","radius");

                soundLevelGraph.getViewport().setMinX(soundLevelDataPoints[0].getX());
                soundLevelGraph.getViewport().setMaxX(soundLevelDataPoints[soundDataList.size() - 1].getX());
                soundLevelGraph.getViewport().setScrollable(true);
                soundLevelGraph.getViewport().setScalable(true);
                Log.d("StatActivity","scal");
                GridLabelRenderer glrSound = soundLevelGraph.getGridLabelRenderer();
                glrSound.setNumHorizontalLabels(5);
                soundLevelGraph.setTitle("Sound Quality");
                glrSound.setVerticalAxisTitle("dB");
                glrSound.setVerticalAxisTitleTextSize(50);
                glrSound.setPadding(20);
                Log.d("StatActivity","rendererl");
                double labelInterval = totalTimeSpan / 4; // 4 intervals for 5 labels
                Log.d("StatActivity","OUTSIDE RENDERING");
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
                                    Log.d("StatActivity",String.format("%02d:%02d", hours, minutes));
                                    Log.d("StatActivity","REnderer");
                                    return String.format("%02d:%02d", hours, minutes);
                                }
                            }
                            return ""; // Return empty string to not draw the label
                        } else {
                            return super.formatLabel(value, false);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("StatActivity", "Error in updateSoundLevelGraph", e);
                e.printStackTrace();
            }

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

            createThresholdLines(soundThresholdValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateVOCGraph(ArrayList<Double> vocDataList, ArrayList<AccessTime> accessTimes) {
        try {
            if (vocDataList.isEmpty() || accessTimes.isEmpty()) {
                // Handle the case where there is no data, perhaps clear the graph or display a message
               VOCDataSeries.resetData(new DataPoint[]{}); // Clear the graph
                return; // Stop further processing
            }
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
            Arrays.sort(vocDataPoints, new Comparator<DataPoint>() {
                @Override
                public int compare(DataPoint dp1, DataPoint dp2) {
                    return Double.compare(dp1.getX(), dp2.getX());
                }
            });
            VOCDataSeries.resetData(vocDataPoints);

            VOCDataSeries.setDrawDataPoints(true);
            VOCDataSeries.setDataPointsRadius(10);

            VOCGraph.getViewport().setMinX(vocDataPoints[0].getX());
            VOCGraph.getViewport().setMaxX(vocDataPoints[vocDataList.size() - 1].getX());
            VOCGraph.getViewport().setScrollable(true);
            VOCGraph.getViewport().setScalable(true);

            GridLabelRenderer glrVOC = VOCGraph.getGridLabelRenderer();
            glrVOC.setNumHorizontalLabels(5);
            VOCGraph.setTitle("VOC Level");
            glrVOC.setVerticalAxisTitle("ppb");
            glrVOC.setVerticalAxisTitleTextSize(50);
            glrVOC.setPadding(20);
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
            createThresholdLines(soundThresholdValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCO2Graph(ArrayList<Double> co2DataList, ArrayList<AccessTime> accessTimes) {
        try {
            if (co2DataList.isEmpty() || accessTimes.isEmpty()) {
                // Handle the case where there is no data, perhaps clear the graph or display a message
                CO2DataSeries.resetData(new DataPoint[]{}); // Clear the graph
                return; // Stop further processing
            }
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
            Arrays.sort(co2DataPoints, new Comparator<DataPoint>() {
                @Override
                public int compare(DataPoint dp1, DataPoint dp2) {
                    return Double.compare(dp1.getX(), dp2.getX());
                }
            });
            CO2DataSeries.resetData(co2DataPoints);

            CO2DataSeries.setDrawDataPoints(true);
            CO2DataSeries.setDataPointsRadius(10);

            CO2Graph.getViewport().setMinX(co2DataPoints[0].getX());
            CO2Graph.getViewport().setMaxX(co2DataPoints[co2DataList.size() - 1].getX());
            CO2Graph.getViewport().setScrollable(true);
            CO2Graph.getViewport().setScalable(true);

            GridLabelRenderer glrCO2 = CO2Graph.getGridLabelRenderer();
            glrCO2.setNumHorizontalLabels(5);
            CO2Graph.setTitle("CO2 Level");
            glrCO2.setVerticalAxisTitle("ppm");
            glrCO2.setVerticalAxisTitleTextSize(50);
            glrCO2.setPadding(20);
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
            createThresholdLines(soundThresholdValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        private void retrieveDailyData(String date) {
        // Call method to retrieve sound data
        DataRetrieveWorker.retrieveSoundDataFromServer(date, new DataRetrieveWorker.DataCallback() {
            @Override
            public void onDataLoaded(ArrayList<Double> soundDataList, ArrayList<AccessTime> accessTimeList) {
                // Log the data size of sound dataset
                Log.d("StatActivity", "Sound Data Loaded: " + soundDataList.size());
                Log.d("StatActivity", "AccSound Data Loaded: " +accessTimeList.size());

                // Update sound level graph with the retrieved sound data
                updateSoundLevelGraph(soundDataList,accessTimeList);
                try {

                    dataSize = soundDataList.size();

                    // Store sound threshold value
                    if(thresholdData.ThresholdExist())
                        soundThresholdValue = thresholdData.getSavedThreshold();
                    // Initialize soundLevelDataPoints
                    soundLevelDataPoints = new DataPoint[soundDataList.size()];

                    // Populate soundLevelDataPoints with data
                    for (int i = 0; i < soundDataList.size(); i++) {
                        AccessTime at = accessTimeList.get(i);
                        double timeInSeconds = at.getHour() * 3600 + at.getMinute() * 60 + at.getSecond();
                        double timeOffset = timeInSeconds - baseTimeInSeconds;
                        soundLevelDataPoints[i] = new DataPoint(timeOffset, soundDataList.get(i));
                    }
                }catch (IndexOutOfBoundsException e)
                {
                    Log.d("StatActivity", e.getMessage() );
                }
                catch (Exception e)
                {
                    Log.d("StatActivity", e.getMessage() );
                }


                // Update dataSize


                // Call method to retrieve VOC data
                DataRetrieveWorker.retrieveVOCDataFromServer(date, new DataRetrieveWorker.DataCallback() {
                    @Override
                    public void onDataLoaded(ArrayList<Double> vocDataList, ArrayList<AccessTime> accessTimeList) {
                        // Log the data size of VOC dataset
                        Log.d("StatActivity", "VOC Data Loaded: " + vocDataList.size());
                        Log.d("StatActivity", "AccVOCData Loaded: " +accessTimeList.size());
                        // Update VOC graph with the retrieved VOC data
                        updateVOCGraph(vocDataList,accessTimeList);
                        try {
                            dataSize = Math.max(dataSize, vocDataList.size());

                            // Initialize vocLevelDataPoints
                            vocLevelDataPoints = new DataPoint[vocDataList.size()];
                            // Populate vocLevelDataPoints with data
                            for (int i = 0; i < vocDataList.size(); i++) {
                                AccessTime at = accessTimeList.get(i);
                                double timeInSeconds = at.getHour() * 3600 + at.getMinute() * 60 + at.getSecond();
                                double timeOffset = timeInSeconds - baseTimeInSeconds;
                                vocLevelDataPoints[i] = new DataPoint(timeOffset, vocDataList.get(i));
                            }

                            // Store VOC threshold value

                        }catch (IndexOutOfBoundsException e)
                        {
                            Log.d("StatActivity", e.getMessage() );
                        }
                        catch (Exception e)
                        {
                            Log.d("StatActivity", e.getMessage() );
                        }


                        // Update dataSize


                        // Call method to retrieve CO2 data
                        DataRetrieveWorker.retrieveCO2DataFromServer(date, new DataRetrieveWorker.DataCallback() {
                            @Override
                            public void onDataLoaded(ArrayList<Double> co2DataList, ArrayList<AccessTime> accessTimeList) {
                                // Log the data size of CO2 dataset
                                Log.d("StatActivity", "CO2 Data Loaded: " + co2DataList.size());
                                Log.d("StatActivity", "Accco2Data Loaded: " +accessTimeList.size());
                                updateCO2Graph(co2DataList,accessTimeList);
                                // Update CO2 graph with the retrieved CO2 data
                                try {
                                    dataSize = Math.max(dataSize, co2DataList.size());

                                    // Initialize co2LevelDataPoints
                                    co2LevelDataPoints = new DataPoint[co2DataList.size()];
                                    // Populate co2LevelDataPoints with data
                                    for (int i = 0; i < co2DataList.size(); i++) {
                                        AccessTime at = accessTimeList.get(i);
                                        double timeInSeconds = at.getHour() * 3600 + at.getMinute() * 60 + at.getSecond();
                                        double timeOffset = timeInSeconds - baseTimeInSeconds;
                                        co2LevelDataPoints[i] = new DataPoint(timeOffset, co2DataList.get(i));
                                    }

                                    createThresholdLines(soundThresholdValue);

                                }catch (IndexOutOfBoundsException e)
                                {
                                    Log.d("StatActivity", e.getMessage() );
                                }
                                catch (Exception e)
                                {
                                    Log.d("StatActivity", e.getMessage() );
                                }


                                // Update dataSize


                                // Store CO2 threshold value


                                // Create threshold lines after updating dataSize

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

    // Retrieves the weekly data
    // Right now, it is setup to only display dummy data
//    private void retrieveWeeklyData(String date) {
//        // Generate dummy data for a week with a loop
//        ArrayList<Double> dummySoundData = new ArrayList<>();
//        ArrayList<Double> dummyVOCData = new ArrayList<>();
//        ArrayList<Double> dummyCO2Data = new ArrayList<>();
//        ArrayList<AccessTime> dummyAccessTimes = new ArrayList<>();
//
//        Random random = new Random();
//        for (int i = 0; i < 7; i++) {
//            // Generate random data points for each day
//            dummySoundData.add((double) random.nextInt(100)); // Adjust range as needed
//            dummyVOCData.add((double) random.nextInt(500)); // Adjust range as needed
//            dummyCO2Data.add((double) random.nextInt(1000)); // Adjust range as needed
//
//            // Create dummy access times (replace with actual implementation)
//            // Ensure AccessTime handles weekly data appropriately (e.g., by storing a day index)
//            dummyAccessTimes.add(new AccessTime(i, 0, 0)); // Example for day-based access times
//        }
//
//        // Update level graphs with weekly dummy data
//        updateSoundLevelGraph(dummySoundData, dummyAccessTimes);
//        updateVOCGraph(dummyVOCData, dummyAccessTimes);
//        updateCO2Graph(dummyCO2Data, dummyAccessTimes);
//
//
//    }

    private void retrieveWeeklyData(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
        } catch (ParseException e) {
            // Handle parsing exception (e.g., log the error, use a default date)
            e.printStackTrace();
        }

        ArrayList<Double> soundDataList = new ArrayList<>();
        ArrayList<Double> vocDataList = new ArrayList<>();
        ArrayList<Double> co2DataList = new ArrayList<>();
        ArrayList<AccessTime> accessTimes = new ArrayList<>();

        // Counter to track the number of data retrieval operations completed
        AtomicInteger dataRetrievalCounter = new AtomicInteger(0);

        // Define a callback to handle completion of all data retrieval operations
        Runnable allDataRetrievedCallback = () -> {
            // Check if all data retrieval operations are completed
            if (dataRetrievalCounter.get() == 7) {
                // Call updateWeeklyData with the accumulated data
                updateWeeklyData(soundDataList, vocDataList, co2DataList, accessTimes);
            }
        };

        for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
            calendar.add(Calendar.DAY_OF_YEAR, dayOfWeek);
            String dailyDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            // Retrieve sound data for the current day
            DataRetrieveWorker.retrieveSoundDataFromServer(dailyDate, new DataRetrieveWorker.DataCallback() {
                @Override
                public void onDataLoaded(ArrayList<Double> dailySoundData, ArrayList<AccessTime> dailyAccessTimes) {
                    // Calculate the daily sound average and add it to the list
                    double dailySoundAverage = calculateAverage(dailySoundData);
                    soundDataList.add(dailySoundAverage);

                    // Increment the data retrieval counter
                    dataRetrievalCounter.incrementAndGet();

                    // Check if all data retrieval operations are completed
                    allDataRetrievedCallback.run();
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle sound data retrieval failure
                    // Increment the data retrieval counter
                    dataRetrievalCounter.incrementAndGet();

                    // Check if all data retrieval operations are completed
                    allDataRetrievedCallback.run();
                }
            });

            // Retrieve VOC data for the current day (similar logic as sound data retrieval)

            // Retrieve CO2 data for the current day (similar logic as sound data retrieval)
        }
    }

    private double calculateAverage(ArrayList<Double> data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.size();
    }

    private void updateWeeklyData(ArrayList<Double> soundDataList, ArrayList<Double> vocDataList, ArrayList<Double> co2DataList, ArrayList<AccessTime> accessTimes) {
        // Update the sound level graph
        updateSoundLevelGraph(soundDataList, accessTimes);

        // Update the VOC graph
        updateVOCGraph(vocDataList, accessTimes);

        // Update the CO2 graph
        updateCO2Graph(co2DataList, accessTimes);
    }







    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}