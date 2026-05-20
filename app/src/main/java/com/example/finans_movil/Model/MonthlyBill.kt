package com.example.finans_movil.Model

data class MonthlyBill(
    val id:        Int     = 0,
    val name:      String  = "",
    val amount:    Double  = 0.0,
    val accountId: Int     = 0,
    val dueDay:    String  = "",
    val status:    Boolean = false,
    val paidDate:  String? = null
)