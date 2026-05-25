package com.example.finans_movil.Data.Repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.withTransaction
import com.example.finans_movil.Data.Local.AppDatabase
import com.example.finans_movil.Data.Local.Dao.AccountDao
import com.example.finans_movil.Data.Local.Dao.MonthlyBillDao
import com.example.finans_movil.Data.Local.Dao.TransactionDao
import com.example.finans_movil.Data.Local.Entities.TransactionEntity
import com.example.finans_movil.Data.Mapper.toDomain
import com.example.finans_movil.Data.Mapper.toEntity
import com.example.finans_movil.Model.MonthlyBill
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MonthlyBillRepository(
    private val database:       AppDatabase,
    private val monthlyBillDao: MonthlyBillDao,
    private val transactionDao: TransactionDao,
    private val accountDao:     AccountDao
) {
    val bills: Flow<List<MonthlyBill>> = monthlyBillDao
        .getMonthlyBills()
        .map { list -> list.map { it.toDomain() } }

    suspend fun addBill(bill: MonthlyBill) {
        monthlyBillDao.insertMonthlyBill(bill.toEntity())
    }

    suspend fun payBill(bill: MonthlyBill) {
        database.withTransaction {
            val accountEntity = accountDao.getAccountById(bill.accountId)
                ?: throw Exception("Cuenta no encontrada: ${bill.accountId}")

            // ← Validación de saldo
            if (bill.amount > accountEntity.balance) {
                throw Exception("Fondos insuficientes en la cuenta '${accountEntity.title}'")
            }

            accountDao.updateBalance(
                accountId  = bill.accountId,
                newBalance = accountEntity.balance - bill.amount
            )

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            transactionDao.insertTransaction(
                TransactionEntity(
                    accountId   = bill.accountId,
                    description = bill.name,
                    amount      = bill.amount,
                    type        = "EGRESO"
                )
            )

            monthlyBillDao.updateMonthlyBill(
                bill.toEntity().copy(
                    status   = true,
                    paidDate = today
                )
            )
        }
    }
}