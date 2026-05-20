package com.example.finans_movil.Viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finans_movil.Data.Repository.MonthlyBillRepository
import com.example.finans_movil.Model.MonthlyBill
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MonthlyBillViewModel(
    private val repository: MonthlyBillRepository
) : ViewModel() {

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
                e.printStackTrace() // por ahora logea, luego puedes mostrar un mensaje
            }
        }
    }
}