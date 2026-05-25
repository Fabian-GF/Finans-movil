package com.example.finans_movil.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finans_movil.Data.Repository.ReportsRepository
import com.example.finans_movil.Model.Transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    private val repository: ReportsRepository
) : ViewModel() {

    // Mes seleccionado
    private val _selectedMonth = MutableStateFlow(
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    )
    val selectedMonth: StateFlow<String> = _selectedMonth

    // Meses disponibles — reactivo
    val availableMonths: StateFlow<List<String>> = repository
        .getAvailableMonths()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Transacciones del mes seleccionado — reactivo
    val transactions: StateFlow<List<Transaction>> = _selectedMonth
        .flatMapLatest { month -> repository.getTransactionsByMonth(month) }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Total ingresos — reactivo
    val totalIngresos: StateFlow<Double> = _selectedMonth
        .flatMapLatest { month -> repository.getTotalIngresos(month) }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )

    // Total egresos — reactivo
    val totalEgresos: StateFlow<Double> = _selectedMonth
        .flatMapLatest { month -> repository.getTotalEgresos(month) }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )

    fun selectMonth(yearMonth: String) {
        _selectedMonth.value = yearMonth
    }

    val balance: StateFlow<Double> = combine(totalIngresos, totalEgresos) { ingresos, egresos ->
        ingresos - egresos
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0.0
    )

    val ratio: StateFlow<Float> = combine(totalIngresos, totalEgresos) { ingresos, egresos ->
        if (ingresos > 0) (egresos / ingresos).coerceIn(0.0, 1.0).toFloat() else 0f
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0f
    )
}