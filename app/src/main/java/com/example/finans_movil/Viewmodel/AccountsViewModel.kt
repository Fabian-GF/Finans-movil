package com.example.finans_movil.Viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finans_movil.Data.Repository.BankRepository
import com.example.finans_movil.Data.Repository.TransactionRepository
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val repository:            BankRepository,
    private val transactionRepository: TransactionRepository   // ← agregar
) : ViewModel() {

    val accounts = repository
        .getAccounts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    // Mapa accountId -> última Transaction
    private val _lastTransactions = MutableStateFlow<Map<Int, Transaction?>>(emptyMap())
    val lastTransactions: StateFlow<Map<Int, Transaction?>> = _lastTransactions

    // Se llama cada vez que cambia la lista de cuentas
    fun loadLastTransactions(accountIds: List<Int>) {
        viewModelScope.launch {
            val map = mutableMapOf<Int, Transaction?>()   // ← tipo explícito
            accountIds.forEach { id ->
                map[id] = transactionRepository.getLastTransaction(id)
            }
            _lastTransactions.value = map
        }
    }

    fun insertAccount(account: Account) {
        viewModelScope.launch {
            repository.insertAccount(account)
        }
    }

    fun insertTestDataIfNeeded() {
        viewModelScope.launch {
            if (repository.getAccountsOnce().isEmpty()) {
                repository.insertAccount(
                    Account(
                        id      = 0,
                        type    = "AHORRO",
                        badge   = "AHORRO",
                        title   = "Cuenta principal",
                        balance = 12000.0,
                        number  = "1241"
                    )
                )
            }
        }
    }

    fun getAccountById(id: Int): Account? {
        return accounts.value.find { it.id == id }
    }
}

