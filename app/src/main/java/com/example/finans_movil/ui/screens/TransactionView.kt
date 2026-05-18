package com.example.finans_movil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
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
import androidx.navigation.NavController
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Viewmodel.TransactionViewModel
import com.example.finans_movil.ui.utilities.ThousandsSeparatorTransformation

private val AppBg     = Color(0xFF000000)
private val CardBg    = Color(0xFF081A3A)
private val Accent    = Color(0xFF25FF00)
private val Muted     = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionView(
    type:          String,
    navController: NavController,
    viewModel:     TransactionViewModel,
    accounts:      List<Account>
) {
    var description     by remember { mutableStateOf("") }
    var amountRaw       by remember { mutableStateOf("") }
    var expanded        by remember { mutableStateOf(false) }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = if (type == "ingreso") "Nuevo Ingreso" else "Nuevo Egreso",
            color      = WhiteSoft,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Selector de cuenta
        ExposedDropdownMenuBox(
            expanded         = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value         = selectedAccount?.title ?: "",
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Seleccionar cuenta") },
                modifier      = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false }
            ) {
                accounts.forEach { account ->
                    DropdownMenuItem(
                        text    = { Text(account.title) },
                        onClick = {
                            selectedAccount = account
                            expanded        = false
                        }
                    )
                }
            }
        }

        // Descripción
        OutlinedTextField(
            value         = description,
            onValueChange = { description = it },
            label         = { Text("Descripción") },
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Monto con formato en tiempo real
        OutlinedTextField(
            value                = amountRaw,
            onValueChange        = { amountRaw = it.filter { c -> c.isDigit() } },
            label                = { Text("Monto") },
            placeholder          = { Text("0", color = MutedSoft) },
            keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = ThousandsSeparatorTransformation,
            modifier             = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Guardar
        Button(
            onClick = {
                viewModel.saveTransaction(
                    accountId   = selectedAccount!!.id,
                    description = description,
                    amount      = amountRaw.toDoubleOrNull() ?: 0.0,
                    type        = type
                )
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}

@Composable
fun InputField(
    value:         String,
    onValueChange: (String) -> Unit,
    placeholder:   String
) {
    Card(
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        TextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(placeholder, color = MutedSoft) },
            colors        = TextFieldDefaults.colors(
                unfocusedContainerColor = CardBg,
                focusedContainerColor   = CardBg,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent
            )
        )
    }
}