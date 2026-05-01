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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Viewmodel.TransferViewModel

private val AppBg = Color(0xFF000000)
private val CardBg = Color(0xFF081A3A)
private val Accent = Color(0xFF25FF00)
private val Muted = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)

@Composable
fun TransferView(
    accounts: List<Account>,
    viewModel: TransferViewModel
) {
    if ( accounts.size < 2) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBg)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Necesitas al menos 2 cuentas para realizar transferencias",
                color = WhiteSoft
            )
        }
        return
    }

    var fromAccount by remember {
        mutableStateOf(accounts.first())
    }

    var toAccount by remember {
        mutableStateOf(accounts.last())
    }

    var amount by remember {
        mutableStateOf("")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(20.dp)
    ) {

        Text("DESDE", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        AccountSelector(
            selectedAccount = fromAccount,
            accounts = accounts,
            onAccountSelected = {
                fromAccount = it
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("PARA", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        AccountSelector(
            selectedAccount = toAccount,
            accounts = accounts.filter { it != fromAccount },
            onAccountSelected = {
                toAccount = it
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("MONTO", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        AmountSection(
            amount = amount,
            onAmountChange = {
                amount = it
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                viewModel.transfer(
                    fromAccountId = fromAccount.id,
                    toAccountId = toAccount.id,
                    amount = amount.toDoubleOrNull() ?: 0.0
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Transferir")
        }
    }
}

@Composable
fun AccountSelector(
    selectedAccount: Account,
    accounts: List<Account>,
    onAccountSelected: (Account) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        selectedAccount.title,
                        color = WhiteSoft,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "$${selectedAccount.balance}",
                        color = MutedSoft,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Muted
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            accounts.forEach { account ->

                DropdownMenuItem(
                    text = {
                        Column {
                            Text(account.title)
                            Text(
                                "$${account.balance}",
                                fontSize = 12.sp,
                                color = Color.Gray
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

@Composable
fun AmountSection(
    amount: String,
    onAmountChange: (String) -> Unit
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {

        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                "$ $amount",
                color = WhiteSoft,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                listOf("50", "100", "200", "500").forEach {

                    QuickAmountButton(it) {
                        onAmountChange(it)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = amount,
                onValueChange = onAmountChange,
                placeholder = {
                    Text("Ingrese monto", color = MutedSoft)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = CardBg,
                    focusedContainerColor = CardBg,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedTextColor = WhiteSoft,
                    unfocusedTextColor = WhiteSoft
                )
            )
        }
    }
}

@Composable
fun QuickAmountButton(
    value: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A47))
    ) {

        Text(
            "$$value",
            modifier = Modifier.padding(12.dp),
            color = WhiteSoft
        )
    }
}