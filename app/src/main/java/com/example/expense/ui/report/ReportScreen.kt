package com.example.expense.ui.report

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense.domain.usecase.CategoryReport
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("支出报表") },
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
        ) {
            // Tab selector
            TabRow(selectedTabIndex = uiState.selectedTab) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("分类占比") }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("每日趋势") }
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.reportData == null || uiState.reportData!!.categoryReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                when (uiState.selectedTab) {
                    0 -> PieChartTab(uiState.reportData!!.categoryReports)
                    1 -> LineChartTab(uiState.reportData!!.dailyTotals.map {
                        Pair(it.date.toString(), it.total.toFloat())
                    })
                }
            }
        }
    }
}

@Composable
fun PieChartTab(categoryReports: List<CategoryReport>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            // Pie Chart
            AndroidView(
                factory = { context ->
                    PieChart(context).apply {
                        description.isEnabled = false
                        isDrawHoleEnabled = true
                        setHoleColor(AndroidColor.TRANSPARENT)
                        holeRadius = 40f
                        transparentCircleRadius = 45f
                        setDrawEntryLabels(true)
                        setEntryLabelColor(AndroidColor.BLACK)
                        setEntryLabelTextSize(12f)
                        legend.isEnabled = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                update = { chart ->
                    val entries = categoryReports.map { report ->
                        PieEntry(report.total.toFloat(), report.categoryName)
                    }
                    val dataSet = PieDataSet(entries, "").apply {
                        colors = ColorTemplate.MATERIAL_COLORS.toList()
                        sliceSpace = 3f
                        selectionShift = 5f
                        valueTextSize = 14f
                        valueTextColor = AndroidColor.WHITE
                    }
                    chart.data = PieData(dataSet)
                    chart.invalidate()
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "分类明细",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(categoryReports) { report ->
            CategoryReportItem(report)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CategoryReportItem(report: CategoryReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(report.categoryColor)),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = report.categoryIcon)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.categoryName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${report.percentage}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "¥${report.total.setScale(2)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LineChartTab(dailyData: List<Pair<String, Float>>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    axisRight.isEnabled = false
                    legend.isEnabled = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            update = { chart ->
                val entries = dailyData.mapIndexed { index, (_, value) ->
                    Entry(index.toFloat(), value)
                }
                val labels = dailyData.map { (date, _) ->
                    date.substring(5) // Show MM-DD
                }

                val dataSet = LineDataSet(entries, "每日支出").apply {
                    color = AndroidColor.BLUE
                    setCircleColor(AndroidColor.BLUE)
                    lineWidth = 2f
                    circleRadius = 4f
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }

                chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                chart.data = LineData(dataSet)
                chart.invalidate()
            }
        )
    }
}
