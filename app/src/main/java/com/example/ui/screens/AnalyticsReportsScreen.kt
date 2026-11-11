package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NodeStatus
import com.example.data.repository.AuraMeshRepository
import com.example.ui.components.EnergySavingsAreaChart
import com.example.ui.components.HourlyTrafficChart
import com.example.ui.components.MetricKpiCard
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmergencyRed
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.AuraWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsReportsScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    val incidents by repository.allIncidents.collectAsState(initial = emptyList())
    val telemetryLogs by repository.recentTelemetry.collectAsState(initial = emptyList())
    val networkState by repository.networkState.collectAsState()

    var selectedRange by remember { mutableStateOf("24H") }
    var csvExportPreview by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val sampleHourlyTraffic = remember {
        listOf(
            "12:00" to 12,
            "14:00" to 19,
            "16:00" to 28,
            "18:00" to 42,
            "20:00" to 65,
            "21:00" to 84, // Peak pedestrian movement
            "22:00" to 58,
            "00:00" to 22,
            "02:00" to 8,
            "04:00" to 4,
            "06:00" to 16,
            "08:00" to 38
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_analytics_reports"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SYSTEM INTELLIGENCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraCyan
                    )
                )
                Text(
                    text = "Analytics & Reports",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = AuraTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            // Date Range Switcher
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("24H", "7D", "30D").forEach { range ->
                    val isSelected = selectedRange == range
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) AuraCyan.copy(alpha = 0.2f) else AuraDarkCard)
                            .border(1.dp, if (isSelected) AuraCyan else AuraBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedRange = range }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = range,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) AuraCyan else AuraTextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        // Performance KPI Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricKpiCard(
                title = "Avg SOS Response",
                value = "14.2s",
                subtitle = "Security ACK latency",
                icon = Icons.Default.Speed,
                accentColor = AuraCyan,
                modifier = Modifier.weight(1f)
            )
            MetricKpiCard(
                title = "Node Uptime",
                value = "99.8%",
                subtitle = "ESP-NOW availability",
                icon = Icons.Default.CheckCircle,
                accentColor = AuraSuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }

        // 1. Hourly Traffic Chart
        HourlyTrafficChart(
            hourlyData = sampleHourlyTraffic
        )

        // 2. Energy Conservation Impact Area Chart (-68.4%)
        EnergySavingsAreaChart()

        // 3. AI / Anomaly Detection Insights Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraViolet.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(AuraViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Hub, contentDescription = null, tint = AuraViolet, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "STATISTICAL TELEMETRY & ANOMALY INSIGHTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuraViolet,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Explainable rule-based pattern analysis across 5 nodes",
                            style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 10.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                InsightItem(
                    status = "OPTIMAL",
                    title = "Photovoltaic Harvest Alignment",
                    description = "Solar voltages across NODE-01 to NODE-03 peak at 4.12V. Battery reserves exceed 72-hour autonomous standby requirements.",
                    color = AuraSuccessGreen
                )

                InsightItem(
                    status = "RECOMMENDED",
                    title = "Pathway Lighting Dwell Adjustment",
                    description = "Peak pedestrian volume concentrated between 20:30 and 22:00. Dwell timeout is configured to 20s, balancing safety illumination with -68% energy savings.",
                    color = AuraCyan
                )

                InsightItem(
                    status = "MESH INTEGRITY",
                    title = "ESP-NOW Link Margin",
                    description = "Minimum link RSSI is -64 dBm (NODE-05 to NODE-04). Packet delivery rate verified at 99.4% with zero unhandled dropouts.",
                    color = AuraViolet
                )
            }
        }

        // 4. Downloadable CSV Report Generation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AUDIT EXPORT & COMPLIANCE REPORTING",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextMuted, letterSpacing = 1.sp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Generate structured CSV incident and telemetry export for campus security audits and energy compliance reporting.",
                    style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val csv = generateCsvReport(nodes, incidents, telemetryLogs)
                        csvExportPreview = csv
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Aura Mesh CSV Report", csv))
                        Toast.makeText(context, "CSV Report generated & copied to clipboard!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_export_csv_report")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = AuraDarkBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate & Copy CSV Report", color = AuraDarkBackground, fontWeight = FontWeight.Bold)
                }

                if (csvExportPreview != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(AuraDarkCardElevated)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = csvExportPreview!!.take(280) + "...\n[Full CSV Copied to Clipboard]",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AuraTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightItem(
    status: String,
    title: String,
    description: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(color.copy(alpha = 0.2f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(status, style = MaterialTheme.typography.labelSmall.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 8.sp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = AuraTextPrimary, fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(description, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 10.sp, lineHeight = 14.sp))
    }
}

private fun generateCsvReport(
    nodes: List<com.example.data.model.IotNode>,
    incidents: List<com.example.data.model.EmergencyIncident>,
    logs: List<com.example.data.model.TelemetryLog>
): String {
    val builder = StringBuilder()
    builder.append("AURA MESH IOT INFRASTRUCTURE AUDIT REPORT\n")
    builder.append("Generated,${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n\n")

    builder.append("--- MESH NODES ---\n")
    builder.append("Node ID,Name,Zone,Status,Battery %,Solar Voltage,Lux,Light %,RSSI,Firmware\n")
    nodes.forEach { n ->
        builder.append("${n.id},${n.name},${n.zone},${n.status},${n.batteryPercent}%,${n.solarVoltage}V,${n.ambientLux},${n.lightBrightness}%,${n.rssi}dBm,${n.firmwareVersion}\n")
    }

    builder.append("\n--- INCIDENTS ---\n")
    builder.append("Incident ID,Origin Node,Location,Severity,Status,Timestamp,Responder,Notes\n")
    incidents.forEach { inc ->
        val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(inc.timestampEpochMs))
        builder.append("${inc.id},${inc.originatingNodeId},${inc.location},${inc.severity},${inc.status},$ts,${inc.responderName},${inc.resolutionNotes}\n")
    }

    builder.append("\n--- RECENT TELEMETRY ---\n")
    builder.append("Timestamp,Node ID,Event,Lux,Light %,Battery %,Message\n")
    logs.take(30).forEach { l ->
        val ts = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(l.timestampEpochMs))
        builder.append("$ts,${l.nodeId},${l.eventType},${l.lux},${l.brightness}%,${l.battery}%,\"${l.message}\"\n")
    }

    return builder.toString()
}
