package com.magicbullet.bluetoothapp.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.magicbullet.bluetoothapp.repo.Repository
import com.magicbullet.bluetoothapp.storage.AccDao
import com.magicbullet.bluetoothapp.storage.AccDatabase
import com.magicbullet.bt.BTUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, AccDatabase::class.java, "accessory_db").build().getDao()


    @Singleton
    @Provides
    fun provideBTUtil(@ApplicationContext context: Context, repository: Repository) = BTUtil(context, repository)

    @Singleton
    @Provides
    fun provideRepository(accDao: AccDao) = Repository(accDao)

    @Singleton
    @Provides
    fun provideGson() = Gson()
}