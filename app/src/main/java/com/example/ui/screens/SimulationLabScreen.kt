package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NodeStatus
import com.example.data.model.SimulationScenario
import com.example.data.repository.AuraMeshRepository
import com.example.ui.components.NetworkMapView
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmergencyRed
import com.example.ui.theme.AuraLightActive
import com.example.ui.theme.AuraLightStandby
import com.example.ui.theme.AuraOfflineGray
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.AuraWarningAmber

@Composable
fun SimulationLabScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val simStatus by repository.simulationStatus.collectAsState()
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    val networkState by repository.networkState.collectAsState()
    val ambientLux by repository.globalAmbientLux.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_simulation_lab"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HARDWARE SIMULATOR & TEST BENCH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraCyan
                    )
                )
                Text(
                    text = "AURA Simulation Lab",
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
                    .background(if (simStatus.isRunning) AuraCyan.copy(alpha = 0.2f) else AuraDarkCard)
                    .border(1.dp, if (simStatus.isRunning) AuraCyan else AuraBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (simStatus.isRunning) "RUNNING" else "READY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (simStatus.isRunning) AuraCyan else AuraTextMuted,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        // Live Interactive Map (Observer View)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE SIMULATION CAMPUS DECK",
                        style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "All events modify real app state",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextMuted, fontSize = 10.sp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                NetworkMapView(
                    nodes = nodes,
                    selectedNodeId = null,
                    onNodeSelected = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }
        }

        // Active Scenario Status Console
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (simStatus.isRunning) AuraCyan else AuraBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = simStatus.title.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (simStatus.isRunning) AuraCyan else AuraTextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    if (simStatus.isRunning) {
                        Text(
                            text = "Step ${simStatus.currentStep}/${simStatus.totalSteps}",
                            style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = simStatus.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AuraTextSecondary,
                        lineHeight = 20.sp
                    )
                )

                if (simStatus.isRunning) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (simStatus.currentStep.toFloat() / simStatus.totalSteps.coerceAtLeast(1)).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = AuraCyan,
                        trackColor = AuraBorder
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (simStatus.isRunning) {
                        OutlinedButton(
                            onClick = { repository.stopSimulation() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuraEmergencyRed)
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = null, tint = AuraEmergencyRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pause Simulation", color = AuraEmergencyRed, fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = { repository.runSimulationScenario(SimulationScenario.SCENARIO_F_FULL_SHOWCASE) },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("btn_run_full_showcase")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AuraDarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("National Showcase Demo", color = AuraDarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 6 Scenario Cards (A through F)
        Text(
            text = "GUIDED EVALUATION SCENARIOS FOR JUDGES",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AuraTextMuted,
                letterSpacing = 1.sp
            )
        )

        ScenarioItemCard(
            title = "Scenario A: Normal Pedestrian Movement",
            description = "Pedestrian approaches West Gate (NODE-01). PIR triggers, light illuminates, and adjacent nodes pre-warm.",
            badge = "ADAPTIVE ILLUMINATION",
            icon = Icons.Default.DirectionsWalk,
            accentColor = AuraCyan,
            onClick = { repository.runSimulationScenario(SimulationScenario.SCENARIO_A_PEDESTRIAN_WALK) }
        )

        ScenarioItemCard(
            title = "Scenario B: Low-Light Dusk Transition",
            description = "Simulates evening sunset. Ambient light drops below 45 lux. Standby illumination autonomous engagement.",
            badge = "BH1750 LUX POLICY",
            icon = Icons.Default.Nightlight,
            accentColor = AuraWarningAmber,
            onClick = { repository.runSimulationScenario(SimulationScenario.SCENARIO_B_LOW_LIGHT) }
        )

        ScenarioItemCard(
            title = "Scenario C: Emergency SOS Activation",
            description = "Physical button pressed at NODE-03. Origin strobe triggers, neighbor nodes floodlight the escape corridor.",
            badge = "CRITICAL SOS",
            icon = Icons.Default.CrisisAlert,
            accentColor = AuraEmergencyRed,
            onClick = { repository.runSimulationScenario(SimulationScenario.SCENARIO_C_EMERGENCY_SOS) }
        )

        ScenarioItemCard(
            title = "Scenario D: Internet Disconnection (Offline Mesh)",
            description = "WAN internet cable severed. Local Gateway & ESP-NOW mesh continue operating with ZERO event drop.",
            badge = "ZERO CLOUD DEPENDENCY",
            icon = Icons.Default.CloudOff,
            accentColor = AuraSuccessGreen,
            onClick = { repository.runSimulationScenario(SimulationScenario.SCENARIO_D_WAN_DISCONNECT) }
        )

        ScenarioItemCard(
            title = "Scenario E: Node Failure & Mesh Bypass",
            description = "NODE-04 is powered off. Gateway detects missing heartbeat, flags node, and assisted routes bypass it.",
            badge = "FAULT TOLERANCE",
            icon = Icons.Default.PowerOff,
            accentColor = AuraOfflineGray,
            onClick = { repository.runSimulationScenario(SimulationScenario.SCENARIO_E_NODE_FAILURE) }
        )

        // Manual Hardware Sandbox Controls
        Text(
            text = "MANUAL HARDWARE CONTROLS",
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
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "PIR SENSOR MOTION INJECTION",
                    style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    nodes.forEach { node ->
                        OutlinedButton(
                            onClick = { repository.triggerPedestrianMotion(node.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuraCyan.copy(alpha = 0.5f)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Text(node.id.replace("NODE-", "N"), fontSize = 10.sp, color = AuraTextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "TOGGLE NODE HARDWARE FAILURE (KILL / RESTORE)",
                    style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    nodes.forEach { node ->
                        val isOffline = node.status == NodeStatus.OFFLINE
                        OutlinedButton(
                            onClick = { repository.toggleNodeOffline(node.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isOffline) AuraSuccessGreen else AuraEmergencyRed.copy(alpha = 0.5f)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Text(
                                if (isOffline) "Fix ${node.id.replace("NODE-", "N")}" else "Kill ${node.id.replace("NODE-", "N")}",
                                fontSize = 9.sp,
                                color = if (isOffline) AuraSuccessGreen else AuraTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScenarioItemCard(
    title: String,
    description: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, style = MaterialTheme.typography.labelSmall.copy(color = accentColor, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextPrimary))
                Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = AuraDarkCardElevated),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Run", fontSize = 11.sp, color = AuraCyan, fontWeight = FontWeight.Bold)
            }
        }
    }
}
