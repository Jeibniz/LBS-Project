package com.jeibniz.lbsapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditCardDao {
    @Query("SELECT * FROM credit_card_table WHERE id = :id")
    fun getById(id: Int = 1): Flow<List<CreditCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CreditCardEntity)

    @Query("DELETE FROM credit_card_table")
    suspend fun deleteAll()
}