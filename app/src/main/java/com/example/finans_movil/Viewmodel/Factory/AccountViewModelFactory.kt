package com.example.finans_movil.Viewmodel.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finans_movil.Data.Repository.BankRepository
import com.example.finans_movil.Viewmodel.AccountsViewModel

class AccountViewModelFactory(
    private val  repository: BankRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViedModel class")
    }
}