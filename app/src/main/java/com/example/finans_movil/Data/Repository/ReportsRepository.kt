package com.example.finans_movil.Data.Repository

import com.example.finans_movil.Data.Local.Dao.TransactionDao
import com.example.finans_movil.Data.Mapper.toModel
import com.example.finans_movil.Model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReportsRepository(
    private val transactionDao: TransactionDao
) {
    fun getTotalIngresos(yearMonth: String): Flow<Double> {
        return transactionDao.getTotalIngresosByMonth(yearMonth)
            .map { it ?: 0.0 }
    }

    fun getTotalEgresos(yearMonth: String): Flow<Double> {
        return transactionDao.getTotalEgresosByMonth(yearMonth)
            .map { it ?: 0.0 }
    }

    fun getTransactionsByMonth(yearMonth: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByMonth(yearMonth)
            .map { list -> list.map { it.toModel() } }
    }

    fun getAvailableMonths(): Flow<List<String>> {
        return transactionDao.getAvailableMonths()
    }
}