package com.magicbullet.bluetoothapp.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.magicbullet.bluetoothapp.model.Accessory

@Dao
interface AccDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAccessory(accessory: Accessory): Long

    @Query("UPDATE Accessory SET status = :status WHERE gpio = :gpio")
    suspend fun updateAccessory(gpio: Int, status: Boolean)

    @Delete
    suspend fun deleteAccessory(accessory: Accessory)

    @Query("SELECT * FROM Accessory")
    suspend fun getAllAccessory(): List<Accessory>


    @Transaction
    suspend fun addNewAccessory(
        accessory: Accessory, result: (status: Boolean, message: String) -> Unit
    ) {
        val accIDs = checkGPIOTaken(accessory.gpio)
        if (accIDs.isNotEmpty()) result(false, "GPIO ${accessory.gpio} is already taken")
        else {
            if (addAccessory(accessory) == -1L) result(false, "Failed to add accessory")
            else result(true, "Accessory added successfully")
        }
    }

    @Query("SELECT acc_id FROM Accessory WHERE gpio == :gpio")
    suspend fun checkGPIOTaken(gpio: Int): List<Int>
}