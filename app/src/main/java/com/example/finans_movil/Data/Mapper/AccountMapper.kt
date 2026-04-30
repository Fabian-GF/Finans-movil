package com.example.finans_movil.Data.Mapper

import com.example.finans_movil.Data.Local.Entities.AccountEntity
import com.example.finans_movil.Model.Account

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        type = type,
        badge = badge,
        title = title,
        balance = balance,
        number = number
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        type = type,
        badge = badge,
        title = title,
        balance = balance,
        number = number
    )
}