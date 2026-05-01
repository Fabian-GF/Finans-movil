package com.example.finans_movil.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finans_movil.Data.Repository.TransferRepository
import kotlinx.coroutines.launch
import java.time.temporal.TemporalAmount

class TransferViewModel(
    private val repository: TransferRepository
) : ViewModel() {

    fun transfer(
        fromAccountId: Int,
        toAccountId: Int,
        amount: Double,
        onError: (String) -> Unit = {}
    ) {

        viewModelScope.launch {

            try {
                repository.transfer(
                    fromAccountId,
                    toAccountId,
                    amount
                )
            } catch (e: Exception) {
                onError(
                    e.message ?: "Error"
                )
            }
        }
    }
}