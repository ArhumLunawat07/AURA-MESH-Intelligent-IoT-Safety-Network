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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
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
import com.example.data.model.LightingConfig
import com.example.data.model.LightingMode
import com.example.data.model.NodeStatus
import com.example.data.repository.AuraMeshRepository
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
import com.example.ui.theme.AuraWarningAmber

@Composable
fun AdaptiveLightingScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val config by repository.lightingConfig.collectAsState()
    val ambientLux by repository.globalAmbientLux.collectAsState()
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    val averageBrightness = if (nodes.isNotEmpty()) nodes.map { it.lightBrightness }.average().toInt() else 20
    val activeZoneCount = nodes.filter { it.lightBrightness > 30 }.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_adaptive_lighting"),
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
                    text = "ILLUMINATION POLICY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraCyan
                    )
                )
                Text(
                    text = "Intelligent Lighting Control",
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
                    text = "AVG: $averageBrightness%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AuraLightActive,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        // Mode Selector (Auto / Manual / Emergency / Scheduled)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "OPERATION MODE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraTextMuted,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModeButton(
                        label = "Auto (PIR+Lux)",
                        isSelected = config.mode == LightingMode.AUTOMATIC,
                        icon = Icons.Default.AutoAwesome,
                        onClick = { repository.updateLightingConfig(config.copy(mode = LightingMode.AUTOMATIC)) },
                        modifier = Modifier.weight(1f)
                    )
                    ModeButton(
                        label = "Manual",
                        isSelected = config.mode == LightingMode.MANUAL,
                        icon = Icons.Default.Tune,
                        onClick = { repository.updateLightingConfig(config.copy(mode = LightingMode.MANUAL)) },
                        modifier = Modifier.weight(1f)
                    )
                    ModeButton(
                        label = "Emergency",
                        isSelected = config.mode == LightingMode.EMERGENCY_OVERRIDE,
                        icon = Icons.Default.CrisisAlert,
                        onClick = { repository.updateLightingConfig(config.copy(mode = LightingMode.EMERGENCY_OVERRIDE)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Real-Time Ambient Light Level Slider (Simulates Dusk / Night / Daylight)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (ambientLux < 45) Icons.Default.Nightlight else Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = if (ambientLux < 45) AuraLightStandby else AuraWarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ENVIRONMENTAL AMBIENT LUX",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuraTextPrimary,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    Text(
                        text = "${ambientLux.toInt()} lx (${if (ambientLux < 45) "Night/Dusk" else "Daylight"})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (ambientLux < 45) AuraCyan else AuraWarningAmber,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Slider(
                    value = ambientLux,
                    onValueChange = { repository.setGlobalAmbientLux(it) },
                    valueRange = 0f..200f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = AuraWarningAmber,
                        activeTrackColor = AuraWarningAmber,
                        inactiveTrackColor = AuraBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Threshold: ${config.ambientLuxThreshold.toInt()} lx. Below threshold, nodes activate standby glow and await motion.",
                    style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp)
                )
            }
        }

        // Explainable Automation Logic Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraCyan.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AUTONOMOUS ILLUMINATION STATE MACHINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraCyan,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                RuleRow(condition = "IF Ambient Lux >= 45 lx (Daylight)", result = "Lights dim to 0% (Power Save Mode)")
                RuleRow(condition = "IF Ambient Lux < 45 lx AND No Motion", result = "Standby glow ${config.standbyBrightness}% (Guiding Light)")
                RuleRow(condition = "IF Pedestrian Motion Detected (PIR)", result = "Pathway illuminates to ${config.activeBrightness}%")
                RuleRow(condition = "IF Neighbor Node Detects Motion", result = "Predictive pre-warm to 45%")
                RuleRow(condition = "IF Verified Emergency SOS Triggered", result = "Strobe 100% on origin, 100% flood on corridor")
            }
        }

        // Brightness & Timeout Sliders
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Standby Brightness
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("STANDBY BRIGHTNESS (IDLE)", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold))
                    Text("${config.standbyBrightness}%", style = MaterialTheme.typography.labelSmall.copy(color = AuraLightStandby, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                }
                Slider(
                    value = config.standbyBrightness.toFloat(),
                    onValueChange = { repository.updateLightingConfig(config.copy(standbyBrightness = it.toInt())) },
                    valueRange = 0f..50f,
                    colors = SliderDefaults.colors(thumbColor = AuraLightStandby, activeTrackColor = AuraLightStandby, inactiveTrackColor = AuraBorder)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Active Brightness
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ACTIVE ILLUMINATION (MOTION)", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold))
                    Text("${config.activeBrightness}%", style = MaterialTheme.typography.labelSmall.copy(color = AuraLightActive, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                }
                Slider(
                    value = config.activeBrightness.toFloat(),
                    onValueChange = { repository.updateLightingConfig(config.copy(activeBrightness = it.toInt())) },
                    valueRange = 50f..100f,
                    colors = SliderDefaults.colors(thumbColor = AuraLightActive, activeTrackColor = AuraLightActive, inactiveTrackColor = AuraBorder)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Motion Dwell Timeout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("MOTION DWELL TIMEOUT", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold))
                    Text("${config.motionTimeoutSeconds} seconds", style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                }
                Slider(
                    value = config.motionTimeoutSeconds.toFloat(),
                    onValueChange = { repository.updateLightingConfig(config.copy(motionTimeoutSeconds = it.toInt())) },
                    valueRange = 10f..90f,
                    steps = 7,
                    colors = SliderDefaults.colors(thumbColor = AuraCyan, activeTrackColor = AuraCyan, inactiveTrackColor = AuraBorder)
                )
            }
        }

        // Zone Status Cards
        Text(
            text = "ZONE ILLUMINATION STATUS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AuraTextMuted,
                letterSpacing = 1.sp
            )
        )

        nodes.forEach { node ->
            ZoneLightingItem(
                node = node,
                onSetBrightness = { repository.setManualNodeBrightness(node.id, it) }
            )
        }
    }
}

@Composable
private fun ModeButton(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AuraCyan.copy(alpha = 0.2f) else AuraDarkCardElevated)
            .border(1.dp, if (isSelected) AuraCyan else AuraBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AuraCyan else AuraTextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) AuraCyan else AuraTextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun RuleRow(condition: String, result: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AuraCyan))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(condition, style = MaterialTheme.typography.labelSmall.copy(color = AuraTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp))
            Text("➔ $result", style = MaterialTheme.typography.bodySmall.copy(color = AuraCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace))
        }
    }
}

@Composable
private fun ZoneLightingItem(
    node: IotNode,
    onSetBrightness: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${node.id} (${node.name})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraTextPrimary
                    )
                )
                Text(
                    text = node.zone,
                    style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${node.lightBrightness}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (node.motionDetected) AuraLightActive else AuraCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
                OutlinedButton(
                    onClick = { onSetBrightness(if (node.lightBrightness > 50) 20 else 90) },
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(if (node.lightBrightness > 50) "Dim" else "Boost", fontSize = 10.sp, color = AuraTextPrimary)
                }
            }
        }
    }
}
