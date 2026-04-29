package com.example.finans_movil.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Viewmodel.AccountsViewModel


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
fun AccountsView(
    navController: NavHostController,
    viewModel: AccountsViewModel) {

    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        containerColor = AppBg,

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.CreateAccount.route)
                },
                containerColor = Accent
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear cuenta",
                    tint = Color.Black
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
                text = "Mis cuentas",
                color = WhiteSoft,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(accounts) {account ->
                    InfoAccountCard(
                        account = account,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoAccountCard(
    account: Account,
    navController: NavHostController) {
    val badgeColor = when(account.badge) {
        "ACTIVA" -> BlueBadge
        "AHORRO" -> Accent
        "PRESTAMO" -> Color.Companion.Red
        else -> Muted
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                navController.navigate(
                    Screen.AccountDetail.createRoute(account.id)) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
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
                    backgroundColor = Red,
                    textColor = Color.White
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
                    text = "$${account.balance}",
                    color = WhiteSoft,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = account.number,
                    color = MutedSoft,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoAccountCardPreview(){

    val fakeAccount = Account(
        id = 1,
        type = "Cuenta Nómina",
        badge = "ACTIVA",
        title = "Cuenta Principal",
        balance = 12430.80,
        number = "**** **** 1128"
    )
    InfoAccountCard(
        account =  fakeAccount,
        navController = rememberNavController()
    )

}

