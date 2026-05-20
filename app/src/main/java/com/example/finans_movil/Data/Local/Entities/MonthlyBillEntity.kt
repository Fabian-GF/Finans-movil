package com.example.finans_movil.Data.Local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_bills")
data class MonthlyBillEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String, // nombre de la cuenta "Arriendo"
    val amount: Double, // valor a pagar
    val accountId: Int, // cuenta desde la que se paga
    val dueDay: String, // dia del mes
    val status: Boolean = false,
    val paidDate: String? = null // fecha en que se pago
)
