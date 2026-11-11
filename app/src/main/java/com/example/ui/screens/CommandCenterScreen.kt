package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyIncident
import com.example.data.model.IncidentSeverity
import com.example.data.model.IncidentStatus
import com.example.data.model.IotNode
import com.example.data.model.NodeStatus
import com.example.data.model.TelemetryEventType
import com.example.data.repository.AuraMeshRepository
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.NetworkMapView
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmergencyGlow
import com.example.ui.theme.AuraEmergencyRed
import com.example.ui.theme.AuraLightActive
import com.example.ui.theme.AuraOfflineGray
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraWarningAmber
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommandCenterScreen(
    repository: AuraMeshRepository,
    onNavigateToEmergency: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToSimLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    val activeIncidents by repository.activeIncidents.collectAsState(initial = emptyList())
    val telemetryLogs by repository.recentTelemetry.collectAsState(initial = emptyList())
    val networkState by repository.networkState.collectAsState()
    val ambientLux by repository.globalAmbientLux.collectAsState()

    var selectedNodeId by remember { mutableStateOf<String?>("NODE-02") }
    val selectedNode = nodes.find { it.id == selectedNodeId } ?: nodes.firstOrNull()

    val onlineCount = nodes.count { it.status != NodeStatus.OFFLINE }
    val motionTriggerCount = telemetryLogs.count { it.eventType == TelemetryEventType.MOTION_DETECTED }
    val activeEmergencyCount = activeIncidents.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .padding(16.dp)
            .testTag("screen_command_center"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top System Status Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CENTRAL COMMAND DECK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuraCyan
                        )
                    )
                    Text(
                        text = "AURA MESH Infrastructure",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = AuraTextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                // WAN & Gateway pills
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NetworkStatusBadge(
                        label = if (networkState.wanConnected) "WAN ONLINE" else "WAN SEVERED",
                        isPositive = networkState.wanConnected,
                        icon = if (networkState.wanConnected) Icons.Default.Cloud else Icons.Default.CloudOff
                    )
                    NetworkStatusBadge(
                        label = if (networkState.localGatewayConnected) "GW LOCAL" else "GW DISCONNECTED",
                        isPositive = networkState.localGatewayConnected,
                        icon = Icons.Default.Hub
                    )
                }
            }
        }

        // Active Emergency Banner (if any)
        if (activeEmergencyCount > 0) {
            item {
                ActiveEmergencyBanner(
                    incident = activeIncidents.first(),
                    onAcknowledge = { repository.acknowledgeIncident(activeIncidents.first().id) },
                    onViewCenter = onNavigateToEmergency
                )
            }
        }

        // 4 KPI Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricKpiCard(
                        title = "Mesh Nodes",
                        value = "$onlineCount/${nodes.size}",
                        subtitle = "${nodes.count { it.status == NodeStatus.OFFLINE }} offline flagged",
                        icon = Icons.Default.Hub,
                        accentColor = AuraCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricKpiCard(
                        title = "Active Alerts",
                        value = "$activeEmergencyCount",
                        subtitle = if (activeEmergencyCount == 0) "Zero active incidents" else "Immediate response active",
                        icon = Icons.Default.CrisisAlert,
                        accentColor = if (activeEmergencyCount > 0) AuraEmergencyRed else AuraSuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricKpiCard(
                        title = "PIR Detections",
                        value = "$motionTriggerCount",
                        subtitle = "Autonomous wave active",
                        icon = Icons.Default.DirectionsWalk,
                        accentColor = AuraElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    MetricKpiCard(
                        title = "Mesh Delivery",
                        value = "${networkState.packetDeliveryRate}%",
                        subtitle = "Ch ${networkState.activeMeshChannel} ESP-NOW link",
                        icon = Icons.Default.CheckCircle,
                        accentColor = AuraSuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Interactive Live Map Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CAMPUS MESH TOPOLOGY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AuraCyan,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Tap any node to view real-time diagnostics",
                                style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary)
                            )
                        }
                        OutlinedButton(
                            onClick = onNavigateToMap,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
                        ) {
                            Text("Full Map", style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    NetworkMapView(
                        nodes = nodes,
                        selectedNodeId = selectedNodeId,
                        onNodeSelected = { selectedNodeId = it.id },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    )

                    // Node Diagnostics Quick Bar
                    if (selectedNode != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        NodeDiagnosticsBar(
                            node = selectedNode,
                            onTriggerMotion = { repository.triggerPedestrianMotion(selectedNode.id) },
                            onTriggerSos = { repository.triggerEmergencySos(selectedNode.id) }
                        )
                    }
                }
            }
        }

        // Quick Command Actions
        item {
            Text(
                text = "FAST SIMULATION COMMANDS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AuraTextMuted,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { repository.triggerPedestrianMotion("NODE-01") },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraDarkCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("btn_trigger_motion")
                ) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Motion", color = AuraTextPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = { repository.triggerEmergencySos("NODE-02", IncidentSeverity.CRITICAL) },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraDarkCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraEmergencyRed.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("btn_trigger_sos")
                ) {
                    Icon(Icons.Default.CrisisAlert, contentDescription = null, tint = AuraEmergencyRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SOS Test", color = AuraEmergencyRed, fontSize = 12.sp)
                }

                Button(
                    onClick = { repository.toggleWanInternet() },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraDarkCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("btn_toggle_wan")
                ) {
                    Icon(
                        if (networkState.wanConnected) Icons.Default.CloudOff else Icons.Default.Cloud,
                        contentDescription = null,
                        tint = if (networkState.wanConnected) AuraWarningAmber else AuraSuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (networkState.wanConnected) "Cut WAN" else "Restore", color = AuraTextPrimary, fontSize = 12.sp)
                }
            }
        }

        // Live Event Stream
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE MESH TELEMETRY STREAM",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraTextMuted,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Channel 7 / ESP-NOW",
                    style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontFamily = FontFamily.Monospace)
                )
            }
        }

        items(telemetryLogs.take(8)) { log ->
            TelemetryLogItem(log = log)
        }
    }
}

@Composable
private fun ActiveEmergencyBanner(
    incident: EmergencyIncident,
    onAcknowledge: () -> Unit,
    onViewCenter: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_emergency_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AuraEmergencyRed.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, AuraEmergencyRed)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AuraEmergencyRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CRITICAL EMERGENCY SOS ACTIVE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = AuraEmergencyRed,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AuraEmergencyRed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = incident.status.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Origin: ${incident.location} | Incident ID: ${incident.id}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AuraTextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )

            Text(
                text = "Safe corridor illumination active. Neighbor nodes floodlit at 100%.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AuraTextSecondary,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (incident.status == IncidentStatus.ACTIVE) {
                    Button(
                        onClick = onAcknowledge,
                        colors = ButtonDefaults.buttonColors(containerColor = AuraEmergencyRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Acknowledge Alert", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
                OutlinedButton(
                    onClick = onViewCenter,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Emergency Center", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextPrimary))
                }
            }
        }
    }
}

@Composable
private fun NodeDiagnosticsBar(
    node: IotNode,
    onTriggerMotion: () -> Unit,
    onTriggerSos: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${node.id} — ${node.name}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraTextPrimary
                        )
                    )
                    Text(
                        text = node.locationDescription,
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextMuted, fontSize = 11.sp)
                    )
                }

                val statusColor = when (node.status) {
                    NodeStatus.HEALTHY -> AuraSuccessGreen
                    NodeStatus.WARNING -> AuraWarningAmber
                    NodeStatus.EMERGENCY -> AuraEmergencyRed
                    NodeStatus.OFFLINE -> AuraOfflineGray
                }
                Text(
                    text = node.status.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickMetricItem(label = "Light", value = "${node.lightBrightness}%")
                QuickMetricItem(label = "Battery", value = "${node.batteryPercent}% (${node.solarVoltage}V)")
                QuickMetricItem(label = "Lux", value = "${node.ambientLux.toInt()} lx")
                QuickMetricItem(label = "RSSI", value = "${node.rssi} dBm")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onTriggerMotion,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
                ) {
                    Text("Trigger PIR", fontSize = 11.sp, color = AuraCyan)
                }
                OutlinedButton(
                    onClick = onTriggerSos,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraEmergencyRed.copy(alpha = 0.5f))
                ) {
                    Text("Trigger SOS", fontSize = 11.sp, color = AuraEmergencyRed)
                }
            }
        }
    }
}

@Composable
private fun QuickMetricItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontSize = 10.sp))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
    }
}

@Composable
private fun NetworkStatusBadge(label: String, isPositive: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPositive) AuraSuccessGreen.copy(alpha = 0.15f) else AuraEmergencyRed.copy(alpha = 0.15f))
            .border(1.dp, if (isPositive) AuraSuccessGreen.copy(alpha = 0.4f) else AuraEmergencyRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (isPositive) AuraSuccessGreen else AuraEmergencyRed, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) AuraSuccessGreen else AuraEmergencyRed,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

@Composable
private fun TelemetryLogItem(log: com.example.data.model.TelemetryLog) {
    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(log.timestampEpochMs))

    val badgeColor: Color
    val badgeIcon: ImageVector
    when (log.eventType) {
        TelemetryEventType.MOTION_DETECTED -> {
            badgeColor = AuraLightActive
            badgeIcon = Icons.Default.DirectionsWalk
        }
        TelemetryEventType.SOS_TRIGGERED -> {
            badgeColor = AuraEmergencyRed
            badgeIcon = Icons.Default.CrisisAlert
        }
        TelemetryEventType.LIGHT_ADJUSTED -> {
            badgeColor = AuraCyan
            badgeIcon = Icons.Default.Lightbulb
        }
        TelemetryEventType.NODE_OFFLINE -> {
            badgeColor = AuraWarningAmber
            badgeIcon = Icons.Default.Warning
        }
        TelemetryEventType.NODE_RESTORED -> {
            badgeColor = AuraSuccessGreen
            badgeIcon = Icons.Default.CheckCircle
        }
        else -> {
            badgeColor = AuraElectricBlue
            badgeIcon = Icons.Default.Hub
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(badgeIcon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.nodeId,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AuraTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = log.message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AuraTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
