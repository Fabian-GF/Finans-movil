package com.example.finans_movil.Viewmodel.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finans_movil.Data.Repository.BankRepository
import com.example.finans_movil.Data.Repository.TransactionRepository
import com.example.finans_movil.Viewmodel.AccountsViewModel

class AccountViewModelFactory(
    private val  repository: BankRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountsViewModel::class.java)) {
            return AccountsViewModel(repository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel ${modelClass.name}")
    }
}