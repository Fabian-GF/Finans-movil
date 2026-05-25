package com.example.finans_movil.Data.Local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val accountId: Int,
    val description: String,
    val amount: Double,
    val type: String,
    val date: String = ""
)
