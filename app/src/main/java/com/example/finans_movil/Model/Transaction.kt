package com.example.finans_movil.Model

data class Transaction(
    val id: Int = 0,
    val accountId: Int,
    val description: String,
    val amount: Double,
    val type: String
)
