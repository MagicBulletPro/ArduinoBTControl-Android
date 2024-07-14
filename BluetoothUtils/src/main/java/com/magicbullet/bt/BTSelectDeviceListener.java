package com.magicbullet.bt;

import android.bluetooth.BluetoothDevice;

public interface BTSelectDeviceListener {
    void onBTDeviceSelected(BluetoothDevice device);
}
