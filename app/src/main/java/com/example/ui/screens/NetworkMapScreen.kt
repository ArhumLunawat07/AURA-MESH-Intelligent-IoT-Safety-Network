package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.data.model.IotNode
import com.example.data.model.NodeStatus
import com.example.data.repository.AuraMeshRepository
import com.example.ui.components.NetworkMapView
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmergencyRed
import com.example.ui.theme.AuraOfflineGray
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NetworkMapScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    var selectedNodeId by remember { mutableStateOf<String?>("NODE-01") }
    var selectedZoneFilter by remember { mutableStateOf("ALL") }

    val filteredNodes = remember(nodes, selectedZoneFilter) {
        if (selectedZoneFilter == "ALL") nodes
        else nodes.filter { it.zone.startsWith(selectedZoneFilter) }
    }

    val selectedNode = nodes.find { it.id == selectedNodeId } ?: nodes.firstOrNull()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_network_map")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LIVE TOPOLOGY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraCyan
                    )
                )
                Text(
                    text = "Interactive Campus Map",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = AuraTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AuraDarkCard)
                    .border(1.dp, AuraBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${nodes.count { it.status != NodeStatus.OFFLINE }} / ${nodes.size} ONLINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraSuccessGreen,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Zone Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL" to "All Nodes", "Zone A" to "Zone A", "Zone B" to "Zone B", "Zone C" to "Zone C", "Zone D" to "Zone D").forEach { (key, label) ->
                val isSelected = selectedZoneFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) AuraCyan.copy(alpha = 0.2f) else AuraDarkCard)
                        .border(1.dp, if (isSelected) AuraCyan else AuraBorder, RoundedCornerShape(6.dp))
                        .clickable { selectedZoneFilter = key }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) AuraCyan else AuraTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Full Interactive Map Canvas
        NetworkMapView(
            nodes = filteredNodes,
            selectedNodeId = selectedNodeId,
            onNodeSelected = { selectedNodeId = it.id },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Node Full Inspector
        if (selectedNode != null) {
            NodeDetailInspector(
                node = selectedNode,
                allNodes = nodes,
                onTriggerMotion = { repository.triggerPedestrianMotion(selectedNode.id) },
                onTriggerSos = { repository.triggerEmergencySos(selectedNode.id) },
                onToggleOffline = { repository.toggleNodeOffline(selectedNode.id) },
                onBrightnessChange = { repository.setManualNodeBrightness(selectedNode.id, it) }
            )
        }
    }
}

@Composable
private fun NodeDetailInspector(
    node: IotNode,
    allNodes: List<IotNode>,
    onTriggerMotion: () -> Unit,
    onTriggerSos: () -> Unit,
    onToggleOffline: () -> Unit,
    onBrightnessChange: (Int) -> Unit
) {
    val statusColor = when (node.status) {
        NodeStatus.HEALTHY -> AuraSuccessGreen
        NodeStatus.WARNING -> AuraWarningAmber
        NodeStatus.EMERGENCY -> AuraEmergencyRed
        NodeStatus.OFFLINE -> AuraOfflineGray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${node.id}: ${node.name}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuraTextPrimary
                            )
                        )
                    }
                    Text(
                        text = "${node.zone} • ${node.locationDescription}",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = node.status.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Technical Telemetry Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelemetryStatBox(
                    label = "BATTERY",
                    value = "${node.batteryPercent}%",
                    sub = "${node.solarVoltage}V Solar",
                    icon = Icons.Default.BatteryChargingFull,
                    accentColor = if (node.batteryPercent < 20) AuraEmergencyRed else AuraSuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                TelemetryStatBox(
                    label = "LUX SENSOR",
                    value = "${node.ambientLux.toInt()} lx",
                    sub = if (node.ambientLux < 45) "Dusk Trigger" else "Daylight",
                    icon = Icons.Default.WbSunny,
                    accentColor = AuraWarningAmber,
                    modifier = Modifier.weight(1f)
                )
                TelemetryStatBox(
                    label = "MESH RSSI",
                    value = "${node.rssi} dBm",
                    sub = "ESP-NOW Direct",
                    icon = Icons.Default.Hub,
                    accentColor = AuraCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Brightness Adjustment Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIGHTING POWER OVERRIDE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                Text(
                    text = "${node.lightBrightness}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AuraCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Slider(
                value = node.lightBrightness.toFloat(),
                onValueChange = { onBrightnessChange(it.toInt()) },
                valueRange = 0f..100f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = AuraCyan,
                    activeTrackColor = AuraCyan,
                    inactiveTrackColor = AuraBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Hardware Identifiers & Mesh Neighbors
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AuraDarkCardElevated)
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("MAC Address:", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted))
                        Text(node.macAddress, style = MaterialTheme.typography.labelSmall.copy(color = AuraTextPrimary, fontFamily = FontFamily.Monospace))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Firmware:", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted))
                        Text(node.firmwareVersion, style = MaterialTheme.typography.labelSmall.copy(color = AuraTextPrimary, fontFamily = FontFamily.Monospace))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ESP-NOW Neighbors:", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted))
                        Text(node.neighborNodeIds.joinToString(", "), style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontFamily = FontFamily.Monospace))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTriggerMotion,
                    colors = ButtonDefaults.buttonColors(containerColor = AuraDarkCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trigger PIR", color = AuraTextPrimary, fontSize = 11.sp)
                }

                Button(
                    onClick = onTriggerSos,
                    colors = ButtonDefaults.buttonColors(containerColor = AuraDarkCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraEmergencyRed.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CrisisAlert, contentDescription = null, tint = AuraEmergencyRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SOS Alert", color = AuraEmergencyRed, fontSize = 11.sp)
                }

                Button(
                    onClick = onToggleOffline,
                    colors = ButtonDefaults.buttonColors(containerColor = AuraDarkCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (node.status == NodeStatus.OFFLINE) Icons.Default.Build else Icons.Default.PowerOff,
                        contentDescription = null,
                        tint = if (node.status == NodeStatus.OFFLINE) AuraSuccessGreen else AuraOfflineGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (node.status == NodeStatus.OFFLINE) "Restore" else "Fail Node", color = AuraTextPrimary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun TelemetryStatBox(
    label: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AuraTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(text = sub, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 9.sp))
        }
    }
}
