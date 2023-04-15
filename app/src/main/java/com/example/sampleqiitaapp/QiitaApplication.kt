package com.example.sampleqiitaapp

import android.app.Application
import androidx.room.Room

class QiitaApplication : Application() {
    companion object {
        lateinit var database: QiitaAppRoomDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            QiitaAppRoomDatabase::class.java,
            "qiita_database"
        ).build()
    }
}