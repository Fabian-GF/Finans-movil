package com.example.finans_movil.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Viewmodel.TransferViewModel
import com.example.finans_movil.ui.utilities.ThousandsSeparatorTransformation
import com.example.finans_movil.ui.utilities.formatCOP
import com.example.finans_movil.ui.utilities.formatCOPLong

private val AppBg     = Color(0xFF000000)
private val CardBg    = Color(0xFF081A3A)
private val Accent    = Color(0xFF25FF00)
private val Muted     = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)

// Tipos de cuenta habilitados para transferencias
private val TRANSFER_TYPES = setOf("AHORRO", "EFECTIVO")

// Pantalla principal
@Composable
fun TransferView(
    accounts:  List<Account>,
    viewModel: TransferViewModel
) {
    val eligibleAccounts = accounts.filter { it.type.uppercase() in TRANSFER_TYPES }

    if (eligibleAccounts.size < 2) {
        Column(
            modifier              = Modifier
                .fillMaxSize()
                .background(AppBg)
                .padding(20.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            Text(
                text  = "Necesitas al menos 2 cuentas de tipo Ahorro o Efectivo para realizar transferencias",
                color = WhiteSoft
            )
        }
        return
    }

    var fromAccount by remember { mutableStateOf(eligibleAccounts.first()) }
    var toAccount   by remember { mutableStateOf(eligibleAccounts.last()) }
    var amountRaw   by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(20.dp)
    ) {
        Text("DESDE", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        AccountSelector(
            selectedAccount   = fromAccount,
            accounts          = eligibleAccounts,
            onAccountSelected = { fromAccount = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("PARA", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        AccountSelector(
            selectedAccount   = toAccount,
            accounts          = eligibleAccounts.filter { it != fromAccount },
            onAccountSelected = { toAccount = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("MONTO", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        AmountSection(
            amountRaw      = amountRaw,
            onAmountChange = { amountRaw = it.filter { c -> c.isDigit() } }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.transfer(
                    fromAccountId = fromAccount.id,
                    toAccountId   = toAccount.id,
                    amount        = amountRaw.toDoubleOrNull() ?: 0.0
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Transferir")
        }
    }
}

// Selector de cuenta
@Composable
fun AccountSelector(
    selectedAccount:   Account,
    accounts:          List<Account>,
    onAccountSelected: (Account) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Row(
                modifier              = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text       = selectedAccount.title,
                        color      = WhiteSoft,
                        fontWeight = FontWeight.Bold
                    )
                    // formatCOP viene de CurrencyUtils.kt
                    Text(
                        text     = formatCOP(selectedAccount.balance),
                        color    = MutedSoft,
                        fontSize = 12.sp
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted)
            }
        }

        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false }
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(account.title, fontWeight = FontWeight.Bold)
                            Text(
                                text     = formatCOP(account.balance),
                                fontSize = 12.sp,
                                color    = Color.Gray
                            )
                        }
                    },
                    onClick = {
                        onAccountSelected(account)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Sección de monto
@Composable
fun AmountSection(
    amountRaw:      String,
    onAmountChange: (String) -> Unit
) {
    val quickAmounts = listOf(10_000L, 20_000L, 50_000L, 100_000L, 200_000L, 500_000L)

    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Vista previa formateada — formatCOP viene de CurrencyUtils.kt
            val preview = if (amountRaw.isEmpty()) "$ 0 COP"
            else formatCOP(amountRaw.toLongOrNull()?.toDouble() ?: 0.0)

            Text(
                text       = preview,
                color      = if (amountRaw.isEmpty()) MutedSoft else WhiteSoft,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Presets en 2 filas de 3 — formatCOPLong viene de CurrencyUtils.kt
            quickAmounts.chunked(3).forEach { row ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { preset ->
                        QuickAmountButton(
                            label    = formatCOPLong(preset),
                            selected = amountRaw == preset.toString(),
                            modifier = Modifier.weight(1f)
                        ) {
                            onAmountChange(preset.toString())
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo libre — ThousandsSeparatorTransformation viene de CurrencyUtils.kt
            TextField(
                value                = amountRaw,
                onValueChange        = onAmountChange,
                placeholder          = { Text("Otro monto", color = MutedSoft) },
                keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = ThousandsSeparatorTransformation,
                colors               = TextFieldDefaults.colors(
                    unfocusedContainerColor = CardBg,
                    focusedContainerColor   = CardBg,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor   = Accent,
                    focusedTextColor        = WhiteSoft,
                    unfocusedTextColor      = WhiteSoft
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Botón de monto rápido
@Composable
fun QuickAmountButton(
    label:    String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {
    val bg      = if (selected) Accent      else Color(0xFF1A2A47)
    val textCol = if (selected) Color.Black else WhiteSoft

    Card(
        modifier = modifier.clickable { onClick() },
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = bg)
    ) {
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = label,
                color      = textCol,
                fontSize   = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}