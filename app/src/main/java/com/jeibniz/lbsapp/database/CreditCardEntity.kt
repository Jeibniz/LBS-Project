package com.jeibniz.lbsapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_card_table")
data class CreditCardEntity(
    @PrimaryKey
    val id: Int = 1,
    val number: ByteArray,
    val date: ByteArray,
    val cvv: ByteArray
)
