package com.example.finans_movil.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finans_movil.Data.Repository.TransactionRepository
import com.example.finans_movil.Model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository //
) : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun saveTransaction(
        accountId: Int,
        description: String,
        amount: Double,
        type: String,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    accountId = accountId,
                    description = description,
                    amount = amount,
                    type = type
                )
                repository.insertTransaction(transaction)
                loadTransactions(accountId)
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    fun loadTransactions(accountId: Int) {
        viewModelScope.launch {
            _transactions.value = repository.getTransactions(accountId)
        }
    }
}