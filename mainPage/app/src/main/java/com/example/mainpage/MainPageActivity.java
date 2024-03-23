package com.example.mainpage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
//import androidx.work.PeriodicWorkRequest;
//import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mainpage.API.DataCallback;
import com.example.mainpage.API.Model.SoundDataSendRequest;
import com.example.mainpage.API.Model.SoundRetrieveData;
import com.example.mainpage.API.RetrofitClient;
import com.example.mainpage.API.SendDataCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity<T> extends AppCompatActivity {
    protected ImageView Stats;
    protected ImageView Settings;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private Runnable readCharacteristicRunnable;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;
    private static final int PERMISSION_BLUETOOTH_SCAN = 3;
    private static final int PERMISSION_BLUETOOTH_CONNECT = 2;
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
    private ArrayList<Integer> Co2data = new ArrayList<Integer>();
    static ArrayList<Integer> Sounddata = new ArrayList<Integer>();
    private ArrayList<Integer> VOCdata = new ArrayList<Integer>();
    private Toast currentToast;
    Handler waitbeforescanning = new Handler();
    private Queue<BluetoothGattCharacteristic> readQueue = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Stats = findViewById(R.id.imageView3);
        Settings = findViewById(R.id.imageButton);

        Button SoundDataCollect = findViewById(R.id.SoundButton);
        Button Logout = findViewById(R.id.LogoutButton);

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogout();
            }
        });
        SoundDataCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Toast.makeText(MainPageActivity.this, "Current dB is :" + averageStr, Toast.LENGTH_LONG).show();
            }
        });
//        receiveDatafromServer("2024-03-21", new DataCallback() {
//            @Override
//            public void onDataLoaded(List<Double> data) {
//                Log.d(Tag,String.valueOf(data.get(14300)));
////               showToast(String.valueOf(data.get(14300)));
//            }
//        });
//        try {
//            Timer timer = new Timer();
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    ArrayList<Double> DummyData=new ArrayList<>();
//                    for(int i=0;i<180;i++){
////                double value=(double) (100+i);
//                        DummyData.add(144.23);
//
//                    }
//                    sendDatatoServer(DummyData);
//                }
//            };
//            long delay = 5000; // Delay before first execution (5 seconds)
//            long interval = 60000; // Interval for repeating (1 minute)
//
//            timer.scheduleAtFixedRate(task, delay, interval);
//
//        }
//
//        catch (Exception e)
//        {
//            Log.e(Tag,e.getMessage());
//        }
//        private void schedulePeriodicWorkWithInitialDelay() {
//            // Assume data preparation takes up to 5 minutes
            long initialDelay = 15; // minutes
//
            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                    DataSendWorker.class, 15, TimeUnit.MINUTES)
                    .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                    .build();

            WorkManager.getInstance(this).enqueue(periodicWorkRequest);
//        }
//        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
//                DataSendWorker.class, 15, TimeUnit.MINUTES) // Adjust time interval as needed
//                .build();

//        Handler handlerfirstTime=new Handler();
//        Runnable task = new Runnable() {
//            @Override
//            public void run() {
//                // Code to execute after the delay
//                schedulePeriodicWork();
//            }
//        };
//        handlerfirstTime.postDelayed(task, 900000);
//        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        long interval = 60000; // 10 minutes in milliseconds
//        long startTime = System.currentTimeMillis() + interval;
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
        Button CO2DataCollect = findViewById(R.id.AirQualitybutton);
        CO2DataCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                String averageVStr = String.format("%.2f", averageV);
                Toast.makeText(MainPageActivity.this, "CO2: " + averageStr + " VOC: " + averageVStr, Toast.LENGTH_LONG).show();
            }

        });
        Stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoStatsPage();
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingsPage();
            }
        });
        checkPermissions();
        enablebluetooth();
        try {
            initializeBluetooth();
        } catch (Exception e) {
            Log.e(Tag, e.getMessage());
        }

//        sendDatatoServer(( new ArrayList<Integer>(Arrays.asList(5))), new SendDataCallback() {
//                    @Override
//                    public void onDataSent() {
//
//                        receiveDatafromServer("2024-03-09", new DataCallback() {
//                            @Override
//                            public void onDataLoaded(List<Double> data) {
//                                String DataLine="";
//                                for (int i = 0; i < data.size(); i++){
//                                    Log.d("MainPageActivity",String.valueOf(data.get(i)));
//
//                                    DataLine+=String.valueOf(data.get(i))+", ";
//                                }
//                                Toast.makeText(MainPageActivity.this, DataLine, Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                });
//        sendDatatoServer(( new ArrayList<Double>(Arrays.asList(2.5,1.5))));

    }

    private void initializeBluetooth() {
        Log.d(Tag, "INITIALIZE");
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        } else {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            Log.d(Tag, "TO GO SCANNING");
            checkScanningpermission();
        }

    }

    private void checkScanningpermission() {
        Log.d(Tag, "Before Permission");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            Log.d(Tag, "Permission Granted");
            startScanning();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
            Log.d(Tag, "Permission Rationale");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Need permission")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainPageActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
            Log.d(Tag, "Demanding Permission");
        }

        Log.d(Tag, "After Permission");

    }
//    private void schedulePeriodicWork() {
//        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
//                DataSendWorker.class, 15, TimeUnit.MINUTES)
//                .build();
//
//        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
//    }


    private void startScanning() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        }
        bluetoothLeScanner.startScan(new ScanCallback() {

            @Override

            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                String targetDeviceName = "ESP32";

                Log.d(Tag, "Scanning");
//                showToast("Scanning for Device");


                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                }
                if (device.getName() != null && device.getName().equals(targetDeviceName)) {
                    Log.d(Tag, "FoundDevice: " + targetDeviceName);

                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        checkScanningpermission();
                    }
                    bluetoothLeScanner.stopScan(this); // Stop scanning as the target device is found
                    connectToDevice(device); // Proceed to connect to the device
                }
            }
        });
    }

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
                    //store the value into to the proper array
                    if (characteristic.getUuid().toString().equals(CharacteristicOneUUID)) {
                        Co2data.add(intValue);
                    } else if (characteristic.getUuid().toString().equals(CharacteristicTwoUUID)) {
                        Sounddata.add(intValue);
                    } else if (characteristic.getUuid().equals(UUID.fromString(CharacteristicThreeUUID))) {
                        VOCdata.add(intValue);
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


    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_FINE_LOCATION);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    PERMISSION_BLUETOOTH_CONNECT);
        }

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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_BLUETOOTH_SCAN) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Tag, "Permission BLUETOOTH_SCAN Granted");
                checkPermissions();

            } else {
                Log.d(Tag, "Permission BLUETOOTH_SCAN DENIED");
                showRationaleDialog("Bluetooth scan permission is necessary for scanning Bluetooth devices. Please grant the permission to continue.", PERMISSION_BLUETOOTH_SCAN);
            }


        } else if (requestCode == PERMISSION_BLUETOOTH_CONNECT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Tag, "Permission BLUETOOTH_CONNECT Granted");
                checkScanningpermission();


            } else {
                Log.d(Tag, "PermissionBLUETOOTH_CONNECT DENIED ");
                showRationaleDialog("Bluetooth connect permission is necessary for connecting to Bluetooth devices. Please grant the permission to continue.", PERMISSION_BLUETOOTH_CONNECT);
            }
        }
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

    protected void onDestroy() {
        super.onDestroy();
        Log.d(Tag, "DESTROY");
        if (bluetoothGatt != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                return;
            }
            handler.removeCallbacks(readCharacteristicRunnable);
            if (bluetoothGatt != null) {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                bluetoothGatt = null;
            }

        }
    }

    public void receiveDatafromServer(String date, DataCallback callback){
        Call<ResponseBody>call =RetrofitClient
                .getInstance()
                .getApi()
                .retrieveSoundData(new SoundRetrieveData(date));
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


    public <T> void sendDatatoServer(List<T>data){
        Call<ResponseBody>call =RetrofitClient
                .getInstance()
                .getApi()
                .AddSoundData(new SoundDataSendRequest(data));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String body=response.body().string();
                    JSONObject jsonObj = new JSONObject(body);
                    String message=jsonObj.getString("message");
                    Toast.makeText(MainPageActivity.this,message,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainPageActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public <T> void sendDatatoServer(List<T>data,SendDataCallback callback ){
        Call<ResponseBody>call =RetrofitClient
                .getInstance()
                .getApi()
                .AddSoundData(new SoundDataSendRequest(data));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String body=response.body().string();
                    JSONObject jsonObj = new JSONObject(body);
                    String message=jsonObj.getString("message");
                    Toast.makeText(MainPageActivity.this,message,Toast.LENGTH_LONG).show();
                    callback.onDataSent();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainPageActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void  verifyAuthentication() {

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .isAuthorized();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean isAuthenticated;
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body().string();
                        JSONObject jsonObj = new JSONObject(body);
                        isAuthenticated= jsonObj.getBoolean("Authentication");
                        if(!isAuthenticated)
                            gotoMainActivity();
                        Toast.makeText(MainPageActivity.this,String.valueOf(isAuthenticated),Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainPageActivity.this,"NullResponse",Toast.LENGTH_LONG).show();
                    }


                } catch (IOException e) {
                    Toast.makeText(MainPageActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainPageActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


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

    public void gotoLogout(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(MainPageActivity.this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}