package com.example.finans_movil.Data.Repository

import com.example.finans_movil.Data.Local.Dao.AccountDao
import com.example.finans_movil.Data.Local.Dao.TransactionDao
import com.example.finans_movil.Data.Mapper.toEntity
import com.example.finans_movil.Model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransferRepository(
    private val accountDao:     AccountDao,
    private val transactionDao: TransactionDao
) {
    suspend fun transfer(
        fromAccountId: Int,
        toAccountId:   Int,
        amount:        Double
    ) {
        val fromAccount = accountDao.getAccountById(fromAccountId)
            ?: throw Exception("Cuenta origen no encontrada")

        val toAccount = accountDao.getAccountById(toAccountId)
            ?: throw Exception("Cuenta destino no encontrada")

        val transactionDate= SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (amount > fromAccount.balance) {
            throw Exception("Fondos insuficientes")
        }

        accountDao.updateBalance(fromAccountId, fromAccount.balance - amount)
        accountDao.updateBalance(toAccountId,   toAccount.balance   + amount)

        transactionDao.insertTransaction(
            Transaction(
                accountId   = fromAccountId,
                description = "Transferencia a ${toAccount.title}",
                amount      = amount,
                type        = "egreso",
                date        = transactionDate
            ).toEntity()
        )

        transactionDao.insertTransaction(
            Transaction(
                accountId   = toAccountId,
                description = "Transferencia recibida de ${fromAccount.title}",
                amount      = amount,
                type        = "ingreso",
                date        = transactionDate
            ).toEntity()
        )
    }
}