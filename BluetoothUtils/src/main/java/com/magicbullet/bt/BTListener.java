package com.magicbullet.bt;

import android.bluetooth.BluetoothDevice;

public interface BTListener {

    void onDeviceConnected(boolean isConnected, String statusMessage, BluetoothDevice bluetoothDevice);

    void onMessageReceive(String message);

    void commandStatus(boolean completed, String status);

    void onDeviceDisconnected(boolean isDisconnected, String statusMessage);

    void onDeviceError(String errorMessage);

    void setHandler(CommandHandler commandHandler);
}
