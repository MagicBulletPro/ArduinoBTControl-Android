package com.magicbullet.bt;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BTDeviceListAdapter extends RecyclerView.Adapter<BTDeviceListAdapter.DeviceListViewHolder> {

    private final BTDeviceListDialog context;
    private final List<BluetoothDevice> bluetoothDeviceList;
    private final BTSelectDeviceListener btSelectDeviceListener;
    private final String macAddress;

    public BTDeviceListAdapter(BTDeviceListDialog context, List<BluetoothDevice> bluetoothDeviceList, BTSelectDeviceListener btSelectDeviceListener) {
        this.context = context;
        this.bluetoothDeviceList = bluetoothDeviceList;
        this.btSelectDeviceListener = btSelectDeviceListener;
        macAddress = BTPrefManager.getInstance(this.context.getContext()).getDeviceMacAddress();
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public BTDeviceListAdapter.DeviceListViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_device, parent, false);
        return new DeviceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull BTDeviceListAdapter.DeviceListViewHolder holder, int position) {
        if (bluetoothDeviceList.size() == 0) {
            holder.textViewNoPrinter.setVisibility(View.VISIBLE);
            holder.cardViewItem.setVisibility(View.GONE);
        } else {
            BluetoothDevice device = bluetoothDeviceList.get(position);
            holder.textViewPrinterName.setText(device.getName());
            holder.textViewMac.setText(device.getAddress());
            if (macAddress.equals(device.getAddress())) {
                holder.cardViewItem.setCardBackgroundColor(context.getContext().getResources().getColor(R.color.colorAccent));
            }
            holder.layoutItem.setOnClickListener(v -> {
                holder.cardViewItem.setCardBackgroundColor(context.getContext().getResources().getColor(R.color.colorAccent));
                btSelectDeviceListener.onBTDeviceSelected(device);
                context.dismiss();
            });
        }

    }

    @Override
    public int getItemCount() {
        return bluetoothDeviceList.size() != 0 ? bluetoothDeviceList.size() : 1;
    }

    protected static class DeviceListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPrinterName, textViewMac, textViewNoPrinter;
        CardView cardViewItem;
        ConstraintLayout layoutItem;

        public DeviceListViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            textViewPrinterName = itemView.findViewById(R.id.textViewDeviceName);
            textViewMac = itemView.findViewById(R.id.textViewMac);
            cardViewItem = itemView.findViewById(R.id.cardViewItem);
            layoutItem = itemView.findViewById(R.id.layoutItem);
            textViewNoPrinter = itemView.findViewById(R.id.textViewNoPrinter);
        }
    }
}
