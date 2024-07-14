package com.magicbullet.bluetoothapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Accessory")
data class Accessory(val name: String, val gpio: Int, var status: Boolean) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "acc_id")
    var id: Int? = null
}
