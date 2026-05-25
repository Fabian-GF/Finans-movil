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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Model.Transaction
import com.example.finans_movil.Viewmodel.AccountsViewModel
import com.example.finans_movil.ui.utilities.formatCOP

private val AppBg     = Color(0xFF000000)
private val CardBg    = Color(0xFF081A3A)
private val CardBorder= Color(0xFF172A4D)
private val Accent    = Color(0xFF25FF00)
private val Danger    = Color(0xFFFF3B30)
private val Muted     = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)
private val BlueBadge = Color(0xFF2D6BFF)

@Composable
fun AccountsView(
    navController:    NavHostController,
    viewModel:        AccountsViewModel,
    lastTransactions: Map<Int, Transaction?>
) {
    val accounts by viewModel.accounts.collectAsState()

    // Recarga cada vez que cambia la lista de cuentas
    LaunchedEffect(accounts) {
        viewModel.loadLastTransactions(accounts.map { it.id })
    }

    Scaffold(
        containerColor = AppBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { navController.navigate(Screen.CreateAccount.route) },
                containerColor = Accent
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Crear cuenta",
                    tint               = Color.Black
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBg)
                .padding(20.dp)
        ) {
            Text(
                text       = "Mis cuentas",
                color      = WhiteSoft,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(accounts) { account ->
                    AccountCard(
                        account         = account,
                        navController   = navController,
                        lastTransaction = lastTransactions[account.id]
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountCard(
    account:         Account,
    navController:   NavHostController,
    lastTransaction: Transaction?
) {
    val badgeColor = when (account.badge) {
        "EFECTIVO" -> BlueBadge
        "AHORRO"   -> Accent
        "PRESTAMO" -> Color.Red
        else       -> Muted
    }
    val badgeTextColor = if (account.badge == "AHORRO") Color.Black else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Screen.AccountDetail.createRoute(account.id))
            },
        shape  = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // Fila superior: badge + título + chevron
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgePill(
                    text            = account.badge,
                    backgroundColor = badgeColor,
                    textColor       = badgeTextColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = account.title,
                    color      = WhiteSoft,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.weight(1f)
                )
                Icon(
                    imageVector        = Icons.Default.ChevronRight,
                    contentDescription = "Abrir detalle",
                    tint               = Muted,
                    modifier           = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila inferior: saldo + último movimiento
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Bottom
            ) {
                // Saldo disponible
                Column {
                    Text(text = "Disponible", color = Muted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text       = formatCOP(account.balance),
                        color      = WhiteSoft,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Último movimiento
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text     = "Último movimiento",
                        color    = Muted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (lastTransaction != null) {
                        val isIngreso = lastTransaction.type.lowercase() == "ingreso"
                        Text(
                            text       = "${if (isIngreso) "+" else "-"}${formatCOP(lastTransaction.amount)}",
                            color      = if (isIngreso) Accent else Danger,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text     = lastTransaction.description,
                            color    = MutedSoft,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text     = "Sin movimientos",
                            color    = MutedSoft,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}