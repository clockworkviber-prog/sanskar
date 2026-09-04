package com.sanskar.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sanskar.app.AppViewModel
import com.sanskar.app.ui.screens.CheckoutScreen
import com.sanskar.app.ui.screens.EditProfileScreen
import com.sanskar.app.ui.screens.HomeScreen
import com.sanskar.app.ui.screens.InvoiceScreen
import com.sanskar.app.ui.screens.LoginScreen
import com.sanskar.app.ui.screens.MuhuratScreen
import com.sanskar.app.ui.screens.OrderHistoryScreen
import com.sanskar.app.ui.screens.PrepGuideScreen
import com.sanskar.app.ui.screens.PriestHomeScreen
import com.sanskar.app.ui.screens.PriestProfileScreen
import com.sanskar.app.ui.screens.PriestSignupScreen
import com.sanskar.app.ui.screens.PriestsScreen
import com.sanskar.app.ui.screens.ProfileScreen
import com.sanskar.app.ui.screens.PujaDetailScreen
import com.sanskar.app.ui.screens.SignupScreen
import com.sanskar.app.ui.screens.SupportChatScreen

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val MUHURAT = "muhurat"
    const val PRIESTS = "priests"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val ORDERS = "orders"
    const val PUJA_DETAIL = "puja/{pujaId}"
    const val PREP_GUIDE = "guide/{pujaId}"
    const val CHECKOUT = "checkout"
    const val INVOICE = "invoice/{bookingId}"
    const val PRIEST_SIGNUP = "priest_signup"
    const val PRIEST_HOME = "priest_home"
    const val PRIEST_PROFILE = "priest/{priestId}"
    const val SUPPORT = "support"
    fun invoice(id: String) = "invoice/$id"
    fun priestProfile(id: String) = "priest/$id"
    fun pujaDetail(id: String) = "puja/$id"
    fun prepGuide(id: String) = "guide/$id"
}

private data class BottomTab(val route: String, val label: String, val icon: ImageVector)

private val bottomTabs = listOf(
    BottomTab(Routes.HOME, "Home", Icons.Filled.Home),
    BottomTab(Routes.MUHURAT, "Muhurat", Icons.Filled.CalendarMonth),
    BottomTab(Routes.PRIESTS, "Priests", Icons.Filled.SelfImprovement),
    BottomTab(Routes.PROFILE, "Profile", Icons.Filled.Person)
)

@Composable
fun SanskarApp(viewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomTabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onPriestLoginSuccess = {
                        navController.navigate(Routes.PRIEST_HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onGoToSignup = { navController.navigate(Routes.SIGNUP) },
                    onGoToPriestSignup = { navController.navigate(Routes.PRIEST_SIGNUP) }
                )
            }
            composable(Routes.PRIEST_SIGNUP) {
                PriestSignupScreen(
                    viewModel = viewModel,
                    onSignupSuccess = {
                        navController.navigate(Routes.PRIEST_HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() }
                )
            }
            composable(Routes.PRIEST_HOME) {
                PriestHomeScreen(
                    viewModel = viewModel,
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.PRIEST_PROFILE) { entry ->
                val priestId = entry.arguments?.getString("priestId").orEmpty()
                PriestProfileScreen(
                    priestId = priestId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SUPPORT) {
                SupportChatScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SIGNUP) {
                SignupScreen(
                    viewModel = viewModel,
                    onSignupSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    onPujaClick = { navController.navigate(Routes.pujaDetail(it)) },
                    onSeeAllMuhurat = { navController.navigate(Routes.MUHURAT) },
                    onBookNow = { navController.navigate(Routes.CHECKOUT) }
                )
            }
            composable(Routes.MUHURAT) {
                MuhuratScreen(viewModel = viewModel)
            }
            composable(Routes.PRIESTS) {
                PriestsScreen(
                    viewModel = viewModel,
                    onPriestClick = { navController.navigate(Routes.priestProfile(it)) }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = viewModel,
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                    onViewOrders = { navController.navigate(Routes.ORDERS) },
                    onSupport = { navController.navigate(Routes.SUPPORT) },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.EDIT_PROFILE) {
                EditProfileScreen(
                    viewModel = viewModel,
                    onDone = { navController.popBackStack() }
                )
            }
            composable(Routes.ORDERS) {
                OrderHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onInvoice = { navController.navigate(Routes.invoice(it)) }
                )
            }
            composable(Routes.INVOICE) { entry ->
                val bookingId = entry.arguments?.getString("bookingId").orEmpty()
                InvoiceScreen(
                    bookingId = bookingId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.PUJA_DETAIL) { entry ->
                val pujaId = entry.arguments?.getString("pujaId").orEmpty()
                PujaDetailScreen(
                    pujaId = pujaId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onBooked = {
                        // Land on order history, then open in-app checkout on top
                        navController.navigate(Routes.ORDERS) {
                            popUpTo(Routes.HOME)
                        }
                        navController.navigate(Routes.CHECKOUT)
                    },
                    onOpenGuide = { navController.navigate(Routes.prepGuide(it)) }
                )
            }
            composable(Routes.PREP_GUIDE) { entry ->
                val pujaId = entry.arguments?.getString("pujaId").orEmpty()
                PrepGuideScreen(
                    pujaId = pujaId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.CHECKOUT) {
                CheckoutScreen(
                    onClose = { navController.popBackStack() },
                    onPayLater = {
                        // Booking stays confirmed with payment pending; return to order history
                        viewModel.recordPayLater()
                        navController.navigate(Routes.ORDERS) {
                            popUpTo(Routes.HOME)
                        }
                    },
                    onPaymentSuccess = {
                        viewModel.recordPaymentSuccess()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
