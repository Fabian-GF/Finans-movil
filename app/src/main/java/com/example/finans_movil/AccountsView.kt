package com.example.finans_movil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


private val AppBg = Color(0xFF000000)
private val CardBg = Color(0xFF081A3A)
private val CardBorder = Color(0xFF172A4D)
private val Accent = Color(0xFF25FF00)
private val Red = Color(0xFFFF0000)
private val Muted = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)
private val BlueBadge = Color(0xFF2D6BFF)

@Composable
fun AccountsView(navController: NavHostController) {

    val accounts = listOf(
        HomeView(
            type = "Cuenta Nómina",
            badge = "ACTIVA",
            title = "Cuenta Principal",
            amount = "$12,430.80",
            maskedNumber = "**** **** 1128",
            badgeColor = BlueBadge
        ),
        HomeView(
            type = "Ahorro",
            badge = "AHORRO",
            title = "Fondo Viaje",
            amount = "$48,200.00",
            maskedNumber = "**** **** 9041",
            badgeColor = Accent
        ),
        HomeView(
            type = "Crédito",
            badge = "PRÉSTAMO",
            title = "Tarjeta de Credito ",
            amount = "-$2,300.00",
            maskedNumber = "**** **** 9041",
            badgeColor = Red
        )

    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(20.dp)
    ) {

        Text(
            text = "Mis Cuentas",
            color = WhiteSoft,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(accounts) { account ->
                InfoAccountCard(account, navController)
            }
        }
    }
}

@Composable
private fun InfoAccountCard(account: HomeView, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                navController.navigate(Screen.AccountDetail.route) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = account.type,
                    color = Muted,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.width(10.dp))

                BadgePill(
                    text = account.badge,
                    backgroundColor = account.badgeColor,
                    textColor = if (account.badgeColor == Accent) Color.Black else Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Abrir",
                    tint = Muted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = account.title,
                color = WhiteSoft,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Disponible",
                color = Muted,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = account.amount,
                    color = WhiteSoft,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = account.maskedNumber,
                    color = MutedSoft,
                    fontSize = 13.sp
                )
            }
        }
    }
}