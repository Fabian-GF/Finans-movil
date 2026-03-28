import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finans_movil.AccountsView

private val AppBg = Color(0xFF000000)
private val CardBg = Color(0xFF081A3A)
private val CardBorder = Color(0xFF172A4D)
private val Accent = Color(0xFF25FF00)
private val Muted = Color(0xFF9FAAC0)
private val MutedSoft = Color(0xFF6F7A92)
private val WhiteSoft = Color(0xFFF5F7FA)
private val BlueBadge = Color(0xFF2D6BFF)

data class DemoAccount(
    val type: String,
    val badge: String,
    val title: String,
    val amount: String,
    val maskedNumber: String,
    val badgeColor: Color
)

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Accounts : Screen("accounts")
    object Transfer : Screen("transfer")
}

@Composable
fun MainView(){
    val navController = rememberNavController()

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            DemoBottomBar(navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route){
                HomeContent()
            }

            composable(Screen.Accounts.route){
                AccountsView()
            }
        }
    }
}
@Composable
fun HomeContent() {
    val accounts = listOf(
        DemoAccount(
            type = "Cuenta Nómina",
            badge = "ACTIVA",
            title = "Cuenta Principal",
            amount = "$12,430.80",
            maskedNumber = "**** **** 1128",
            badgeColor = BlueBadge
        ),
        DemoAccount(
            type = "Ahorro",
            badge = "AHORRO",
            title = "Fondo Viaje",
            amount = "$48,200.00",
            maskedNumber = "**** **** 9041",
            badgeColor = Accent
        )
    )


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBg)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }

            item {
                HeroBalanceCard()
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                SectionHeader(
                    title = "Mis Cuentas",
                    actionText = "Ver todos"
                )
            }

            items(accounts) { account ->
                InfoAccountCard(account = account)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
}

//tarjeta superior
@Composable
private fun HeroBalanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$61,830.25",
                        color = WhiteSoft,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = "Ver",
                    tint = Muted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    label = "Enviar",
                    icon = Icons.Default.Send,
                    selected = true
                )

                QuickActionCard(
                    label = "Escanear",
                    icon = Icons.Default.QrCodeScanner,
                    selected = false
                )

                QuickActionCard(
                    label = "Pagar",
                    icon = Icons.Default.AccountBalanceWallet,
                    selected = false
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean
) {
    val bg = if (selected) Accent else Color(0xFF1A2A47)
    val content = if (selected) Color.Black else WhiteSoft

    Card(
        modifier = Modifier
            .width(90.dp)
            .height(74.dp)
            .clickable { },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30415F))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = content,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                color = content,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = WhiteSoft,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = actionText,
            color = Accent,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { }
        )
    }
}

@Composable
private fun InfoAccountCard(account: DemoAccount) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
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
                    backgroundColor = account.badgeColor,
                    textColor = if (account.badgeColor == Accent) Color.Black else Color.White
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
                    text = account.amount,
                    color = WhiteSoft,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = account.maskedNumber,
                    color = MutedSoft,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun BadgePill(
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
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DemoBottomBar(navController: NavHostController) {

    val items = listOf(
        Triple("Inicio", Icons.Default.Home, Screen.Home.route),
        Triple("Cuentas", Icons.Default.AccountBalanceWallet, Screen.Accounts.route),
        Triple("Transferir", Icons.Default.SwapHoriz, Screen.Transfer.route)
    )

    NavigationBar(
        containerColor = AppBg,
        tonalElevation = 0.dp,
        modifier = Modifier
            .border(1.dp, Color(0xFF101820))
            .navigationBarsPadding()
    ) {
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStack?.destination?.route

        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = currentRoute == route,

                onClick = {
                    navController.navigate(route){
                          popUpTo(Screen.Home.route)
                          launchSingleTop = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 12.sp
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