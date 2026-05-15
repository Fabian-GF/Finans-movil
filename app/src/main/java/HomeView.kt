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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
    val currency: MoneyCurrency = MoneyCurrency.COP,
    val badgeColor: Color,
    val badgeTextColor: Color = Color.White,
    val movements: List<AccountMovement>
) {
    val balanceAmount: Long
        get() = movements.sumOf { it.amount }
}

data class AccountMovement(
    val title: String,
    val subtitle: String,
    val amount: Long
) {
    val isIncome: Boolean
        get() = amount >= 0
}

data class BankNotification(
    val title: String,
    val accountTitle: String,
    val amount: Long,
    val currency: MoneyCurrency,
    val subtitle: String
)

enum class MoneyCurrency(
    val code: String,
    val symbol: String,
    val groupingSeparator: Char
) {
    COP("COP", "$", '.'),
    USD("USD", "US$", ','),
    EUR("EUR", "€", '.')
}

private enum class AppSection {
    Home,
    Accounts,
    Notifications,
    Transfer
}

@Composable
fun HomeView() {
    val accounts = remember { mutableStateListOf(*demoAccounts().toTypedArray()) }
    val notifications = remember { mutableStateListOf<BankNotification>() }
    var selectedSection by remember { mutableStateOf(AppSection.Home) }
    var selectedAccount by remember { mutableStateOf<DemoAccount?>(null) }
    var creatingAccount by remember { mutableStateOf(false) }
    val totalBalanceAmount = accounts
        .filter { it.currency == MoneyCurrency.COP }
        .sumOf { it.balanceAmount }

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            DemoBottomBar(
                selectedSection = selectedSection,
                onSectionSelected = {
                    selectedSection = it
                    selectedAccount = null
                    creatingAccount = false
                }
            )
        }
    ) { innerPadding ->
        if (selectedAccount != null) {
            AccountDetailContent(
                account = selectedAccount!!,
                modifier = Modifier.padding(innerPadding),
                onBack = { selectedAccount = null },
                onSavingsMovement = { account, movement ->
                    val updatedAccount = account.copy(movements = listOf(movement) + account.movements)
                    val index = accounts.indexOf(account)
                    if (index >= 0) {
                        accounts[index] = updatedAccount
                    }
                    selectedAccount = updatedAccount
                    notifications.add(
                        0,
                        BankNotification(
                            title = movement.title,
                            accountTitle = account.title,
                            amount = movement.amount,
                            currency = account.currency,
                            subtitle = movement.subtitle
                        )
                    )
                }
            )
        } else {
            when (selectedSection) {
                AppSection.Home -> HomeContent(
                    totalBalanceAmount = totalBalanceAmount,
                    accounts = accounts,
                    onAccountClick = { selectedAccount = it },
                    onSeeAllClick = {
                        selectedSection = AppSection.Accounts
                        creatingAccount = false
                    },
                    modifier = Modifier.padding(innerPadding)
                )

                AppSection.Accounts -> {
                    if (creatingAccount) {
                        CreateAccountContent(
                            modifier = Modifier.padding(innerPadding),
                            onBack = { creatingAccount = false },
                            onCreate = { account ->
                                accounts.add(account)
                                creatingAccount = false
                            }
                        )
                    } else {
                        AccountsContent(
                            accounts = accounts,
                            modifier = Modifier.padding(innerPadding),
                            onAccountClick = { selectedAccount = it },
                            onCreateAccountClick = { creatingAccount = true }
                        )
                    }
                }

                AppSection.Notifications -> NotificationsContent(
                    notifications = notifications,
                    modifier = Modifier.padding(innerPadding)
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
    totalBalanceAmount: Long,
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
            item { HeroBalanceCard(totalBalanceAmount = totalBalanceAmount) }
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
    onAccountClick: (DemoAccount) -> Unit,
    onCreateAccountClick: () -> Unit
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis Cuentas",
                        color = WhiteSoft,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(onClick = onCreateAccountClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Nueva")
                    }
                }
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
private fun CreateAccountContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onCreate: (DemoAccount) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Cuenta de ahorro") }
    var initialAmount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(MoneyCurrency.COP) }
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(modifier = Modifier.height(54.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = "Nueva cuenta",
                color = WhiteSoft,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Tipo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = initialAmount,
            onValueChange = { value ->
                initialAmount = normalizeIntegerAmountInput(value)
            },
            label = { Text("Saldo inicial entero") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { currencyMenuExpanded = true },
                shape = RoundedCornerShape(8.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, MutedSoft)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Divisa: ${selectedCurrency.code}",
                        color = WhiteSoft,
                        fontSize = 16.sp
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = WhiteSoft
                    )
                }
            }

            DropdownMenu(
                expanded = currencyMenuExpanded,
                onDismissRequest = { currencyMenuExpanded = false }
            ) {
                MoneyCurrency.values().forEach { currency ->
                    DropdownMenuItem(
                        text = { Text("${currency.code} ${currency.symbol}") },
                        onClick = {
                            selectedCurrency = currency
                            currencyMenuExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            enabled = title.isNotBlank() && type.isNotBlank(),
            onClick = {
                val amount = initialAmount.toLongOrNull() ?: 0L
                onCreate(
                    DemoAccount(
                        type = type.trim(),
                        badge = selectedCurrency.code,
                        title = title.trim(),
                        balanceLabel = "Disponible",
                        maskedNumber = "**** **** ${System.currentTimeMillis().toString().takeLast(4)}",
                        currency = selectedCurrency,
                        badgeColor = if (selectedCurrency == MoneyCurrency.COP) Accent else BlueBadge,
                        badgeTextColor = if (selectedCurrency == MoneyCurrency.COP) Color.Black else Color.White,
                        movements = if (amount != 0L) {
                            listOf(AccountMovement("Saldo inicial", "Apertura - Hoy", amount))
                        } else {
                            emptyList()
                        }
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Crear cuenta")
        }
    }
}

@Composable
private fun NotificationsContent(
    notifications: List<BankNotification>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(54.dp))
            Text(
                text = "Notificaciones",
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (notifications.isEmpty()) {
            item {
                Text(
                    text = "Aún no tienes movimientos guardados.",
                    color = Muted,
                    fontSize = 18.sp
                )
            }
        } else {
            items(notifications) { notification ->
                NotificationCard(notification = notification)
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }
    }
}

@Composable
private fun NotificationCard(notification: BankNotification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (notification.amount >= 0) Color(0xFF0C4927) else Color(0xFF4B071A)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    color = WhiteSoft,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${notification.accountTitle} · ${notification.subtitle}",
                    color = MutedSoft,
                    fontSize = 14.sp
                )
            }

            Text(
                text = formatMoney(notification.amount, notification.currency),
                color = if (notification.amount >= 0) Accent else Danger,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
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
private fun HeroBalanceCard(totalBalanceAmount: Long) {
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
                        text = "Balance Total COP",
                        color = Muted,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (showBalance) formatMoney(totalBalanceAmount, MoneyCurrency.COP) else "--------",
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
    onBack: () -> Unit,
    onSavingsMovement: (DemoAccount, AccountMovement) -> Unit
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

        if (account.isSavingsAccount()) {
            item {
                SavingsAccountActions(
                    account = account,
                    onMovementCreated = { movement ->
                        onSavingsMovement(account, movement)
                    }
                )
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
                        MovementRow(
                            movement = movement,
                            currency = account.currency
                        )
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
private fun SavingsAccountActions(
    account: DemoAccount,
    onMovementCreated: (AccountMovement) -> Unit
) {
    var notificationName by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Mover dinero",
                color = WhiteSoft,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = notificationName,
                onValueChange = { notificationName = it },
                label = { Text("Nombre de la notificación") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = normalizeIntegerAmountInput(it) },
                label = { Text("Monto entero") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    enabled = notificationName.isNotBlank() && normalizedAmountOrZero(amountInput) != 0L,
                    onClick = {
                        val amount = kotlin.math.abs(normalizedAmountOrZero(amountInput))
                        onMovementCreated(
                            AccountMovement(
                                title = notificationName.trim(),
                                subtitle = "Consignación · Hoy",
                                amount = amount
                            )
                        )
                        notificationName = ""
                        amountInput = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Consignar")
                }

                Button(
                    enabled = notificationName.isNotBlank() && normalizedAmountOrZero(amountInput) != 0L,
                    onClick = {
                        val amount = -kotlin.math.abs(normalizedAmountOrZero(amountInput))
                        onMovementCreated(
                            AccountMovement(
                                title = notificationName.trim(),
                                subtitle = "Extracción · Hoy",
                                amount = amount
                            )
                        )
                        notificationName = ""
                        amountInput = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Extraer")
                }
            }

            Text(
                text = "Saldo actual: ${formatMoney(account.balanceAmount, account.currency)}",
                color = Muted,
                fontSize = 15.sp
            )
        }
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
            text = formatMoney(account.balanceAmount, account.currency),
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
private fun MovementRow(
    movement: AccountMovement,
    currency: MoneyCurrency
) {
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
            text = formatMoney(movement.amount, currency),
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
        BottomItem("Avisos", Icons.Default.Notifications, AppSection.Notifications),
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
            AccountMovement("Pago de nómina", "Salario · 19 feb", 13_000_000),
            AccountMovement("Amazon.com", "Compras · Ayer", -85_900),
            AccountMovement("Transferencia enviada", "Egreso · Hoy", -480_700),
            AccountMovement("Starbucks", "Café · Hoy", -12_500)
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
            AccountMovement("Aporte inicial", "Ahorro · 15 feb", 50_000_000),
            AccountMovement("Reserva hotel", "Viajes · Ayer", -420_000),
            AccountMovement("Compra de tiquetes", "Viajes · 20 feb", -1_380_000)
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
            AccountMovement("Pago recibido", "Abono · Hoy", 500_000),
            AccountMovement("Amazon.com", "Compras · Ayer", -985_900),
            AccountMovement("Cuota préstamo", "Crédito · 19 feb", -1_801_500),
            AccountMovement("Starbucks", "Café · Hoy", -12_500)
        )
    )
)

private fun formatMoney(amount: Long, currency: MoneyCurrency): String {
    val sign = if (amount < 0) "-" else ""
    val absolute = kotlin.math.abs(amount)
    return "$sign${currency.symbol} ${formatThousands(absolute, currency.groupingSeparator)} ${currency.code}"
}

private fun DemoAccount.isSavingsAccount(): Boolean {
    return type.contains("ahorro", ignoreCase = true) || badge.equals("AHORRO", ignoreCase = true)
}

private fun normalizedAmountOrZero(value: String): Long {
    return normalizeIntegerAmountInput(value).toLongOrNull() ?: 0L
}

private fun normalizeIntegerAmountInput(value: String): String {
    val hasNegativeSign = value.contains('-')
    val digits = value.filter { it.isDigit() }
    return if (hasNegativeSign) "-$digits" else digits
}

private fun formatThousands(value: Long, separator: Char): String {
    val raw = value.toString()
    return raw.reversed().chunked(3).joinToString(separator.toString()).reversed()
}
