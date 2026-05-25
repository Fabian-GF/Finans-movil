package com.example.finans_movil.Data.Local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.finans_movil.Data.Local.Entities.TransactionEntity

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY id DESC")
    suspend fun getTransactionByAccount(accountId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY id DESC LIMIT 1")
    suspend fun getLastTransactionByAccount(accountId: Int): TransactionEntity?
}