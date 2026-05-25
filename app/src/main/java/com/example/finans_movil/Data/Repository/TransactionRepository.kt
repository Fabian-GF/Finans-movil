package com.example.finans_movil.Data.Repository

import com.example.finans_movil.Data.Local.Dao.AccountDao
import com.example.finans_movil.Data.Local.Dao.TransactionDao
import com.example.finans_movil.Data.Mapper.toEntity
import com.example.finans_movil.Data.Mapper.toModel
import com.example.finans_movil.Model.Transaction

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val accountDao:     AccountDao
) {
    suspend fun insertTransaction(transaction: Transaction) {

        val account = accountDao.getAccountById(transaction.accountId)
            ?: throw Exception("Cuenta no encontrada")

        val newBalance = when (transaction.type) {
            "ingreso" -> account.balance + transaction.amount
            "egreso"  -> {
                if (transaction.amount > account.balance) {
                    throw Exception("Fondos insuficientes")
                }
                account.balance - transaction.amount
            }
            else -> account.balance
        }

        accountDao.updateBalance(
            accountId  = transaction.accountId,
            newBalance = newBalance
        )

        transactionDao.insertTransaction(transaction.toEntity())
    }

    suspend fun getTransactions(accountId: Int): List<Transaction> {
        return transactionDao
            .getTransactionByAccount(accountId)
            .map { it.toModel() }
    }

    suspend fun getLastTransaction(accountId: Int): Transaction? {
        return transactionDao
            .getLastTransactionByAccount(accountId)
            ?.toModel()
    }
}