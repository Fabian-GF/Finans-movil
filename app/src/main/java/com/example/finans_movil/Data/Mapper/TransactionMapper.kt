package com.example.finans_movil.Data.Mapper

import com.example.finans_movil.Data.Local.Entities.TransactionEntity
import com.example.finans_movil.Model.Transaction

fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id = id,
        accountId = accountId,
        description = description,
        amount = amount,
        type = type,
        date = date
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        accountId = accountId,
        description = description,
        amount = amount,
        type = type,
        date = date
    )
}