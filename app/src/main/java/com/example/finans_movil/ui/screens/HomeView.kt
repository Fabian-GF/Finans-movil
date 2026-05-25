package com.example.finans_movil.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finans_movil.Data.Local.AppDatabase
import com.example.finans_movil.Data.Repository.BankRepository
import com.example.finans_movil.Data.Repository.MonthlyBillRepository
import com.example.finans_movil.Data.Repository.TransactionRepository
import com.example.finans_movil.Data.Repository.TransferRepository
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Model.MonthlyBill
import com.example.finans_movil.Model.Transaction
import com.example.finans_movil.Viewmodel.AccountsViewModel
import com.example.finans_movil.Viewmodel.Factory.AccountViewModelFactory
import com.example.finans_movil.Viewmodel.Factory.MonthlyBillViewModelFactory
import com.example.finans_movil.Viewmodel.Factory.TransactionViewModelFactory
import com.example.finans_movil.Viewmodel.Factory.TransferViewModelFactory
import com.example.finans_movil.Viewmodel.MonthlyBillViewModel
import com.example.finans_movil.Viewmodel.TransactionViewModel
import com.example.finans_movil.Viewmodel.TransferViewModel
import com.example.finans_movil.ui.utilities.formatCOP
import kotlin.collections.take

// Colores
private val AppBg      = Color(0xFF000000)
private val CardBg     = Color(0xFF081A3A)
private val CardBorder = Color(0xFF172A4D)
private val Accent     = Color(0xFF25FF00)
private val Danger     = Color(0xFFFF3B30)
private val Muted      = Color(0xFF9FAAC0)
private val MutedSoft  = Color(0xFF6F7A92)
private val WhiteSoft  = Color(0xFFF5F7FA)
private val BlueBadge  = Color(0xFF2D6BFF)
private val ButtonIdle = Color(0xFF152849)

// Rutas de navegacion
sealed class Screen(val route: String) {
    object Home          : Screen("home")
    object Accounts      : Screen("accounts")
    object Transfer      : Screen("transfer")
    object AccountDetail : Screen("accountDetail/{accountId}") {
        fun createRoute(accountId: Int) = "accountDetail/$accountId"
    }
    object Transaction   : Screen("transaction/{type}") {
        fun createRoute(type: String) = "transaction/$type"
    }
    object CreateAccount : Screen("createAccount")

    object Reports : Screen("reports")

    object MonthlyBill   : Screen("monthlyBills")
}

// Punto de entrada principal
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainView(
    repository: BankRepository,
    database: AppDatabase
) {
    val viewModel: AccountsViewModel = viewModel(
        factory = AccountViewModelFactory(
            repository = repository,
            transactionRepository = TransactionRepository(
                transactionDao = database.transactionDao(),
                accountDao = database.accountDao()
            )
        )
    )
    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(
            TransactionRepository(
                transactionDao = database.transactionDao(),
                accountDao     = database.accountDao()
            )
        )
    )
    val transferViewModel: TransferViewModel = viewModel(
        factory = TransferViewModelFactory(
            TransferRepository(
                database.accountDao(),
                database.transactionDao()
            )
        )
    )

    val monthlyBillViewModel: MonthlyBillViewModel = viewModel(
        factory = MonthlyBillViewModelFactory(
            MonthlyBillRepository(
                database       = database,              // ← nuevo parámetro
                monthlyBillDao = database.monthlyBillDao(),
                transactionDao = database.transactionDao(),
                accountDao     = database.accountDao()
            )
        )
    )

    val bills by monthlyBillViewModel.bills.collectAsState()

    val accounts     by viewModel.accounts.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val totalAhorros = accounts.filter { it.type == "AHORRO" || it.type == "EFECTIVO"}.sumOf { it.balance }
    val navController = rememberNavController()
    val lastTransaction by viewModel.lastTransactions.collectAsState()

    LaunchedEffect(accounts) {
        viewModel.loadLastTransactions(accounts.map { it.id })
    }
    Scaffold(
        containerColor = AppBg,
        bottomBar = { DemoBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeContent(
                    navController = navController,
                    accounts      = accounts,
                    transactions  = transactions,
                    totalAhorros = totalAhorros,
                    bills = bills,
                    monthlyBillViewModel = monthlyBillViewModel,
                )
            }
            composable(Screen.Accounts.route) {
                AccountsView(
                    navController    = navController,
                    viewModel        = viewModel,
                    lastTransactions = lastTransaction
                )
            }

            composable(Screen.Transfer.route) {
                TransferView(
                    accounts  = accounts,
                    viewModel = transferViewModel
                )
            }
            composable(Screen.AccountDetail.route) { backStackEntry ->
                val accountId = backStackEntry.arguments
                    ?.getString("accountId")
                    ?.toIntOrNull() ?: 0
                AccountDetailView(
                    accountId           = accountId,
                    viewModel           = viewModel,
                    transactionViewModel = transactionViewModel,
                    navController       = navController
                )
            }
            composable(Screen.Transaction.route) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: "ingreso"
                TransactionView(
                    type      = type,
                    navController = navController,
                    viewModel = transactionViewModel,
                    accounts  = accounts
                )
            }
            composable(Screen.CreateAccount.route) {
                CreateAccountView(
                    navController = navController,
                    viewModel     = viewModel
                )
            }
            composable(Screen.Reports.route) {
                ReportsView()
            }

            composable(Screen.MonthlyBill.route) {
                MonthlyBillsView(
                    bills = bills,
                    accounts = accounts,
                    viewModel = monthlyBillViewModel,
                    navController = navController
                )
            }
        }
    }
}

// Pantalla Home
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeContent(
    navController: NavHostController,
    accounts:      List<Account>,
    transactions:  List<Transaction>,
    totalAhorros:         Double,
    bills:                List<MonthlyBill>,
    monthlyBillViewModel: MonthlyBillViewModel
) {
    val listState = rememberLazyListState()
    val errorMessage by monthlyBillViewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        LazyColumn(
            state    = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(36.dp)) }

            // 0. Hero balance
            item {
                HeroBalanceCard(
                    navController = navController,
                    totalAhorros  = totalAhorros
                )
            }

            // 1. Gastos del mes
            item {
                MonthlyExpensesCard(
                    bills     = bills,
                    accounts  = accounts,
                    viewModel = monthlyBillViewModel,
                    navController = navController
                )
            }

            // 2. Movimientos recientes
            item {
                MovementsSection(transactions = transactions)
            }

            // 3. Solo 2 cuentas + botón "Ver más"
            item {
                SectionHeader(
                    title      = "Mis Cuentas",
                    actionText = "Ver todos",
                    onAction   = { navController.navigate(Screen.Accounts.route) }
                )
            }

            items(accounts.take(2)) { account ->
                InfoAccountCard(account = account, navController = navController)
            }

            if (accounts.size > 2) {
                item {
                    VerMasButton { navController.navigate(Screen.Accounts.route) }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        ScrollBarIndicator(
            listState = listState,
            modifier  = Modifier
                .align(Alignment.CenterEnd)
                .padding(top = 16.dp, end = 6.dp, bottom = 16.dp)
                .fillMaxHeight()
        )
    }
}

// Tarjeta hero con balance total
@Composable
private fun HeroBalanceCard(
    navController: NavHostController,
    totalAhorros:  Double
) {
    var showBalance   by remember { mutableStateOf(true) }
    var selectedType  by remember { mutableStateOf<TransactionType?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = CardBg),
        border   = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment   = Alignment.Top
            ) {
                Column {
                    Text(
                        text       = "Saldo Total",
                        color      = Muted,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text       = if (showBalance) formatCOP(totalAhorros) else "--------",
                        color      = WhiteSoft,
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector     = if (showBalance) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (showBalance) "Ocultar saldo" else "Mostrar saldo",
                    tint            = Muted,
                    modifier        = Modifier
                        .size(28.dp)
                        .clickable { showBalance = !showBalance }
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TransactionButton(
                    label         = "Ingreso",
                    icon          = Icons.Default.KeyboardArrowDown,
                    selectedColor = Accent,
                    selected      = selectedType == TransactionType.Income,
                    modifier      = Modifier.width(108.dp),
                    onClick       = {
                        selectedType = TransactionType.Income
                        navController.navigate(Screen.Transaction.createRoute("ingreso"))
                    }
                )

                Spacer(modifier = Modifier.width(18.dp))

                TransactionButton(
                    label         = "Egreso",
                    icon          = Icons.Default.KeyboardArrowUp,
                    selectedColor = Danger,
                    selected      = selectedType == TransactionType.Expense,
                    modifier      = Modifier.width(108.dp),
                    onClick       = {
                        selectedType = TransactionType.Expense
                        navController.navigate(Screen.Transaction.createRoute("egreso"))
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyExpensesCard(
    bills:         List<MonthlyBill>,
    accounts:      List<Account>,
    viewModel:     MonthlyBillViewModel,
    navController: NavHostController
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(22.dp),
        colors   = CardDefaults.cardColors(containerColor = CardBg),
        border   = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Gastos del mes",
                    color      = WhiteSoft,
                    fontSize   = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1A2F1A), RoundedCornerShape(999.dp))
                        .clickable { showDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Add,
                        contentDescription = "Agregar gasto fijo",
                        tint               = Accent,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (bills.isEmpty()) {
                Text(
                    text     = "Sin gastos fijos este mes",
                    color    = MutedSoft,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                bills.take(3).forEach { bill ->
                    ExpenseItem(
                        bill  = bill,
                        onPay = { viewModel.payBill(bill) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (bills.size > 3) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier         = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text       = "ver más",
                        color      = Accent,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.clickable {
                            navController.navigate(Screen.MonthlyBill.route)
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        CreateMonthlyBillDialog(
            accounts  = accounts,
            onDismiss = { showDialog = false },
            onCreate  = { bill ->
                viewModel.addBill(bill)
                showDialog = false
            }
        )
    }
}

// Botón de acción rápida (ingreso / egreso)
private enum class TransactionType { Income, Expense }

@Composable
private fun TransactionButton(
    label:         String,
    icon:          ImageVector,
    selectedColor: Color,
    selected:      Boolean,
    modifier:      Modifier = Modifier,
    onClick:       () -> Unit
) {
    val bg      = if (selected) selectedColor else ButtonIdle
    val content = if (selectedColor == Accent && selected) Color.Black else WhiteSoft

    Surface(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(22.dp),
        color  = bg,
        border = if (selected) null else BorderStroke(1.dp, Color(0xFF30415F))
    ) {
        Column(
            modifier              = Modifier.fillMaxSize(),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = content,
                modifier           = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text       = label,
                color      = content,
                fontSize   = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Cabecera de seccion
@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onAction: () -> Unit = {}) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(text = title,      color = WhiteSoft, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text(
            text     = actionText,
            color    = Accent,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onAction() }   // ← antes era vacío
        )
    }
}

// Tarjeta de cuenta
@Composable
private fun InfoAccountCard(
    account:       Account,
    navController: NavHostController
) {
    val badgeColor = when (account.badge) {
        "EFECTIVO"   -> BlueBadge
        "AHORRO"   -> Accent
        "PRESTAMO" -> Red
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
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = account.type, color = Muted, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(10.dp))
                BadgePill(
                    text            = account.badge,
                    backgroundColor = badgeColor,
                    textColor       = badgeTextColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector        = Icons.Default.ChevronRight,
                    contentDescription = "Abrir detalle",
                    tint               = Muted,
                    modifier           = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text       = account.title,
                color      = WhiteSoft,
                fontSize   = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Disponible", color = Muted, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Bottom
            ) {
                Text(
                    text       = formatCOP(account.balance),
                    color      = WhiteSoft,
                    fontSize   = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = account.number, color = MutedSoft, fontSize = 14.sp)
            }
        }
    }
}

// Badge pill
@Composable
fun BadgePill(
    text:            String,
    backgroundColor: Color,
    textColor:       Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = backgroundColor
    ) {
        Text(
            text       = text,
            color      = textColor,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

// Scrollbar lateral
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ScrollBarIndicator(
    listState: LazyListState,
    modifier:  Modifier = Modifier
) {
    val layoutInfo       = listState.layoutInfo
    val totalItems       = layoutInfo.totalItemsCount
    val visibleItems     = layoutInfo.visibleItemsInfo.size

    if (totalItems <= visibleItems || visibleItems == 0) return

    val firstVisibleIndex    = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
    val maxFirstVisibleIndex = (totalItems - visibleItems).coerceAtLeast(1)
    val progress             = firstVisibleIndex.toFloat() / maxFirstVisibleIndex.toFloat()
    val visibleFraction      = visibleItems.toFloat() / totalItems.toFloat()

    BoxWithConstraints(modifier = modifier.width(4.dp)) {
        val thumbHeight = (maxHeight * visibleFraction)
            .coerceAtLeast(42.dp)
            .coerceAtMost(maxHeight)
        val thumbTop = (maxHeight - thumbHeight) * progress

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF14213A), RoundedCornerShape(999.dp))
        )
        Box(
            modifier = Modifier
                .offset(y = thumbTop)
                .width(4.dp)
                .height(thumbHeight)
                .background(Accent, RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun VerMasButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.clickable { onClick() },
            shape    = RoundedCornerShape(999.dp),
            color    = ButtonIdle,
            border   = BorderStroke(1.dp, CardBorder)
        ) {
            Text(
                text       = "Ver más",
                color      = Accent,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 28.dp, vertical = 10.dp)
            )
        }
    }
}

// Bottom Navigation Bar
@Composable
private fun DemoBottomBar(navController: NavHostController) {
    val items = listOf(
        Triple("Inicio",     Icons.Default.Home,                  Screen.Home.route),
        Triple("Cuentas",    Icons.Default.AccountBalanceWallet,  Screen.Accounts.route),
        Triple("Transferir", Icons.Default.SwapHoriz,             Screen.Transfer.route),
        Triple("Reportes",   Icons.Default.BarChart,             Screen.Reports.route)
    )

    NavigationBar(
        containerColor = AppBg,
        tonalElevation = 0.dp,
        modifier       = Modifier
            .border(1.dp, Color(0xFF101820))
            .navigationBarsPadding()
    ) {
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentRoute     = currentBackStack?.destination?.route

        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick  = {
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                },
                icon  = {
                    Icon(
                        imageVector        = icon,
                        contentDescription = label,
                        modifier           = Modifier.size(30.dp)
                    )
                },
                label = {
                    Text(
                        text       = label,
                        fontSize   = 15.sp,
                        fontWeight = if (currentRoute == route) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Accent,
                    selectedTextColor   = Accent,
                    unselectedIconColor = MutedSoft,
                    unselectedTextColor = MutedSoft,
                    indicatorColor      = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun ExpenseItem(
    bill:  MonthlyBill,
    onPay: () -> Unit
) {
    val isPaid       = bill.status
    val badgeBg      = if (isPaid) Color(0xFF1A2F1A) else Color(0xFF2F1A1A)
    val badgeColor   = if (isPaid) Accent             else Danger

    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot indicador
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(badgeColor, RoundedCornerShape(999.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))

        // Nombre del gasto
        Text(
            text     = bill.name,
            color    = WhiteSoft,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Badge clickeable (rojo = pendiente, verde = pagado)
        Surface(
            modifier = Modifier.clickable(enabled = !isPaid) { onPay() },
            shape    = RoundedCornerShape(12.dp),
            color    = badgeBg
        ) {
            Row(
                modifier          = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPaid) {
                    Icon(
                        imageVector        = Icons.Default.Check,
                        contentDescription = null,
                        tint               = Accent,
                        modifier           = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text       = formatCOP(bill.amount),
                    color      = badgeColor,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CreateMonthlyBillDialog(
    accounts:  List<Account>,
    onDismiss: () -> Unit,
    onCreate:  (MonthlyBill) -> Unit
) {
    var name            by remember { mutableStateOf("") }
    var amount          by remember { mutableStateOf("") }
    var dueDay          by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<Account?>(accounts.firstOrNull()) }
    var expanded        by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape  = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // Título
                Text(
                    text       = "Nuevo gasto fijo",
                    color      = WhiteSoft,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Nombre
                DialogTextField(
                    value       = name,
                    onValueChange = { name = it },
                    placeholder = "Nombre  (ej: Arriendo)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Monto
                DialogTextField(
                    value         = amount,
                    onValueChange = { amount = it },
                    placeholder   = "Monto",
                    keyboardType  = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Día de vencimiento
                DialogTextField(
                    value         = dueDay,
                    onValueChange = { dueDay = it },
                    placeholder   = "Día de vencimiento  (1 - 31)",
                    keyboardType  = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown de cuenta
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        shape  = RoundedCornerShape(14.dp),
                        color  = ButtonIdle,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text  = selectedAccount?.title ?: "Selecciona una cuenta",
                                color = if (selectedAccount != null) WhiteSoft else MutedSoft,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector        = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint               = MutedSoft,
                                modifier           = Modifier.size(20.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded         = expanded,
                        onDismissRequest = { expanded = false },
                        modifier         = Modifier.background(CardBg)
                    ) {
                        accounts.forEach { account ->
                            DropdownMenuItem(
                                text    = {
                                    Column {
                                        Text(account.title, color = WhiteSoft, fontSize = 14.sp)
                                        Text(formatCOP(account.balance), color = MutedSoft, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    selectedAccount = account
                                    expanded        = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancelar
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDismiss() },
                        shape  = RoundedCornerShape(14.dp),
                        color  = ButtonIdle,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text       = "Cancelar",
                            color      = MutedSoft,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier
                                .padding(vertical = 14.dp)
                                .fillMaxWidth(),
                            textAlign  = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    // Guardar
                    val isValid = name.isNotBlank()
                            && amount.toDoubleOrNull() != null
                            && dueDay.isNotBlank()
                            && selectedAccount != null

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = isValid) {
                                onCreate(
                                    MonthlyBill(
                                        name      = name.trim(),
                                        amount    = amount.toDouble(),
                                        accountId = selectedAccount!!.id,
                                        dueDay    = dueDay.trim(),
                                        status    = false
                                    )
                                )
                            },
                        shape  = RoundedCornerShape(14.dp),
                        color  = if (isValid) Accent else ButtonIdle,
                        border = if (isValid) null else BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text       = "Guardar",
                            color      = if (isValid) Color.Black else MutedSoft,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier
                                .padding(vertical = 14.dp)
                                .fillMaxWidth(),
                            textAlign  = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogTextField(
    value:         String,
    onValueChange: (String) -> Unit,
    placeholder:   String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    BasicTextField(
        value         = value,
        onValueChange = onValueChange,
        singleLine    = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle     = androidx.compose.ui.text.TextStyle(
            color    = WhiteSoft,
            fontSize = 14.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(ButtonIdle, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(text = placeholder, color = MutedSoft, fontSize = 14.sp)
            }
            inner()
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyBillsView(
    bills:         List<MonthlyBill>,
    accounts:      List<Account>,
    viewModel:     MonthlyBillViewModel,
    navController: NavHostController
) {
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "Gastos del mes",
                        color      = WhiteSoft,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF1A2F1A), RoundedCornerShape(999.dp))
                            .clickable { showDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Add,
                            contentDescription = "Agregar",
                            tint               = Accent,
                            modifier           = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Resumen pagado / pendiente
            item {
                val pagados   = bills.count { it.status }
                val pendientes = bills.count { !it.status }
                val totalPagado   = bills.filter { it.status }.sumOf { it.amount }
                val totalPendiente = bills.filter { !it.status }.sumOf { it.amount }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(18.dp),
                    colors   = CardDefaults.cardColors(containerColor = CardBg),
                    border   = BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$pagados pagados", color = Accent,    fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = formatCOP(totalPagado),   color = Accent,    fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(CardBorder))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$pendientes pendientes", color = Danger, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = formatCOP(totalPendiente), color = Danger, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (bills.isEmpty()) {
                item {
                    Text(
                        text     = "Sin gastos fijos este mes",
                        color    = MutedSoft,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(bills) { bill ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = CardBg),
                        border   = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ExpenseItem(
                                bill  = bill,
                                onPay = { viewModel.payBill(bill) }
                            )
                            // Muestra fecha de pago si ya está pagada
                            if (bill.status && bill.paidDate != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text     = "Pagado el ${bill.paidDate}",
                                    color    = MutedSoft,
                                    fontSize = 11.sp
                                )
                            }
                            // Muestra día de vencimiento si está pendiente
                            if (!bill.status) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text     = "Vence el día ${bill.dueDay}",
                                    color    = MutedSoft,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Toast de error — fondos insuficientes
        errorMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Surface(
                    shape  = RoundedCornerShape(999.dp),
                    color  = Color(0xFF2F1A1A),
                    border = BorderStroke(1.dp, Danger)
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Check,
                            contentDescription = null,
                            tint               = Danger,
                            modifier           = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = msg, color = Danger, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            // Auto-dismiss después de 3 segundos
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(3_000)
                viewModel.clearError()
            }
        }
    }

    if (showDialog) {
        CreateMonthlyBillDialog(
            accounts  = accounts,
            onDismiss = { showDialog = false },
            onCreate  = { bill ->
                viewModel.addBill(bill)
                showDialog = false
            }
        )
    }
}