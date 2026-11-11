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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.repository.AuraMeshRepository
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraLightActive
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.AuraWarningAmber

@Composable
fun EnvironmentalMonitoringScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    val ambientLux by repository.globalAmbientLux.collectAsState()
    val scrollState = rememberScrollState()

    var pirSensitivity by remember { mutableFloatStateOf(80f) }

    val activeMotions = nodes.count { it.motionDetected }
    val avgBattery = if (nodes.isNotEmpty()) nodes.map { it.batteryPercent }.average().toInt() else 88

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_environmental_monitoring"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Sensor Mode Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SENSOR TELEMETRY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraCyan
                    )
                )
                Text(
                    text = "Environmental Monitoring",
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
                    .border(1.dp, AuraCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "LIVE SENSOR FEED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AuraCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                )
            }
        }

        // Sensor Categories Cards
        // 1. BH1750 Ambient Lux Sensor
        SensorCategoryCard(
            title = "BH1750 Digital Lux Sensor",
            value = "${ambientLux.toInt()} LUX",
            status = if (ambientLux < 45) "DUSK / NIGHT MODE ENGAGED" else "DAYLIGHT AMBIENT",
            statusColor = if (ambientLux < 45) AuraCyan else AuraWarningAmber,
            icon = Icons.Default.WbSunny,
            accentColor = AuraWarningAmber,
            details = listOf(
                "Precision Range" to "1 - 65,535 lx (I2C Bus 0x23)",
                "Automation Threshold" to "< 45 lx triggers standby pathway glow",
                "Sampling Frequency" to "2 Hz continuous"
            )
        )

        // 2. PIR Motion Detection
        SensorCategoryCard(
            title = "Passive Infrared (PIR) Motion",
            value = "$activeMotions ACTIVE",
            status = if (activeMotions > 0) "PEDESTRIAN PRESENT" else "PATHWAY IDLE",
            statusColor = if (activeMotions > 0) AuraLightActive else AuraSuccessGreen,
            icon = Icons.Default.DirectionsWalk,
            accentColor = AuraCyan,
            details = listOf(
                "Detection Angle" to "120° Wide-angle Fresnel Lens",
                "Range" to "7.5 meters pedestrian corridor",
                "Hardware Type" to "HC-SR501 / AM312 Low Power"
            )
        )

        // 3. mmWave Presence Radar (24GHz)
        SensorCategoryCard(
            title = "mmWave Micro-Presence Radar",
            value = "LD2410B ACTIVE",
            status = "STATIONARY TARGET SCANNING",
            statusColor = AuraViolet,
            icon = Icons.Default.Radar,
            accentColor = AuraViolet,
            details = listOf(
                "Operating Frequency" to "24.00 GHz - 24.25 GHz FMCW",
                "Micro-motion Range" to "Up to 6m (detects breathing/still pedestrians)",
                "Interference Filter" to "Bypasses wind & foliage false alarms"
            )
        )

        // 4. Power & Solar Harvesting (LiFePO4)
        SensorCategoryCard(
            title = "Power & Solar Energy Subsystem",
            value = "$avgBattery% AVERAGE",
            status = "SOLAR HARVESTING OPERATIONAL",
            statusColor = AuraSuccessGreen,
            icon = Icons.Default.BatteryChargingFull,
            accentColor = AuraSuccessGreen,
            details = listOf(
                "Cell Chemistry" to "LiFePO4 3.2V 6000mAh (2000+ cycle life)",
                "Solar Array" to "5V 2W Monocrystalline Panel with CN3791 MPPT",
                "Autonomy Reserve" to "72 hours continuous operation in zero sun"
            )
        )

        // 5. Environmental Climate (Temp / Humidity)
        SensorCategoryCard(
            title = "Microclimate Telemetry (SHT30)",
            value = "21.4°C / 58% RH",
            status = "NORMAL OPERATING RANGE",
            statusColor = AuraCyan,
            icon = Icons.Default.DeviceThermostat,
            accentColor = AuraElectricBlue,
            details = listOf(
                "Dew Point" to "12.8°C (No condensation on optical lenses)",
                "Operating Enclosure" to "IP66 Weatherproof Polycarbonate Post",
                "Thermal Status" to "MCU Core: 34.2°C"
            )
        )

        // Configurable PIR Sensitivity Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PIR SENSOR HARDWARE SENSITIVITY",
                        style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${pirSensitivity.toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    )
                }

                Slider(
                    value = pirSensitivity,
                    onValueChange = { pirSensitivity = it },
                    valueRange = 20f..100f,
                    colors = SliderDefaults.colors(thumbColor = AuraCyan, activeTrackColor = AuraCyan, inactiveTrackColor = AuraBorder)
                )

                Text(
                    text = "Adjusts comparator threshold on nodes to balance rapid pedestrian trigger against environmental foliage movement.",
                    style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp)
                )
            }
        }
    }
}

@Composable
private fun SensorCategoryCard(
    title: String,
    value: String,
    status: String,
    statusColor: Color,
    icon: ImageVector,
    accentColor: Color,
    details: List<Pair<String, String>>
) {
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
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuraTextPrimary
                            )
                        )
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = AuraTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AuraDarkCardElevated)
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    details.forEach { (label, desc) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontSize = 11.sp))
                            Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp))
                        }
                    }
                }
            }
        }
    }
}
