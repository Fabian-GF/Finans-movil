package com.example.finans_movil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Viewmodel.AccountsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  CreateAccountView(
    navController: NavController,
    viewModel: AccountsViewModel
) {
    var  title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    val accountTypes = listOf(
        "EFECTIVO",
        "AHORRO",
        "PRESTAMO"
    )

    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Crear cuenta",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = title,
                onValueChange = {title = it},
                label = { Text("Nombre de la cuenta")},
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    accountTypes.forEach { option ->

                        DropdownMenuItem(
                            text = {
                                Text(option)
                            },
                            onClick = {
                                type = option
                                expanded = false
                            }
                        )
                    }
                }
            }



            OutlinedTextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Saldo inicial") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Número") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Button(
                onClick = {

                    if (isSaving) return@Button

                    isSaving = true

                    viewModel.insertAccount(
                        Account(
                            id = 0,
                            type = type,
                            badge = type,
                            title = title,
                            balance = balance.toDoubleOrNull() ?: 0.0,
                            number = number
                        )
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Cuenta creada correctamente"
                        )

                        navController.navigate(Screen.Accounts.route) {
                            popUpTo(Screen.CreateAccount.route) {
                                inclusive = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isSaving)"Creando cuenta..."
                    else "Crear cuenta" )
            }
        }
    }
}
