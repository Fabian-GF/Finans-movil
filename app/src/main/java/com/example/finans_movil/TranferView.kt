package com.example.finans_movil

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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


private val AppBg = Color(0xFF000000)
private val CardBg = Color(0xFF081A3A)
private val Accent = Color(0xFF25FF00)
private val Muted = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)

@Composable
fun TransferView() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(20.dp)
    ) {

        Text("DESDE", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        FromAccountCard()

        Spacer(modifier = Modifier.height(20.dp))

        Text("TRANSFERENCIAS RECIENTES", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        RecentTransfers()

        Spacer(modifier = Modifier.height(20.dp))

        Text("MONTO", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        AmountSection()

        Spacer(modifier = Modifier.height(20.dp))

        Text("PARA", color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        ToAccountInput()
    }
}

@Composable
fun FromAccountCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Cuenta Principal - $15,420.50",
                color = WhiteSoft,
                fontWeight = FontWeight.Bold
            )

            Icon(Icons.Default.ChevronRight, null, tint = Muted)
        }
    }
}

data class Contact(
    val initials: String,
    val name: String,
    val account: String
)

@Composable
fun RecentTransfers() {

    val contacts = listOf(
        Contact("MG", "María García", "**** 1234"),
        Contact("JP", "Juan Pérez", "**** 5678"),
        Contact("AL", "Ana López", "**** 9012")
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {

        Column {
            contacts.forEachIndexed { index, contact ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // círculo iniciales
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Accent, RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(contact.initials, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.name, color = WhiteSoft)
                        Text(contact.account, color = MutedSoft, fontSize = 12.sp)
                    }

                    Icon(Icons.Default.ChevronRight, null, tint = Muted)
                }

                if (index != contacts.lastIndex) {
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

@Composable
fun AmountSection() {

    var amount by remember { mutableStateOf("0.00") }

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
                        amount = it
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAmountButton(value: String, onClick: () -> Unit) {
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

@Composable
fun ToAccountInput() {

    var text by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = {
                Text("Nombre o número de cuenta", color = MutedSoft)
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = CardBg,
                focusedContainerColor = CardBg,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }
}