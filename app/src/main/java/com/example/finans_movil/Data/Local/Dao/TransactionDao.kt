package com.example.finans_movil.Data.Local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.finans_movil.Data.Local.Entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY id DESC")
    suspend fun getTransactionByAccount(accountId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY id DESC LIMIT 1")
    suspend fun getLastTransactionByAccount(accountId: Int): TransactionEntity?

    @Query("SELECT SUM(amount) FROM transactions WHERE LOWER(type) = 'ingreso' AND date LIKE :transactionDate || '%'")
    fun getTotalIngresosByMonth(transactionDate: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE LOWER(type) = 'egreso' AND date LIKE :transactionDate || '%'")
    fun getTotalEgresosByMonth(transactionDate: String): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE date LIKE :transactionDate || '%' ORDER BY id DESC")
    fun getTransactionsByMonth(transactionDate: String): Flow<List<TransactionEntity>>

    @Query("SELECT DISTINCT SUBSTR(date, 1, 7) FROM transactions WHERE date != '' ORDER BY date DESC")
    fun getAvailableMonths(): Flow<List<String>>
}