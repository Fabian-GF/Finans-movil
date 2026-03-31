package com.example.finans_movil

 import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AppBg = Color(0xFF000000)
private val CardBg = Color(0xFF081A3A)
private val Accent = Color(0xFF25FF00)
private val Muted = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)
private val BlueBadge = Color(0xFF2D6BFF)

@Composable
fun AccountDetailView() {

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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = WhiteSoft)

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                "Detalles de Cuenta",
                color = WhiteSoft,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AccountDetailCard()

        Spacer(modifier = Modifier.height(20.dp))

        MovementsSection()
    }
}

@Composable
fun AccountDetailCard() {

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row {
                Text("Cuenta Corriente", color = Muted)
                Spacer(modifier = Modifier.width(10.dp))
                BadgePill("ACTIVO", BlueBadge, Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Cuenta Principal", color = WhiteSoft, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Text("Saldo Disponible", color = Muted)

            Text(
                "$15,420.50",
                color = WhiteSoft,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("**** **** **** 4532", color = MutedSoft)
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
fun MovementsSection() {

    val movements = listOf(
        Movement("Amazon.com", "Compras · Ayer", "-$85.99", false),
        Movement("Pago de nómina", "Salario · 19 feb", "+$3,500.00", true),
        Movement("Starbucks", "Café · Hoy", "-$12.50", false)
    )

    Text("Movimientos", color = WhiteSoft, fontSize = 18.sp)

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {

        Column {
            movements.forEachIndexed { index, m ->

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
                                if (m.isPositive) Color(0xFF0F3D2E) else Color(0xFF3D0F1E),
                                RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (m.isPositive) "$" else "🛒",
                            color = WhiteSoft
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(m.title, color = WhiteSoft)
                        Text(m.subtitle, color = MutedSoft, fontSize = 12.sp)
                    }

                    Text(
                        m.amount,
                        color = if (m.isPositive) Accent else Color.Red
                    )
                }

                if (index != movements.lastIndex) {
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

