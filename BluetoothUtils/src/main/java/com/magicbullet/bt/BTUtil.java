package com.magicbullet.bt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BTUtil {
    public static final int BT_ENABLE_REQUEST_CODE = 100;
    public static final String DEVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    @SuppressLint("StaticFieldLeak")
    private static BTUtil btUtilInstance;
    private Activity activity;
    private Context context;
    private final List<BluetoothDevice> paredDevices = new ArrayList<>();
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BluetoothDevice bluetoothDevice = null;
    private String defaultDeviceMac;

    private boolean isConnected = false;
    private int readBufferPosition;
    private volatile boolean stopWorker;

    private final BTListener btListener;

    /*public static synchronized BTUtil getInstance(Activity activity, PrintStatusListener printStatusListener) {
        if (btUtilInstance == null) {
            btUtilInstance = new BTUtil(activity, printStatusListener);
        }
        return btUtilInstance;
    }*/

    public BTUtil(Context context, BTListener btListener) {
        this.context = context;
        this.btListener = btListener;
        this.btListener.setHandler(this::sendCommand);
        defaultDeviceMac = BTPrefManager.getInstance(context).getDeviceMacAddress();
        findBTDevices();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void findBTDevices() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                btListener.onDeviceError("Bluetooth adapter not found!");
            }
            assert bluetoothAdapter != null;
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(intent, BT_ENABLE_REQUEST_CODE);
            }
            Set<BluetoothDevice> availParedDevices = bluetoothAdapter.getBondedDevices();
            if (!availParedDevices.isEmpty()) {
                for (BluetoothDevice device : availParedDevices) {
                    ParcelUuid[] uuids = device.getUuids();
                    /*for (ParcelUuid uuid : uuids) {
                        if (uuid.getUuid().toString().equals(PRINTER_UUID)) {
                            paredDevices.add(device);
                            break;
                        }
                    }*/
                    paredDevices.add(device);
                    if (defaultDeviceMac.equals(device.getAddress())) {
                        bluetoothDevice = device;
                    }
                }
            } else {
                btListener.onDeviceError("No bluetooth device available!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseBTDeviceDialog() {
        BTDeviceListDialog btDeviceListDialog = new BTDeviceListDialog(activity, paredDevices, device -> {
            bluetoothDevice = device;
            defaultDeviceMac = device.getAddress();
            connectBTDevice();
        });
        btDeviceListDialog.show();
    }

    public void connectBTDevice() {
        if (defaultDeviceMac.isEmpty()) {
            btListener.onDeviceConnected(false, "No device is connected!", bluetoothDevice);
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.showProgress("Please wait", bluetoothDevice != null ? "Connecting to " + bluetoothDevice.getName() : "Bluetooth device error");
        progressDialog.show();
        new Thread(() -> {
            try {
                if (bluetoothDevice != null) {
                    //Old code
                    UUID uuid = UUID.fromString(DEVICE_UUID);
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    inputStream = bluetoothSocket.getInputStream();
                    /*
                     * New code
                     * */
                    /*connection = new BluetoothConnectionInsecure(bluetoothDevice.getAddress());
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    connection.open();*/
                    isConnected = true;
                    beginListenForData();
                } else {
                    isConnected = false;
                }
                progressDialog.dismissProgress();
            } catch (Exception e) {
                e.printStackTrace();
                isConnected = false;
                progressDialog.dismissProgress();
            } finally {
                if (isConnected) {
                    btListener.onDeviceConnected(true, "Device connected successfully", bluetoothDevice);
                    BTPrefManager.getInstance(context).saveDeviceMacAddress(bluetoothDevice.getAddress());
                } else {
                    btListener.onDeviceConnected(false, "Failed to connect to device!", bluetoothDevice);
                }
            }
        }).start();
    }

    private void sendCommand(String command) {
        if (bluetoothDevice != null && isConnected) {
            Thread commandThread = new Thread(() -> {
                try {
                    //Old codes
                    //outputStream.write(String.valueOf(command).getBytes());
                    outputStream.write(command.getBytes());
                    //connection.write(text.getBytes());
                    btListener.commandStatus(true, "Command sent successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                    btListener.commandStatus(false, "There was a device error - unable to send the command!");
                }
            });
            commandThread.setPriority(Thread.MAX_PRIORITY);
            commandThread.start();
        } else {
            btListener.commandStatus(false, "There was a device error - unable to send the command!");
        }
    }

    private void beginListenForData() {
        // final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        byte[] readBuffer = new byte[1024];
        Thread receiveThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                try {
                    int bytesAvailable = inputStream.available();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        inputStream.read(packetBytes);
                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetBytes[i];
                            if (b == delimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;
                                System.out.println("Client message " + data);
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (IOException ex) {
                    stopWorker = true;
                }
            }
        });
        receiveThread.setPriority(Thread.MAX_PRIORITY);
        receiveThread.start();
    }

    public void disconnectBTDevice() {
        if (bluetoothDevice != null && isConnected) {
            new Thread(() -> {
                try {
                    //Old code
                    outputStream.close();
                    inputStream.close();
                    bluetoothSocket.close();
                    //New code
                    /*connection.close();
                    Looper.myLooper().quit();*/
                    isConnected = false;
                    Log.d("Device connection", "Device disconnected successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (isConnected) {
                        btListener.onDeviceDisconnected(false, "Failed to disconnect!!");
                        Log.d("Device connection", "Failed to disconnect!!");
                    } else {
                        btListener.onDeviceDisconnected(true, "Device disconnected successfully");
                    }
                }
            }).start();
        } else {
            btListener.onDeviceDisconnected(false, "Device not found");
        }
    }
}
