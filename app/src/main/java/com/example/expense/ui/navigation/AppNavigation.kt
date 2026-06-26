package com.example.expense.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expense.ui.add.AddExpenseScreen
import com.example.expense.ui.budget.BudgetScreen
import com.example.expense.ui.edit.EditExpenseScreen
import com.example.expense.ui.home.HomeScreen
import com.example.expense.ui.report.ReportScreen
import com.example.expense.ui.settings.SettingsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "首页", Icons.Default.Home)
    object Reports : Screen("reports", "报表", Icons.Default.BarChart)
    object Budget : Screen("budget", "预算", Icons.Default.AccountBalanceWallet)
    object Settings : Screen("settings", "设置", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarScreens = listOf(Screen.Home, Screen.Reports, Screen.Budget, Screen.Settings)

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in bottomBarScreens.map { it.route }) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAddExpense = {
                        navController.navigate("add_expense")
                    },
                    onNavigateToEditExpense = { expenseId ->
                        navController.navigate("edit_expense/$expenseId")
                    }
                )
            }
            composable(Screen.Reports.route) {
                ReportScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Budget.route) {
                BudgetScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable("add_expense") {
                AddExpenseScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("edit_expense/{expenseId}") { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getString("expenseId")?.toLongOrNull() ?: 0L
                EditExpenseScreen(
                    expenseId = expenseId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
