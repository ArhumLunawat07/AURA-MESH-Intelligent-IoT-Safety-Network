package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NodeStatus
import com.example.data.repository.AuraMeshRepository
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
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.AuraWarningAmber

@Composable
fun NetworkHealthScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val networkState by repository.networkState.collectAsState()
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_network_health"),
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
                    text = "ESP-NOW MESH DIAGNOSTICS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraCyan
                    )
                )
                Text(
                    text = "Offline-First Network Health",
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
                    .background(if (networkState.wanConnected) AuraSuccessGreen.copy(alpha = 0.15f) else AuraWarningAmber.copy(alpha = 0.2f))
                    .border(1.dp, if (networkState.wanConnected) AuraSuccessGreen else AuraWarningAmber, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (networkState.wanConnected) "WAN ONLINE" else "OFFLINE MESH MODE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (networkState.wanConnected) AuraSuccessGreen else AuraWarningAmber,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        // Offline-First Mesh Architecture Visualizer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "OFFLINE-FIRST DUAL LAYER TOPOLOGY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraCyan,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                ArchitectureDiagramRow(
                    tier1 = "ESP32 SENSOR NODES (1 to 5)",
                    protocol = "ESP-NOW 2.4GHz Peer-to-Peer MAC Mesh",
                    tier2 = "CENTRAL LOCAL GATEWAY (Master Broker)",
                    secondaryProtocol = "Local WebSocket / MQTT (Port 1883/8083)",
                    tier3 = "AURA MESH COMMAND DASHBOARD",
                    wanStatus = if (networkState.wanConnected) "Cloud Sync: ENABLED" else "Cloud WAN: SEVERED (Local Mesh 100% OK)"
                )
            }
        }

        // Live Health KPI Matrix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MeshMetricBox(
                title = "Delivery Rate (PDR)",
                value = "${networkState.packetDeliveryRate}%",
                sub = "Target: > 98.0%",
                icon = Icons.Default.CheckCircle,
                accentColor = AuraSuccessGreen,
                modifier = Modifier.weight(1f)
            )
            MeshMetricBox(
                title = "Mesh Latency",
                value = "${networkState.averageLatencyMs} ms",
                sub = "ESP-NOW direct frame",
                icon = Icons.Default.Speed,
                accentColor = AuraCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MeshMetricBox(
                title = "Total Transmitted",
                value = "${networkState.packetsTransmitted}",
                sub = "CRC verified packets",
                icon = Icons.Default.NetworkCheck,
                accentColor = AuraElectricBlue,
                modifier = Modifier.weight(1f)
            )
            MeshMetricBox(
                title = "Deduplicated",
                value = "${networkState.duplicatePacketsFiltered}",
                sub = "Zero duplicate triggers",
                icon = Icons.Default.Security,
                accentColor = AuraViolet,
                modifier = Modifier.weight(1f)
            )
        }

        // WAN Severance Simulation Control
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (networkState.wanConnected) AuraBorder else AuraWarningAmber)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "INTERNET RESILIENCE EXPERIMENT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextMuted, letterSpacing = 1.sp)
                        )
                        Text(
                            text = if (networkState.wanConnected) "Disconnect WAN to verify that local mesh SOS, motion, and corridor lights operate autonomously."
                                   else "WAN is severed. Local ESP-NOW network remains 100% operational with zero event loss.",
                            style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { repository.toggleWanInternet() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (networkState.wanConnected) AuraEmergencyRed.copy(alpha = 0.8f) else AuraSuccessGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(if (networkState.wanConnected) Icons.Default.CloudOff else Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (networkState.wanConnected) "Sever WAN" else "Restore WAN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Node Mesh Link Quality & RSSI Matrix Table
        Text(
            text = "NODE LINK QUALITY & RSSI MATRIX",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AuraTextMuted,
                letterSpacing = 1.sp
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("NODE ID", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.2f))
                    Text("RSSI", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                    Text("LINK STATUS", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.2f))
                    Text("NEIGHBORS", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.2f))
                }

                nodes.forEach { node ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(node.id, style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.2f))
                        Text(
                            text = "${node.rssi} dBm",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (node.rssi > -60) AuraSuccessGreen else AuraWarningAmber,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (node.status == NodeStatus.OFFLINE) "DISCONNECTED" else "HEALTHY MESH",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (node.status == NodeStatus.OFFLINE) AuraOfflineGray else AuraSuccessGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = "${node.neighborNodeIds.size} peers",
                            style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 10.sp),
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchitectureDiagramRow(
    tier1: String,
    protocol: String,
    tier2: String,
    secondaryProtocol: String,
    tier3: String,
    wanStatus: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ArchStepBox(title = tier1, sub = "Field Hardware: PIR + Lux + SOS + LED Beam", color = AuraCyan)
        ArchConnectorLine(protocol = protocol)
        ArchStepBox(title = tier2, sub = "Raspberry Pi / ESP32 Master Hub (192.168.4.1)", color = AuraElectricBlue)
        ArchConnectorLine(protocol = secondaryProtocol)
        ArchStepBox(title = tier3, sub = "Local Central Security Room Console", color = AuraViolet)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(AuraDarkCard)
                .padding(8.dp)
        ) {
            Text(wanStatus, style = MaterialTheme.typography.labelSmall.copy(color = AuraTextSecondary, fontFamily = FontFamily.Monospace))
        }
    }
}

@Composable
private fun ArchStepBox(title: String, sub: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = color, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
            Text(sub, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 10.sp))
        }
    }
}

@Composable
private fun ArchConnectorLine(protocol: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(2.dp).height(14.dp).background(AuraCyan.copy(alpha = 0.5f)))
        Spacer(modifier = Modifier.width(8.dp))
        Text("▼  $protocol", style = MaterialTheme.typography.bodySmall.copy(color = AuraCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace))
    }
}

@Composable
private fun MeshMetricBox(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title.uppercase(), style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = AuraTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(sub, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 10.sp))
        }
    }
}
