package com.example.finans_movil.Viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finans_movil.Data.Repository.MonthlyBillRepository
import com.example.finans_movil.Model.MonthlyBill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MonthlyBillViewModel(
    private val repository: MonthlyBillRepository,
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    val bills: StateFlow<List<MonthlyBill>> = repository.bills
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addBill(bill: MonthlyBill) {
        viewModelScope.launch {
            try {
                repository.addBill(bill)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun payBill(bill: MonthlyBill) {
        viewModelScope.launch {
            try {
                repository.payBill(bill)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}