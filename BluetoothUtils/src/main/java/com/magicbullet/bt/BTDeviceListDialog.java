package com.magicbullet.bt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Objects;

public class BTDeviceListDialog extends AppCompatDialog implements View.OnClickListener {
    private final Activity context;
    List<BluetoothDevice> bluetoothDevices;
    private final BTSelectDeviceListener btSelectDeviceListener;

    public BTDeviceListDialog(Activity context, List<BluetoothDevice> bluetoothDevices, BTSelectDeviceListener btSelectDeviceListener) {
        super(context);
        this.context = context;
        this.bluetoothDevices = bluetoothDevices;
        this.btSelectDeviceListener = btSelectDeviceListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_device_list_dialog);
        //setTitle("Choose device");
        //context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RecyclerView recyclerViewDeviceList = findViewById(R.id.recyclerViewDeviceList);
        MaterialButton save = findViewById(R.id.buttonCancel);
        Objects.requireNonNull(save).setOnClickListener(this);

        assert recyclerViewDeviceList != null;
        recyclerViewDeviceList.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewDeviceList.addItemDecoration(new RecyclerViewDecoration((int) context.getResources().getDimension(R.dimen.spacing_small)));
        recyclerViewDeviceList.setAdapter(new BTDeviceListAdapter(this, bluetoothDevices, btSelectDeviceListener));

    }

    @Override
    public void onClick(View v) {
        /*if (v.getId() == R.id.buttonSave) {
            //BTPrefManager.getInstance(context).savePrintTemplatePath(PRINT_TEMPLATE_FILE_PATH);
        }*/
        dismiss();
    }
}
