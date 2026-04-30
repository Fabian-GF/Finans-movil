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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
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
private val CardBorder = Color(0xFF172A4D)
private val Accent = Color(0xFF25FF00)
private val Danger = Color(0xFFFF1717)
private val Muted = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)
private val BlueBadge = Color(0xFF2D6BFF)
private val ButtonIdle = Color(0xFF152849)

data class DemoAccount(
    val type: String,
    val badge: String,
    val title: String,
    val balanceLabel: String,
    val maskedNumber: String,
    val badgeColor: Color,
    val badgeTextColor: Color = Color.White,
    val movements: List<AccountMovement>
) {
    val balanceCents: Long
        get() = movements.sumOf { it.amountCents }
}

data class AccountMovement(
    val title: String,
    val subtitle: String,
    val amountCents: Long
) {
    val isIncome: Boolean
        get() = amountCents >= 0
}

private enum class AppSection {
    Home,
    Accounts,
    Transfer
}

@Composable
fun HomeView() {
    val accounts = remember { demoAccounts() }
    var selectedSection by remember { mutableStateOf(AppSection.Home) }
    var selectedAccount by remember { mutableStateOf<DemoAccount?>(null) }
    val totalBalanceCents = accounts.sumOf { it.balanceCents }

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            DemoBottomBar(
                selectedSection = selectedSection,
                onSectionSelected = {
                    selectedSection = it
                    selectedAccount = null
                }
            )
        }
    ) { innerPadding ->
        if (selectedAccount != null) {
            AccountDetailContent(
                account = selectedAccount!!,
                modifier = Modifier.padding(innerPadding),
                onBack = { selectedAccount = null }
            )
        } else {
            when (selectedSection) {
                AppSection.Home -> HomeContent(
                    totalBalanceCents = totalBalanceCents,
                    accounts = accounts,
                    onAccountClick = { selectedAccount = it },
                    onSeeAllClick = { selectedSection = AppSection.Accounts },
                    modifier = Modifier.padding(innerPadding)
                )

                AppSection.Accounts -> AccountsContent(
                    accounts = accounts,
                    modifier = Modifier.padding(innerPadding),
                    onAccountClick = { selectedAccount = it }
                )

                AppSection.Transfer -> TransferContent(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    totalBalanceCents: Long,
    accounts: List<DemoAccount>,
    onAccountClick: (DemoAccount) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(36.dp)) }
            item { HeroBalanceCard(totalBalanceCents = totalBalanceCents) }
            item { Spacer(modifier = Modifier.height(178.dp)) }
            item {
                AccountsPreviewHeader(
                    onSeeAllClick = onSeeAllClick
                )
            }
            items(accounts) { account ->
                InfoAccountCard(
                    account = account,
                    onClick = { onAccountClick(account) }
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        ScrollBarIndicator(
            listState = listState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(top = 16.dp, end = 6.dp, bottom = 16.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun AccountsContent(
    accounts: List<DemoAccount>,
    modifier: Modifier = Modifier,
    onAccountClick: (DemoAccount) -> Unit
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(54.dp))
                Text(
                    text = "Mis Cuentas",
                    color = WhiteSoft,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            items(accounts) { account ->
                InfoAccountCard(
                    account = account,
                    onClick = { onAccountClick(account) }
                )
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        ScrollBarIndicator(
            listState = listState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(top = 16.dp, end = 6.dp, bottom = 16.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun TransferContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Transferir",
            color = WhiteSoft,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ScrollBarIndicator(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val layoutInfo = listState.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    val visibleItems = layoutInfo.visibleItemsInfo.size

    if (totalItems <= visibleItems || visibleItems == 0) return

    val firstVisibleIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
    val maxFirstVisibleIndex = (totalItems - visibleItems).coerceAtLeast(1)
    val progress = firstVisibleIndex.toFloat() / maxFirstVisibleIndex.toFloat()
    val visibleFraction = visibleItems.toFloat() / totalItems.toFloat()

    BoxWithConstraints(
        modifier = modifier.width(4.dp)
    ) {
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
                .padding(top = thumbTop)
                .width(4.dp)
                .height(thumbHeight)
                .background(Accent, RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun HeroBalanceCard(totalBalanceCents: Long) {
    var showBalance by remember { mutableStateOf(true) }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Balance Total",
                        color = Muted,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (showBalance) formatMoney(totalBalanceCents) else "--------",
                        color = WhiteSoft,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = if (showBalance) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (showBalance) "Ocultar saldo" else "Mostrar saldo",
                    tint = Muted,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { showBalance = !showBalance }
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TransactionButton(
                    label = "Ingreso",
                    icon = Icons.Default.KeyboardArrowDown,
                    selectedColor = Accent,
                    selected = selectedType == TransactionType.Income,
                    modifier = Modifier.width(108.dp),
                    onClick = { selectedType = TransactionType.Income }
                )

                Spacer(modifier = Modifier.width(18.dp))

                TransactionButton(
                    label = "Egreso",
                    icon = Icons.Default.KeyboardArrowUp,
                    selectedColor = Danger,
                    selected = selectedType == TransactionType.Expense,
                    modifier = Modifier.width(108.dp),
                    onClick = { selectedType = TransactionType.Expense }
                )
            }
        }
    }
}

private enum class TransactionType {
    Income,
    Expense
}

@Composable
private fun TransactionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedColor: Color,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (selected) selectedColor else ButtonIdle
    val content = if (selectedColor == Accent && selected) Color.Black else WhiteSoft

    Surface(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = bg,
        border = if (selected) null else BorderStroke(1.dp, Color(0xFF30415F))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = label,
                color = content,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AccountsPreviewHeader(
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mis Cuentas",
            color = WhiteSoft,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Ver todos",
            color = Accent,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onSeeAllClick)
        )
    }
}

@Composable
private fun InfoAccountCard(
    account: DemoAccount,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        AccountCardContent(account = account, compact = true)
    }
}

@Composable
private fun AccountDetailContent(
    account: DemoAccount,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = WhiteSoft,
                    modifier = Modifier
                        .size(34.dp)
                        .clickable(onClick = onBack)
                )

                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = "Detalles de cuenta",
                    color = WhiteSoft,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                AccountCardContent(account = account, compact = false)
            }
        }

        item {
            Text(
                text = "Movimientos",
                color = WhiteSoft,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column {
                    account.movements.forEachIndexed { index, movement ->
                        MovementRow(movement = movement)
                        if (index < account.movements.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(CardBorder)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }
    }
}

@Composable
private fun AccountCardContent(
    account: DemoAccount,
    compact: Boolean
) {
    Column(
        modifier = Modifier.padding(if (compact) 18.dp else 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = account.type,
                color = Muted,
                fontSize = if (compact) 15.sp else 22.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            BadgePill(
                text = account.badge,
                backgroundColor = account.badgeColor,
                textColor = account.badgeTextColor
            )

            Spacer(modifier = Modifier.weight(1f))

            if (compact) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Abrir detalle",
                    tint = Muted,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Copiar",
                    color = Accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { }
                )
            }
        }

        Spacer(modifier = Modifier.height(if (compact) 14.dp else 18.dp))

        Text(
            text = account.title,
            color = WhiteSoft,
            fontSize = if (compact) 19.sp else 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(if (compact) 24.dp else 28.dp))

        Text(
            text = account.balanceLabel,
            color = Muted,
            fontSize = if (compact) 14.sp else 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = formatMoney(account.balanceCents),
            color = WhiteSoft,
            fontSize = if (compact) 21.sp else 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(if (compact) 10.dp else 24.dp))

        Text(
            text = account.maskedNumber,
            color = MutedSoft,
            fontSize = if (compact) 14.sp else 19.sp
        )
    }
}

@Composable
private fun MovementRow(movement: AccountMovement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(54.dp),
            shape = CircleShape,
            color = if (movement.isIncome) Color(0xFF0C4927) else Color(0xFF4B071A)
        ) {
            Icon(
                imageVector = if (movement.isIncome) Icons.Default.AttachMoney else Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = WhiteSoft,
                modifier = Modifier.padding(13.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movement.title,
                color = WhiteSoft,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = movement.subtitle,
                color = MutedSoft,
                fontSize = 15.sp
            )
        }

        Text(
            text = formatMoney(movement.amountCents),
            color = if (movement.isIncome) Accent else Danger,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BadgePill(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun DemoBottomBar(
    selectedSection: AppSection,
    onSectionSelected: (AppSection) -> Unit
) {
    val items = listOf(
        BottomItem("Inicio", Icons.Default.Home, AppSection.Home),
        BottomItem("Cuentas", Icons.Default.AccountBalanceWallet, AppSection.Accounts),
        BottomItem("Transferir", Icons.Default.SwapHoriz, AppSection.Transfer)
    )

    NavigationBar(
        containerColor = AppBg,
        tonalElevation = 0.dp,
        modifier = Modifier
            .border(1.dp, Color(0xFF101820))
            .navigationBarsPadding()
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedSection == item.section,
                onClick = { onSectionSelected(item.section) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 15.sp,
                        fontWeight = if (selectedSection == item.section) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Accent,
                    selectedTextColor = Accent,
                    unselectedIconColor = MutedSoft,
                    unselectedTextColor = MutedSoft,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private data class BottomItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val section: AppSection
)

private fun demoAccounts() = listOf(
    DemoAccount(
        type = "Cuenta Nómina",
        badge = "ACTIVA",
        title = "Cuenta Principal",
        balanceLabel = "Disponible",
        maskedNumber = "**** **** 1128",
        badgeColor = BlueBadge,
        movements = listOf(
            AccountMovement("Pago de nómina", "Salario · 19 feb", 13_000_00),
            AccountMovement("Amazon.com", "Compras · Ayer", -85_99),
            AccountMovement("Transferencia enviada", "Egreso · Hoy", -480_71),
            AccountMovement("Starbucks", "Café · Hoy", -2_50)
        )
    ),
    DemoAccount(
        type = "Ahorro",
        badge = "AHORRO",
        title = "Fondo Viaje",
        balanceLabel = "Disponible",
        maskedNumber = "**** **** 9041",
        badgeColor = Accent,
        badgeTextColor = Color.Black,
        movements = listOf(
            AccountMovement("Aporte inicial", "Ahorro · 15 feb", 50_000_00),
            AccountMovement("Reserva hotel", "Viajes · Ayer", -420_00),
            AccountMovement("Compra de tiquetes", "Viajes · 20 feb", -1_380_00)
        )
    ),
    DemoAccount(
        type = "Crédito",
        badge = "PRÉSTAMO",
        title = "Tarjeta de Crédito",
        balanceLabel = "Disponible",
        maskedNumber = "**** **** 9041",
        badgeColor = Danger,
        movements = listOf(
            AccountMovement("Pago recibido", "Abono · Hoy", 500_00),
            AccountMovement("Amazon.com", "Compras · Ayer", -985_99),
            AccountMovement("Cuota préstamo", "Crédito · 19 feb", -1_801_51),
            AccountMovement("Starbucks", "Café · Hoy", -12_50)
        )
    )
)

private fun formatMoney(cents: Long): String {
    val sign = if (cents < 0) "-" else ""
    val absolute = kotlin.math.abs(cents)
    val whole = absolute / 100
    val decimals = absolute % 100
    return "$sign\$${formatThousands(whole)}.${decimals.toString().padStart(2, '0')}"
}

private fun formatThousands(value: Long): String {
    val raw = value.toString()
    return raw.reversed().chunked(3).joinToString(",").reversed()
}
