package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraLightActive
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet

@Composable
fun MetricKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color = AuraCyan,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraTextSecondary
                    )
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = AuraTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AuraTextMuted
                )
            )
        }
    }
}

@Composable
fun HourlyTrafficChart(
    hourlyData: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PEDESTRIAN TRAFFIC DENSITY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraCyan,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Motion detections across 5 zones (Last 12 hours)",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AuraDarkCardElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "PEAK: 21:00",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AuraLightActive,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val maxVal = (hourlyData.maxOfOrNull { it.second } ?: 1).coerceAtLeast(10)
                val barSpacing = size.width / hourlyData.size
                val barWidth = barSpacing * 0.55f

                // Draw horizontal guide lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = size.height * (i.toFloat() / gridLines)
                    drawLine(
                        color = AuraBorder.copy(alpha = 0.35f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 0.8f
                    )
                }

                hourlyData.forEachIndexed { index, (label, count) ->
                    val barHeight = (count.toFloat() / maxVal) * (size.height - 24.dp.toPx())
                    val x = (index * barSpacing) + (barSpacing - barWidth) / 2
                    val y = size.height - 18.dp.toPx() - barHeight

                    // Bar gradient
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(AuraCyan, AuraElectricBlue)
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Text label under bar
                    drawContext.canvas.nativeCanvas.apply {
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#94A7C6")
                            textSize = 20f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(label, x + barWidth / 2, size.height - 2.dp.toPx(), textPaint)
                    }
                }
            }
        }
    }
}

@Composable
fun EnergySavingsAreaChart(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ENERGY CONSERVATION IMPACT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraSuccessGreen,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Adaptive Mesh vs Legacy 100% Always-On High Pressure Sodium",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary)
                    )
                }
                Text(
                    text = "-68.4%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = AuraSuccessGreen,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val w = size.width
                val h = size.height

                // Baseline Legacy Line (Flat 100% power across night)
                val legacyY = 15.dp.toPx()
                drawLine(
                    color = Color.Red.copy(alpha = 0.5f),
                    start = Offset(0f, legacyY),
                    end = Offset(w, legacyY),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Aura Mesh Curve (Dips to 20% standby, spikes on motion, daylight zero)
                val points = listOf(
                    0.0f to 0.95f, // 18:00 dusk starts
                    0.15f to 0.75f,
                    0.3f to 0.40f, // 20:00 busy motion
                    0.45f to 0.78f, // 22:00 quiet
                    0.60f to 0.82f, // 00:00 standby 20%
                    0.75f to 0.76f, // 02:00 occasional pass
                    0.90f to 0.82f, // 04:00 quiet
                    1.0f to 0.95f  // 06:00 dawn
                )

                val path = Path()
                val fillPath = Path()

                points.forEachIndexed { i, (px, py) ->
                    val x = px * w
                    val y = py * h
                    if (i == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, h)
                        fillPath.lineTo(x, y)
                    } else {
                        val prev = points[i - 1]
                        val prevX = prev.first * w
                        val prevY = prev.second * h
                        val cx = (prevX + x) / 2
                        path.cubicTo(cx, prevY, cx, y, x, y)
                        fillPath.cubicTo(cx, prevY, cx, y, x, y)
                    }
                }

                fillPath.lineTo(w, h)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(AuraSuccessGreen.copy(alpha = 0.35f), Color.Transparent)
                    )
                )

                drawPath(
                    path = path,
                    color = AuraSuccessGreen,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color.Red.copy(alpha = 0.7f), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Legacy HPS: 420 kWh", style = MaterialTheme.typography.bodySmall.copy(color = AuraTextMuted))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(AuraSuccessGreen, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aura Mesh: 132 kWh", style = MaterialTheme.typography.bodySmall.copy(color = AuraSuccessGreen, fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
