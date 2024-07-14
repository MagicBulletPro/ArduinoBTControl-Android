package com.magicbullet.bluetoothapp.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.magicbullet.bluetoothapp.model.Accessory
import com.magicbullet.bluetoothapp.R
import com.magicbullet.bluetoothapp.databinding.CardViewAccBinding

class AccListAdapter(
    private val accessoryList: List<Accessory>,
    private val deleteAccessory: (accessory: Accessory) -> Unit,
    private val toggleAccessory: (accessory: Accessory) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = CardViewAccBinding.bind(itemView)
    }

    inner class NoItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEMS -> {
                ItemListViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.card_view_acc,
                        parent,
                        false
                    )
                )
            }

            NO_ITEM -> {
                NoItemHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.card_view_no_item,
                        parent,
                        false
                    )
                )
            }

            else -> {
                ItemListViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.card_view_acc,
                        parent,
                        false
                    )
                )
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemListViewHolder) {
            holder.binding.accessoryName.text = accessoryList[position].name
            holder.binding.gpio.text = "GPIO ${accessoryList[position].gpio}"
            holder.binding.gpioStatus.text = if (accessoryList[position].status) "ON" else "OFF"
            holder.binding.layout.setOnClickListener {
                toggleAccessory(accessoryList[position])
            }
            holder.binding.delete.setOnClickListener {
                deleteAccessory(accessoryList[position])
            }
            holder.binding.gpioStatus.setBackgroundResource(
                if (accessoryList[position].status) {
                    R.drawable.bg_round_green
                } else {
                    R.drawable.bg_round_gray
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return if (accessoryList.isNotEmpty()) {
            return accessoryList.size
        } else {
            NO_ITEM
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (accessoryList.isNotEmpty()) {
            ITEMS
        } else {
            NO_ITEM
        }
    }

    companion object {
        const val ITEMS = 0
        const val NO_ITEM = 1
    }
}