package com.magicbullet.bluetoothapp.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.magicbullet.bluetoothapp.R
import com.magicbullet.bluetoothapp.model.Accessory
import com.magicbullet.bluetoothapp.databinding.ActivityMainBinding
import com.magicbullet.bluetoothapp.databinding.LayoutAddAccessoryBinding
import com.magicbullet.bluetoothapp.databinding.LayoutItemMenuBinding
import com.magicbullet.bluetoothapp.databinding.LayoutMessageBinding
import com.magicbullet.bluetoothapp.utils.CircularProgress
import com.magicbullet.bt.BTUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.system.exitProcess

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var btUtil: BTUtil
    private lateinit var popupWindow: PopupWindow
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var accListAdapter: AccListAdapter
    private var accList = ArrayList<Accessory>()
    private lateinit var progress: CircularProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout and set the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize the circular progress dialog
        progress = CircularProgress(this)
        // Set the activity for Bluetooth utility
        btUtil.setActivity(this)
        // Setup activity components
        setupActivity()
    }

    // Function to setup the main activity components
    private fun setupActivity() {
        setupPopup()  // Setup the popup menu
        setupItemListAdapter()  // Setup the item list adapter
        btUtil.connectBTDevice()  // Connect to Bluetooth device

        // Setup menu button click listener to show the popup menu
        binding.menu.setOnClickListener { popupWindow.showAsDropDown(it) }

        // Collect loading status and show/hide progress dialog
        lifecycleScope.launchWhenCreated {
            viewModel.isLoading.collect { if (it) progress.showDialog() else progress.dismiss() }
        }

        // Collect device status and update the UI
        lifecycleScope.launchWhenCreated {
            viewModel.deviceStatus.collect {
                binding.deviceStatus.text = it
            }
        }

        // Collect toast messages and show them
        lifecycleScope.launchWhenCreated {
            viewModel.showToast.collect {
                if (it.isNotEmpty())
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Collect accessory list and update the adapter
        lifecycleScope.launchWhenCreated {
            viewModel.accList.collectLatest {
                accList.clear()
                accList.addAll(it)
                accListAdapter.notifyDataSetChanged()
            }
        }
    }

    // Function to setup the item list adapter
    private fun setupItemListAdapter() {
        accListAdapter = AccListAdapter(
            accList,
            deleteAccessory = {
                showDialog(
                    getString(R.string.delete),
                    getString(R.string.are_you_sure_you_want_to_delete_this_accessory)
                ) {
                    viewModel.deleteAccessory(it)
                }
            },
            toggleAccessory = { viewModel.sendCommand(it) }
        )
        // Set the adapter and layout manager for the item list
        binding.itemList.adapter = accListAdapter
        binding.itemList.layoutManager = LinearLayoutManager(this)
        // Fetch all accessories from the view model
        viewModel.getAllAccessory()
    }

    // Function to setup the popup menu
    private fun setupPopup() {
        val view = layoutInflater.inflate(
            R.layout.layout_item_menu,
            this.window.decorView.findViewById(android.R.id.content),
            false
        )
        val menu = LayoutItemMenuBinding.bind(view)

        // Setup menu item click listeners
        menu.addAccessory.setOnClickListener {
            popupWindow.dismiss()
            addAccessory()
        }
        menu.searchBluetooth.setOnClickListener {
            popupWindow.dismiss()
            searchBluetooth()
        }
        menu.exit.setOnClickListener {
            popupWindow.dismiss()
            showDialog(
                title = getString(R.string.exit),
                message = getString(R.string.are_you_sure_you_want_to_exit)
            ) {
                finishAffinity()
                exitProcess(0)
            }
        }

        // Initialize the popup window with the menu view
        popupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    // Function to start the Bluetooth device search dialog
    private fun searchBluetooth() {
        btUtil.chooseBTDeviceDialog()
    }

    // Function to show a custom dialog
    fun showDialog(
        title: String,
        message: String,
        positiveButton: String = "Yes",
        negativeButton: String = "No",
        func: () -> Unit
    ) {
        runOnUiThread {
            val dialogBuilder = MaterialAlertDialogBuilder(this)
            val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_message, null)
            val dialogBinding = LayoutMessageBinding.bind(dialogView)
            dialogBuilder.setView(dialogBinding.root)
            dialogBuilder.background = ColorDrawable(Color.TRANSPARENT)
            dialogBuilder.setCancelable(false)
            val dialog = dialogBuilder.create()
            dialogBinding.title.text = title
            dialogBinding.message.text = message
            dialogBinding.buttonYes.text = positiveButton
            dialogBinding.buttonNo.text = negativeButton
            dialogBinding.buttonYes.setOnClickListener {
                dialog.dismiss()
                func()
            }
            dialogBinding.buttonNo.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    // Function to show a dialog to add a new accessory
    private fun addAccessory() {
        runOnUiThread {
            val dialogBuilder = MaterialAlertDialogBuilder(this)
            val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_add_accessory, null)
            val dialogBinding = LayoutAddAccessoryBinding.bind(dialogView)
            dialogBuilder.setView(dialogBinding.root)
            dialogBuilder.background = ColorDrawable(Color.TRANSPARENT)
            dialogBuilder.setCancelable(false)
            val dialog = dialogBuilder.create()
            dialogBinding.title.text = getString(R.string.add_accessory)
            dialogBinding.add.setOnClickListener {
                try {
                    val gpio = dialogBinding.gpio.editText?.text.toString().toInt()
                    viewModel.addAccessory(
                        dialogBinding.accessoryName.editText?.text.toString(),
                        gpio,
                        false
                    )
                } catch (exception: Exception) {
                    Toast.makeText(this, R.string.invalid_gpio, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
                viewModel.getAllAccessory()
            }
            dialogBinding.cancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    // Function to handle activity destruction
    override fun onDestroy() {
        super.onDestroy()
        btUtil.disconnectBTDevice()
    }
}
