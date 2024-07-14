package com.magicbullet.bluetoothapp.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.magicbullet.bluetoothapp.model.Accessory

@Database(entities = [Accessory::class], version = 1)
abstract class AccDatabase : RoomDatabase() {
    abstract fun getDao(): AccDao
}