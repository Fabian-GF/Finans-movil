package com.example.finans_movil.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finans_movil.Data.Repository.BankRepository
import com.example.finans_movil.Model.Account
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val repository: BankRepository
) : ViewModel() {
    val accounts = repository
        .getAccounts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
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
                        id = 0,
                        type = "AHORRO",
                        badge = "AHORRO",
                        title = "Cuenta principal",
                        balance = 12000.0,
                        number = "1241"
                    )
                )
            }
        }
    }

}

