package com.example.finans_movil.Model

data class Account(
    val id: Int,
    val type: String,
    val badge: String,
    val title: String,
    val balance: Double,
    val number: String
)
