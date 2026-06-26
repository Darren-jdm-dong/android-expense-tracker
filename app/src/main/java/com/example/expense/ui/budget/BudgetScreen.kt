package com.example.expense.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense.domain.usecase.BudgetStatus
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预算管理") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加预算")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.budgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无预算设置",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.budgets) { budget ->
                    BudgetCard(budget)
                }
            }
        }
    }

    // Add budget dialog
    if (uiState.showAddDialog) {
        AddBudgetDialog(
            amount = uiState.budgetAmount,
            onAmountChange = { viewModel.updateBudgetAmount(it) },
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { viewModel.saveBudget() }
        )
    }
}

@Composable
fun BudgetCard(budget: BudgetStatus) {
    val progressColor = when {
        budget.isExceeded -> Color(0xFFEF5350)
        budget.isWarning -> Color(0xFFFFA726)
        else -> Color(0xFF66BB6A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(android.graphics.Color.parseColor(budget.categoryColor)),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = budget.categoryIcon)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = budget.categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "已花费 ¥${budget.spentAmount.setScale(2)} / ¥${budget.budgetAmount.setScale(2)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (budget.isExceeded) {
                    Text(
                        text = "已超支",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFEF5350),
                        fontWeight = FontWeight.Bold
                    )
                } else if (budget.isWarning) {
                    Text(
                        text = "即将超支",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFA726),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { (budget.usagePercent.toFloat() / 100).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "剩余 ¥${budget.remainingAmount.setScale(2)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (budget.isExceeded) Color(0xFFEF5350) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${budget.usagePercent}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AddBudgetDialog(
    amount: String,
    onAmountChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置预算") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("预算金额") },
                prefix = { Text("¥") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
