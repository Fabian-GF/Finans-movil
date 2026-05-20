package com.example.finans_movil.Data.Local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.finans_movil.Data.Local.Entities.MonthlyBillEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MonthlyBillDao {

    @Query("SELECT * FROM monthly_bills")
    fun getMonthlyBills(): Flow<List<MonthlyBillEntity>>

    @Insert
    suspend fun insertMonthlyBill(monthlyBill: MonthlyBillEntity)

    @Update
    suspend fun updateMonthlyBill(monthlyBill: MonthlyBillEntity)

}