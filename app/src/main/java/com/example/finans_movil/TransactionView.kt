package com.example.finans_movil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AppBg = Color(0xFF000000)
private val CardBg = Color(0xFF081A3A)
private val Accent = Color(0xFF25FF00)
private val Muted = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)
@Composable
fun TransactionView(type: String) {

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var account by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if (type == "ingreso") "Nuevo Ingreso" else "Nuevo Egreso",
            color = WhiteSoft,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Cuenta
        Text("Cuenta", color = Muted)
        Spacer(modifier = Modifier.height(8.dp))

        InputField(
            value = account,
            onValueChange = { account = it },
            placeholder = "Seleccionar cuenta"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Monto
        Text("Monto", color = Muted)
        Spacer(modifier = Modifier.height(8.dp))

        InputField(
            value = amount,
            onValueChange = { amount = it },
            placeholder = "$0.00"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text("Descripción", color = Muted)
        Spacer(modifier = Modifier.height(8.dp))

        InputField(
            value = description,
            onValueChange = { description = it },
            placeholder = "Ej: Pago, salario..."
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Botón
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* guardar */ },
            colors = CardDefaults.cardColors(containerColor = Accent)
        ) {
            Text(
                text = if (type == "ingreso") "Guardar ingreso" else "Guardar egreso",
                modifier = Modifier.padding(16.dp),
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = MutedSoft)
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