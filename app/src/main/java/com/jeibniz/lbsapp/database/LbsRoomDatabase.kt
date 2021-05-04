package com.jeibniz.lbsapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(CreditCardEntity::class), version = 1, exportSchema = false)
abstract class LbsRoomDatabase: RoomDatabase() {

    abstract fun getCreditCardDao(): CreditCardDao

    companion object{
        val DATABASE_NAME: String = "lbs_database"
    }
}