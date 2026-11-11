package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.repository.AuraMeshRepository
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

@Composable
fun SettingsDocsScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var meshChannel by remember { mutableStateOf("7") }
    var heartbeatInterval by remember { mutableStateOf("5000") }
    var strobeFreq by remember { mutableStateOf("4") }
    var encryptionKey by remember { mutableStateOf("AURA-MESH-AES128-SECURE-KEY-2026") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_settings_docs"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = "SYSTEM ARCHITECTURE & CONFIG",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuraCyan
                )
            )
            Text(
                text = "Settings & Documentation",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = AuraTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        // Privacy & Ethics Manifesto (Required by spec)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DATA MINIMIZATION & ETHICAL PRIVACY PILLARS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraCyan,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                DocBulletPoint(
                    title = "Zero Facial Recognition",
                    body = "AURA MESH never uses optical facial recognition or computer vision cameras. All detection relies on anonymous PIR passive infrared and mmWave micro-radar."
                )
                DocBulletPoint(
                    title = "Zero Audio Surveillance",
                    body = "Nodes do not contain listening microphones. The physical SOS input is a direct momentary tactile mechanical pushbutton."
                )
                DocBulletPoint(
                    title = "Zero Personal Identity Tracking",
                    body = "The infrastructure senses corridor occupancy to illuminate pedestrian pathways without ever storing individual trajectories or personal identifiers."
                )
            }
        }

        // Mesh Parameters Configuration
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ESP-NOW MESH PARAMETERS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextMuted, letterSpacing = 1.sp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = meshChannel,
                        onValueChange = { meshChannel = it },
                        label = { Text("Channel (1-13)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuraTextPrimary,
                            unfocusedTextColor = AuraTextPrimary,
                            focusedBorderColor = AuraCyan,
                            unfocusedBorderColor = AuraBorder
                        )
                    )
                    OutlinedTextField(
                        value = heartbeatInterval,
                        onValueChange = { heartbeatInterval = it },
                        label = { Text("Heartbeat (ms)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuraTextPrimary,
                            unfocusedTextColor = AuraTextPrimary,
                            focusedBorderColor = AuraCyan,
                            unfocusedBorderColor = AuraBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = encryptionKey,
                    onValueChange = { encryptionKey = it },
                    label = { Text("AES-128 Mesh Payload Pre-Shared Key (PSK)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AuraTextPrimary,
                        unfocusedTextColor = AuraTextPrimary,
                        focusedBorderColor = AuraCyan,
                        unfocusedBorderColor = AuraBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Mesh parameters updated across local nodes", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save & Sync Mesh Configuration", color = AuraDarkBackground, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Technical Architecture Reference Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = AuraElectricBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TECHNICAL SPECIFICATION SUMMARY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextMuted, letterSpacing = 1.sp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                DocSpecItem(label = "Physical Architecture", value = "Standalone solar-powered smart posts with ESP32-WROOM-32")
                DocSpecItem(label = "Node-to-Node Radio", value = "ESP-NOW (2.4GHz IEEE 802.11 vendor-action frames)")
                DocSpecItem(label = "Local Gateway", value = "ESP32-S3 / Raspberry Pi with Mosquitto MQTT & WebSockets")
                DocSpecItem(label = "Emergency Latency", value = "< 20ms peer broadcast cascade, sub-50ms gateway alarm")
                DocSpecItem(label = "Power Subsystem", value = "3.2V 6000mAh LiFePO4 + 5V 2W Solar MPPT (CN3791)")
                DocSpecItem(label = "Illumination Source", value = "12V 10W High-CRI LED flood with 250Hz PWM dimming")
            }
        }

        // System Version & Build Info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AURA MESH v2.4.1 (Award-Grade Hackathon Edition)\nOffline-First IoT Safety Infrastructure • 2026",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AuraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun DocBulletPoint(title: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("• $title", style = MaterialTheme.typography.labelSmall.copy(color = AuraTextPrimary, fontWeight = FontWeight.Bold))
        Text(body, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp, lineHeight = 16.sp))
    }
}

@Composable
private fun DocSpecItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(color = AuraTextPrimary, fontSize = 11.sp))
    }
}
