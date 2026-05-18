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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
 import androidx.navigation.NavController
 import com.example.finans_movil.Model.Account
 import com.example.finans_movil.Model.Transaction
 import com.example.finans_movil.Viewmodel.AccountsViewModel
 import com.example.finans_movil.Viewmodel.TransactionViewModel

private val AppBg = Color(0xFF000000)
private val CardBg = Color(0xFF081A3A)
private val Accent = Color(0xFF25FF00)
private val Muted = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)
private val BlueBadge = Color(0xFF2D6BFF)

// Formato de moneda
private fun formatCOP(amount: Double): String {
    val locale   = java.util.Locale("es", "CO")
    val formatter = java.text.NumberFormat.getNumberInstance(locale).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
        isGroupingUsed        = true
    }
    return "$ ${formatter.format(amount)} COP"
}

@Composable
fun AccountDetailView(
    accountId: Int,
    viewModel: AccountsViewModel,
    transactionViewModel: TransactionViewModel,
    navController: NavController
) {
    val account = viewModel.getAccountById(accountId)


    val transactions by transactionViewModel
        .transactions
        .collectAsState()

    LaunchedEffect(accountId) {

        transactionViewModel.loadTransactions(
            accountId
        )
    }

    account ?: return


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(20.dp)
    ) {

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                },
                tint = WhiteSoft
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                "Detalles de Cuenta",
                color = WhiteSoft,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AccountDetailCard(account)

        Spacer(modifier = Modifier.height(20.dp))

        MovementsSection(
            transactions = transactions
        )
    }
}

@Composable
fun AccountDetailCard(account: Account) {

    val badgeColor = when (account.badge) {
        "EFECTIVO"   -> BlueBadge
        "AHORRO"   -> Accent
        "PRESTAMO" -> Color.Companion.Red
        else       -> Muted
    }
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row {
                Text(account.type, color = Muted)
                Spacer(modifier = Modifier.width(10.dp))
                BadgePill(account.type, badgeColor, Color.Black)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(account.title, color = WhiteSoft, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Text("Saldo Disponible", color = Muted)

            Text(
                "${formatCOP(account.balance)}",
                color = WhiteSoft,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("**** **** **** ${account.number.takeLast(4)}", color = MutedSoft)
                Text("Copiar", color = Accent)
            }
        }
    }
}

data class Movement(
    val title: String,
    val subtitle: String,
    val amount: String,
    val isPositive: Boolean
)

@Composable
fun MovementsSection(
    transactions: List<Transaction>
) {

    Text(
        "Movimientos",
        color = WhiteSoft,
        fontSize = 18.sp
    )

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        )
    ) {

        Column {

            transactions.forEachIndexed { index, t ->

                val isPositive =
                    t.type == "ingreso"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isPositive)
                                    Color(0xFF0F3D2E)
                                else
                                    Color(0xFF3D0F1E),

                                RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            if (isPositive) "+" else "-",
                            color = WhiteSoft
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            t.description,
                            color = WhiteSoft
                        )

                        Text(
                            t.type,
                            color = MutedSoft,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = if(isPositive)
                            "+${formatCOP(t.amount)}"
                        else
                            "-${formatCOP(t.amount)}",

                        color = if (isPositive)
                            Accent
                        else
                            Color.Red
                    )
                }

                if(index != transactions.lastIndex) {

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF1A2A47))
                    )
                }
            }
        }
    }
}

