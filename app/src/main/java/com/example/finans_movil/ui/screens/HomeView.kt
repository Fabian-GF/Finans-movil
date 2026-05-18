package com.example.finans_movil.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finans_movil.Data.Local.AppDatabase
import com.example.finans_movil.Data.Repository.BankRepository
import com.example.finans_movil.Data.Repository.TransactionRepository
import com.example.finans_movil.Data.Repository.TransferRepository
import com.example.finans_movil.Model.Account
import com.example.finans_movil.Model.Transaction
import com.example.finans_movil.Viewmodel.AccountsViewModel
import com.example.finans_movil.Viewmodel.Factory.AccountViewModelFactory
import com.example.finans_movil.Viewmodel.Factory.TransactionViewModelFactory
import com.example.finans_movil.Viewmodel.Factory.TransferViewModelFactory
import com.example.finans_movil.Viewmodel.TransactionViewModel
import com.example.finans_movil.Viewmodel.TransferViewModel
import com.example.finans_movil.ui.utilities.formatCOP

// ── Colores ──────────────────────────────────────────────────────────────────
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
}

// Punto de entrada principal
@Composable
fun MainView(
    repository: BankRepository,
    database: AppDatabase
) {
    val viewModel: AccountsViewModel = viewModel(
        factory = AccountViewModelFactory(repository)
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

    val accounts     by viewModel.accounts.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val totalAhorros = accounts.filter { it.type == "AHORRO" || it.type == "EFECTIVO"}.sumOf { it.balance }
    val navController = rememberNavController()

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
                    totalAhorros  = totalAhorros
                )
            }
            composable(Screen.Accounts.route) {
                AccountsView(navController, viewModel)
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
        }
    }
}

// Pantalla Home
@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeContent(
    navController: NavHostController,
    accounts:      List<Account>,
    transactions:  List<Transaction>,
    totalAhorros:  Double
) {
    val listState = rememberLazyListState()

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

            item {
                HeroBalanceCard(
                    navController = navController,
                    totalAhorros  = totalAhorros
                )
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            item {
                SectionHeader(
                    title      = "Mis Cuentas",
                    actionText = "Ver todos"
                )
            }

            items(accounts) { account ->
                InfoAccountCard(
                    account       = account,
                    navController = navController
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                MovementsSection(transactions = transactions)
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
                        text       = "Balance Total",
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
private fun SectionHeader(title: String, actionText: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = title,
            color      = WhiteSoft,
            fontSize   = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text       = actionText,
            color      = Accent,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.clickable { }
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

// Bottom Navigation Bar
@Composable
private fun DemoBottomBar(navController: NavHostController) {
    val items = listOf(
        Triple("Inicio",     Icons.Default.Home,                  Screen.Home.route),
        Triple("Cuentas",    Icons.Default.AccountBalanceWallet,  Screen.Accounts.route),
        Triple("Transferir", Icons.Default.SwapHoriz,             Screen.Transfer.route)
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

// Preview
@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val navController   = rememberNavController()
    val fakeAccounts    = listOf(
        Account(
            id      = 1,
            type    = "AHORRO",
            badge   = "AHORRO",
            title   = "Cuenta Principal",
            balance = 12_000.0,
            number  = "1241"
        )
    )
    val fakeTransactions = listOf(
        Transaction(id = 1, accountId = 1, description = "Compra Supermercado", amount = 50.5,    type = "EGRESO"),
        Transaction(id = 2, accountId = 1, description = "Pago Nómina",         amount = 1_500.0, type = "INGRESO")
    )

    HomeContent(
        navController = navController,
        accounts      = fakeAccounts,
        transactions  = fakeTransactions,
        totalAhorros  = 12_000.0
    )
}