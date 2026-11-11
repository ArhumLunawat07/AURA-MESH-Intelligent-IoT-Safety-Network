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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun ExploreLandingScreen(
    onLaunchSimulation: () -> Unit,
    onOpenDashboard: () -> Unit,
    onViewHardware: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Security Infrastructure Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(AuraDarkCard)
                .border(1.dp, AuraBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AuraSuccessGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OFFLINE-FIRST IOT SAFETY INFRASTRUCTURE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraCyan,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Title
        Text(
            text = "AURA MESH",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = AuraTextPrimary,
                fontFamily = FontFamily.Monospace
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tagline
        Text(
            text = "\"Don't just send an SOS. Make the environment respond.\"",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = AuraCyan,
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Autonomous campus & walkway safety network. Physical ESP32 nodes coordinate via local ESP-NOW mesh to detect pedestrians, illuminate paths, and floodlight corridors during emergencies—even when internet connectivity is severed.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AuraTextSecondary,
                lineHeight = 20.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.95f)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // The 3 Core Choices
        Text(
            text = "EXPLORE AURA MESH",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = AuraTextMuted
            ),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Choice 1: Launch Interactive Simulation
        ExploreChoiceCard(
            title = "1. Launch Interactive Simulation",
            badge = "RECOMMENDED FOR JUDGES",
            badgeColor = AuraCyan,
            description = "Experience normal pedestrian motion, dusk illumination, SOS cascade, and live internet severance tests.",
            icon = Icons.Default.PlayArrow,
            accentColor = AuraCyan,
            testTag = "choice_launch_simulation",
            onClick = onLaunchSimulation
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Choice 2: Open Monitoring Dashboard
        ExploreChoiceCard(
            title = "2. Open Monitoring Dashboard",
            badge = "COMMAND CENTER",
            badgeColor = AuraElectricBlue,
            description = "Live security control room: 5 IoT nodes, active emergency alerts, adaptive lighting, and real-time telemetry.",
            icon = Icons.Default.Speed,
            accentColor = AuraElectricBlue,
            testTag = "choice_open_dashboard",
            onClick = onOpenDashboard
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Choice 3: View Hardware Architecture
        ExploreChoiceCard(
            title = "3. View Hardware Architecture",
            badge = "ESP32 & ESP-NOW",
            badgeColor = AuraViolet,
            description = "Inspect local Gateway specs, MQTT topics, JSON telemetry schemas, and ready-to-flash C++ firmware.",
            icon = Icons.Default.DeveloperBoard,
            accentColor = AuraViolet,
            testTag = "choice_view_hardware",
            onClick = onViewHardware
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Engineering Highlights Grid
        Text(
            text = "CORE SYSTEM CAPABILITIES",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = AuraTextMuted
            ),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HighlightChip(
                icon = Icons.Default.WifiOff,
                title = "Offline-First",
                subtitle = "Zero cloud dependency",
                modifier = Modifier.weight(1f)
            )
            HighlightChip(
                icon = Icons.Default.Hub,
                title = "ESP-NOW Mesh",
                subtitle = "Sub-20ms node peer link",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HighlightChip(
                icon = Icons.Default.Lightbulb,
                title = "Adaptive Light",
                subtitle = "Lux + PIR power save",
                modifier = Modifier.weight(1f)
            )
            HighlightChip(
                icon = Icons.Default.CrisisAlert,
                title = "Corridor SOS",
                subtitle = "Strobe + neighbor flood",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Safety Principle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = AuraTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SAFETY PRINCIPLE & SYSTEM TRANSPARENCY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AURA MESH is an environmental assistance and monitoring infrastructure. It does not replace emergency personnel and never automatically calls real emergency services from a demonstration platform.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AuraTextSecondary,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreChoiceCard(
    title: String,
    badge: String,
    badgeColor: Color,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AuraTextSecondary,
                        lineHeight = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = AuraTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun HighlightChip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AuraCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraTextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = AuraTextSecondary
                    )
                )
            }
        }
    }
}
