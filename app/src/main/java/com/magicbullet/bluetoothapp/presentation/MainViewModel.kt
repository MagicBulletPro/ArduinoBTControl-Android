package com.magicbullet.bluetoothapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.magicbullet.bluetoothapp.model.Accessory
import com.magicbullet.bluetoothapp.model.CommandModel
import com.magicbullet.bluetoothapp.repo.Repository
import com.magicbullet.bluetoothapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val gson: Gson
) : ViewModel() {

    // State flow to indicate loading status
    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading

    // State flow to hold a list of accessories
    private val _accList = MutableStateFlow(ArrayList<Accessory>())
    val accList: Flow<List<Accessory>> = _accList

    // State flow to hold the status of the device
    private val _deviceStatus = MutableStateFlow("")
    val deviceStatus: Flow<String> = _deviceStatus

    // State flow to hold toast messages
    private val _showToast = MutableStateFlow("")
    val showToast: Flow<String> = _showToast

    // Initial block to get Bluetooth data
    init {
        getBTData()
    }

    // Function to get Bluetooth data from the repository
    private fun getBTData() = viewModelScope.launch {
        repository.getBTData().collect {
            when (it) {
                is Resource.Initial -> {
                    _isLoading.value = false
                    _deviceStatus.value = ""
                }

                is Resource.Loading -> _isLoading.value = true
                is Resource.Connected -> {
                    _deviceStatus.value = it.message!!
                    _isLoading.value = false
                    syncController()
                }

                is Resource.Received -> {}
                is Resource.Disconnected -> {
                    _isLoading.value = false
                    _deviceStatus.value = it.message!!
                }

                is Resource.CommandSent -> {
                    _isLoading.value = false
                }

                is Resource.Error -> {
                    _deviceStatus.value = it.message!!
                    _isLoading.value = false
                }
            }
        }
    }

    // Function to sync the controller by sending all accessory status
    fun syncController() {
        val command = CommandModel("sync")
        command.accessory_list = _accList.value
        val jsonData = gson.toJson(command)
        repository.sendCommand(jsonData)
    }

    // Function to add a new accessory
    fun addAccessory(name: String, gpio: Int, status: Boolean) = viewModelScope.launch {
        val accessory = Accessory(name, gpio, status)
        repository.addAccessory(accessory) { status, message ->
            if (status) getAllAccessory()
            else _showToast.value = message
        }
    }

    // Function to retrieve all accessories from the repository
    fun getAllAccessory() = viewModelScope.launch {
        _accList.value = repository.getAllAccessory() as ArrayList<Accessory>
    }

    // Function to update an accessory
    fun updateAccessory(accessory: Accessory) = viewModelScope.launch {
        repository.updateAccessory(accessory.gpio, accessory.status)
        getAllAccessory()
    }

    // Function to delete an accessory
    fun deleteAccessory(accessory: Accessory) = viewModelScope.launch {
        repository.deleteAccessory(accessory)
        getAllAccessory()
    }

    // Function to send a command to toggle the status of an accessory
    fun sendCommand(accessory: Accessory) = viewModelScope.launch {
        _isLoading.value = true
        val acc = accessory.copy(status = !accessory.status)
        updateAccessory(acc)
        val command = CommandModel("command")
        command.accessory = acc
        val jsonData = gson.toJson(command)
        repository.sendCommand(jsonData)
    }
}
