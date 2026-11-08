package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BarItem
import com.example.data.model.ChartData
import com.example.data.model.ChartSegment

@Composable
fun RenderChart(chartData: ChartData, modifier: Modifier = Modifier) {
    when (chartData) {
        is ChartData.DonutOrPie -> DonutChart(segments = chartData.segments, modifier = modifier)
        is ChartData.ComparisonBars -> ComparisonBarChart(bars = chartData.bars, modifier = modifier)
        is ChartData.ScaleGauge -> ScaleGaugeChart(gauge = chartData, modifier = modifier)
    }
}

@Composable
fun DonutChart(segments: List<ChartSegment>, modifier: Modifier = Modifier) {
    val total = segments.sumOf { it.value }
    if (total <= 0.0) return

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800),
        label = "donut_anim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val strokeWidth = 24.dp.toPx()

                for (segment in segments) {
                    val sweepAngle = ((segment.value / total) * 360f * animatedProgress).toFloat()
                    drawArc(
                        color = Color(segment.colorHex),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ratio",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "100%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (segment in segments) {
                val pct = (segment.value / total) * 100.0
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(segment.colorHex))
                    )
                    Text(
                        text = "${segment.label} (${pct.toInt()}%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonBarChart(bars: List<BarItem>, modifier: Modifier = Modifier) {
    val maxValue = bars.maxOfOrNull { it.value } ?: 1.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (bar in bars) {
            val fraction = (bar.value / maxValue).coerceIn(0.05, 1.0).toFloat()
            val animatedFraction by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(durationMillis = 700),
                label = "bar_anim"
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = bar.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = bar.displayValue,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(bar.colorHex)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(bar.colorHex))
                    )
                }
            }
        }
    }
}

@Composable
fun ScaleGaugeChart(gauge: ChartData.ScaleGauge, modifier: Modifier = Modifier) {
    val progress = ((gauge.currentValue - gauge.minValue) / (gauge.maxValue - gauge.minValue)).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Gradient segments for Underweight, Normal, Overweight, Obese
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.18f).fillMaxHeight().background(Color(0xFF3B82F6))) // Blue
                Box(modifier = Modifier.weight(0.28f).fillMaxHeight().background(Color(0xFF10B981))) // Green
                Box(modifier = Modifier.weight(0.24f).fillMaxHeight().background(Color(0xFFF59E0B))) // Amber
                Box(modifier = Modifier.weight(0.30f).fillMaxHeight().background(Color(0xFFEF4444))) // Red
            }

            // Indicator pointer
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp, 28.dp)
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("18.5 (Under)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("24.9 (Normal)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("29.9 (Over)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("40.0 (Obese)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
