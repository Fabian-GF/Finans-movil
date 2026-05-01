package com.example.finans_movil.Viewmodel.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finans_movil.Data.Repository.TransferRepository
import com.example.finans_movil.Viewmodel.TransferViewModel

@Suppress("UNCHECKED_CAST")
class TransferViewModelFactory(
    private val repository: TransferRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TransferViewModel(
            repository
        ) as T
    }
}