package com.example.finans_movil.Data.Repository

import com.example.finans_movil.Data.Local.Dao.AccountDao
import com.example.finans_movil.Data.Local.Dao.TransactionDao
import com.example.finans_movil.Data.Mapper.toEntity
import com.example.finans_movil.Model.Transaction

class TransferRepository(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao
) {

    suspend fun transfer(
        fromAccountId: Int,
        toAccountId: Int,
        amount: Double
    ) {
        val fromAccount =
            accountDao.getAccountById(fromAccountId)

        val toAccount =
            accountDao.getAccountById(toAccountId)

        //Validar datos

        if (amount > fromAccount.balance){
            throw Exception("Fondos insuficientes")
        }

        //Actualizar balance

        accountDao.updateBalance(
            fromAccountId,
            fromAccount.balance - amount
        )

        accountDao.updateBalance(
            toAccountId,
            toAccount.balance + amount
        )

        //Registrar egreso
        transactionDao.insertTransaction(
            Transaction(
                accountId = fromAccountId,
                description = "Transferencia a ${toAccount.title}",
                amount = amount,
                type = "egreso"
            ).toEntity()
        )

        //Registrar ingreso
        transactionDao.insertTransaction(
            Transaction(
                accountId = toAccountId,
                description = "Transferencia recibida de ${fromAccount.title}",
                amount = amount,
                type = "ingreso"
            ).toEntity()
        )
    }
}