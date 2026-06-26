package com.example.expense.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense.data.db.entity.CategoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加支出") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Amount input
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.updateAmount(it) },
                label = { Text("金额") },
                prefix = { Text(if (uiState.currency == "CNY") "¥" else "$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Currency selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.currency == "CNY",
                    onClick = { viewModel.updateCurrency("CNY") },
                    label = { Text("¥ CNY") }
                )
                FilterChip(
                    selected = uiState.currency == "USD",
                    onClick = { viewModel.updateCurrency("USD") },
                    label = { Text("$ USD") }
                )
                FilterChip(
                    selected = uiState.currency == "EUR",
                    onClick = { viewModel.updateCurrency("EUR") },
                    label = { Text("€ EUR") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category selection
            Text(
                text = "分类",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = uiState.selectedCategory?.id == category.id,
                        onClick = { viewModel.selectCategory(category) }
                    )
                }
            }

            // Category suggestion
            if (uiState.suggestedCategory != null && uiState.selectedCategory?.id != uiState.suggestedCategory?.id) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectCategory(uiState.suggestedCategory!!) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "建议: ${uiState.suggestedCategory?.icon} ${uiState.suggestedCategory?.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "选择",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date input
            OutlinedTextField(
                value = uiState.date.toString(),
                onValueChange = {},
                label = { Text("日期") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Note input
            OutlinedTextField(
                value = uiState.note,
                onValueChange = { viewModel.updateNote(it) },
                label = { Text("备注 (可选)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Save button
            Button(
                onClick = { viewModel.saveExpense() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("保存")
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: CategoryEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder()
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.icon,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
