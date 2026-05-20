package com.example.finans_movil.Data.Mapper

import com.example.finans_movil.Data.Local.Entities.MonthlyBillEntity
import com.example.finans_movil.Model.MonthlyBill

fun MonthlyBillEntity.toDomain(): MonthlyBill = MonthlyBill(
    id = id,
    name = name,
    amount = amount,
    accountId = accountId,
    dueDay = dueDay,
    status = status,
    paidDate = paidDate
)

fun MonthlyBill.toEntity(): MonthlyBillEntity = MonthlyBillEntity(
    id = id,
    name = name,
    amount = amount,
    accountId = accountId,
    dueDay = dueDay,
    status = status,
    paidDate = paidDate
)