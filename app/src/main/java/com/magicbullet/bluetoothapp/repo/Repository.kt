package com.magicbullet.bluetoothapp.repo

import android.bluetooth.BluetoothDevice
import android.util.Log
import com.magicbullet.bluetoothapp.model.Accessory
import com.magicbullet.bluetoothapp.storage.AccDao
import com.magicbullet.bluetoothapp.utils.Resource
import com.magicbullet.bt.BTListener
import com.magicbullet.bt.CommandHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class Repository @Inject constructor(private val accDao: AccDao) : BTListener {

    private var commandHandler: CommandHandler? = null

    private val btData = MutableStateFlow<Resource<Accessory>>(Resource.Initial())

    fun getBTData(): Flow<Resource<Accessory>> = btData

    suspend fun addAccessory(accessory: Accessory, result: (status: Boolean, message: String) -> Unit) = accDao.addNewAccessory(accessory, result)
    suspend fun updateAccessory(gpio: Int, status: Boolean) = accDao.updateAccessory(gpio, status)
    suspend fun deleteAccessory(accessory: Accessory) = accDao.deleteAccessory(accessory)
    suspend fun getAllAccessory() = accDao.getAllAccessory()

    fun sendCommand(command: String)  {
        btData.value = Resource.Loading()
        commandHandler?.sendCommand(command)
    }

    override fun onDeviceConnected(
        isConnected: Boolean,
        statusMessage: String?,
        bluetoothDevice: BluetoothDevice?
    ) {
        Log.d("TAG", "onDeviceConnected: $statusMessage")
        btData.value = Resource.Connected(statusMessage)
    }

    override fun onMessageReceive(message: String?) {
        Log.d("TAG", "onMessageReceive: $message")
        val accessory = Accessory("", 2, false)
        btData.value = Resource.Received(accessory, message)
    }

    override fun commandStatus(completed: Boolean, status: String?) {
        Log.d("TAG", "onDeviceStateChange: $status")
        if (completed) btData.value = Resource.CommandSent(status)
        else btData.value = Resource.Error(status)
    }

    override fun onDeviceDisconnected(isDisconnected: Boolean, statusMessage: String?) {
        Log.d("TAG", "onDeviceDisconnected: $statusMessage")
        if (isDisconnected) btData.value = Resource.Disconnected(statusMessage)
        else btData.value = Resource.Error(statusMessage)
    }

    override fun onDeviceError(errorMessage: String?) {
        Log.d("TAG", "onDeviceError: $errorMessage")
        btData.value = Resource.Error(errorMessage)
    }

    override fun setHandler(commandHandler: CommandHandler?) {
        this.commandHandler = commandHandler
    }
}