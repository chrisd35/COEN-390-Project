package com.example.mainpage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
//import androidx.work.PeriodicWorkRequest;
//import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mainpage.API.DataCallback;
import com.example.mainpage.API.Model.AccessTime;
import com.example.mainpage.API.Model.DataSendRequest;
import com.example.mainpage.API.Model.RetrieveData;
import com.example.mainpage.API.RetrofitClient;
import com.example.mainpage.API.SendDataCallback;
import com.example.mainpage.API.ThresholdData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity<T> extends AppCompatActivity {
    public static String averageCO2;
    public static String averageVOC;
    private AlertDialog currentDialog;
    public static String averageSound;
    public static LocalTime now;
    public static int hour;
    public static int minutes;
    private long lastNotificationTime = 0;
  ;
    private static final long NOTIFICATION_DELAY = 900000;
    private long lastNotificationTimeCO2 = 0;
    ;
    private static final long NOTIFICATION_DELAYCO2 = 300000;

    public static boolean issoundclicked = false;
    public static boolean isairclicked = false;
    protected ImageView Stats;
    protected ImageView Setting;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private Runnable readCharacteristicRunnable;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;
    private static final int PERMISSION_BLUETOOTH_SCAN = 3;
    private static final int PERMISSION_BLUETOOTH_CONNECT = 4;
    private static final int PERMISSION_POST_NOTIFICATIONS = 5;
    private static final int REQUEST_ALL_PERMISSIONS = 6;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private boolean shouldContinueReading = true;
    private String Tag = "HardBLE";
    private Handler handler = new Handler();
    private UUID currentReadingCharacteristicUUID;
    private static final String serviceUUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    private static final String CharacteristicOneUUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";
    private static final String CharacteristicTwoUUID = "beb5483e-36e1-4688-b7f5-ea07361b27a9";
    private static final String CharacteristicThreeUUID = "beb5483e-36e1-4688-b7f5-ea07361b26a9";
    private static final long READ_INTERVAL_MS = 1000;
    static ArrayList<Integer> Co2data = new ArrayList<Integer>();
    static ArrayList<Integer> Sounddata = new ArrayList<Integer>();
    static ArrayList<Integer> VOCdata = new ArrayList<Integer>();
    static ArrayList<AccessTime> Co2dataTime = new ArrayList<>();
    static ArrayList<AccessTime> SounddataTime = new ArrayList<>();
    static ArrayList<AccessTime> VOCdataTIme = new ArrayList<>();
    Button SoundDataCollect;
    Button CO2DataCollect;
    private Toast currentToast;
    Handler waitbeforescanning = new Handler();
    private ThresholdData thresholdData;
    private Queue<BluetoothGattCharacteristic> readQueue = new LinkedList<>();

    private Authentication authentication;
    private boolean isBluetoothInitialized = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        authentication = new Authentication(getBaseContext());
        authentication.verifyAuthentication();
        Stats = findViewById(R.id.imageView3);
        Setting = findViewById(R.id.imageButton);

        SoundDataCollect = findViewById(R.id.SoundButton);
        Button Logout = findViewById(R.id.LogoutButton);
        thresholdData = new ThresholdData(this);
        thresholdData.fetchAndSetThreshold();
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authentication.logout();
            }
        });
        SoundDataCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issoundclicked = true;
                //take the "instanteous" reading of the sound leve, by taking the five last element and averaging
                int size = Sounddata.size();
                // Start from the fifth-to-last element if there are at least five elements
                int start = Math.max(0, size - 5);
                int sum = 0;
                int count = 0;

                for (int i = start; i < size; i++) {
                    sum += Sounddata.get(i);
                    count++;
                }
                double average = 0;
                if (count > 0) {
                    average = (double) sum / count;
                }
                String averageStr = String.format("%.2f", average);
                setAverageSound(averageStr);
//                Toast.makeText(MainPageActivity.this, "Current dB is :" + averageStr, Toast.LENGTH_LONG).show();

                DialogFragment dialog = new DialogFragment();
                dialog.show(getSupportFragmentManager(), "My Fragment");
            }
        });

        schedulePeriodicWorkWithInitialDelay();





        CO2DataCollect = findViewById(R.id.AirQualitybutton);
        CO2DataCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isairclicked = true;
                //take the "instanteous" reading of the CO2 and VOC level, by taking the five last element and averaging

                int size = Co2data.size();
                // Start from the fifth-to-last element if there are at least five elements
                int start = Math.max(0, size - 5);
                int sum = 0;
                int count = 0;

                for (int i = start; i < size; i++) {
                    sum += Co2data.get(i);
                    count++;
                }
                double average = 0;
                if (count > 0) {
                    average = (double) sum / count;
                }
//                for (int pd : Co2data) {
//                    line += pd + ", ";
//                }
                int sizeV = VOCdata.size();
                // Start from the fifth-to-last element if there are at least five elements
                int startV = Math.max(0, size - 5);
                int sumV = 0;
                int countV = 0;

                for (int i = startV; i < sizeV; i++) {
                    sumV += VOCdata.get(i);
                    countV++;
                }
                double averageV = 0;
                if (countV > 0) {
                    averageV = (double) sumV / countV;
                }
                String averageStr = String.format("%.2f", average);
                setAverageC02(averageStr);

                String averageVStr = String.format("%.2f", averageV);
                setAverageVOC(averageVStr);
//                Toast.makeText(MainPageActivity.this, "CO2: " + averageStr + " VOC: " + averageVStr, Toast.LENGTH_LONG).show();
                DialogFragment dialog = new DialogFragment();
                dialog.show(getSupportFragmentManager(), "My Fragment");

            }

        });
        Stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoStatsPage();
            }
        });

        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingsPage();
            }
        });
        ;
        if (checkPermissions()) {
            initializeBluetooth();
        }





    }

    private void initializeBluetooth() {
        if (isBluetoothInitialized) {
            Log.d("MainPageActivity", "Bluetooth is already initialized.");
            return;
        }
        isBluetoothInitialized = true;
        Log.d(Tag, "INITIALIZE");
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        } else {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            Log.d(Tag, "TO GO SCANNING");

            startScanning();

        }

    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_ALL_PERMISSIONS);
    }



    private void startScanning() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        }

        try {
           if(checkPermissions())
            bluetoothLeScanner.startScan(scanCallback);
            else{
               Log.e(Tag, "requiredPermission");
            }
        }catch (Exception e) {
            Log.e(Tag, e.getMessage());
        }



    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String targetDeviceName = "ESP32";
            Log.d(Tag, "Scanning");


            if (device.getName() != null && device.getName().equals(targetDeviceName)) {
                Log.d(Tag, "FoundDevice: " + targetDeviceName);
                bluetoothLeScanner.stopScan(this);
                connectToDevice(device);
            }
        }
    };
    private LinkedList<BluetoothGatt> gattInstances = new LinkedList<>();
    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }

        bluetoothGatt = device.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//let user know when connected
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    runOnUiThread(() -> showToast("Connected to ESP32"));
                     now=LocalTime.now();
                     hour=now.getHour();
                     minutes=now.getMinute();
                    if (ActivityCompat.checkSelfPermission(MainPageActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        checkPermissions();
                        return;
                    }
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    runOnUiThread(() -> showToast("Disconnected From ESP32"));
                    //wait to  scan for 5 second after it has been disconnected
                    waitbeforescanning.postDelayed(() -> startScanning(), 5000);
                }

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // Retrieve the service you're interested in
                    Log.d(Tag, "Service Discovered");

                    BluetoothGattService service = gatt.getService(UUID.fromString(serviceUUID));

                    if (service != null) {
                        // Retrieve the characteristic from the service
                        BluetoothGattCharacteristic characteristic1 = service.getCharacteristic(UUID.fromString(CharacteristicOneUUID));

                        enqueueCharacteristicsForReading(service);
                        if (characteristic1 != null) {
                            //read the first value of the first characteristic
                            gatt.readCharacteristic(characteristic1);
                        }

                    }
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d(Tag, "READ VALUES");
                if (status == BluetoothGatt.GATT_SUCCESS) {

                    // Retrieve the characteristic's value
                    // Convert the value
                    byte[] value = characteristic.getValue();
                    // Example: Converting the byte array to a integer
                    int intValue = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    now=LocalTime.now();
                    AccessTime accessTime=new AccessTime(now.getHour(),now.getMinute(),now.getSecond());

                    //store the value into to the proper array
                    if (characteristic.getUuid().toString().equals(CharacteristicOneUUID)) {

                        Co2data.add(intValue);
                        Co2dataTime.add(accessTime);
                        long currentTime = System.currentTimeMillis();
                        if(intValue>1000 && intValue<1500 && (currentTime - lastNotificationTimeCO2 > NOTIFICATION_DELAYCO2)) {
                            makeNotification("Air Quality Alert", "CO2 is over 1000 ppm", "Our sensors detected a high concentration of CO2 in your environment", DIALOG_AIR, 100);
                        lastNotificationTimeCO2=currentTime;
                        }
                        else if(intValue<1600 && (currentTime - lastNotificationTimeCO2 > NOTIFICATION_DELAYCO2)){
                            makeNotification(" Urgent Air Quality Alert", "CO2 is over 1600 ppm ", "Our sensors detected a concentration of CO2 in your environment above the safety level ",DIALOG_AIR,100);
                            lastNotificationTimeCO2=currentTime;
                        }

                    }
                    else if (characteristic.getUuid().toString().equals(CharacteristicTwoUUID)) {
                        Sounddata.add(intValue);
                        SounddataTime.add(accessTime);
                        long currentTime = System.currentTimeMillis();
                        int currentThreshold = thresholdData.getSavedThreshold();
                        boolean thresholdDataexist = thresholdData.ThresholdExist();
                        Log.d(Tag, "Threshold" + String.valueOf(currentThreshold));
                        Log.d(Tag, "DataExist" + String.valueOf(thresholdDataexist));
                        if (thresholdDataexist && intValue > currentThreshold && (currentTime - lastNotificationTime > NOTIFICATION_DELAY)) {
                            makeNotification("Noise Alert", "Noise is over " + currentThreshold + " dB", "Your current environment exceeds the threshold of " + currentThreshold + "dB you have set.", DIALOG_SOUND, 101);
                            lastNotificationTime = currentTime; // Update the last notification time
                        }
                    } else if (characteristic.getUuid().equals(UUID.fromString(CharacteristicThreeUUID))) {
                        VOCdata.add(intValue);
                        VOCdataTIme.add(accessTime);
                    }


                    Log.d(Tag, String.valueOf(intValue));

                    //implement for later use , to give control to the user when he wants to have access to the data
                    if (shouldContinueReading) {

                        handler.postDelayed(() -> readNextCharacteristic(gatt), READ_INTERVAL_MS);


                    }

                } else {
                    Log.w(Tag, "Characteristic read failed, status: " + status);
                }
            }


        });
        gattInstances.add(bluetoothGatt);



    }

    private boolean checkPermissions() {

        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);  // Assuming you want to request both together
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        // Add more permissions as needed

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), REQUEST_ALL_PERMISSIONS);
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void enablebluetooth() {
        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Bluetooth has been enabled, proceed with your Bluetooth operation
                        checkPermissions();
                    } else {
                        // Handle the case where the user declines to enable Bluetooth
                        Toast.makeText(MainPageActivity.this, "Bluetooth is required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void enqueueCharacteristicsForReading(BluetoothGattService service) {
        BluetoothGattCharacteristic characteristic1 = service.getCharacteristic(UUID.fromString(CharacteristicOneUUID));
        BluetoothGattCharacteristic characteristic2 = service.getCharacteristic(UUID.fromString(CharacteristicTwoUUID));
        BluetoothGattCharacteristic characteristic3 = service.getCharacteristic(UUID.fromString(CharacteristicThreeUUID));

        if (characteristic1 != null) {
            readQueue.offer(characteristic1);
        }
        if (characteristic2 != null) {
            readQueue.offer(characteristic2);
        }
        if (characteristic3 != null) {
            readQueue.offer(characteristic3);
        }

        processNextCharacteristicRead();
    }

    private void processNextCharacteristicRead() {
        if (!readQueue.isEmpty() && bluetoothGatt != null) {
            BluetoothGattCharacteristic characteristic = readQueue.poll();
            if (characteristic != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                    return;
                }
                bluetoothGatt.readCharacteristic(characteristic);
            }
        }
    }

    private void readNextCharacteristic(final BluetoothGatt gatt) {
        try {
            // Check if the currentReadingCharacteristicUUID is not initialized
            if (currentReadingCharacteristicUUID == null) {
                currentReadingCharacteristicUUID = UUID.fromString(CharacteristicOneUUID);
            }

            UUID nextCharacteristicUUID;

            // Determine the next characteristic to read based on the current one
            if (currentReadingCharacteristicUUID.equals(UUID.fromString(CharacteristicOneUUID))) {
                nextCharacteristicUUID = UUID.fromString(CharacteristicTwoUUID);
            } else if (currentReadingCharacteristicUUID.equals(UUID.fromString(CharacteristicTwoUUID))) {
                nextCharacteristicUUID = UUID.fromString(CharacteristicThreeUUID);
            } else {
                nextCharacteristicUUID = UUID.fromString(CharacteristicOneUUID);
            }

            BluetoothGattCharacteristic nextCharacteristic = gatt.getService(UUID.fromString(serviceUUID)).getCharacteristic(nextCharacteristicUUID);
            if (nextCharacteristic != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                }
                gatt.readCharacteristic(nextCharacteristic);
                currentReadingCharacteristicUUID = nextCharacteristicUUID; // Update the current reading characteristic UUID
            }
        } catch (Exception e) {
            Log.e(Tag, "Error in readNextCharacteristic: " + e.getMessage());
        }
    }
    private boolean checkBluetoothPermissions() {
        boolean fineLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean bluetoothConnectGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        boolean bluetoothScanGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;

        return fineLocationGranted && bluetoothConnectGranted && bluetoothScanGranted;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ALL_PERMISSIONS) {
            if (checkBluetoothPermissions()) {
                initializeBluetooth();
                Log.d(Tag, "Required Bluetooth permissions are granted");
            } else {
                // Explain to the user that Bluetooth permissions are necessary
                Log.d(Tag, "Bluetooth permissions not granted");
                showRationaleOrSettings();
            }
        }
    }
    private void showRationaleOrSettings() {
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN);

        if (shouldShowRationale) {
            // Show rationale and request permission again
            showSettingsAlert();; // Method to show rationale dialog
        } else {
            // User denied with "Don't ask again". Direct them to settings.
            showSettingsAlert();
        }
    }
    private void showSettingsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions Required")
                .setMessage("Bluetooth and Location services need to be granted for the app to function. Please go to settings to enable the permissions")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the user to the app's settings page
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        // Consider closing the app or disabling functionality if permissions are essential
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void stopReading() {
        shouldContinueReading = false;
        handler.removeCallbacksAndMessages(null); // Remove all callbacks and messages
    }

    private void showRationaleDialog(String message, int permissionRequestCode) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage(message)
                .setPositiveButton("Grant", (dialog, which) -> {
                    if (permissionRequestCode == PERMISSION_BLUETOOTH_SCAN) {
                        ActivityCompat.requestPermissions(MainPageActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);

                    } else if (permissionRequestCode == PERMISSION_BLUETOOTH_CONNECT) {
                        ActivityCompat.requestPermissions(MainPageActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @SuppressLint("MissingPermission")
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Tag, "DESTROY");
        Co2data.clear();
        Sounddata.clear();
        VOCdata.clear();
        Co2dataTime.clear();
        SounddataTime.clear();
        VOCdataTIme.clear();
        // Stop the Bluetooth scanning explicitly if it's still running
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);

        }
        WorkManager.getInstance(this).cancelAllWork();
        if (bluetoothGatt != null) {
                handler.removeCallbacks(readCharacteristicRunnable);
            while (!gattInstances.isEmpty()) {
                BluetoothGatt gatt = gattInstances.pop();
                gatt.disconnect();
                gatt.close();
            }
                bluetoothGatt = null;
            }

    }
    private void schedulePeriodicWorkWithInitialDelay() {
        long initialDelay = 15; // Delay in minutes before the first execution
        long repeatInterval = 15; // Repeat interval in minutes

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                DataSendWorker.class, repeatInterval, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
    }
    public void receiveDatafromServer(String date, DataCallback callback){
        Call<ResponseBody>call =RetrofitClient
                .getInstance()
                .getApi()
                .retrieveSoundData(new RetrieveData(date));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                {
                    try {
                        if (response != null && response.body() != null) {
                            String body = response.body().string();
                            JSONObject jsonObj = new JSONObject(body);
                            JSONArray dataArray = jsonObj.getJSONArray("data");
                            ArrayList<Double> dataList = new ArrayList<>();
                            String message = jsonObj.getString("message");
                            Toast.makeText(MainPageActivity.this, message, Toast.LENGTH_LONG).show();
                            for (int i = 0; i < dataArray.length(); i++) {
                                double value = dataArray.getDouble(i); // Get the double value at the current position
                                dataList.add(value); // Add the double value to the ArrayList
                            }
                            callback.onDataLoaded(dataList);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    catch (Exception e) {
                        Log.e("MainPageActivity", "Error ", e);
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainPageActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    private static final String EXTRA_DIALOG_TYPE = "DialogType";
    private static final String DIALOG_SOUND = "SoundDialog";
    private static final String DIALOG_AIR = "AirDialog";


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);  // Important to ensure getIntent() returns the latest intent

        String dialogType = intent.getStringExtra(EXTRA_DIALOG_TYPE);
        if (DIALOG_SOUND.equals(dialogType)) {
            SoundDataCollect.performClick();
            Log.d(Tag, "NEW SOUND INTENT");
        }
        else if (DIALOG_AIR.equals(dialogType)) {
            CO2DataCollect.performClick();
            Log.d(Tag, "NEW AIR INTENT");
        }
    }
    public void gotoMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void gotoStatsPage(){
        Intent intent = new Intent(this, StatActivity.class);
        startActivity(intent);
    }

    public void gotoSettingsPage(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }


    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(MainPageActivity.this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }

public void makeNotification(String title, String message, String expandedText, String dialog, int ID) {
    String channelID = "Channel_ID_notifications";
    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID)
            .setSmallIcon(R.drawable.logoleaf)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(expandedText))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    Class<?> activityClass = authentication.isAuthenticated()? MainPageActivity.class : MainActivity.class;
    Intent intent = new Intent(getApplicationContext(), activityClass);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Set flags
    intent.putExtra(EXTRA_DIALOG_TYPE, dialog);

    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), ID, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pendingIntent);

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
        if (notificationChannel == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            notificationChannel = new NotificationChannel(channelID, "Some Description", importance);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    notificationManager.notify(ID, builder.build());
}



    public void setAverageC02(String averageCO2){
        this.averageCO2 = averageCO2;
    }
    @Override
    public void onBackPressed() {
        return;
    }
    public void setAverageVOC(String averageVOC){
        this.averageVOC = averageVOC;
    }
    public void setAverageSound(String averageSound){
        this.averageSound = averageSound;
    }
    public String getAverageCO2(){
        return averageCO2;
    }
    public String getAverageVOC(){
        return averageVOC;
    }
    public String getAverageSound(){
        return averageSound;
    }
}