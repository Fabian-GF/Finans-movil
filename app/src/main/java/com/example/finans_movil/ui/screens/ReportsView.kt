package com.example.finans_movil.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finans_movil.Model.Transaction
import com.example.finans_movil.Viewmodel.ReportsViewModel
import com.example.finans_movil.ui.utilities.formatCOP

private val AppBg     = Color(0xFF000000)
private val CardBg    = Color(0xFF081A3A)
private val CardBorder= Color(0xFF172A4D)
private val Accent    = Color(0xFF25FF00)
private val Danger    = Color(0xFFFF3B30)
private val Muted     = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)
private val ButtonIdle= Color(0xFF152849)

@Composable
fun ReportsView(viewModel: ReportsViewModel) {

    val selectedMonth   by viewModel.selectedMonth.collectAsState()
    val availableMonths by viewModel.availableMonths.collectAsState()
    val totalIngresos   by viewModel.totalIngresos.collectAsState()
    val totalEgresos    by viewModel.totalEgresos.collectAsState()
    val transactions    by viewModel.transactions.collectAsState()

    val balance by viewModel.balance.collectAsState()
    val ratio   by viewModel.ratio.collectAsState()

    LazyColumn(
        modifier            = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Título
        item {
            Text(
                text       = "Informes",
                color      = WhiteSoft,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Selector de mes
        item {
            MonthSelector(
                selectedMonth   = selectedMonth,
                availableMonths = availableMonths,
                onMonthSelected = { viewModel.selectMonth(it) }
            )
        }

        // Tarjetas de totales
        item {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TotalCard(
                    label  = "Ingresos",
                    amount = totalIngresos,
                    color  = Accent,
                    icon   = Icons.AutoMirrored.Filled.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
                TotalCard(
                    label  = "Egresos",
                    amount = totalEgresos,
                    color  = Danger,
                    icon   = Icons.AutoMirrored.Filled.TrendingDown,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Dashboard ingresos vs egresos
        item {
            DashboardCard(
                totalIngresos = totalIngresos,
                totalEgresos  = totalEgresos,
                balance       = balance,
                ratio         = ratio
            )
        }

        // Indicadores
        item {
            IndicadoresCard(
                totalIngresos = totalIngresos,
                totalEgresos  = totalEgresos,
                transactions  = transactions
            )
        }

        // Movimientos del mes
        item {
            Text(
                text       = "Movimientos del mes",
                color      = WhiteSoft,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (transactions.isEmpty()) {
            item {
                Text(
                    text      = "Sin movimientos este mes",
                    color     = MutedSoft,
                    fontSize  = 14.sp,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(transactions) { tx ->
                TransactionRowItem(tx)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// Selector de mes
@Composable
private fun MonthSelector(
    selectedMonth:   String,
    availableMonths: List<String>,
    onMonthSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape  = RoundedCornerShape(14.dp),
            color  = ButtonIdle,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = formatYearMonth(selectedMonth),
                    color      = WhiteSoft,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector        = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint               = Muted
                )
            }
        }

        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(CardBg)
        ) {
            if (availableMonths.isEmpty()) {
                DropdownMenuItem(
                    text    = { Text("Sin datos", color = MutedSoft) },
                    onClick = { expanded = false }
                )
            } else {
                availableMonths.forEach { month ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text  = formatYearMonth(month),
                                color = if (month == selectedMonth) Accent else WhiteSoft,
                                fontWeight = if (month == selectedMonth)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onMonthSelected(month)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// Tarjeta de total (ingresos o egresos)
@Composable
private fun TotalCard(
    label:    String,
    amount:   Double,
    color:    Color,
    icon:     androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = CardBg),
        border   = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = color,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = label, color = Muted, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = formatCOP(amount),
                color      = color,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Dashboard con barra y balance
@Composable
private fun DashboardCard(
    totalIngresos: Double,
    totalEgresos:  Double,
    balance:       Double,
    ratio:         Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = CardBg),
        border   = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text       = "Gastos vs Ingresos",
                color      = WhiteSoft,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de progreso doble
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(Color(0xFF152849), RoundedCornerShape(999.dp))
            ) {
                // Barra de egresos sobre ingresos
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = ratio)
                        .height(12.dp)
                        .background(
                            if (ratio > 0.8f) Danger else Danger,
                            RoundedCornerShape(999.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text     = "${(ratio * 100).toInt()}% gastado",
                    color    = if (ratio > 0.8f) Danger else Muted,
                    fontSize = 12.sp
                )
                Text(
                    text     = "${((1f - ratio) * 100).toInt()}% disponible",
                    color    = if (ratio > 0.8f) Danger else Accent,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Balance del mes
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(text = "Balance del mes", color = Muted, fontSize = 14.sp)
                Text(
                    text       = formatCOP(balance),
                    color      = if (balance >= 0) Accent else Danger,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Indicadores clave
@Composable
private fun IndicadoresCard(
    totalIngresos: Double,
    totalEgresos:  Double,
    transactions:  List<Transaction>
) {
    val cantidadIngresos  = transactions.count { it.type.lowercase() == "ingreso" }
    val cantidadEgresos   = transactions.count { it.type.lowercase() == "egreso" }
    val promedioEgreso    = if (cantidadEgresos > 0) totalEgresos / cantidadEgresos else 0.0
    val mayorEgreso       = transactions
        .filter { it.type.lowercase() == "egreso" }
        .maxByOrNull { it.amount }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = CardBg),
        border   = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text       = "Indicadores",
                color      = WhiteSoft,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            IndicadorRow(label = "Transacciones de ingreso", value = "$cantidadIngresos",      valueColor = Accent)
            Spacer(modifier = Modifier.height(10.dp))
            IndicadorRow(label = "Transacciones de egreso",  value = "$cantidadEgresos",       valueColor = Danger)
            Spacer(modifier = Modifier.height(10.dp))
            IndicadorRow(label = "Promedio por egreso",      value = formatCOP(promedioEgreso), valueColor = WhiteSoft)
            Spacer(modifier = Modifier.height(10.dp))
            IndicadorRow(
                label      = "Mayor egreso",
                value      = if (mayorEgreso != null) formatCOP(mayorEgreso.amount) else "—",
                valueColor = if (mayorEgreso != null) Danger else MutedSoft,
                subtitle   = mayorEgreso?.description
            )
        }
    }
}

// Fila de indicador
@Composable
private fun IndicadorRow(
    label:      String,
    value:      String,
    valueColor: Color,
    subtitle:   String? = null
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, color = Muted, fontSize = 13.sp)
            if (subtitle != null) {
                Text(text = subtitle, color = MutedSoft, fontSize = 11.sp)
            }
        }
        Text(
            text       = value,
            color      = valueColor,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Fila de transacción
@Composable
private fun TransactionRowItem(transaction: Transaction) {
    val isIngreso = transaction.type.lowercase() == "ingreso"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = CardBg),
        border   = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        if (isIngreso) Color(0xFF0F3D2E) else Color(0xFF3D0F1E),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (isIngreso)
                        Icons.Default.KeyboardArrowDown
                    else
                        Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint               = if (isIngreso) Accent else Danger,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Descripción
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = transaction.description,
                    color      = WhiteSoft,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text     = if (isIngreso) "Ingreso" else "Egreso",
                    color    = MutedSoft,
                    fontSize = 12.sp
                )
            }

            // Monto
            Text(
                text       = "${if (isIngreso) "+" else "-"}${formatCOP(transaction.amount)}",
                color      = if (isIngreso) Accent else Danger,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Formatea "2026-05" → "Mayo 2026"
private fun formatYearMonth(yearMonth: String): String {
    return try {
        val parts = yearMonth.split("-")
        val year  = parts[0]
        val month = when (parts[1]) {
            "01" -> "Enero";   "02" -> "Febrero"; "03" -> "Marzo"
            "04" -> "Abril";   "05" -> "Mayo";    "06" -> "Junio"
            "07" -> "Julio";   "08" -> "Agosto";  "09" -> "Septiembre"
            "10" -> "Octubre"; "11" -> "Noviembre"; "12" -> "Diciembre"
            else -> yearMonth
        }
        "$month $year"
    } catch (e: Exception) {
        yearMonth
    }
}