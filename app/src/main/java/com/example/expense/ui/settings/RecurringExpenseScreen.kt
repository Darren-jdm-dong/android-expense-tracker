package com.example.expense.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expense.data.db.entity.RecurringExpenseEntity
import com.example.expense.data.db.entity.RecurringFrequency
import com.example.expense.domain.model.Frequency
import com.example.expense.domain.model.RecurringExpense
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringExpenseScreen(
    onNavigateBack: () -> Unit
) {
    var recurringExpenses by remember { mutableStateOf<List<RecurringExpenseEntity>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("定期支出") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加定期支出")
            }
        }
    ) { padding ->
        if (recurringExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无定期支出",
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recurringExpenses) { expense ->
                    RecurringExpenseItem(
                        expense = expense,
                        onDelete = { /* TODO */ }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddRecurringExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { /* TODO */ }
        )
    }
}

@Composable
fun RecurringExpenseItem(
    expense: RecurringExpenseEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.note ?: "定期支出",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "¥${expense.amount.setScale(2)} / ${
                        when (expense.frequency) {
                            RecurringFrequency.DAILY -> "每天"
                            RecurringFrequency.WEEKLY -> "每周"
                            RecurringFrequency.MONTHLY -> "每月"
                        }
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (RecurringExpenseEntity) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf(RecurringFrequency.MONTHLY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加定期支出") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("金额") },
                    prefix = { Text("¥") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("频率", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFrequency == RecurringFrequency.DAILY,
                        onClick = { selectedFrequency = RecurringFrequency.DAILY },
                        label = { Text("每天") }
                    )
                    FilterChip(
                        selected = selectedFrequency == RecurringFrequency.WEEKLY,
                        onClick = { selectedFrequency = RecurringFrequency.WEEKLY },
                        label = { Text("每周") }
                    )
                    FilterChip(
                        selected = selectedFrequency == RecurringFrequency.MONTHLY,
                        onClick = { selectedFrequency = RecurringFrequency.MONTHLY },
                        label = { Text("每月") }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toBigDecimalOrNull()
                    if (amountValue != null && amountValue > BigDecimal.ZERO) {
                        onDismiss()
                    }
                }
            ) {
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
